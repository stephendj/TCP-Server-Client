package tcpserver;

import java.sql.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
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

    public JSONObject register(String username, String password) throws JSONException {
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
                                                            + "VALUES(NULL, ?, ?)");
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                preparedStatement.executeUpdate();
                
                response.put("status", "ok");
                return response;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        response.put("status", "error");
        close();
        return response;
    }
}
