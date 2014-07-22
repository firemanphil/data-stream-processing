var sys = require('sys'),
       http = require('http');

http.createServer(function (request, response) {
        sys.puts('received message');
        response.writeHead(200, {'Content-Type': 'text/plain'});
            response.write(request.method);
                response.end();
}).listen(8000);

sys.puts('Server running at http://127.0.0.1:8000/');