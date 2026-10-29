
pipeline {
    agent any

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
                bat 'docker build -t priyajit01/cicd-pipeline:%BUILD_NUMBER% .'
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
                    bat 'docker push priyajit01/cicd-pipeline:%BUILD_NUMBER%'
                    bat 'docker tag priyajit01/cicd-pipeline:%BUILD_NUMBER% priyajit01/cicd-pipeline:latest'
                    bat 'docker push priyajit01/cicd-pipeline:latest'
                }
            }
        }
    }

    post {
        success {
            echo 'Build, tests, Docker image creation, and publishing completed successfully.'
        }
        failure {
            echo 'Pipeline failed. Check the stage logs.'
        }
    }
}
