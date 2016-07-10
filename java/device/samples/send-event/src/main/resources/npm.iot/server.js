// Let’s make node/socketio listen on port 3000
var io = require('socket.io').listen(3000);

//Initialize Web Server
var express = require('express');
var path = require("path");
var web = function() {
    var app = express();
    app.use('/_assets', express.static(__dirname + '/_assets'));
    app.get('/index.html', function (req, res) {
        res.sendFile(path.join(__dirname + '/ngIndex.html'));
    });
    return app;
};
web().listen(8000, function(){
    console.log('Node Server has Started @8000');
});

// Configuration to Database
var sql = require('mssql');
var config = {
    user: 'rajesh',
    password: 'jobvite@12345',
    server: 'raspberry.database.windows.net',
    database: 'raspberrysqldb',
    stream: true,
    options: {
        encrypt: true
    }
};

// Define/initialize our global vars
var messages = [];
var isInitMessages = false;
var socketCount = 0;
var prevId = 0;

sql.connect(config, function(err){
    if (err) console.log(err);
    
    io.sockets.on('connection', function(socket){
        // Socket has connected, increase socket count
        socketCount++;
        io.sockets.emit('users connected', socketCount);
        socket.on('disconnect', function() {
            socketCount--;
            io.sockets.emit('users connected', socketCount);
        });
        
        socket.on('new message', function(data){
            messages.push(data);
            io.sockets.emit('new message', data);
        });
        
        setInterval(function(data) {
            console.log("looking for data greater than " + prevId);
            new sql.Request().query("select id, message from messages where id > " + prevId + " order by id")
                .on('row', function(data){
                    // Push results onto the notes array
                    messages.push(data);
                    io.sockets.emit('new message', data);
                    prevId = data.id;
                });
        }, 3000);
        
        // Check to see if initial query/notes are set
        if (! isInitMessages) {
            // Initial app start, run db query
            console.log("running query");
            var request = new sql.Request();
            request.query('select id, message from messages')
                .on('row', function(data){
                    // Push results onto the notes array
                    messages.push(data);
                    prevId = data.id;
                })
                .on('done', function(){
                    // Only emit notes after query has been completed
                    socket.emit('initial messages', messages);
                });

            isInitMessages = true;
        } else {
            // Initial notes already exist, send out
            socket.emit('initial messages', messages);
        }
    });
});
