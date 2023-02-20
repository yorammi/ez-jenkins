import yorammi.ez.ezEasy

def call(Map config) {
    def ezPipeline = null
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
        options {
            timestamps()
            buildDiscarder(logRotator(numToKeepStr: config.ezNumToKeepStr))
            ansiColor('xterm')
            skipDefaultCheckout()
            disableConcurrentBuilds()
            quietPeriod(config.ezQuietPeriod)
        }
        stages {
            stage ("[ez]") {
                steps {
                    script {
                        try {
                            checkout scm
                        }
                        catch (error) {
                        }
                        ezPipeline = new yorammi.ez.ezEasy(this)
                        ezPipeline.config=config
                        ezPipeline.activate()
                    }
                }
            }
        }
        post {
            always {
                script {
                    if(ezPipeline.yaml.configuration.notifications.slackNotifications) {
                        ezNotifications.notifications.sendSlackNotification()
                    }
                    if(ezPipeline.yaml.configuration.emailNotifications) {
                        ezNotifications.sendEmailNotification(to:"yorammi@yorammi.com", channel:(ezPipeline.yaml.configuration.notifications.slack.channel?ezPipeline.yaml.configuration.notifications.slack.channel:"general"))
                    }
                    sleep (config.ezSleep)
                }
            }
        }
    }
}