#!/usr/bin/env groovy

def call (Map params) {
  def yamlFile = "ez.yaml"
  def yaml = readYaml file: yamlFile
  def stages = yaml.stages
  node {
    stages.each { stage ->
      stage("${stage.name}") {
        stage.steps.each { step ->
          eval "${step}"
        }
      }
    }
  }
}
