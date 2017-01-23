<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@page import="org.json.JSONObject"%>
<%@page import="org.json.JSONArray"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<link href='http://fonts.googleapis.com/css?family=Roboto' rel='stylesheet' type='text/css'>

  <!-- use the font -->
  <style>
    body {
      font-family: 'Roboto', sans-serif;
      font-size: 12px;
    }
    div{
      max-width:500px;
    }
    #title { 
    	font-size: 28px;
    }
    #logout { 
    	font-size: 12px;
		text-align: right;
	}
  </style>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>View Requests</title>
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
<script src="https://code.jquery.com/jquery-1.12.3.js"></script>
<script src="https://cdn.datatables.net/1.10.12/js/jquery.dataTables.min.js"></script>
<link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/1.10.12/css/jquery.dataTables.min.css">
<script>
	$(document).ready(function() {
		$.ajax({
            url : "Requests",
            dataType : 'json',
            contentType:"application/json",
            error : function() {
                alert("Error");
            },
            success : function(response) {
				
            	var data = response["data"];
				var receivedData = [];
				
                $.each(data, function(index, item) {
                    var curr = [];
                    curr.push(data[index]["riderid"]);
                    curr.push(data[index]["rideid"]);
                    curr.push(data[index]["type"].toString().toUpperCase());
					curr.push("<a href='RequestHandle?rideid="+curr[1]+"&op="+curr[2].toString().toLowerCase()+"&type=accept'>✔</a>");
					curr.push("<a href='RequestHandle?rideid="+curr[1]+"&op="+curr[2].toString().toLowerCase()+"&type=cancel'>✘</a>");					
                    receivedData.push(curr);
                });
               	$('#example').DataTable({
               	 	processing: true,
               	 	data: receivedData,
                	aoColumns: [
                    	{ title: "RiderID" },
                    	{ title: "RideId" },
                    	{ title: "Request Type" },
                    	{ title: "Accept" },
                    	{ title: "Reject" }
                	]
            	});
            }
   		});
	});
</script>
</head>
<body >

	<center>
	<div id="title">Admin Page</div>
	<div id="logout"><a href='AdminLogout'>Logout</a></div>
	</center>
	<hr>
	<br>
	<br>
	<center>
	<div>
		<hr>
		<table id="example" cellspacing="0" class="display" style="text-align:center;"></table>
		<hr>
	</div>
	</center>
</body>
</html>