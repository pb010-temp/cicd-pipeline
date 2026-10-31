pipeline {
    agent any

    environment {
        DOCKER_IMAGE = 'priyajit01/cicd-pipeline'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build and Test') {
            steps {
                bat 'mvn clean package'
            }
        }

        stage('Build Docker Image') {
            steps {
                bat 'docker build -t %DOCKER_IMAGE%:%BUILD_NUMBER% .'
            }
        }

        stage('Publish to Docker Hub') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-credentials',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_TOKEN'
                )]) {
                    bat 'docker login -u "%DOCKER_USER%" -p "%DOCKER_TOKEN%"'
                    bat 'docker push %DOCKER_IMAGE%:%BUILD_NUMBER%'
                    bat 'docker tag %DOCKER_IMAGE%:%BUILD_NUMBER% %DOCKER_IMAGE%:latest'
                    bat 'docker push %DOCKER_IMAGE%:latest'
                }
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                withCredentials([sshUserPrivateKey(
                    credentialsId: 'ec2-ssh-key',
                    keyFileVariable: 'SSH_KEY',
                    usernameVariable: 'SSH_USER'
                )]) {
                    bat '''
                        icacls "%SSH_KEY%" /inheritance:r
                        icacls "%SSH_KEY%" /grant:r "*S-1-5-18:R"

                        C:\\Windows\\System32\\OpenSSH\\ssh.exe -i "%SSH_KEY%" -o StrictHostKeyChecking=yes %SSH_USER%@%EC2_HOST% "sudo k3s kubectl set image deployment/expense-tracker expense-tracker=%DOCKER_IMAGE%:%BUILD_NUMBER% && sudo k3s kubectl rollout status deployment/expense-tracker --timeout=180s"
                    '''
                }
            }
        }
    }

    post {
        success {
            echo 'Build, tests, Docker publishing, and Kubernetes deployment completed successfully.'
        }
        failure {
            echo 'Pipeline failed. Check the stage logs.'
        }
    }
}