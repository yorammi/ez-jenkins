#!/usr/bin/env groovy

def wgetAndUnzip(String url) {

    sh "wget -O wgetAndUnzip.zip '${url.trim()}'"
    sh "unzip wgetAndUnzip.zip"
    sh "rm -f wgetAndUnzip.zip"
}

def isBuildStartedByUser() {
    try{
        def isStartedByUser = currentBuild.rawBuild.getCause(hudson.model.Cause$UserIdCause) != null
        return isStartedByUser
    }
    catch (Exception error)
    {
        return false
    }
}

def deleteWorkspace() {
    step([$class: 'WsCleanup'])
}

def saveConsoleLogToFile(Map config=null)
{
    try {
        if (config == null) {
            config = [:]
        }
        if (config.path == null) {
            config.path = 'Console-log.info.txt'
        }

        archiveArtifacts artifacts: '**/*.info.txt'
    }
    catch (error) {

    }
}

def validateServerIsUp(String ipAddress) {
    def pingExitCode = sh(script: 'ping -c 1 '+ipAddress,returnStatus:true)
    if(pingExitCode != 0) {
        echo "[ERROR] fail to ping ${ipAddress}"
        sh "exit -1"
    }
}

def parseYamlFile(String fileName) {
    try {
        if(fileExists(fileName)) {
            def yaml = readYaml file: fileName
        }
        return true
    }
    catch (error) {
        ezLog.error '[ERROR] parsing \''+fileName+'\':\n'+error.message
    }
}

