/* This script is executed by Jenkins on the Jenkins server's
   local clone of this repository in order to build the JAR for
   this project before it is uploaded to Synology.

   The only change needed here is ensuring this file
   correctly specifies the branch it is currently located in. If
   you are merging this branch into another, make sure the
   Jenkinsfile in that branch denotes the correct branch.
*/

import java.text.SimpleDateFormat

def gitCredentials = "23f3d22a-30b4-479d-8616-0761b340629e"
def remoteRepo = "https://bitbucket.org/BottleRocket/automation_library.git"

def frameworkName = "automation_library"
def cicdHome = "/Users/automation/.Jenkins_CICD/"

def gradle_JVM = "/Library/Java/JavaVirtualMachines/jdk-15.0.1.jdk/Contents/Home"

def branchName = env.BRANCH_NAME

pipeline {
    agent {
        label 'master'
    }

    stages {

        stage('Clone') {
            steps {
                dir(cicdHome){
                    sh 'rm -rf '+frameworkName
                    // Clones the repository from the current branch name
                    sh 'mkdir -p '+branchName+'/'+frameworkName
                    echo 'Cloning files from (branch: "' + branchName + '" )'
                }
                dir(cicdHome+branchName+'/'+frameworkName) {
                    git branch: branchName,
                    credentialsId: gitCredentials,
                    url: remoteRepo
                }
            }
        }

        stage('Build') {
            steps {
                dir(cicdHome+branchName+'/'+frameworkName){
                    sh './gradlew build -Dorg.gradle.java.home=' + gradle_JVM
                }
            }
        }

        stage('Push to Synology') {
            steps {
                sh './gradlew publishAutomationLibraryPublicationToSynologyRepository -Dorg.gradle.java.home=' + gradle_JVM
            }
        }

        stage('Cleanup'){
            steps {
                dir(cicdHome){
                    sh 'rm -rf '+branchName
                }
            }
        }
    }
}

