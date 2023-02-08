#!/usr/bin/env groovy

def call (String yaml = "ez.yaml") {
  def yamlFile = yaml
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
