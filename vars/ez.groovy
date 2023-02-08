#!/usr/bin/env groovy

def call (String ezYamlFilePath = "ez.yaml") {
  def yaml = readYaml file: ezYamlFilePath
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
