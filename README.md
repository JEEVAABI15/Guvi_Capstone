# Java Spring Boot Application with DevOps CI/CD Pipeline

This project demonstrates how to create a Java Spring Boot application, containerize it with Docker, and set up a Jenkins CI/CD pipeline to build, test, push the Docker image to Docker Hub, and deploy it on an AWS EC2 instance.

## Tools and Technologies Used
- **Java (Spring Boot)**
- **Maven** for project build
- **Docker** for containerization
- **Jenkins** for CI/CD automation
- **Git** for version control
- **AWS EC2** for deployment
- **JUnit** for testing

---

## Steps to Set Up the Project

### Step 1: Create a Spring Boot Application

1. **Set up a new Spring Boot project** using Spring Initializr or via the command line.

    ```bash
    curl https://start.spring.io/starter.zip \
      -d dependencies=web \
      -d name=DevOpsJavaApp \
      -d type=maven-project \
      -o DevOpsJavaApp.zip
    ```

2. **Unzip the project**:

    ```bash
    unzip DevOpsJavaApp.zip -d DevOpsJavaApp/
    ```

3. **Create a simple REST endpoint** in `DevOpsJavaAppApplication.java`:

    ```java
    package com.example.demo;

    import org.springframework.boot.SpringApplication;
    import org.springframework.boot.autoconfigure.SpringBootApplication;
    import org.springframework.web.bind.annotation.GetMapping;
    import org.springframework.web.bind.annotation.RestController;

    @SpringBootApplication
    public class DevOpsJavaAppApplication {
        public static void main(String[] args) {
            SpringApplication.run(DevOpsJavaAppApplication.class, args);
        }
    }

    @RestController
    class HelloWorldController {
        @GetMapping("/hello")
        public String hello() {
            return "Hello, DevOps World from Java!";
        }
    }
    ```

4. **Build the project**:

    ```bash
    mvn clean install
    ```

---

### Step 2: Create a Docker Image

1. **Create a Dockerfile** in the root directory of the project:

    ```Dockerfile
    FROM openjdk:11-jre-slim
    WORKDIR /app
    COPY target/demo-0.0.1-SNAPSHOT.jar app.jar
    EXPOSE 8080
    ENTRYPOINT ["java", "-jar", "app.jar"]
    ```

2. **Build the Docker image**:

    ```bash
    docker build -t java-devops-app .
    ```

3. **Run the Docker container locally**:

    ```bash
    docker run -p 8080:8080 java-devops-app
    ```

4. **Test the application** by visiting:

    ```
    http://localhost:8080/hello
    ```

---

### Step 3: Push Docker Image to Docker Hub

1. **Login to Docker Hub**:

    ```bash
    docker login
    ```

2. **Tag the Docker image**:

    ```bash
    docker tag java-devops-app <your-dockerhub-username>/java-devops-app
    ```

3. **Push the image**:

    ```bash
    docker push <your-dockerhub-username>/java-devops-app
    ```

---

### Step 4: Set Up Jenkins for CI/CD

1. **Install Jenkins** on an EC2 instance or any VM:

    ```bash
    sudo yum install java-17-amazon-corretto-headless -y
    sudo yum install maven -y
    sudo dnf install git -y
    sudo yum install docker -y
    sudo wget -O /etc/yum.repos.d/jenkins.repo \
        https://pkg.jenkins.io/redhat-stable/jenkins.repo
    sudo rpm --import https://pkg.jenkins.io/redhat-stable/jenkins.io-2023.key
    sudo dnf install jenkins -y
    sudo systemctl enable jenkins
    sudo systemctl start jenkins
    ```

2. **Configure Jenkins**:
    - Access Jenkins at `http://<jenkins-ip>:8080` and use the initial admin password:

      ```bash
      sudo cat /var/lib/jenkins/secrets/initialAdminPassword
      ```

3. **Grant Jenkins Docker Permissions**:

    ```bash
    sudo usermod -aG docker jenkins
    sudo systemctl restart docker
    sudo systemctl restart jenkins
    ```

---

### Step 5: Jenkins CI/CD Pipeline Script

1. **Create a new Jenkins Pipeline job** and use the following pipeline script:

    ```groovy
    pipeline {
        agent any
        environment {
            DOCKER_IMAGE = "jeeva1512/java-devops-app"
        }
        stages {
            stage('Clone Repository') {
                steps {
                    git branch: 'main', url: 'https://github.com/JEEVAABI15/Guvi_Capstone'
                }
            }
            stage('Run Tests') {
                steps {
                    sh 'mvn test'
                }
                post {
                    always {
                        junit 'target/surefire-reports/*.xml'
                    }
                }
            }
            stage('Build with Maven') {
                steps {
                    sh 'mvn clean install'
                }
            }
            stage('Build Docker Image') {
                steps {
                    script {
                        docker.build(DOCKER_IMAGE)
                    }
                }
            }
            stage('Push Docker Image') {
                steps {
                    script {
                        docker.withRegistry('https://index.docker.io/v1/', 'dockerhub-credentials') {
                            docker.image(DOCKER_IMAGE).push()
                        }
                    }
                }
            }
            stage('Run Docker Container') {
                steps {
                    script {
                        def containerId = sh(script: "docker run -d -p 80:8080 ${DOCKER_IMAGE}", returnStdout: true).trim()
                        echo "Container is running with ID: ${containerId}"
                    }
                }
            }
        }
        post {
            success {
                echo 'Deployment Successful!'
            }
            failure {
                echo 'Build or Deployment Failed!'
            }
        }
    }
    ```

---

### Step 6: Deploy to AWS EC2

1. **Set up an EC2 instance**:
    - Install Docker on EC2:

    ```bash
    sudo yum update -y
    sudo yum install docker -y
    sudo service docker start
    sudo usermod -aG docker ec2-user
    ```

2. **Run the Jenkins pipeline** to deploy the Docker container on EC2:

    ```bash
    http://<ec2-instance-ip>:8080/hello
    ```

---

### Step 7: Automate Testing (Optional)

- Add unit tests in `DevOpsJavaAppApplicationTests.java` using JUnit:

    ```java
    @Test
    void helloEndpointTest() {
        ResponseEntity<String> response = restTemplate.getForEntity("/hello", String.class);
        assertThat(response.getBody()).isEqualTo("Hello, DevOps World from Java!");
    }
    ```

- Jenkins will automatically run the tests as part of the pipeline.

---

## Conclusion

This project walks through setting up a Java Spring Boot application, containerizing it with Docker, pushing the image to Docker Hub, and automating the entire process with Jenkins. By following these steps, you can implement a scalable CI/CD pipeline that integrates Docker and deploys to AWS EC2 seamlessly.

Feel free to clone this repository and modify it to suit your needs!
