# Cloud Storage Application
This Cloud Storage application is a Java-based solution for securely storing, managing, and accessing files over a cloud-based storage system. It utilizes MinIO for object storage, PostgreSQL for user data management, and JWT for secure authentication.

## Features
- **File Upload and Download:** Users can upload and download files securely.
- **User Authentication:** Login and logout functionalities using JWT.
- **Role-Based Access Control:** Different access levels for admins and users.
- **File Management:** Users can list, edit, and delete files.
## Prerequisites
- Java JDK 21
- Docker and Docker Compose
- PostgreSQL Database
- MinIO Storage
- Liquibase
## Installation
1. **Clone the Repository:**
```bash
   git clone https://github.com/adonix300/Cloud-Storage
   cd CloudStorageDemo
```
2. **Build the Project:**
```bash
   mvn clean install
```
3. **Docker setup:**

Ensure Docker and Docker Compose are installed on your system. Use the provided Dockerfile and docker-compose.yml to set up the environment.
```bash
   docker-compose up -d
```

## Usage
- **Starting the Application:**

    After setting up the Docker containers, the application will be available at http://localhost:8081.


- **Pre-configured Accounts:**

    Two user accounts are available for testing:
    
  - Login: user1 Password: password
  - Login: user2 Password: password


- **Registering a New User:**

    Use the /register endpoint to create a new user.

    - username:
    - password:


- **Logging In:**

    Use the /login endpoint to authenticate a user and receive a JWT token.


- **Uploading a File:**

    Use the /file endpoint with the POST method to upload a file.


- **Listing Files:**

    Access the /list endpoint to view the files.


- **Downloading Files:**

    Use the /file endpoint with the GET method and provide the filename to download a file.


- **Editing and Deleting Files:**

    Use the /file endpoint with PUT or DELETE methods to edit or delete files.
