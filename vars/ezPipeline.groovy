import yorammi.ez.ezEasy

def call(Map config) {
    if (config == null) {
        config = [:]
    }
    if (config.sleep == null)
    {
        config.sleep = 0
    }
    if (config.quietPeriod == null)
    {
        config.quietPeriod = 5
    }
    if (config.numToKeepStr == null)
    {
        config.numToKeepStr = "5"
    }

    pipeline {
        agent any
        options {
            timestamps()
            buildDiscarder(logRotator(numToKeepStr: config.numToKeepStr))
            ansiColor('xterm')
            skipDefaultCheckout()
            disableConcurrentBuilds()
            quietPeriod(config.quietPeriod)
        }
        // parameters {
        //     string (name: 'PRID', defaultValue: '', description:'Pull request ID')
        //     string (name: 'PRDESTINATION', defaultValue: '', description:'Pull request destination branch')
        // }
        stages {
            stage ("[Pipeline setup]") {
                steps {
                    script {
                        // checkout scm
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
                    sleep (config.sleep)
                }
            }
        }
    }
}