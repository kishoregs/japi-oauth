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

source ~/.bashrc

async function measurePostApiCallTime(url, data = {}) {
  console.log(`Calling API: ${url}`);
  
  const start = performance.now();
  
  try {
    const response = await fetch(url, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(data)
    });
    
    const responseData = await response.json();
    
    const end = performance.now();
    const timeTaken = end - start;
    
    console.log(`API call completed in ${timeTaken.toFixed(2)} milliseconds`);
    console.log('Response status:', response.status);
    console.log('Response data:', responseData);
    
    return { timeTaken, responseData, status: response.status };
  } catch (error) {
    console.error('Error calling API:', error);
    throw error;
  }
}

// Usage example
const apiUrl = 'https://api.example.com/endpoint';
const postData = {
  key1: 'value1',
  key2: 'value2'
};

measurePostApiCallTime(apiUrl, postData)
  .then(result => {
    console.log('Measurement complete');
    console.log(`Time taken: ${result.timeTaken.toFixed(2)} ms`);
    console.log(`Status: ${result.status}`);
    console.log('Response data:', result.responseData);
  })
  .catch(error => {
    console.error('Measurement failed:', error);
  });