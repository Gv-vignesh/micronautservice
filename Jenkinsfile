def COLOR_MAP = [
    'SUCCESS': 'good',
    'FAILURE': 'danger'
]

pipeline {
    agent any

    environment {
        AWS_ACCOUNT_ID = credentials('aws-account-id')
        AWS_REGION = 'us-east-1'
        ECR_REPO = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/micronaut-app"
        //KUBE_CONFIG = credentials('eks-kubeconfig')
        DOCKER_IMAGE = "micronaut-app"
        DOCKER_IMAGE_TAG = "v${BUILD_NUMBER}"
        S3_BUCKET = 'your-frontend-bucket'
       // CLOUDFRONT_DISTRIBUTION_ID = 'your-distribution-id'
        NOTIFICATION_EMAIL = 'vignesh.g@ideas2it.com'
        //MINIKUBE_IP = sh(script: 'minikube ip', returnStdout: true).trim()
    }

    stages {
        stage('Initialize Environment') {
            steps {
                script {
                    bat '''
                        @echo off
                        REM Verify Minikube is running
                        minikube status
                        
                        REM Set Docker environment to use local Minikube's Docker daemon
                        FOR /f "tokens=*" %%i IN ('minikube -p minikube docker-env') DO @%%i
                        
                        REM Get Minikube IP
                        FOR /f "tokens=*" %%i IN ('minikube ip') DO SET MINIKUBE_IP=%%i
                        echo Minikube IP: %MINIKUBE_IP%
                        
                        REM Verify kubectl connection
                        kubectl config use-context minikube
                        kubectl cluster-info
                    '''
                    
                    // Store Minikube IP for later use
                    env.MINIKUBE_IP = bat(script: 'minikube ip', returnStdout: true).trim()
                }
            }
        }

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build and Test') {
            parallel {
                stage('Backend') {
                    steps {
                        dir('micronautservice') {
                            bat './gradlew clean build test'
                        }
                    }
                }

                stage('Frontend') {
                    steps {
                        dir('frontend') {
                            bat '''
                                npm install
                                npm run test
                            '''
                        }
                    }
                }
            }
        }

        stage('Build and Push Docker Image') {
            steps {
                script {
                    bat '''
                        @echo off
                        REM Set Docker environment to use Minikube's Docker daemon
                        FOR /f "tokens=*" %%i IN ('minikube -p minikube docker-env') DO @%%i
                        
                        REM Build the image
                        docker build -t %DOCKER_IMAGE%:%DOCKER_IMAGE_TAG% ./micronautservice
                        
                        REM Verify image
                        docker images | findstr %DOCKER_IMAGE%
                    '''
                }
            }
        }

        stage('Deploy to Minikube') {
            steps {
                script {
                    bat '''
                        @echo off
                        REM Set Docker environment
                        FOR /f "tokens=*" %%i IN ('minikube -p minikube docker-env') DO @%%i
                        
                        REM Update deployment image
                        powershell -Command "(gc k8s/deployment.yaml) -replace 'image: .*', 'image: %DOCKER_IMAGE%:%DOCKER_IMAGE_TAG%' | Set-Content k8s/deployment.yaml"
                        
                        REM Deploy to Minikube
                        kubectl apply -f k8s/deployment.yaml
                        kubectl apply -f k8s/service.yaml
                    '''

                    // Add detailed deployment verification
                    bat '''
                        @echo off
                        echo "=== Deployment Status ==="
                        kubectl rollout status deployment/micronaut-app --timeout=60s
                        
                        echo "=== Pod Status ==="
                        kubectl get pods -l app=micronaut-app
                        
                        echo "=== Pod Logs ==="
                        FOR /f "tokens=*" %%p in ('kubectl get pods -l app=micronaut-app -o name') do (
                            echo "Logs for %%p:"
                            kubectl logs %%p
                            echo "=== Pod Description ==="
                            kubectl describe pod %%p
                        )
                    '''
                }
            }
        }

        stage('Deploy Frontend') {
            steps {
                dir('frontend') {
                    bat """
                        @echo off
                        set BACKEND_URL=http://${env.MINIKUBE_IP}:80
                        echo REACT_APP_API_URL=%BACKEND_URL% > .env
                        npm run build
                        
                        call npm install -g serve
                        start /B serve -s build -l 3000
                    """
                }
            }
        }

        stage('Post Deployment Validation') {
            parallel {
                stage('Backend Health Check') {
                    steps {
                        script {
                            bat """
                                @echo off
                                timeout /t 30 /nobreak
                                curl -f http://${env.MINIKUBE_IP}:80/health || exit 1
                            """
                        }
                    }
                }

                stage('Frontend Availability Check') {
                    steps {
                        bat 'curl -f http://localhost:3000 || exit 1'
                    }
                }
            }
        }

        stage('Verify Deployment') {
            steps {
                script {
                    bat '''
                        @echo off
                        echo "=== All Resources ==="
                        kubectl get all
                        
                        echo "=== Events ==="
                        kubectl get events --sort-by=.metadata.creationTimestamp
                        
                        echo "=== Pod Details ==="
                        kubectl get pods -l app=micronaut-app -o wide
                        
                        echo "=== Service Details ==="
                        kubectl get svc micronaut-app -o wide
                    '''
                }
            }
        }
    }

    post {
        always {
            script {
                // Send email notification
                emailext (
                    subject: "Pipeline Status: ${currentBuild.result}",
                    body: """
                        Job: ${env.JOB_NAME}
                        Build Number: ${env.BUILD_NUMBER}
                        Status: ${currentBuild.result}
                        
                        Backend URL: http://${env.MINIKUBE_IP}:80
                        Frontend URL: http://localhost:3000
                        
                        Check console output at: ${env.BUILD_URL}
                    """,
                    to: "${NOTIFICATION_EMAIL}",
                    attachLog: true
                )
            }
        }
        
        cleanup {
            cleanWs()
        }
    }
} 