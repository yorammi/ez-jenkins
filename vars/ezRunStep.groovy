import yorammi.ez.ezEasy

def call(String step) {
    script {
        eval "${step}"
    }
}