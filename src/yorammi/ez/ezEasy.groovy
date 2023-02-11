package yorammi.ez;

import yorammi.ez.ezBaseJob
import java.text.SimpleDateFormat 
import java.util.Date

class ezEasy extends ezBaseJob {

    // internal class attributes
    def buildNumber
    def deleteWorkspace = true
    def componentBranch = ''
    Map config

    // constructor
    ezEasy(script) {
        super(script)
    }

    @Override
    void activateImpl() {
        try {
            buildNumber = script.env.BUILD_NUMBER
            activateStage('Setup', this.&setup)
            def yaml = script.readYaml file: config.ezYamlFilePath
            def stages = yaml.stages
            stages.each { stage ->
                script.ezLog.anchor "${stage.name}"
                script.stage("${stage.name}") {
                    stage.steps.each { step ->
                        script.ezLog.info "${step}"
                        switch (step.type) {
                        case "sh":
                            script.sh step.args
                            break
                        case "echo":
                            script.echo step.args
                            break
                        case "step":
                            eval ("${step.args}")
                            break
                        default:
                            echo "Invalid step type"
                            error "Invalid step type: ${step.type}"
                        }
                    }
                }
            }
        } catch (error) {
            script.ezLog.debug "[ERROR] "+error.message
            script.currentBuild.result = "FAILURE"
        }
        finally {
        }
    }

    void setup() {
        script.ezLog.info "setup start"

        if(config == null) 
        {
            config = [:]
        }
        if(config.ezYamlFilePath == null)
        {
            config.ezYamlFilePath = "ez.yaml"
        }
    }
}


