package tcpserver;

import java.io.*;
import java.net.*;
import org.json.*;

public class TCPServer {
    public ServerSocket serverSocket;
    public Socket connectionSocket;
    public MySQLAccess sql;
    public String request;
    public String token;
    public int userID;
    
    public TCPServer(int port) throws IOException {
        System.out.println("Server running! Waiting for connection...");
        serverSocket = new ServerSocket(port);
        connectionSocket = serverSocket.accept();
        sql = new MySQLAccess();
    }
    
    public JSONObject signup(JSONObject clientObject) throws JSONException {
        String username = clientObject.getString("username");
        String password = clientObject.getString("password");
        return sql.signup(username, password);
    }
    
    public JSONObject login(JSONObject clientObject) throws JSONException {
        String username = clientObject.getString("username");
        String password = clientObject.getString("password");
        return sql.login(username, password);
    }
    
    public JSONObject inventory(JSONObject clientObject) throws JSONException {
        if(clientObject.getString("token").equalsIgnoreCase(token)) {
            return sql.inventory(userID);
        } else {
            JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", "error");
            return responseJSON;
        }
    }
    
    public JSONObject mixitem(JSONObject clientObject) throws JSONException {
        if(clientObject.getString("token").equalsIgnoreCase(token)) {
            return sql.mixitem(userID, clientObject.getInt("item1"), clientObject.getInt("item2"));
        } else {
            JSONObject responseJSON = new JSONObject();
            responseJSON.put("status", "error");
            return responseJSON;
        }
    }
    
    public static void main(String[] args) throws Exception {
        int port = 6789;
        TCPServer server = new TCPServer(port);
        System.out.println("User connected to " + server.serverSocket.getLocalSocketAddress() + ", " + server.connectionSocket.getLocalSocketAddress());
        
        while(true)
        {
            BufferedReader inFromClient =new BufferedReader(new InputStreamReader(server.connectionSocket.getInputStream()));
            DataOutputStream outToClient = new DataOutputStream(server.connectionSocket.getOutputStream());
            server.request = inFromClient.readLine();

            System.out.println("Received: " + server.request);
            JSONObject clientObject = new JSONObject(server.request);
            String method = clientObject.getString("method");
            
            if(method.equalsIgnoreCase("signup")) {
                JSONObject responseJSON = server.signup(clientObject);
                System.out.println(responseJSON.toString());
                if(responseJSON.getString("status").equalsIgnoreCase("ok")) {
                    server.sql.createInventory(clientObject.getString("username"));
                }
                outToClient.writeBytes(responseJSON.toString() + "\n");
            } else if(method.equalsIgnoreCase("login")) {
                JSONObject responseJSON = server.login(clientObject);
                System.out.println(responseJSON.toString());
                if(responseJSON.getString("status").equalsIgnoreCase("ok")) {
                    server.userID = server.sql.getUserId(clientObject.getString("username"));
                    server.token = responseJSON.getString("token");
                    System.out.println("ID    : " + server.userID);
                    System.out.println("token : " + server.token);
                }
                outToClient.writeBytes(responseJSON.toString() + "\n");
            } else if(method.equalsIgnoreCase("inventory")) {
                JSONObject responseJSON = server.inventory(clientObject);
                System.out.println(responseJSON.toString());
                outToClient.writeBytes(responseJSON.toString() + "\n");
            } else if(method.equalsIgnoreCase("mixitem")) {
                JSONObject responseJSON = server.mixitem(clientObject);
                System.out.println(responseJSON.toString());
                outToClient.writeBytes(responseJSON.toString() + "\n");
            }
        }
    }
}
