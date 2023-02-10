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

        script.sh "env"
    }
}


