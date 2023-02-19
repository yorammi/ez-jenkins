package yorammi.ez;

import yorammi.ez.ezBaseJob
import java.text.SimpleDateFormat 
import java.util.Date

class ezEasy extends ezBaseJob {

    // internal class attributes
    def buildNumber
    def deleteWorkspace = true
    def componentBranch = ''
    def yaml
    Map config

    // constructor
    ezEasy(script) {
        super(script)
    }

    @Override
    void activateImpl() {
        buildNumber = script.env.BUILD_NUMBER
        activateStage('setup', this.&ezSetup)
        activateStage('[flow]', this.&activateFlow)
    }

    void ezSetup() {
        if(config == null) 
        {
            config = [:]
        }
        if(config.ezYamlFilePath == null)
        {
            config.ezYamlFilePath = "ez.yaml"
        }
        yaml = script.readYaml file: config.ezYamlFilePath
        if(yaml.environment != null) {
            script.ezLog.info "Set flow environment variables"
        }
        else {
            script.ezLog.info "No flow environment variables"
        }
    }

    void activateFlow() {
        def phases = yaml.phases
        phases.each { phase ->
            activatePhase(phase)
        }
    }

    void activatePhase(def phase) {
        script.ezLog.anchor "{Phase}: ${phase.name}"
        def parallelBlocks = [:]
        def stages = phase.stages
        stages.each { stage ->
            yaml.stages.each { loopStage ->
                if(loopStage.name == stage) {
                    activateStage(parallelBlocks, phase.name, loopStage)
                }
            }
        }
        script.parallel parallelBlocks
    }

    void activateStage(def parallelBlocks, String phase, def stage) {
        script.ezLog.anchor "Stage: ${stage.name}"
        File file = File.createTempFile("temp",".groovy")
        file.deleteOnExit()
        def currentSteps = ""
        parallelBlocks["{Phase:${phase}}:{Stage:${stage.name}}"] = {
            script.stage("{Phase:${phase}}:{Stage:${stage.name}}") {
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
}


