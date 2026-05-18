pipeline {
    agent any

    environment {
        DOCKERHUB_REPO     = 'davidnguyendev/be-clinic'   // VD: john/my-spring-app
        APP_CONTAINER_NAME = 'be-clinic'                            // Tên container chạy trên VPS
        APP_PORT           = '8080'                                      // Port expose ra ngoài VPS
        KEEP_IMAGES        = '3'                                         // Số lượng image giữ lại trên VPS

        // Credentials ID đã khai báo trong Jenkins → Manage Credentials
        DOCKERHUB_CREDS    = 'dockerhub-credentials'   // Username + Password (DockerHub)
        SSH_CREDS          = 'vps-ssh-credentials'      // SSH Username with private key
        TELEGRAM_CREDS     = 'telegram-bot-token'       // Secret text — token của Bot
        TELEGRAM_CHAT_ID   = 'telegram-chat-id'         // Secret text — chat/group ID

        VPS_HOST           = 'vps.ip.address'      // IP hoặc domain VPS chạy app
        VPS_USER           = 'root'                   // SSH user trên VPS

        // Computed
        GIT_COMMIT_SHORT   = ''
        IMAGE_TAG          = ''
    }

    // Chỉ chạy pipeline trên branch main hoặc master
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
                    GIT_COMMIT_SHORT = sh(
                        script: "git rev-parse --short HEAD",
                        returnStdout: true
                    ).trim()
                    IMAGE_TAG = "${DOCKERHUB_REPO}:${GIT_COMMIT_SHORT}"
                    env.GIT_COMMIT_SHORT = GIT_COMMIT_SHORT
                    env.IMAGE_TAG        = IMAGE_TAG
                    echo "📦 Image sẽ được tag: ${IMAGE_TAG}"
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
                    echo "🔨 Building image: ${IMAGE_TAG}"
                    sh "docker build -t ${IMAGE_TAG} ."
                    // Đồng thời tag thêm latest để tiện khi cần
                    sh "docker tag ${IMAGE_TAG} ${DOCKERHUB_REPO}:latest"
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
                        docker push ''' + IMAGE_TAG + '''
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
                        def deployScript = """
                            set -e

                            echo "=== [1/5] Login DockerHub ==="
                            echo "${env.DOCKER_PASS}" | docker login -u "${env.DOCKER_USER}" --password-stdin

                            echo "=== [2/5] Pull image mới: ${env.IMAGE_TAG} ==="
                            docker pull ${env.IMAGE_TAG}

                            echo "=== [3/5] Dừng & xóa container cũ (nếu có) ==="
                            docker stop ${APP_CONTAINER_NAME} 2>/dev/null || true
                            docker rm   ${APP_CONTAINER_NAME} 2>/dev/null || true

                            echo "=== [4/5] Chạy container mới ==="
                            docker run -d \\
                                --name ${APP_CONTAINER_NAME} \\
                                --restart unless-stopped \\
                                -p ${APP_PORT}:8080 \\
                                ${env.IMAGE_TAG}

                            echo "=== [5/5] Dọn image cũ — giữ lại ${KEEP_IMAGES} gần nhất ==="
                            docker images ${DOCKERHUB_REPO} --format '{{.Tag}} {{.ID}}' \\
                                | grep -v latest \\
                                | sort -r \\
                                | tail -n +\$((${KEEP_IMAGES} + 1)) \\
                                | awk '{print \$2}' \\
                                | xargs -r docker rmi -f || true

                            docker logout
                            echo "✅ Deploy thành công!"
                        """

                        sh """
                            ssh -i \$SSH_KEY \
                                -o StrictHostKeyChecking=no \
                                -o ConnectTimeout=10 \
                                ${VPS_USER}@${VPS_HOST} '${deployScript}'
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
                sendTelegram("✅ *BUILD THÀNH CÔNG*\n" +
                    "📦 *Project:* `${env.JOB_NAME}`\n" +
                    "🔖 *Image:* `${env.IMAGE_TAG}`\n" +
                    "🔢 *Build:* [#${env.BUILD_NUMBER}](${env.BUILD_URL})\n" +
                    "🌿 *Branch:* `${env.GIT_BRANCH}`\n" +
                    "⏱️ *Thời gian:* ${currentBuild.durationString}")
            }
        }
        failure {
            script {
                sendTelegram("❌ *BUILD THẤT BẠI*\n" +
                    "📦 *Project:* `${env.JOB_NAME}`\n" +
                    "🔢 *Build:* [#${env.BUILD_NUMBER}](${env.BUILD_URL})\n" +
                    "🌿 *Branch:* `${env.GIT_BRANCH}`\n" +
                    "⏱️ *Thời gian:* ${currentBuild.durationString}\n" +
                    "👉 Xem log để biết chi tiết lỗi.")
            }
        }
        aborted {
            script {
                sendTelegram("⚠️ *BUILD BỊ HỦY*\n" +
                    "📦 *Project:* `${env.JOB_NAME}`\n" +
                    "🔢 *Build:* [#${env.BUILD_NUMBER}](${env.BUILD_URL})")
            }
        }
        // Dọn image đã build trên Jenkins server (không cần giữ lại)
        always {
            script {
                sh "docker rmi ${env.IMAGE_TAG} ${DOCKERHUB_REPO}:latest 2>/dev/null || true"
            }
        }
    }
}

// Helper: gửi message Telegram
def sendTelegram(String message) {
    withCredentials([
        string(credentialsId: "${TELEGRAM_CREDS}",   variable: 'BOT_TOKEN'),
        string(credentialsId: "${TELEGRAM_CHAT_ID}", variable: 'CHAT_ID')
    ]) {
        sh """
            curl -s -X POST "https://api.telegram.org/bot\${BOT_TOKEN}/sendMessage" \\
                -d chat_id="\${CHAT_ID}" \\
                -d parse_mode="Markdown" \\
                -d disable_web_page_preview="true" \\
                -d text="${message.replace('"', '\\"')}"
        """
    }
}