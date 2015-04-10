package tcpclient;

import java.io.*;
import java.net.*;
import org.json.JSONObject;

public class TCPClient {
    public static void main(String argv[]) throws Exception
    {
        String username, password;
        String responseStatus;
        
        try (Socket clientSocket = new Socket("localhost", 6789)) {
            do {
                BufferedReader inFromUser = new BufferedReader( new InputStreamReader(System.in));
                DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                
                System.out.println("Register");
                System.out.print("Username : "); username = inFromUser.readLine();
                System.out.print("Password : "); password = inFromUser.readLine();
                
                BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                String toSend = registerJSON(username, password).toString();
                
                outToServer.writeBytes(toSend + '\n');
                
                String response = inFromServer.readLine();
                JSONObject responseJSON = new JSONObject(response);
                responseStatus = responseJSON.getString("status");
                if(responseStatus.equalsIgnoreCase("ok")) {
                    System.out.println("Registration Success!");
                } else if(responseStatus.equalsIgnoreCase("fail")) {
                    String reason = responseJSON.getString("description");
                    System.out.println("Registration fail, " + reason);
                }
                System.out.println();
            } while(!username.equalsIgnoreCase("exit"));
        }
    }

    public static JSONObject registerJSON(String username, String password) {
        JSONObject obj = new JSONObject();

        try {
            obj.put("method", "signup");
            obj.put("username", username);
            obj.put("password", password);	
        } catch(Exception e) {
            e.printStackTrace();
        }

        return obj;
    }   
}
