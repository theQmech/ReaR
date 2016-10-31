var express = require('express');
var app = express();

// This responds with "Hello World" on the homepage
app.get('/', function (req, res) {
   console.log("Got a GET request for the homepage");
   dummy();
   res.send('Hello GET');
})

// This responds a POST request for the homepage
app.post('/', function (req, res) {
   console.log("Got a POST request for the homepage");
   res.send('Hello POST');
})

// This responds a DELETE request for the /del_user page.
app.delete('/del_user', function (req, res) {
   console.log("Got a DELETE request for /del_user");
   res.send('Hello DELETE');
})

// This responds a GET request for the /list_user page.
app.get('/list_user', function (req, res) {
   console.log("Got a GET request for /list_user");
   res.send('Page Listing');
})

// This responds a GET request for abcd, abxcd, ab123cd, and so on
app.get('/ab*cd', function(req, res) {   
   console.log("Got a GET request for /ab*cd");
   res.send('Page Pattern Match');
})

var server = app.listen(8081, function () {

   var host = server.address().address
   var port = server.address().port

   console.log("Example app listening at http://%s:%s", host, port)
})

var dummy = function(){
	var pg = require('pg');
	//or native libpq bindings
	//var pg = require('pg').native

	var conString = process.env.ELEPHANTSQL_URL || "postgres://rganvir:@localhost:5050/postgres";

	var client = new pg.Client(conString);
	client.connect(function(err) {
	  if(err) {
	    return console.error('could not connect to postgres', err);
	  }
	  client.query("SELECT * from student where id=$1", ["1000"],function(err, result) {
	    if(err) {
	      return console.error('error running query', err);
	    }
	    console.log(result.rows[0]['name']);
	    //output: Tue Jan 15 2013 19:12:47 GMT-600 (CST)
	    client.end();
	  });
	});
}