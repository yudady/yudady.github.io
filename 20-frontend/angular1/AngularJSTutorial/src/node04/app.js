import {addTwo} from './addTwo.js';
import * as http from 'http'; // ES 6
import {readFileSync} from 'fs';
import {readFile} from 'fs/promises';


const buffer = readFileSync(new URL('./data.json', import.meta.url));
const json = JSON.parse(await readFile(new URL('./data.json', import.meta.url)));

//create a server object:
http.createServer(function (req, res) {

    res.writeHead(200, {'Content-Type': 'application/json; charset=utf-8'});
    // res.end("Hello World  : addTwo " + addTwo(4) + " buffer " + buffer); //end the response
    res.end(JSON.stringify(json, null, 2)); //end the response

}).listen(8080); //the server object listens on port 8080

// Prints: 6
console.log(buffer);
console.log(addTwo(4));
console.log("http://127.0.0.1:8080")