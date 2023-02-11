import yorammi.ez.ezEasy

def call(Map config) {
    if (config == null) {
        config = [:]
    }
    if (config.ezSleep == null)
    {
        config.ezSleep = 0
    }
    if (config.ezQuietPeriod == null)
    {
        config.ezQuietPeriod = 5
    }
    if (config.ezNumToKeepStr == null)
    {
        config.ezNumToKeepStr = "5"
    }
    if (config.ezMainLabel == null)
    {
        config.ezNumToKeepStr = "5"
    }

    pipeline {
        agent any
        // if (config.ezMainLabel != null) {
        //     agent { label '${config.ezMainLabel}' }
        // }
        // else {
        // }
        options {
            timestamps()
            buildDiscarder(logRotator(numToKeepStr: config.ezNumToKeepStr))
            ansiColor('xterm')
            skipDefaultCheckout()
            disableConcurrentBuilds()
            quietPeriod(config.ezQuietPeriod)
        }
        stages {
            stage ("[ez setup]") {
                steps {
                    script {
                        try {
                            checkout scm
                        }
                        catch (error) {

                        }
                    }
                }
            }
            stage ("[ez Flow]") {
                steps {
                    script {
                        def ezPipeline = new yorammi.ez.ezEasy(this)
                        ezPipeline.config=config
                        ezPipeline.activate()
                    }
                }
            }
        }
        post {
            always {
                script {
                    sleep (config.ezSleep)
                }
            }
        }
    }
}