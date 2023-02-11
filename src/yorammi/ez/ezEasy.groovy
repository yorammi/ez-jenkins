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
                script.ezLog.anchor "Stage: ${stage.name}"
                File file = File.createTempFile("temp",".groovy")
                file.deleteOnExit()
                def currentSteps = ""
                script.stage("${stage.name}") {
                    stage.steps.each { step ->
                        currentSteps+="\n"+step
                    }
                }
                try {
                    script.writeFile file: file.absolutePath, text: "#!/usr/bin/env groovy\n${currentSteps}"
                    script.load(file.absolutePath)
                } catch (error) {
                    script.ezLog.debug "[ERROR] "+error.message
                    script.currentBuild.result = "FAILURE"
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


