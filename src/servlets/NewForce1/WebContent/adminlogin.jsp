<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link href='http://fonts.googleapis.com/css?family=Roboto' rel='stylesheet' type='text/css'>

  <!-- use the font -->
  <style>
    body {
      font-family: 'Roboto', sans-serif;
    }
  </style>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Admin Login </title>
</head>

<body>
<center>
<hr/>  
  
<h3>Login Form</h3>  

 <br/>  
<form action="/newForce/AdminLogin" method="post">  
Username:<input type="text" name="id"/><br/><br/>  
Password:<input type="password" name="password"/><br/><br/>  
<input type="submit" value="Login"/>  
</form>  
<hr/>
</center>
</body>
</html>