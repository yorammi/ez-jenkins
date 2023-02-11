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
        buildNumber = script.env.BUILD_NUMBER
        activateStage('Setup', this.&setup)
        def yaml = script.readYaml file: config.ezYamlFilePath
        def phases = yaml.phases
        phases.each { phase ->
            script.ezLog.info "${phase}"
            script.ezLog.anchor "{Phase}: ${phase.name}"
            activatePhase(phase)
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

    void activatePhase(def phase) {
        def stages = phase.stages
        stages.each { stage ->
            activateStage(stage)
        }
    }

    void activateStage(def stage) {
            script.ezLog.anchor "Stage: ${stage.name}"
            File file = File.createTempFile("temp",".groovy")
            file.deleteOnExit()
            def currentSteps = ""
            // script.stage("${stage.name}") {
            script.stage("1") {
                stage.steps.each { step ->
                    currentSteps+="\n"+step
                }
                try {
                    script.writeFile file: file.absolutePath, text: "#!/usr/bin/env groovy\n${currentSteps}"
                    script.load(file.absolutePath)
                } catch (error) {
                    script.ezLog.debug "[ERROR] "+error.message
                    script.currentBuild.result = "FAILURE"
                    throw error
                }
                finally {
                }
            }
    }
}


