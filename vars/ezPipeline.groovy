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
                    def configuration = ezPipeline.yaml.configuration
                    if(configuration && configuration.notifications) {
                        if(configuration.notifications.emailNotifications) {
                            ezNotifications.sendEmailNotification(to:"yorammi@yorammi.com")
                        }
                        if(configuration.notifications.slackNotifications) {
                            if(configuration.notifications.slack) {
                                if(configuration.notifications.slack.channel) {
                                    ezLog.info("sending Slack message to channel ${configuration.notifications.slack.channel}")
                                    ezNotifications.sendSlackNotification(channel:configuration.notifications.slack.channel)
                                }
                                else{
                                    ezLog.info("sending Slack message to default channel")
                                    ezNotifications.sendSlackNotification()
                                }
                            }
                            else{
                                ezLog.info("sending slack message to default channel")
                                ezNotifications.sendSlackNotification()
                            }
                        }
                        else {
                                ezLog.info("not sending slack message")
                        }
                    }
                    sleep (config.ezSleep)
                }
            }
        }
    }
}
