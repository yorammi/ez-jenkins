import yorammi.ez.ezEasy

def call(String step) {
    script {
        def shell = new GroovyShell()
        shell.evaluate "${step}"
    }
}