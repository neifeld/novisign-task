#### Task Overview
Your objective is to create a Java Spring Boot application showcasing your skills in modern Java technologies and practices. The application will allow users to maintain a list of image URLs and play a slideshow with transitions. Each image change even to be floowed by proof-of-play notification

---

### **Updated Requirements**

#### **Core Requirements**

1. **Backend Development**
   - Create a **Java Spring Boot** application implementing RESTful APIs to:
     - **Add an image**: Add a new image URL with a specified duration (`POST /addImage`).
     - **Delete an image**: Remove an image by its ID (`DELETE /deleteImage/{id}`).
     - **Add a slideshow**: Add a new slideshow (`POST /addSlideshow`).
     - **Delete a slideshow**: Remove a slideShow by its ID (`DELETE /deleteSlideshow/{id}`).
     - **Search images**: Retrieve images and slideshows found images belong to based on keywords in the URL or duration (`GET /images/search`).
     - **Retrieve slideshow's images order**: Get the images ordered by duration (`GET /slideShow/{id}/slideshowOrder`).
     - **Track finish play of the image**: When image is being replaced to the next one, record this event (`GET /slideShow/{id}/proof-of-play/{id}`).

2. **Data Storage**
   - Use **MySQL** or **PostgreSQL** for persistent data storage.

3. **Modern Java Techniques**
   - Use modern technqiues whenever it's possible and fits the place

4. **Error Handling**
   - Return user-friendly error responses in a standard format.

5. **Testing**
   - Write unit tests using **JUnit 5**.

---

#### **Advanced Features**

1. **Containerization**
   - Create a **Dockerfile** to containerize the application.
   - Add a `docker-compose.yml` for local setup with the database.

2. **Event-Driven Architecture**
   - Demonstrate **event-driven design** using **Spring EventPublisher** or **Kafka** to log significant API actions (e.g., adding/deleting images).

---

#### **Submission Guidelines**
1. **Code Submission**:  
   - Upload your code to a public GitHub repository.  
   - Ensure it is well-structured, adheres to best practices, and includes comments where necessary.

2. **Functional Requirements**:
   - Ensure all APIs handle errors gracefully.
   - Include sample payloads for testing in the documentation.

3. **Evaluation Criteria**:
   - Quality of code and adherence to best practices.
   - Use of modern Java and Spring features.
   - Completeness of documentation and testing.
