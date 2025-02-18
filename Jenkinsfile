def COLOR_MAP = [
    'SUCCESS': 'good',
    'FAILURE': 'danger'
]

pipeline {
    agent any

    environment {
        AWS_REGION = 'us-east-1'
        ECR_REPO = '${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/micronaut-app'
        KUBE_CONFIG = credentials('eks-kubeconfig')
        DOCKER_IMAGE = "micronaut-app"
        DOCKER_IMAGE_TAG = "v${BUILD_NUMBER}"
        S3_BUCKET = 'your-frontend-bucket'
        CLOUDFRONT_DISTRIBUTION_ID = 'your-distribution-id'
        NOTIFICATION_EMAIL = 'your-email@example.com'
        MINIKUBE_IP = sh(script: 'minikube ip', returnStdout: true).trim()
    }

    stages {
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
                            sh './gradlew clean build test'
                        }
                    }
                }

                stage('Frontend') {
                    steps {
                        dir('frontend') {
                            sh '''
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
                    // Build image
                    sh """
                        eval \$(minikube docker-env)
                        docker build -t ${DOCKER_IMAGE}:${DOCKER_IMAGE_TAG} ./micronautservice
                    """
                }
            }
        }

        stage('Deploy to Minikube') {
            steps {
                script {
                    // Update deployment manifest with new image tag
                    sh """
                        sed -i 's|image: .*|image: ${DOCKER_IMAGE}:${DOCKER_IMAGE_TAG}|' k8s/deployment.yaml
                        
                        # Apply Kubernetes manifests
                        kubectl apply -f k8s/deployment.yaml
                        kubectl apply -f k8s/service.yaml
                        
                        # Wait for deployment to complete
                        kubectl rollout status deployment/micronaut-app -n default
                    """
                }
            }
        }

        stage('Deploy Frontend') {
            steps {
                dir('frontend') {
                    // Update API URL in frontend configuration to use Minikube IP
                    sh """
                        BACKEND_URL="http://${MINIKUBE_IP}:80"
                        echo "REACT_APP_API_URL=\${BACKEND_URL}" > .env
                        npm run build
                        
                        # Serve frontend using a simple HTTP server
                        npm install -g serve
                        serve -s build -l 3000 &
                    """
                }
            }
        }

        stage('Post Deployment Validation') {
            parallel {
                stage('Backend Health Check') {
                    steps {
                        script {
                            sh """
                                # Wait for service to be available
                                sleep 30
                                curl -f http://${MINIKUBE_IP}:80/health || exit 1
                            """
                        }
                    }
                }

                stage('Frontend Availability Check') {
                    steps {
                        sh 'curl -f http://localhost:3000 || exit 1'
                    }
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
                        
                        Backend URL: http://${MINIKUBE_IP}:80
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