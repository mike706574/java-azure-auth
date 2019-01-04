package fun.mike.azure.auth;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class EchoServer {
    private HttpServer server;
    
    private final Integer port;
    private final String response;

    public EchoServer(Integer port, String response) {
        this.port = port;
        this.response = response;
    }

    public static EchoServer start(Integer port, String response) {
        return new EchoServer(port, response).start();
    }

    public EchoServer start() {
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/", new EchoHandler(response));
            server.start();
            return this;
        }
        catch( IOException ex ) {
            throw new RuntimeException( ex );
        }
    }

    public static void with(Integer port, String response, Runnable runnable) {
        EchoServer server = EchoServer.start(port, response);
        try {
            runnable.run();
        }
        finally {
            server.stop();
        }
    }

    public EchoServer stop() {
        server.stop(1);
        server = null;
        return this;
    }

    private static final class EchoHandler implements HttpHandler {
        private final String response;
        
        public EchoHandler(String response) {
            this.response = response;
        }
            
        @Override
        public void handle( HttpExchange exchange ) throws IOException {
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}
