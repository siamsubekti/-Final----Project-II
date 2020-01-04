def sonarProjectKey = "semicolonsquad"
def repoBackend = 'https://git.enigmacamp.com/enigma-camp/class-b/final-project/kelompok-03/backend.git'
def credentialsId =  "alfred-pennyworth"
pipeline {
    agent any
    stages {
        stage('checkout'){
            steps{
              checkout([$class: 'GitSCM', branches: [[name: '*/dev/tri']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: credentialsId, url: repoBackend ]]])
            }
        }
        stage('build') {
            steps {
                sh "${env.M2_HOME}/bin/mvn clean package"
            }
        }
        stage('sonar') {
            steps {
                withCredentials([string(credentialsId: 'sonar-key', variable: 'TOKEN'),string(credentialsId: 'sonar-access', variable: 'ACCESS')]) {
                   sh "${env.M2_HOME}/bin/mvn sonar:sonar -Dsonar.projectKey=${sonarProjectKey} -Dsonar.host.url=$ACCESS -Dsonar.login=$TOKEN"
                }
            }
        }
        stage('deploy'){
            steps{
                sh "${env.DOCKER_HOME}/docker build -t ged/ged-be:1.0 ."
            }
        }
         stage('docker deploy'){
            steps{
                sh "${env.DOCKER_HOME}/docker-compose down && ${env.DOCKER_HOME}/docker-compose up -d"
            }
        }
    }
}