<html>
<body>
<h2>Welcome to this Users API! This API accepts the following requests:</h2>
<h3><em>GET</em> /v1/users : This will return a list of all users in JSON format.</h3>
<h3><em>GET</em> /v1/users/{id} : This will return the user associated with the given ID in JSON format, or a message that the user could not be found. 
<h3><em>POST</em> /v1/users : This will accept a user firstName and lastName in JSON format, create a new user, save that user to the database, and return the user information (including ID) as JSON in the response. 
</body>
</html>
