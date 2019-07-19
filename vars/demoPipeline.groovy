def call(){
	
pipeline{

	agent none
	
	stages{
		stage('Build'){
		
			agent {
				kubernetes {
				  cloud 'kubernetes-qa'
				  label 'dynamicslavek8sdotnetbuild'
				  defaultContainer 'dynamicslavedotnetbuild'
				  yaml """
apiVersion: v1
kind: Pod
metadata:
  labels:
  podtype: jenkinsdynamimcslavedotnetbuild
spec:
  containers:
  - name: dynamicslavedotnetbuild
    image: mcr.microsoft.com/dotnet/core/sdk:2.1
    command:
    - cat
    tty: true
"""
				}
			}
			
			steps{
				
				echo "Building application"
				sh 'dotnet --version'
				
				Map config = readJSON(file: "./config.json")
				
				echo config.key1
				sh '''
				set +x -v
				
				echo ${config.build.projectFile}
				
				#Restoring Packages
				dotnet restore ./${config.build.projectFile} --source https://api.nuget.org/v3/index.json --source http://stage-packagegallery.tavisca.com/api/odata -v:q
				
				#Building Project
				dotnet msbuild ./${config.build.projectFile} -p:Configuration=release -v:q
		
				'''
				sh 'sleep 1'
			}
		}

		stage('Docker build image'){
			agent {
				kubernetes {
				  cloud 'kubernetes-qa'
				  label 'dynamicslavek8sdockerbuild'
				  defaultContainer 'dynamicslavedockerbuild'
				  yaml """
apiVersion: v1
kind: Pod
metadata:
  labels:
  podtype: jenkinsdynamimcslavedockerbuild
spec:
  containers:
  - name: dynamicslavedockerbuild
    image: docker
    command:
    - cat
    tty: true
"""
				}
			}
			steps{
				sh 'docker --version'
				echo config.key2
				sh 'sleep 10'
			}
		}
	}
}
}