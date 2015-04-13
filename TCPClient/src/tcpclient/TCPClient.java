package tcpclient;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import org.json.*;

public class TCPClient {
    public static void main(String argv[]) throws Exception
    {
        String username, password;
        String token = null;
        String command = null;
        String responseStatus;
        
        try (Socket clientSocket = new Socket("localhost", 6789)) {
            do {
                BufferedReader inFromUser = new BufferedReader( new InputStreamReader(System.in));
                DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
                BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                
                // Test Register
//                do {
//                    System.out.println("Register");
//                    System.out.print("Username : "); username = inFromUser.readLine();
//                    System.out.print("Password : "); password = inFromUser.readLine();
//                    String toSend = registerJSON(username, password).toString();
//                
//                    outToServer.writeBytes(toSend + '\n');
//                
//                    String response = inFromServer.readLine();
//                    JSONObject responseJSON = new JSONObject(response);
//                    responseStatus = responseJSON.getString("status");
//                    if(responseStatus.equalsIgnoreCase("ok")) {
//                        System.out.println("Registration Success!");
//                    } else if(responseStatus.equalsIgnoreCase("fail")) {
//                        String reason = responseJSON.getString("description");
//                        System.out.println("Registration fail, " + reason);
//                    }
//                    System.out.println();
//                } while(!responseStatus.equalsIgnoreCase("ok"));
                
                // Test Login
                do {
                    System.out.println("Login");
                    System.out.print("Username : "); username = inFromUser.readLine();
                    System.out.print("Password : "); password = inFromUser.readLine();
                    String toSend = loginJSON(username, password).toString();
                
                    outToServer.writeBytes(toSend + '\n');
                
                    String response = inFromServer.readLine();
                    JSONObject responseJSON = new JSONObject(response);
                    responseStatus = responseJSON.getString("status");
                    if(responseStatus.equalsIgnoreCase("ok")) {
                        System.out.println("Login Success!");
                        System.out.println("Token : " + responseJSON.getString("token"));
                        token = responseJSON.getString("token");
                    } else if(responseStatus.equalsIgnoreCase("fail")) {
                        String reason = responseJSON.getString("description");
                        System.out.println("Login fail, " + reason);
                    }
                    System.out.println();
                } while(!responseStatus.equalsIgnoreCase("ok"));
                
                // Test Inventory
//                do {
//                    do {
//                        System.out.print("Command : "); command = inFromUser.readLine();
//                    } while(!command.equalsIgnoreCase("inventory") && !command.equalsIgnoreCase("exit"));
//                    
//                    if(command.equalsIgnoreCase("inventory")) {
//                        String toSend = inventoryJSON(token).toString();
//
//                        outToServer.writeBytes(toSend + '\n');
//
//                        String response = inFromServer.readLine();
//                        JSONObject responseJSON = new JSONObject(response);
//                        responseStatus = responseJSON.getString("status");
//                        if(responseStatus.equalsIgnoreCase("ok")) {
//                            System.out.println("Your Inventory : ");
//                            List inventory = new ArrayList<Integer>();
//                            JSONArray jsonarray = responseJSON.getJSONArray("inventory");
//                            for(int i=0; i<jsonarray.length(); i++) {
//                                inventory.add(jsonarray.getInt(i));
//                            }
//                            printItemList(inventory);
//                        } else if(responseStatus.equalsIgnoreCase("error")) {
//                            System.out.println("Error opening inventory");
//                        }
//                        System.out.println();
//                    }
//                } while(!command.equalsIgnoreCase("exit"));
                
                // Test Mix Item
                do {
                    System.out.println("Mix Item");
                    System.out.print("Item 1 ID : "); int id1 = Integer.parseInt(inFromUser.readLine());
                    System.out.print("Item 2 ID : "); int id2 = Integer.parseInt(inFromUser.readLine());
                    String toSend = mixitemJSON(token, id1, id2).toString();

                    outToServer.writeBytes(toSend + '\n');

                    String response = inFromServer.readLine();
                    JSONObject responseJSON = new JSONObject(response);
                    responseStatus = responseJSON.getString("status");
                    if(responseStatus.equalsIgnoreCase("ok")) {
                        System.out.println("Mix item succeded, your new item id : " + responseJSON.getInt("item"));
                    } else if(responseStatus.equalsIgnoreCase("fail")) {
                        System.out.println("Failed to mix item, " + responseJSON.getString("description"));
                    } else if(responseStatus.equalsIgnoreCase("error")) {
                        System.out.println("Error mixing item");
                    }
                    System.out.println();
                } while(true);
            } while(!command.equalsIgnoreCase("exit"));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void printItemList(List inventory) {
        System.out.print("Honey " + inventory.get(0) + ", ");
        System.out.print("Herbs " + inventory.get(1) + ", ");
        System.out.print("Clay " + inventory.get(2) + ", ");
        System.out.print("Mineral " + inventory.get(3) + ", ");
        System.out.print("Potion " + inventory.get(4) + ", ");
        System.out.print("Incense " + inventory.get(5) + ", ");
        System.out.print("Elixir " + inventory.get(6) + ", ");
        System.out.print("Gems " + inventory.get(7) + ", ");
        System.out.print("Crystal " + inventory.get(8) + ", ");
        System.out.print("Stone " + inventory.get(9));
    }
    
    public static JSONObject mixitemJSON(String token, int id1, int id2) {
        JSONObject obj = new JSONObject();

        try {
            obj.put("method", "mixitem");
            obj.put("token", token);
            obj.put("item1", id1);
            obj.put("item2", id2);
        } catch(Exception e) {
            e.printStackTrace();
        }

        return obj;
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
    
    public static JSONObject loginJSON(String username, String password) {
        JSONObject obj = new JSONObject();

        try {
            obj.put("method", "login");
            obj.put("username", username);
            obj.put("password", password);	
        } catch(Exception e) {
            e.printStackTrace();
        }

        return obj;
    }
    
    public static JSONObject inventoryJSON(String token) {
        JSONObject obj = new JSONObject();

        try {
            obj.put("method", "inventory");
            obj.put("token", token);
        } catch(Exception e) {
            e.printStackTrace();
        }

        return obj;
    }
}
