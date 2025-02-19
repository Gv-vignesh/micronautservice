1. Create an S3 bucket for templates:

aws s3 mb s3://your-template-bucket

2. Deploy the stack:

# For dev environment
./deploy.sh dev

# For prod environment
./deploy.sh prod

3. Verify deployment:

# Check EKS cluster
aws eks list-clusters

# Check node groups
aws eks list-nodegroups --cluster-name eks-cluster-dev

# Check Istio
kubectl get pods -n istio-system

Key features of this setup:

Modular template structure
Environment-specific configurations
High availability with multi-AZ setup
Secure networking with private subnets
Automated Istio installation
Scalable node groups
IAM roles and security groups
Let me know if you need any clarification or have questions!
add the deployment steps to a readme file inside cloudformation folder. 


