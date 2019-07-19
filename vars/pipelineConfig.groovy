def call() {
  Map pipelineConfig = readJSON(file: "${WORKSPACE}/config.json")
  return pipelineConfig
}