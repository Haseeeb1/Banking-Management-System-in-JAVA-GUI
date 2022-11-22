package com.example.banking_managemen_system;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class withdraw_page_controller implements Initializable {
    @FXML
    private Button goto_dashboard;
    @FXML
    private TextField withdraw_amount;

    @FXML
    private Button withdraw_enter;

    @FXML
    private Label withdraw_error;

    @FXML
    private PasswordField withdraw_pin;
    @FXML
    private Label Date_time_label;
    @FXML
    private Button withdraw_reset;

    @FXML
    private TextField withdraw_username;

    @FXML
    void enterwithdraw(ActionEvent event) {
        String id=withdraw_username.getText();

        if(withdraw_amount.getText().isBlank() || withdraw_pin.getText().isBlank() || withdraw_username.getText().isBlank()){
            withdraw_error.setText("Text-fields cannot be empty");
        }

        else if(!login_page_controller.list.get((login_page_controller.list.toArray().length)-1).equals(id)){
            withdraw_error.setText("Not your id");
        }

        else if(Double.parseDouble(withdraw_amount.getText())>25000){
            withdraw_error.setText("Limit per transaction is 25000.");
        }


        else if(withdraw_amount.getText().matches("[0-9]+")){
            Connection connection = null;
            try{
                PreparedStatement psInsert=null;
                int pin=Integer.parseInt(withdraw_pin.getText());
                double amount=Double.parseDouble(withdraw_amount.getText());
                try{
                    connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/accounts_table", "root", "Nightmare111");
                    Statement statement = connection.createStatement();

                    String sql = "SELECT * FROM accounts_data_table WHERE (account_id='" + id + "' AND account_pin='" + pin + "')";
                    ResultSet resultSet = statement.executeQuery(sql);


                    if(resultSet.next()) {

                        Date date = new Date(System.currentTimeMillis());
                        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                        String date1 = formatter.format(date) + "";

                        double curamount = resultSet.getDouble("account_balance");

                        if (curamount > amount) {
                            psInsert = connection.prepareStatement("INSERT INTO transaction_data_table(account_id, transaction_type," +
                                    " transaction_amount, transaction_datetime,account_balance) VALUES (? ,? ,? ,?,?)");
                            psInsert.setString(1, id);
                            psInsert.setString(2, "Cash Withdrawl");
                            psInsert.setDouble(3, amount);
                            psInsert.setString(4, date1);
                            psInsert.setDouble(5, (curamount-amount));
                            psInsert.executeUpdate();

                            String query1 = "UPDATE accounts_data_table SET account_balance='"
                                    + (curamount - amount) + "', account_lastw_date='" + date1 +
                                    "',account_lastw_amount='" + amount + "' WHERE account_id='" + id + "'";

                            PreparedStatement preparedStmt1 = connection.prepareStatement(query1);
                            preparedStmt1.executeUpdate();
                            withdraw_error.setText("Amount withdrawl sucessfull");
                            withdraw_amount.setText("");
                            withdraw_pin.setText("");
                            withdraw_username.setText("");
                        }

                        else{
                            withdraw_error.setText("Not enough balance.");
                            withdraw_amount.setText("");
                            withdraw_pin.setText("");
                            withdraw_username.setText("");

                        }
                    }
                    else{
                        withdraw_error.setText("Incorrect id or PIN");
                        withdraw_username.setText("");
                        withdraw_pin.setText("");
                    }

                }
                catch (SQLException e){
                    e.printStackTrace();
                }
                finally {
                    if(psInsert!=null){
                        try{
                            psInsert.close();
                        }
                        catch(SQLException e){
                            e.printStackTrace();
                        }
                    }
                    if(connection!=null){
                        try{
                            connection.close();
                        }
                        catch(SQLException e){
                            e.printStackTrace();
                        }
                    }
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        else{
            withdraw_error.setText("Only numbers allowed for pin and amount");
            withdraw_amount.setText("");
            withdraw_pin.setText("");
        }
    }

    @FXML
    void resetwithdraw(ActionEvent event) {
          withdraw_amount.setText("");
          withdraw_pin.setText("");
          withdraw_username.setText("");
    }

    public void dashboard_changescene(ActionEvent event) throws IOException {
        login_page_controller.changescene("main_page", event);
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                Date_time_label
                        .setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEEE-MMM-d-yyyy HH:mm:ss")));
            }
        };
        timer.start();
    }
}


