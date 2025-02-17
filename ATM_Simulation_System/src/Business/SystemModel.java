package Business;

import Data_Access.MainDAL;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class SystemModel {
    public List<Role> roles;
    public static Role role = new Admin("ADMIN", 1);

    public SystemModel() {
        roles = new ArrayList<Role>();
        role = null;
    }

    public Role VerifyLogin(String username, String password) {
        // If user is invalid than return null
        boolean isValidUser = false;
        String userTypeStr = "";
        int userId = 0;
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/atmsystem", "root", "123456");
            Statement statement = con.createStatement();
            ResultSet query1 = statement.executeQuery("Select * from LoginSession");
            while (query1.next()) {
                MainDAL.write("Delete from LoginSession where UserId = '" + query1.getInt("UserId") + "'");
            }

            ResultSet query = statement.executeQuery("Select * from User");

            while (query.next()) {
                String name = query.getString("name"); // Column "name" (String)
                String pwd = query.getString("password");
                String userType = query.getString("userType");
                if (username.equals(name) && password.equals(pwd) && userType.toUpperCase().equals("ADMIN")) {
                    isValidUser = true;
                    userTypeStr = userType;
                    userId = query.getInt("Id");
                    break;
                }
                if (username.equals(name) && password.equals(pwd) && userType.toUpperCase().equals("USER")) {
                    isValidUser = true;
                    userTypeStr = userType;
                    userId = query.getInt("Id");
                    break;
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if (isValidUser) {
            if (userTypeStr.toUpperCase().equals(ROLE_TYPE.ADMIN.toString())) {
                role = new Admin("Admin", 1);
            } else {
                role = new User("User", 2);
            }

            //Manage the session
            // SessionManager.getInstance().setUsername(username);
            addSession(userId);

        }
        return role;
    }

    public static boolean Log_Out() {
        try {

            ResultSet query = MainDAL.read("Select * from LoginSession");
            while (query.next()) {
                MainDAL.write("Delete from LoginSession Where UserId=" + query.getInt("UserId"));
            }
            return true;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void addSession(int userId) {


//        ResultSet query = MainDAL.read(String.format("Select * from LoginSession"));
//        while (query.next()) {
//            userId = query.getInt("UserId");
//        }
        MainDAL.write(String.format("Insert into LoginSession (UserId) Values ('%d')", userId));
    }

    public static List<User> getUserList() {
        try {
            List<User> list = new ArrayList<>();
            ResultSet query = MainDAL.read(String.format("Select * from User Where UserType='%s'", ROLE_TYPE.USER.toString()));
            while (query.next()) {
                list.add(new User(query.getString("name"), query.getInt("Id")));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static List<Account> getAccountList(){
        try {
            List<Account> list = new ArrayList<>();
            ResultSet query = MainDAL.read(String.format("Select * from Account"));
            while (query.next()) {
                User user = new User(query.getString("name"),query.getInt("UserId"));
                list.add(new Account(user,query.getInt("AccNumber"), query.getDouble("Balance"),query.getString("Address"),query.getString("Phone")));
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public static List<Object []> getTransactions(){
        try {
            List<Object[]> list = new ArrayList<>();
            ResultSet query = MainDAL.read(String.format("Select * from Transaction"));
            int i=0;
            while (query.next()) {
                Object[] row = new Object[5];
                row[0] = ++i;
                row[1] = query.getString("TransactionType");
                row[2] = query.getDate("Date");
                row[3] = query.getDouble("Amount");
                row[4] = query.getInt("SenderAcc");
//                row[]
                list.add(row);
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
