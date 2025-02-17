package Business;

import Data_Access.MainDAL;

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Account {
    private int accountNumber;


    private double balance;
    private String pin;
    public User holderName;
    private List<Transcation> transactions;
    public String address;
    public String phone;

    public Account(User holderName, int accountNumber, double balance, String address, String phone) {
        this.accountNumber = accountNumber;
        this.holderName = holderName;
        this.address = address;
        this.phone = phone;
        this.balance = balance;
    }


    public String getPin() {
        return pin;
    }

    public List<Transcation> getTransactions() {
        return transactions;
    }

    public double checkBalance() {
        return balance;
    }

    public static boolean withdraw(double amount) {
        try {
            int userId = 0;
            int accnumber = 0;
            double balance = 0;
            ResultSet query = MainDAL.read("Select * from LoginSession");
            while (query.next()) {
                userId = query.getInt("UserId");
            }
            ResultSet query1 = MainDAL.read("Select * from Account where UserId = " + userId);
            while (query1.next()) {
                accnumber = query1.getInt("AccNumber");
                balance = query1.getDouble("Balance");
            }
            double newBalance = balance - amount;
            String sql = "UPDATE account SET Balance = " + newBalance + " WHERE ACCNUMBER=" + accnumber; // Change to your table and columns
            if (MainDAL.write(sql)) {
                //JOptionPane.showMessageDialog(null, "Money is successful withdrawal");
                AccountOperations op = new AccountOperations();
                op.addTransaction(AccountOperations.getAccountNumber(), AccountOperations.getAccountNumber(), TransactionType.WITHDRAWAL, amount);
                return true;
            } else
                return false;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public boolean deposit(double amount) throws SQLException {
//        this.balance += amount;
//        String sql = "SELECT BALANCE FROM account WHERE ACCNUMBER=" + this.accountNumber; // Change to your table and columns
//        ResultSet reslut = MainDAL.read(sql);
//        double lastBalance = 0.0;
//        if (reslut.next())
//            lastBalance = reslut.getDouble(1);
//
//        lastBalance += this.balance;

        int userId = 0;
        int accnumber = 0;
        double balance = 0;
        ResultSet query = MainDAL.read("Select * from LoginSession");
        while (query.next()) {
            userId = query.getInt("UserId");
        }
        ResultSet query1 = MainDAL.read("Select * from Account where UserId = " + userId);
        while (query1.next()) {
            balance = query1.getDouble("Balance");
        }
        double newBalance = balance + amount;

        String sql = "UPDATE account SET Balance = " + newBalance + "WHERE ACCNUMBER=" + AccountOperations.getAccountNumber(); // Change to your table and columns
        if (MainDAL.write(sql)) {
            //addTransaction(TransactionType.DEPOSITE, amount);
            AccountOperations op = new AccountOperations();
            op.addTransaction(AccountOperations.getAccountNumber(), AccountOperations.getAccountNumber(), TransactionType.DEPOSITE, amount);
            return true;
        } else
            return false;

    }

    public void cahngePin(String pin) {
        this.pin = pin;
    }

    public boolean transfer(Account receiver, double amount) throws SQLException {
        if (amount > balance) {
            balance -= amount;
            withdraw(amount);
            receiver.deposit(amount);
            addTransaction(receiver, TransactionType.TRANSFER, amount);
            return true;
        } else {
            JOptionPane.showMessageDialog(null, "You do not have sufficient money to do withdrawal");
            return false;
        }
    }

    public boolean addTransaction(Account receiver, TransactionType type, double amount) {
        if (transactions == null)
            transactions = new ArrayList<>();

        Transcation trans = new Transcation(this, receiver, type, amount);
        transactions.add(trans);

        String sql = "INSERT INTO TRANSACTION (Amount,SenderAcc, ReceiverAcc, TransactionType,UserId) VALUES (" + amount + "," + this.accountNumber + "," + receiver.getAccountNumber() + ",'" + type.toString() + "',"+AccountOperations.getUserId()+")";
//        System.out.println(sql);
        if (MainDAL.write(sql)) {
//            addTransaction(type, amount);

            return true;
        } else
            return false;

    }

    public void addTransaction(TransactionType type, double amount) {
        if (transactions == null)
            transactions = new ArrayList<>();

        Transcation trans = new Transcation(this, type, amount);
        transactions.add(trans);
    }

    public int getAccountNumber() {
        return this.accountNumber;
    }

    public double getBalanceDefault() {
        return this.balance;
    }

    public User getUser() {
        return this.holderName;
    }

    public static double getBalance() {
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/atmsystem", "root", "123456");
            Statement statement = con.createStatement();
            int userId = 0;
            ResultSet query = statement.executeQuery("Select * from LoginSession");
            while (query.next()) {
                userId = query.getInt("UserId");
            }
            double totalBalance = 0;
            ResultSet query2 = statement.executeQuery("Select * from Account where UserId = " + userId);
            while (query2.next()) {
                totalBalance = query2.getDouble("Balance");
            }
            return totalBalance;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
