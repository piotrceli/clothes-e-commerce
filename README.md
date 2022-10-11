# e-commerce

This application is a sample online clothes store. 
<h3>Main application's functionalities:</h3> 
<ul> 
    <li>Different CRUD operations for Users, Categories and Products</li> 
    <li>Ability for a logged user to: add/remove item to/from a shopping cart, 
        view products stored in the shopping cart, to checkout the shopping cart, check order history</li> 
    <li>Ability to view products based on a specific category</li> 
    <li>Ability to assign and unassign product to/from a category</li> 
    <li>Weather-dependent products display</li>
</ul> 
<br>
<h3>Used technologies:</h3>
<ol>
  <li>Java</li>
  <li>Spring Boot, Spring Security, Spring Data JPA</li>
  <li>JUnit, AssertJ, Mockito</li>
  <li>MySQL</li>
  <li>Lombok, Swagger</li>
</ol>
<br>
<h3>Testing flow proposal:</h3>
<ol>
  <li>Use an SQL script to create a database and tables in the MySQL environment</li>
  <li>Fill in information in the application.properties file to configure the database environment <i>note: refers to username and password</i></li>
  <li>Start the application</li>
  <li>A Sample data will be created when the application starts by a CommandLineRunner</li>
  <li>Test the application with the use of an API platform (e.g. Postman) and Swagger (http://localhost:8080/swagger-ui/index.html)</li>
  <li>The additional information about the API and its endpoints is available in Swagger's documentation</li>
</ol>
<h4>External APIs' credits:</h4> 
<ul> 
    <li>https://openweathermap.org/</li> 
    <li>https://positionstack.com/</li> 
</ul> 
<br>
<h3>Description from Swagger's documentation.</h3>
<h4>IMPORTANT: To use different endpoints it may be necessary to log in</h4> 
<p>The application is secured with a JWT token. </p> 
<p>To obtain the <strong>access_token*</strong> it is recommended to use an API platform (e.g. Postman)</p> 
<h4>Request URL for login: localhost: http://localhost:8080/api/v1/login</h4> 
<p>Sample credentials after data creation by a CommandLineRunner </p> 
<p>{ username: "user", password: "password" } - role <strong>USER</strong></p> 
<p>{ username: "admin", password: "password" } - role <strong>ADMIN</strong></p> 
<h4>To Authorize and test the API's endpoints with granted permissions add: Bearer access_token<strong>*</strong></h4> 
<p>Requirements for the permissions are described in the notes of every request.<p>
