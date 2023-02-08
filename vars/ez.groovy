#!/usr/bin/env groovy

def call (String ezYamlFilePath = "ez.yaml") {
  def yamlFile = ezYamlFilePath
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
