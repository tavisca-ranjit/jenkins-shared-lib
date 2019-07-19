def call() {
  Map pipelineConfig = readJSON(file: "./config.json")
  return pipelineConfig
}