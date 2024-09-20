# japi-oauth

To run the application locally:

```bash
mvn spring-boot:run -Dspring-boot.run.profiles=local

mvn clean package



pom.xml:
Configured for WAR packaging
Includes spring-boot-starter-tomcat with provided scope
Has a local profile defined
DemoApplication.java:
Extends SpringBootServletInitializer for WAR deployment
Overrides configure method for external server deployment
application.properties:
Contains shared configurations
Uses spring.main.web-application-type=servlet for embedded server
WebSphere-specific line is commented out
application-local.properties:
Contains local-specific settings
Explicitly sets spring.main.web-application-type=servlet for local development
The setup looks correct for both scenarios. However, to ensure smooth operation in both environments, consider the following adjustments:
1. Update application.properties:
Update application-local.properties:
Add a new application-websphere.properties:
4. Update the pom.xml to include a WebSphere profile:
With these changes:


local development: 
mvn spring-boot:run -Dspring-boot.run.profiles=local
WebSphere deployment:
 mvn clean package -Pwebsphere




This setup ensures that the application can run both locally with the embedded server and on WebSphere with the appropriate configurations.


mvn clean install
cd demo-web
mvn spring-boot:run -Dspring-boot.run.profiles=local


http://localhost:8092/api/search-spotify?query=beatles
http://localhost:8092/api/search-spotify-oauth2?query=beatles


http://localhost:8092/api/call-github-api
http://localhost:8092/api/call-github-api-oauth2 [doesn't work due to credentials]

http://localhost:8092/api/dummy




source ~/.bashrc  # or ~/.bash_profile or ~/.zshrc


echo -e "\nexport M2_HOME=/Users/kishoreshiraguppi/java/apache-maven-3.9.3/bin/\nexport PATH=\$PATH:\$M2_HOME" >> ~/.bashrc