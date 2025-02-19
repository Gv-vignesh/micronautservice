#!/bin/bash

# Variables
ENVIRONMENT=$1
REGION="us-east-1"
STACK_NAME="eks-infrastructure"
TEMPLATE_BUCKET="your-template-bucket"

# Upload templates to S3
aws s3 cp cloudformation/ s3://${TEMPLATE_BUCKET}/ --recursive

# Deploy the stack
aws cloudformation create-stack \
  --stack-name ${STACK_NAME}-${ENVIRONMENT} \
  --template-url https://${TEMPLATE_BUCKET}.s3.amazonaws.com/main.yaml \
  --parameters ParameterKey=Environment,ParameterValue=${ENVIRONMENT} \
  --capabilities CAPABILITY_IAM \
  --region ${REGION}

# Wait for stack creation
aws cloudformation wait stack-create-complete \
  --stack-name ${STACK_NAME}-${ENVIRONMENT} \
  --region ${REGION}

# Update kubeconfig
aws eks update-kubeconfig \
  --name eks-cluster-${ENVIRONMENT} \
  --region ${REGION}

# Install Istio
kubectl create namespace istio-system
istioctl install --set profile=demo -y

# Enable Istio injection
kubectl label namespace default istio-injection=enabled 