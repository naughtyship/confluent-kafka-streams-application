# Deploying the App on Minikube
## Prerequisites
- **Java Development Kit (JDK)**: Ensure you have JDK installed to build the Maven project.
- **Maven**: Ensure Maven is installed to build the JAR file.
- **Docker**: Ensure Docker is installed to build the Docker image.
- **Kubernetes Cluster**: Ensure you have access to a Kubernetes cluster (e.g., Minikube, Azure Kubernetes Service).
- **Kubectl**: Ensure `kubectl` is installed and configured to interact with your Kubernetes cluster.

[link to install minikube](https://minikube.sigs.k8s.io/docs/start/?arch=%2Fwindows%2Fx86-64%2Fstable%2F.exe+download)
### 1. Build the Jar file
Use Maven to compile and package your application into a JAR file. Run the following command from the root of your project directory:

`mvn clean package`

This will create a JAR file in the target directory `(e.g., target/kafka-kstreams-ref-1.0-SNAPSHOT-jar-with-dependencies.jar)`.

### 2. Create the Docker Image
Ensure your Dockerfile is in the root directory of your project.

#### Build the Docker Image
   Replace <tag> with your desired image tag (e.g., my-app:latest). Run the following command to build the Docker image: 
   
   `docker build -t username/reponame:<tag> .`

   For example:  `docker build -t demo-user/my-app:latest .`

### 3.Verify the Docker Image
List the Docker images to ensure your image was built correctly:

`docker images`

You should see my-app (or whatever tag you used) listed among the available images.

### 4. Push the Image
Push the iamge into the docker hub using the below command:

`docker push demo-user/my-app:latest`


> [!NOTE]
> - You can have an extra step to scan your custom image with a tool (trivy tool or wizcli scanner) for vulnerabilities in the custom image.
>- Before pushing the image make sure that you have logged in with your DockerHub Account.



### 4. Deploy the Maven Application on Minikube
#### Prepare Kubernetes Secrets

Create a Kubernetes Secret to store sensitive configuration data used by your application. You can use `kubectl` to create the secret from a file or directly with `kubectl create secret` commands.

For example, to create a secret from literal values:

    kubectl create secret generic kafka-secrets \
    --from-literal=bootstrapServers=<your_bootstrap_servers> \
    --from-literal=apiKey=<your_api_key> \
    --from-literal=apiSecret=<your_api_secret> \
    --from-literal=schemaRegistryUrl=<your_schema_registry_url \
    --from-literal=schemaRegistryKey=<your_schema_registry_key> \
    --from-literal=schemaRegistrySecret=<your_schema_registry_secret>


Replace the placeholders with your actual values.

### 5. Deploy the Application
#### Create Deployment:
Save your Deployment YAML to a file,`kafka-streams-deployment.yaml`, and apply it to your Kubernetes cluster:

    kubectl apply -f kafka-streams-deployment.yaml

Ensure the image field in the YAML file points to your Docker image repository. Replace image field with your actual image URL and tag.

#### Verify Deployment

Check the status of your Deployment:

    kubectl get deployments

Get detailed information about your Deployment:

    kubectl describe deployment kafka-streams-app

List the pods to see if they are running:

    kubectl get pods

View logs of a specific pod to debug:

    kubectl logs <pod-name>
