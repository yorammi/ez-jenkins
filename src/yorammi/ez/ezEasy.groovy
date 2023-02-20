package yorammi.ez;

import yorammi.ez.ezBaseJob
import java.text.SimpleDateFormat 
import java.util.Date
import hudson.EnvVars;
import hudson.slaves.EnvironmentVariablesNodeProperty;
import hudson.slaves.NodeProperty;
import hudson.slaves.NodePropertyDescriptor;
import hudson.util.DescribableList;
import jenkins.model.Jenkins;

class ezEasy extends ezBaseJob {

    // internal class attributes
    def buildNumber
    def deleteWorkspace = true
    def componentBranch = ''
    def yaml
    Map config
    def branch = ""
    def branchIsMandatory = false

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

        script.ezEnvSetup.initEnv()

        if(config.ezYamlFilePath == null)
        {
            config.ezYamlFilePath = "ez.yaml"
        }
        yaml = script.readYaml file: config.ezYamlFilePath
        if(yaml.environment != null) {
            script.ezLog.info "Set flow environment variables"
            def yamlEnvVars = yaml.environment
            Jenkins instance = Jenkins.getInstance();
        
            DescribableList<NodeProperty<?>, NodePropertyDescriptor> globalNodeProperties = instance.getGlobalNodeProperties();
            List<EnvironmentVariablesNodeProperty> envVarsNodePropertyList = globalNodeProperties.getAll(EnvironmentVariablesNodeProperty.class);
        
            EnvironmentVariablesNodeProperty newEnvVarsNodeProperty = null;
            EnvVars envVars = null;
        
            if ( envVarsNodePropertyList == null || envVarsNodePropertyList.size() == 0 ) {
                newEnvVarsNodeProperty = new hudson.slaves.EnvironmentVariablesNodeProperty();
                globalNodeProperties.add(newEnvVarsNodeProperty);
                envVars = newEnvVarsNodeProperty.getEnvVars();
            } else {
                envVars = envVarsNodePropertyList.get(0).getEnvVars();
            }
            yamlEnvVars.each { key, value ->
                envVars.put(key, value)
            }
           instance.save()
        }
        else {
            script.ezLog.info "No flow environment variables"
        }
        if(script.env.BRANCH_NAME!=null) {
            def branch = script.env.BRANCH_NAME
            script.currentBuild.displayName += " {branch:${branch}}"
            try {
                if("${script.env.BRANCH_IS_PRIMARY}"=="true") {
                    branchIsMandatory=true
                }
            }
            catch (error) {}
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


