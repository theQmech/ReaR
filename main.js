var express = require('express');
var app = express();

// This responds with "Hello World" on the homepage
app.get('/', function (req, res) {
	console.log("Got a GET request for the homepage");
	// query_db("select * from student where id=$1", ["1000"], function(obj){res.send(obj)});
	// res.send('Hello GET');
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

app.get('/Bike', function (req, res) {

	var bikeid = req.query.bikeid;
	console.log(bikeid);

	res.setHeader('Content-Type', 'application/json');

	if (bikeid == null){
		console.log("BikeID is null");
		res.send(JSON.stringify({ 'status': false, 'message': 'null BikeID' }, null, 2));
	}
	else{
		query_db("select * from bike where bikeid=$1 ", [bikeid], sendBike, res);
	}
})

app.get('/Bike', function (req, res) {

	var bikeid = req.query.bikeid;
	console.log(bikeid);

	res.setHeader('Content-Type', 'application/json');

	if (bikeid == null){
		console.log("BikeID is null");
		res.send(JSON.stringify({ 'status': false, 'message': 'null BikeID' }, null, 2));
	}
	else{
		query_db("select * from bike where bikeid=$1 ", [bikeid], sendBike, res);
	}
})

var sendBike = function(result, res){
	if (result.rows.length == 0) {
		res.send(JSON.stringify({ 'status': false, 'message': 'bikeid not found' }, null, 2));
	}

	var row = result.rows[0];
	var data = {
		'BikeID' : row['bikeid'],
		'Name' : null,
		'Model/Make' : row['makemodel'],
		'Color' : row['color'],
		'Status' : row['status']
	}
	res.send(JSON.stringify({ 'status': true, 'data': data }, null, 2));
}

app.get('/BikeList', function (req, res) {

	var UserID = req.query.UserID;
	var StandID = req.query.StandID;
	var ShowroomID = req.query.ShowroomID;

	res.setHeader('Content-Type', 'application/json');

	var ret_cols = ' bikeid as BikeID,makemodel as Model,color as Color,status as Status ';

	if(UserID) {
		// select * from bike natural join ownership where ownerid=$1
		query_db('select '+ret_cols+' from bike natural join ownership where ownerid=$1',[UserID],transform,res);
	}
	else if(StandID) {
		// select * from bike natural join bike_at_stand where bikestandid=$1
		query_db('select '+ret_cols+' from bike natural join bike_at_stand where bikestandid=$1',[StandID],transform,res);
	}
	else if(ShowroomID) {
		res.send(JSON.stringify({ 'status': false, 'message': 'Not implemented Showroom search yet' }, null, 2));
	}
	else {
		res.send(JSON.stringify({ 'status': false, 'message': 'null BikeID' }, null, 2));
	}
})

app.get('/Stands', function (req, res) {

	var UserID = req.query.UserID;
	var StandID = req.query.StandID;
	var ShowroomID = req.query.ShowroomID;

	res.setHeader('Content-Type', 'application/json');
	res.send(JSON.stringify({ 'status': false, 'message': 'Stands not yet implemented' }, null, 2));
})

app.get('/RentOp', function (req, res) {

	var UserID = req.query.UserID;
	var BikeID = req.query.BikeID;
	var StandID = req.query.StandID;
	var op = req.query.Op;

	if (UserID == null || BikeID == null || StandID == null || (op != 'Rent' || op!='Unrent')){
		res.send(JSON.stringify({ 'status': false, 'message': 'incomplete fields' }, null, 2));
	}

	res.setHeader('Content-Type', 'application/json');

	var ret_cols = ' bikeid as BikeID,makemodel as Model,color as Color,status as Status ';

	if(UserID) {
		// select * from bike natural join ownership where ownerid=$1
		query_db('select '+ret_cols+' from bike natural join ownership where ownerid=$1',[UserID],transform,res);
	}
	else if(StandID) {
		// select * from bike natural join bike_at_stand where bikestandid=$1
		query_db('select '+ret_cols+' from bike natural join bike_at_stand where bikestandid=$1',[StandID],transform,res);
	}
	else if(ShowroomID) {
		res.send(JSON.stringify({ 'status': false, 'message': 'Not implemented Showroom search yet' }, null, 2));
	}
	else {
		res.send(JSON.stringify({ 'status': false, 'message': 'null BikeID' }, null, 2));
	}
})

var rent = function(){

};

var transform = function(result, res){
	var ret = {};

	var metadata = [];
	result.fields.forEach(function (item){
		metadata.push(item['name']);
	});
	ret['metadata'] = metadata;

	var new_rows = [];
	result.rows.forEach(function (item){
		var curr_row = {};
		metadata.forEach(function(col_title){
			curr_row[col_title] = item[col_title];
		});
		new_rows.push(curr_row);
	});
	ret['rows'] = new_rows;

	console.log(ret);

	res.send(JSON.stringify(ret));
}

var server = app.listen(8081, function () {
	var host = server.address().address
	var port = server.address().port

	console.log("Example app listening at http://%s:%s", host, port)
})

function query_db(query_str, params, callback, response){
	var pg = require('pg');

	var connString = process.env.ELEPHANTSQL_URL || "postgres://rganvir:@localhost:5432/postgres";

	var client = new pg.Client(connString);

	var ret = "dbis";

	client.connect(function(err){
		if(err) return console.error('could not connect to db', err);

		client.query(query_str, params, function(err, result) {
			console.log("here");
			if(err) return console.error('error running query', err);
			callback(result, response);
			client.end();
		});

	});

	return ret;
};

var rent_transaction = function(){
	var pgp = require('pg-promise')(options);
// See also: https://github.com/vitaly-t/pg-promise#initialization-options

// Database connection details;
var cn = {
    host: 'localhost', // 'localhost' is the default;
    port: 5432, // 5432 is the default;
    database: 'postgres',
    user: 'rganvir',
    password: ''
};
// You can check for all default values in:
// https://github.com/brianc/node-postgres/blob/master/lib/defaults.js

var db = pgp(cn); // database instance;

// NOTE: The default ES6 Promise doesn't have methods `.spread` and `.finally`,
// but they are available within Bluebird library used here as an example.

db.tx(function (t) {
    // t = this;
    return this.batch([
        this.one("insert into users(name) values($1) returning id", "John"),
        this.one("insert into events(code) values($1) returning id", 123)
    ]);
})
    .spread(function (user, event) {
        // print new user id + new event id;
        console.log("DATA:", user.id, event.id);
    })
    .catch(function (error) {
        console.log("ERROR:", error); // print the error;
    })
    .finally(function () {
        // If we do not close the connection pool when exiting the application,
        // it may take 30 seconds (poolIdleTimeout) before the process terminates,
        // waiting for the connection to expire in the pool.

        // But if you normally just kill the process, then it doesn't matter.

        pgp.end(); // for immediate app exit, closing the connection pool.

        // See also:
        // https://github.com/vitaly-t/pg-promise#library-de-initialization
    });
	});
}