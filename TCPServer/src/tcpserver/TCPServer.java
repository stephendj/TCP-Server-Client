package tcpserver;

import java.io.*;
import java.net.*;
import org.json.*;

public class TCPServer {
    public ServerSocket serverSocket;
    public Socket connectionSocket;
    public MySQLAccess sql;
    public String request;
    
    public TCPServer() throws IOException {
        System.out.println("Server running! Waiting for connection...");
        serverSocket = new ServerSocket(6789);
        connectionSocket = serverSocket.accept();
        sql = new MySQLAccess();
    }
    
    public String register(JSONObject clientObject) throws JSONException {
        String username = clientObject.getString("username");
        String password = clientObject.getString("password");
        String response = sql.register(username, password).toString();
        
        return response;
    }
    
    public static void main(String[] args) throws Exception {
        
        TCPServer server = new TCPServer();
        System.out.println("User connected to " + server.serverSocket + ", " + server.connectionSocket);
        
        while(true)
        {
            BufferedReader inFromClient =new BufferedReader(new InputStreamReader(server.connectionSocket.getInputStream()));
            DataOutputStream outToClient = new DataOutputStream(server.connectionSocket.getOutputStream());
            server.request = inFromClient.readLine();

            System.out.println("Received: " + server.request);
            JSONObject clientObject = new JSONObject(server.request);
            String method = clientObject.getString("method");
            
            if(method.equalsIgnoreCase("signup")) {
                String response = server.register(clientObject);
                System.out.println(response);
                outToClient.writeBytes(response + "\n");
            }
        }
    }
}
