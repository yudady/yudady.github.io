var dt = require('./node01_1');


var http = require('http');
//create a server object:
http.createServer(function (req, res) {

    res.writeHead(200, {'Content-Type': 'application/json'});

    res.write('{"key":"value" , "now":"' + dt.myDateTime() + '"}'); //write a response to the client
    res.end(); //end the response
}).listen(8080); //the server object listens on port 8080

console.log(dt.myDateTime());
console.log("http://127.0.0.1:8080")