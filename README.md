# Microservice Spring Boot Guide

[![Java CI with Maven](https://github.com/MDAG-Millennials-Digital-Asset-Group/microservice-spring-boot-guide/actions/workflows/maven.yml/badge.svg)](https://github.com/MDAG-Millennials-Digital-Asset-Group/microservice-spring-boot-guide/actions/workflows/maven.yml)

***
## Dockerfile 

````dockerfile
FROM openjdk:11.0.8-jre-slim
WORKDIR application
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
ENTRYPOINT ["java", "-jar", "application.jar"]
````

*** 
## CI (Continuous integration) 

This workflow will build a Java project with Maven

Java version 15.0.1 
##### .github/workflows/package.yml

````yaml
name: Java CI with Maven

on:
  push:
    branches: [ master, develop ]
  pull_request:
    branches: [ master, develop ]

jobs:
  package:

    runs-on: ubuntu-latest

    steps:
    - uses: actions/checkout@v2
    - name: Set up openjdk-15
      uses: actions/setup-java@v1
      with:
        java-version: 15.0.1
    - name: Build with Maven
      run: mvn -B package --file pom.xml
````

***
## CD (Continuous deployment)

### Kubernetes Yaml File

##### service.yaml 
````yaml
apiVersion: v1
kind: Service 
metadata:
  name:  app-demo # Set Service Name 
spec:
  selector:
    app:  app-demo 
  type:  LoadBalancer
  ports:
    - port: 80
      targetPort: 8080
```` 

##### deployment.yaml

````yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: app-demo # Set Deployment Name 
spec:
  selector:
    matchLabels:
      app: app-demo
  replicas: 1
  strategy:
    rollingUpdate:
      maxSurge: 1
      maxUnavailable: 1
  template:
    metadata:
      labels:
        app: app-demo
    spec:
      containers:
        - name: app-demo
          image: gcr.io/PROJECT_ID/IMAGE:TAG
          ports:
            - containerPort: 8080
````

##### kustomization.yml

````yaml
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization
resources:
  - deployment.yml
  - service.yml
```` 

***

##### .github/workflows/deploy_to_gke.yaml 
```yaml
name: Deploy to GKE

on:
  push:
    branches:
      - main

env:
  PROJECT_ID: ${{ secrets.GKE_PROJECT }}
  GKE_CLUSTER: cluster-test-2021
  GKE_ZONE: asia-northeast3-c
  DEPLOYMENT_NAME: app-demo
  IMAGE: gke-image-name

jobs:
  setup-build-publish-deploy:
    name: SetUp Build Publish Deploy
    runs-on: ubuntu-latest

    steps:

      # Checkout Repository
      - name: Checkout Repository
        uses: actions/checkout@v2

      # Setup Google Cloud CLI
      - name: Setup Google Cloud CLI
        uses: google-github-actions/setup-gcloud@v0.2.0
        with:
          service_account_key: ${{ secrets.GKE_SA_KEY }}
          project_id: ${{ secrets.GKE_PROJECT }}

      - run: |-
          gcloud --quiet auth configure-docker

      # Get the GKE credentials so we can deploy to the cluster
      - uses: google-github-actions/get-gke-credentials@v0.2.1
        with:
          cluster_name: ${{ env.GKE_CLUSTER }}
          location: ${{ env.GKE_ZONE }}
          credentials: ${{ secrets.GKE_SA_KEY }}

      # Build the Docker image
      - name: Build
        run: |-
          docker build \
            --tag "gcr.io/$PROJECT_ID/$IMAGE:$GITHUB_SHA" \
            --build-arg GITHUB_SHA="$GITHUB_SHA" \
            --build-arg GITHUB_REF="$GITHUB_REF" \
            .

      # Push the Docker image to Google Container Registry
      - name: Publish
        run: |-
          docker push "gcr.io/$PROJECT_ID/$IMAGE:$GITHUB_SHA"

      # Set up  Kustomize
      - name: Set up Kustomize
        run: |-
          curl -sfLo kustomize https://github.com/kubernetes-sigs/kustomize/releases/download/v3.1.0/kustomize_3.1.0_linux_amd64
          chmod u+x ./kustomize

      # Deploy the Docker image to the GKE cluster
      - name: Deploy
        run: |-
          ./kustomize edit set image gcr.io/PROJECT_ID/IMAGE:TAG=gcr.io/$PROJECT_ID/$IMAGE:$GITHUB_SHA
          ./kustomize build . | kubectl apply -f -
          kubectl rollout status deployment/$DEPLOYMENT_NAME
          kubectl get services -o wide
```


****
## Service Discovery Pattern 


