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
                        script.eval "${step}"
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
        // if(deleteWorkspace) {
        //     script.checkout script.scm
        // }

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


