pipeline {
    agent any

    environment {
        DOCKERHUB_REPO = 'davidnguyendev/be-clinic'
        APP_CONTAINER_NAME = 'be-clinic'
        APP_PORT = '8080'
        KEEP_IMAGES = '3'

        DOCKERHUB_CREDS = 'dockerhub-credentials'
        DOCKER_USER = 'dockerhub-username'
        DOCKER_PASS = 'dockerhub-password'
        SSH_CREDS = 'vps-ssh-credentials'
        TELEGRAM_CREDS = 'telegram-bot-token'
        TELEGRAM_CHAT_ID = 'telegram-chat-id'
        JENKINS_API_CREDS = 'jenkins-api-credentials'
        ENV_FILE = 'be-clinic-env'

        VPS_HOST = '159.223.41.100'
        VPS_USER = 'root'
    }

    triggers {
        githubPush()
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timeout(time: 30, unit: 'MINUTES')
        disableConcurrentBuilds()
    }

    stages {
        stage('🔍 Checkout') {
            when {
                expression {
                    return env.GIT_BRANCH == 'master' || env.GIT_BRANCH == 'origin/master'
                }
            }
            steps {
                checkout scm
                script {
                    // FIX: dùng def để tránh memory-leak warning,
                    //      rồi gán lại vào env.* để các stage sau dùng được
                    def commitShort = sh(script: "git rev-parse --short HEAD", returnStdout: true).trim()
                    def imageTag = "${DOCKERHUB_REPO}:${commitShort}"

                    env.GIT_COMMIT_SHORT = commitShort
                    env.IMAGE_TAG = imageTag

                    echo "📦 Image sẽ được tag: ${env.IMAGE_TAG}"
                }
            }
        }

        stage('🏗️ Build Docker Image') {
            when {
                expression {
                    return env.GIT_BRANCH == 'master' || env.GIT_BRANCH == 'origin/master'
                }
            }
            steps {
                script {
                    withCredentials([file(credentialsId: "${env.ENV_FILE}", variable: 'DOTENV_FILE')]) {
                        echo "🔨 Building image: ${env.IMAGE_TAG}"
                        sh 'cp $DOTENV_FILE .env'
                        sh "docker build -t ${env.IMAGE_TAG} ."
                        sh "docker tag ${env.IMAGE_TAG} ${DOCKERHUB_REPO}:latest"
                        sh 'rm -f .env'
                    }
                }
            }
        }

        stage('🚀 Push to DockerHub') {
            when {
                expression {
                    return env.GIT_BRANCH == 'master' || env.GIT_BRANCH == 'origin/master'
                }
            }
            steps {
                withCredentials([usernamePassword(
                        credentialsId: "${DOCKERHUB_CREDS}",
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh '''
                            echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                            docker push ''' + env.IMAGE_TAG + '''
                            docker push ''' + DOCKERHUB_REPO + ''':latest
                            docker logout
                        '''
                }
            }
        }

        stage('🌐 Deploy to VPS') {
            when {
                expression {
                    return env.GIT_BRANCH == 'master' || env.GIT_BRANCH == 'origin/master'
                }
            }
            steps {
                withCredentials([
                        sshUserPrivateKey(
                                credentialsId: "${SSH_CREDS}",
                                keyFileVariable: 'SSH_KEY'
                        ),
                        usernamePassword(
                                credentialsId: "${DOCKERHUB_CREDS}",
                                usernameVariable: 'DOCKER_USER',
                                passwordVariable: 'DOCKER_PASS'
                        )
                ]) {
                    script {
                        def tag = env.IMAGE_TAG
                        def repo = DOCKERHUB_REPO
                        def keep = KEEP_IMAGES
                        def name = APP_CONTAINER_NAME
                        def port = APP_PORT
                        def host = VPS_HOST
                        def user = VPS_USER

                        def deployScript = """#!/bin/bash
                                set -e

                                echo "=== [1/5] Login DockerHub ==="
                                echo "\${DOCKER_PASS_ARG}" | docker login -u "\${DOCKER_USER_ARG}" --password-stdin

                                echo "=== [2/5] Pull image moi: ${tag} ==="
                                docker pull ${tag}

                                echo "=== [3/5] Dung & xoa container cu (neu co) ==="
                                docker kill ${name} 2>/dev/null || true
                                docker stop ${name} 2>/dev/null || true
                                docker rm -f ${name} 2>/dev/null || true

                                echo "--- Cho port 8080 release..."
                                for i in \$(seq 1 15); do
                                    if ! ss -tlnp | grep -q ':8080 '; then
                                        echo "--- Port 8080 da free sau \${i}s"
                                        break
                                    fi
                                    if [ "\$i" -eq 15 ]; then
                                        echo "--- Port van bi chiem sau 15s, thu kill process..."
                                        # Kill bất kỳ process nào đang giữ 8080 (kể cả non-docker)
                                        fuser -k 8080/tcp 2>/dev/null || true
                                        sleep 1
                                    fi
                                    sleep 1
                                done

                                echo "=== [4/5] Chay container moi ==="
                                docker run -d \\
                                    --name ${name} \\
                                    --restart unless-stopped \\
                                    -p ${port}:8080 \\
                                    ${tag}

                                echo "=== [5/5] Don image cu - giu lai ${keep} gan nhat ==="
                                docker images ${repo} --format '{{.Tag}} {{.ID}}' \\
                                    | grep -v latest \\
                                    | sort -r \\
                                    | tail -n +\$(( ${keep} + 1 )) \\
                                    | awk '{print \$2}' \\
                                    | xargs -r docker rmi -f || true

                                docker logout
                                echo "Deploy thanh cong!"
                                """
                        // Ghi script vào file tạm trên Jenkins agent
                        writeFile file: '/tmp/deploy.sh', text: deployScript

                        // scp file lên VPS, rồi ssh chạy — truyền credentials qua env vars
                        sh '''
                                scp -i "$SSH_KEY" \
                                    -o StrictHostKeyChecking=no \
                                    -o ConnectTimeout=10 \
                                    /tmp/deploy.sh ''' + "${user}@${host}" + ''':/tmp/deploy_clinic.sh

                                ssh -i "$SSH_KEY" \
                                    -o StrictHostKeyChecking=no \
                                    -o ConnectTimeout=10 \
                                    ''' + "${user}@${host}" + ''' \
                                    "DOCKER_USER_ARG=$DOCKER_USER DOCKER_PASS_ARG=$DOCKER_PASS bash /tmp/deploy_clinic.sh; rm -f /tmp/deploy_clinic.sh"
                            '''

                        sh 'rm -f /tmp/deploy.sh'
                    }
                }
            }
        }
    }

    post {
        success {
            script {
                sendTelegram(
                        "✅ *BUILD THÀNH CÔNG*\n" +
                                "📦 *Project:* `${env.JOB_NAME}`\n" +
                                "🔖 *Image:* `${env.IMAGE_TAG}`\n" +
                                "🔢 *Build:* [#${env.BUILD_NUMBER}](${env.BUILD_URL})\n" +
                                "🌿 *Branch:* `${env.GIT_BRANCH}`\n" +
                                "⏱️ *Thời gian:* ${currentBuild.durationString}"
                )
            }
        }

        failure {
            script {
                sendTelegram(
                        "❌ *BUILD THẤT BẠI*\n" +
                                "📦 *Project:* `${env.JOB_NAME}`\n" +
                                "🔢 *Build:* [#${env.BUILD_NUMBER}](${env.BUILD_URL})\n" +
                                "🌿 *Branch:* `${env.GIT_BRANCH}`\n" +
                                "⏱️ *Thời gian:* ${currentBuild.durationString}"
                )
                saveAndSendLog("📋 Log build *#${env.BUILD_NUMBER}*")
            }
        }

        aborted {
            script {
                sendTelegram(
                        "⚠️ *BUILD BỊ HỦY*\n" +
                                "📦 *Project:* `${env.JOB_NAME}`\n" +
                                "🔢 *Build:* [#${env.BUILD_NUMBER}](${env.BUILD_URL})\n" +
                                "🌿 *Branch:* `${env.GIT_BRANCH}`\n" +
                                "⏱️ *Thời gian:* ${currentBuild.durationString}"
                )
                saveAndSendLog("📋 Log build *#${env.BUILD_NUMBER}*")
            }
        }

        always {
            script {
                // Dọn image trên Jenkins agent sau mỗi build
                sh "docker rmi ${env.IMAGE_TAG} ${DOCKERHUB_REPO}:latest 2>/dev/null || true"
            }
        }
    }
}


// Helper: gửi message text qua Telegram
// FIX: dùng --data-urlencode thay vì -d text="..." để tránh vỡ shell
//      khi message chứa ký tự đặc biệt (&, =, newline, quote…)
def sendTelegram(String message) {
    withCredentials([
            string(credentialsId: "${TELEGRAM_CREDS}", variable: 'BOT_TOKEN'),
            string(credentialsId: "${TELEGRAM_CHAT_ID}", variable: 'CHAT_ID')
    ]) {
        def tmpFile = "/tmp/tg_msg_${env.BUILD_NUMBER}.txt"
        writeFile file: tmpFile, text: message
        sh """
            TEXT=\$(cat ${tmpFile})
            curl -s -X POST "https://api.telegram.org/bot\${BOT_TOKEN}/sendMessage" \\
                -F chat_id="\${CHAT_ID}" \\
                -F parse_mode="Markdown" \\
                -F disable_web_page_preview="true" \\
                -F text="\${TEXT}"
            rm -f ${tmpFile}
        """
    }
}

def sendTelegramFile(String filePath, String caption = "") {
    withCredentials([
            string(credentialsId: "${TELEGRAM_CREDS}", variable: 'BOT_TOKEN'),
            string(credentialsId: "${TELEGRAM_CHAT_ID}", variable: 'CHAT_ID')
    ]) {
        def tmpCaption = "/tmp/tg_caption_${env.BUILD_NUMBER}.txt"
        writeFile file: tmpCaption, text: caption
        sh """
            CAPTION=\$(cat ${tmpCaption})
            curl -s -X POST "https://api.telegram.org/bot\${BOT_TOKEN}/sendDocument" \\
                -F chat_id="\${CHAT_ID}" \\
                -F parse_mode="Markdown" \\
                -F caption="\${CAPTION}" \\
                -F document=@"${filePath}"
            rm -f ${tmpCaption}
        """
    }
}

def getLogContent() {
    withCredentials([
            usernamePassword(
                    credentialsId: "${JENKINS_API_CREDS}",
                    usernameVariable: 'JENKINS_USER',
                    passwordVariable: 'JENKINS_TOKEN'
            )
    ]) {
        return sh(
                script: """
                curl -s -u "\${JENKINS_USER}:\${JENKINS_TOKEN}" \\
                    "${env.JENKINS_URL}job/${env.JOB_NAME}/${env.BUILD_NUMBER}/consoleText"
            """,
                returnStdout: true
        ).trim()
    }
}

def saveAndSendLog(String caption = "") {
    def logFile = "/tmp/build_log_${env.BUILD_NUMBER}.txt"
    writeFile file: logFile, text: getLogContent()
    sendTelegramFile(logFile, caption)
    sh "rm -f ${logFile}"
}
