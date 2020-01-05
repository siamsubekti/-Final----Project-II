def repoFeLanding = 'https://git.enigmacamp.com/enigma-camp/class-b/final-project/kelompok-03/landing-page.git'
def repoFeLogin = 'https://git.enigmacamp.com/enigma-camp/class-b/final-project/kelompok-03/frontend.git'
def repoPipeline = 'https://git.enigmacamp.com/enigma-camp/class-b/final-project/kelompok-03/pipeline-semicolon-squad.git'
def credintilsId  = 'alfred-pennyworth'
pipeline {
    agent any
    tools {
        nodejs 'node12'
    }
    stages {
        stage('checkout'){
            steps{
                dir('fe-landing') {
                    checkout([$class: 'GitSCM', branches: [[name: '*/development']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: credintilsId, url: repoFeLanding]]])
                }
                dir('fe-login') {
                    checkout([$class: 'GitSCM', branches: [[name: '*/development']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: credintilsId, url: repoFeLogin]]])
                }

            }
        }

        stage('build') {
            steps {
                dir ('fe-landing') {
                    sh "npm install"
                    sh "npm run build"
                }

                dir ('fe-login') {
                    sh "npm install"
                    sh "npm run build"
                }
            }
        }
        stage('merge') {
            steps{
                dir ('dist') {
                    sh "cp -r ../fe-landing/build/* ."
                    sh "cp -r ../fe-login/build* ./login"
                    sh "ls -l"
                }
            }
        }

        stage('deploy'){
            steps {
                checkout([$class: 'GitSCM', branches: [[name: '*/master']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: credintilsId, url: repoPipeline]]])
                sh "cp ./dockerfile ./dist"
                sh "cp ./nginx.conf ./dist/ged.conf"

                dir ('dist') {
                    script {
                        sh "${env.DOCKER_HOME}/docker build -t ged/ged-fe:1.0 ."
                    }
                }
            }
        }
        stage('docker deploy'){
            steps{
                dir ('dist'){
                    sh "${env.DOCKER_HOME}/docker-compose down && ${env.DOCKER_HOME}/docker-compose up -d"
                }

            }
        }
    }
}
