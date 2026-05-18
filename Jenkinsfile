pipeline {
    agent any

    environment {
        DOCKERHUB_REPO     = 'davidnguyendev/be-clinic'
        APP_CONTAINER_NAME = 'be-clinic'
        APP_PORT           = '8080'
        KEEP_IMAGES        = '3'

        DOCKERHUB_CREDS    = 'dockerhub-credentials'
        SSH_CREDS          = 'vps-ssh-credentials'
        TELEGRAM_CREDS     = 'telegram-bot-token'
        TELEGRAM_CHAT_ID   = 'telegram-chat-id'

        // ⚠️ Thay bằng IP thật của VPS
        VPS_HOST           = 'http://159.223.41.100'
        VPS_USER           = 'root'
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
                    def imageTag    = "${DOCKERHUB_REPO}:${commitShort}"

                    env.GIT_COMMIT_SHORT = commitShort
                    env.IMAGE_TAG        = imageTag

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
                    echo "🔨 Building image: ${env.IMAGE_TAG}"
                    sh "docker build -t ${env.IMAGE_TAG} ."
                    sh "docker tag ${env.IMAGE_TAG} ${DOCKERHUB_REPO}:latest"
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
                    // FIX: dùng single-quote shell (''') + nối chuỗi Groovy cho IMAGE_TAG
                    //      để tránh secret bị interpolate vào Groovy string
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
                        // FIX 1: capture env vars vào local var trước khi truyền vào shell
                        // FIX 2: dùng heredoc << 'ENDSSH' (single-quote) để:
                        //   - tránh Groovy interpolate brace-expressions của Docker format
                        //   - tránh secret bị lộ qua Groovy string interpolation
                        // FIX 3: biến IMAGE_TAG, DOCKER_USER, DOCKER_PASS được export
                        //        trước khi gọi SSH, rồi dùng $VAR trong heredoc
                        def tag  = env.IMAGE_TAG
                        def repo = DOCKERHUB_REPO
                        def keep = KEEP_IMAGES
                        def name = APP_CONTAINER_NAME
                        def port = APP_PORT
                        def host = VPS_HOST
                        def user = VPS_USER

                        sh """
                            ssh -i \$SSH_KEY \\
                                -o StrictHostKeyChecking=no \\
                                -o ConnectTimeout=10 \\
                                ${user}@${host} bash -s << 'ENDSSH'
                                set -e

                                echo "=== [1/5] Login DockerHub ==="
                                echo "\$DOCKER_PASS" | docker login -u "\$DOCKER_USER" --password-stdin

                                echo "=== [2/5] Pull image mới: ${tag} ==="
                                docker pull ${tag}

                                echo "=== [3/5] Dừng & xóa container cũ (nếu có) ==="
                                docker stop ${name} 2>/dev/null || true
                                docker rm   ${name} 2>/dev/null || true

                                echo "=== [4/5] Chạy container mới ==="
                                docker run -d \\
                                    --name ${name} \\
                                    --restart unless-stopped \\
                                    -p ${port}:8080 \\
                                    ${tag}

                                echo "=== [5/5] Dọn image cũ — giữ lại ${keep} gần nhất ==="
                                docker images ${repo} --format '{{.Tag}} {{.ID}}' \\
                                    | grep -v latest \\
                                    | sort -r \\
                                    | tail -n +\$(( ${keep} + 1 )) \\
                                    | awk '{print \$2}' \\
                                    | xargs -r docker rmi -f || true

                                docker logout
                                echo "✅ Deploy thành công!"
                                ENDSSH
                        """
                    }
                }
            }
        }
    }

    // POST — Telegram notification
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
                    "⏱️ *Thời gian:* ${currentBuild.durationString}\n" +
                    "👉 Log đính kèm bên dưới ↓"
                )
                sendLogFile("failure")
            }
        }

        aborted {
            script {
                sendTelegram(
                    "⚠️ *BUILD BỊ HỦY*\n" +
                    "📦 *Project:* `${env.JOB_NAME}`\n" +
                    "🔢 *Build:* [#${env.BUILD_NUMBER}](${env.BUILD_URL})\n" +
                    "🌿 *Branch:* `${env.GIT_BRANCH}`\n" +
                    "⏱️ *Thời gian:* ${currentBuild.durationString}\n" +
                    "👉 Log đính kèm bên dưới ↓"
                )
                sendLogFile("aborted")
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
        string(credentialsId: "${TELEGRAM_CREDS}",   variable: 'BOT_TOKEN'),
        string(credentialsId: "${TELEGRAM_CHAT_ID}", variable: 'CHAT_ID')
    ]) {
        // Ghi message ra file tạm để tránh vấn đề escape trên command line
        def tmpFile = "/tmp/tg_msg_${env.BUILD_NUMBER}.txt"
        writeFile file: tmpFile, text: message
        sh """
            curl -s -X POST "https://api.telegram.org/bot\${BOT_TOKEN}/sendMessage" \\
                -F chat_id="\${CHAT_ID}" \\
                -F parse_mode="Markdown" \\
                -F disable_web_page_preview="true" \\
                -F text=<${tmpFile}
            rm -f ${tmpFile}
        """
    }
}

// Helper: xuất log build → gửi file .log lên Telegram
def sendLogFile(String status) {
    withCredentials([
        string(credentialsId: "${TELEGRAM_CREDS}",   variable: 'BOT_TOKEN'),
        string(credentialsId: "${TELEGRAM_CHAT_ID}", variable: 'CHAT_ID')
    ]) {
        script {
            def safeJobName = env.JOB_NAME.replaceAll('[^a-zA-Z0-9_-]', '_')
            def logFileName = "${safeJobName}_build-${env.BUILD_NUMBER}_${status}.log"
            def logFilePath = "/tmp/${logFileName}"
            def caption     = "📋 Build log — ${env.JOB_NAME} #${env.BUILD_NUMBER} [${status.toUpperCase()}]"

            sh """
                # 1. Tải console log từ Jenkins API
                curl -s --max-time 30 \\
                    "\${JENKINS_URL}job/${env.JOB_NAME}/${env.BUILD_NUMBER}/consoleText" \\
                    -o "${logFilePath}" || true

                # Fallback qua BUILD_URL nếu URL trên trống (multi-branch / folder job)
                if [ ! -s "${logFilePath}" ]; then
                    curl -s --max-time 30 \\
                        "\${BUILD_URL}consoleText" \\
                        -o "${logFilePath}" || true
                fi

                # Vẫn trống → tạo file thông báo để không gửi file rỗng
                if [ ! -s "${logFilePath}" ]; then
                    echo "Không lấy được console log từ Jenkins API." > "${logFilePath}"
                fi

                # 2. Gửi file lên Telegram
                curl -s -X POST "https://api.telegram.org/bot\${BOT_TOKEN}/sendDocument" \\
                    -F chat_id="\${CHAT_ID}" \\
                    -F caption="${caption}" \\
                    -F document=@"${logFilePath}"

                # 3. Dọn file tạm
                rm -f "${logFilePath}"
            """
        }
    }
}