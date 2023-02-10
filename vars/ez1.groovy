#!/usr/bin/env groovy

def call(Map config) {
  if(config == null) 
  {
    config = [:]
  }
  if(config.ezYamlFilePath == null)
  {
    config.ezYamlFilePath = "ez.yaml"
  }
  def yaml = readYaml file: config.ezYamlFilePath
  def stages = yaml.stages
  stages.each { stage ->
    // stage("${stage.name}") {
    //   stage.steps.each { step ->
    //     eval "${step}"
    //   }
    // }
  }
}
