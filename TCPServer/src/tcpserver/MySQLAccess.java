package tcpserver;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MySQLAccess {
    private Connection connect;
    private Statement statement;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;

    public MySQLAccess() {}
        
    private void open() {
        try {
            Class.forName("com.mysql.jdbc.Driver");
            connect = DriverManager.getConnection("jdbc:mysql://127.0.0.1/sister", "root","");
            statement = connect.createStatement();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void close() {
        try {
            if (resultSet != null) {
                    resultSet.close();
            }
            if (preparedStatement != null) {
                    preparedStatement.close();
            }
            if (connect != null) {
                    connect.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public JSONObject signup(String username, String password) throws JSONException {
        open();
        JSONObject response = new JSONObject();
        try {
            preparedStatement = connect.prepareStatement("SELECT * FROM USER WHERE "
                                                        + "username=\"" + username + "\";");
            resultSet = preparedStatement.executeQuery();
            
            if(resultSet.first()) {
                response.put("status", "fail");
                response.put("description", "username exists");
                return response;
            }
            else
            {
                preparedStatement = connect.prepareStatement("INSERT INTO user "
                                                            + "VALUES(NULL, ?, ?, 0, 0)");
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                preparedStatement.executeUpdate();
                
                response.put("status", "ok");
                return response;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.put("status", "error");
        }
        close();
        return response;
    }
    
    public JSONObject login(String username, String password) throws JSONException {
        open();
        JSONObject response = new JSONObject();
        try {
            preparedStatement = connect.prepareStatement("SELECT * FROM USER WHERE "
                                                        + "username=\"" + username + "\" AND "
                                                        + "password=\"" + password + "\";");
            resultSet = preparedStatement.executeQuery();
            
            if(resultSet.first()) {
                String token = new BigInteger(130, new SecureRandom()).toString(32);
                response.put("status", "ok");
                response.put("token", token);
                response.put("x", resultSet.getInt("position_x"));
                response.put("y", resultSet.getInt("position_y"));
                response.put("time", System.currentTimeMillis()/1000);
                
                return response;
            }
            else
            {
                response.put("status", "fail");
                response.put("description", "username/password combination is not found");
                return response;
            }
        } catch (SQLException e) {
            response.put("status", "error");
            e.printStackTrace();
        }
        close();
        return response;
    }
    
    public JSONObject inventory(int userID) throws JSONException {
        open();
        JSONObject response = new JSONObject();
        
        try {
            preparedStatement = connect.prepareStatement("SELECT * FROM inventory WHERE "
                                                        + "id_user=" + userID + ";");
            resultSet = preparedStatement.executeQuery();
            
            if(resultSet.first()) {
                response.put("status", "ok");
                JSONArray array = new JSONArray();
                array.put(resultSet.getString("honey"));
                array.put(resultSet.getString("herbs"));
                array.put(resultSet.getString("clay"));
                array.put(resultSet.getString("mineral"));
                array.put(resultSet.getString("potion"));
                array.put(resultSet.getString("incense"));
                array.put(resultSet.getString("gems"));
                array.put(resultSet.getString("elixir"));
                array.put(resultSet.getString("crystal"));
                array.put(resultSet.getString("stone"));
                response.put("inventory", array);
            }
        } catch (SQLException e) {
            response.put("status", "error");
            e.printStackTrace();
        }
        close();
        
        return response;
    }
    
    public JSONObject mixitem(int userID, int item1, int item2) throws JSONException {
        open();
        JSONObject response = new JSONObject();
        try {
            // Cek apakah kedua item tersebut  dapat digabungkan
            preparedStatement = connect.prepareStatement("SELECT * FROM mixitem WHERE "
                                                        + "item1=" + item1 + " AND "
                                                        + "item2=" + item2 + ";");
            resultSet = preparedStatement.executeQuery();
            
            if(resultSet.first()) {
                int item_result = resultSet.getInt("item_result");
                ResultSet resultSet2;
                String itemName1 = null, itemName2 = null, itemNameResult = null;
                
                // Cek apa nama dari item 1
                preparedStatement = connect.prepareStatement("SELECT * FROM items WHERE "
                                                        + "id_item=" + item1 + ";");
                resultSet2 = preparedStatement.executeQuery();
                if(resultSet2.first())
                    itemName1 = resultSet2.getString("item_name").toLowerCase();
                
                // Cek apa nama dari item 2
                preparedStatement = connect.prepareStatement("SELECT * FROM items WHERE "
                                                        + "id_item=" + item2 + ";");
                resultSet2 = preparedStatement.executeQuery();
                if(resultSet2.first())
                    itemName2 = resultSet2.getString("item_name").toLowerCase();
                
                // Cek apa nama dari item hasil
                preparedStatement = connect.prepareStatement("SELECT * FROM items WHERE "
                                                        + "id_item=" + item_result + ";");
                resultSet2 = preparedStatement.executeQuery();
                if(resultSet2.first())
                    itemNameResult = resultSet2.getString("item_name").toLowerCase();
                
                // Cek apakah pengguna memiliki jumlah barang yang cukup (masing - masing 3)
                preparedStatement = connect.prepareStatement("SELECT * FROM inventory WHERE "
                                                        + "id_user=" + userID + " AND "
                                                        + itemName1 + ">=3 AND "
                                                        + itemName2 + ">=3;");
                resultSet2 = preparedStatement.executeQuery();
                
                if(resultSet2.first()) {
                    preparedStatement = connect.prepareStatement("UPDATE inventory SET "
                                                            + itemName1 + "=" + itemName1 + "-3, "
                                                            + itemName2 + "=" + itemName2 + "-3, "
                                                            + itemNameResult + "=" + itemNameResult + "+1 "
                                                            + "WHERE id_user=" + userID + ";");
                    preparedStatement.executeUpdate();
                    response.put("status", "ok");
                    response.put("item", item_result);
                } else {
                    response.put("status", "fail");
                    response.put("description", "insufficient item");
                }
            } else {
                response.put("status", "fail");
                response.put("description", "item mixture is not available");
            }
        } catch (SQLException e) {
            response.put("status", "error");
            e.printStackTrace();
        }
        close();
        return response;
    }
    
    public void createInventory(String username) {
        open();
        try {
            preparedStatement = connect.prepareStatement("SELECT * FROM USER WHERE "
                                                        + "username=\"" + username + "\";");
            resultSet = preparedStatement.executeQuery();
            
            if(resultSet.first()) {
                int id = resultSet.getInt("id");
                
                preparedStatement = connect.prepareStatement("INSERT INTO inventory "
                                                            + "VALUES(" + id + ", 0, 0, 0, 0, 0, 0, 0, 0, 0, 0)");
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        close();
    }
    
    public int getUserId(String username) {
        open();
        int id = 0;
        try {
            preparedStatement = connect.prepareStatement("SELECT * FROM USER WHERE "
                                                        + "username=\"" + username + "\";");
            resultSet = preparedStatement.executeQuery();
            
            if(resultSet.first()) {
                id = resultSet.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        close();
        return id;
    }
}
