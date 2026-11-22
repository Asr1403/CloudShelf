
pipeline {
    agent any

    tools {
        jdk 'JDK21'
        maven 'Maven3'
    }

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
                echo 'CloudShelf source code downloaded.'
            }
        }

        stage('Verify Environment') {
            steps {
                bat 'java -version'
                bat 'mvn -version'
            }
        }

        stage('Build and Test') {
            steps {
                bat 'mvn -B clean verify'
            }
        }

        stage('Verify JAR') {
            steps {
                bat 'dir target\\*.jar'
            }
        }
    }

    post {
        success {
            echo 'CloudShelf CI build and tests passed!'
        }

        failure {
            echo 'CloudShelf CI failed. Check console output.'
        }
    }
}
