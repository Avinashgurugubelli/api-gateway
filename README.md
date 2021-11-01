# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.5.6/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/2.5.6/maven-plugin/reference/html/#build-image)

### Guides
The following guides illustrate how to use some features concretely:

* [Using Spring Cloud Gateway](https://github.com/spring-cloud-samples/spring-cloud-gateway-sample)

### References
https://roytuts.com/spring-cloud-gateway-security-with-jwt-json-web-token/
https://www.baeldung.com/spring-cloud-securing-services

### Frequently asked questions?
1) How to control the direct access to microservices? in other words: Don't allow direct calls to Microservices. Only allow through API Gateway.
   - **Answer:** 
     - Assuming that you have a firewall in place, you could restrict inbound traffic to server to the ports that your Zuul endpoints are exposed on and disallow anyone from accessing the microservices' ports directly.
     - If you want to avoid going the firewall route, you could force the endpoints to check for a specific HTTP header or something that is set by Zuul/API gateway prior to forwarding a request, but that would be hacky and easy to circumvent. Based on my past experiences, the "right" way would be to do this via a firewall. Your app should be responsible for dealing with requests. Your firewall should be responsible for deciding who can hit specific endpoints.
    


