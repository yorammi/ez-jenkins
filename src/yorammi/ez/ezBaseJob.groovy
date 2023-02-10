package yorammi.ez;

abstract class ezBaseJob implements Serializable {

    def script
    def currentStage

    ezBaseJob(script) {
        this.script = script
    }
    
    void activate() {
        script.timestamps() {
            try {
                script.echo "Activating with params: ${this.properties}"
                activateImpl()
            } finally {
                try {
                } catch (all) {
                }

            }
        }
    }

    void activateImpl() {
    }

    void activateStage(String name, Closure stage) {
        script.ezLog.anchor("${name} stage start")
        currentStage = name
        script.stage(name, stage)
        script.ezLog.anchor("${name} stage end")
    }
}
