package com.example.banking_managemen_system;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class Deposit_page_controller implements Initializable {
    @FXML
    private TextField deposit_amount,deposit_username;

    @FXML
    private Button deposit_enter;

    @FXML
    private PasswordField deposit_pin;

    @FXML
    private Button deposit_reset;

    @FXML
    private Button goto_dashboard;

    @FXML
    private Label deposit_error;

    @FXML
    private Label Date_time_label;

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                Date_time_label
                        .setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEEE-MMM-d-yyyy HH:mm:ss")));
            }
        };
        timer.start();
    }


    public void dashboard_changescene(ActionEvent event) throws IOException {
        login_page_controller.changescene("main_page", event);
    }

    public void enterdeposit(ActionEvent event)throws IOException{
        String id=deposit_username.getText();

        if(deposit_amount.getText().isBlank() || deposit_pin.getText().isBlank() || deposit_username.getText().isBlank()){
            deposit_error.setText("Text-fields cannot be empty");
        }

        else if(!login_page_controller.list.get((login_page_controller.list.toArray().length)-1).equals(id)){
            deposit_error.setText("Not your id");
        }

        else if(Double.parseDouble(deposit_amount.getText())>25000){
           deposit_error.setText("Limit per transaction is 25000.");
        }

        else if(deposit_amount.getText().matches("[0-9]+")){
            Connection connection = null;
            try{
                PreparedStatement psInsert=null;
                int pin=Integer.parseInt(deposit_pin.getText());
                double amount=Double.parseDouble(deposit_amount.getText());
                try{
                    connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/accounts_table", "root", "Nightmare111");
                    Statement statement = connection.createStatement();

                    String sql = "SELECT * FROM accounts_data_table WHERE (account_id='" + id + "' AND account_pin='" + pin + "')";
                    ResultSet resultSet = statement.executeQuery(sql);
                 if(resultSet.next()){

                     Date date = new Date(System.currentTimeMillis());
                     SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                     String date1 = formatter.format(date) + "";

                     double curamount=resultSet.getDouble("account_balance");



                     psInsert=connection.prepareStatement("INSERT INTO transaction_data_table(account_id, transaction_type," +
                             " transaction_amount, transaction_datetime,account_balance) VALUES (? ,? ,? ,?,?)");
                     psInsert.setString(1,id);
                     psInsert.setString(2,"Cash Deposit");
                     psInsert.setDouble(3,amount);
                     psInsert.setString(4,date1);
                     psInsert.setDouble(5,(amount+curamount));
                     psInsert.executeUpdate();

                     String query1 = "UPDATE accounts_data_table SET account_balance='"
                             + (curamount+amount) + "', account_lastd_date='" + date1 +
                             "',account_lastd_amount='"+amount+"' WHERE account_id='" + id + "'";

                     PreparedStatement preparedStmt1 = connection.prepareStatement(query1);
                     preparedStmt1.executeUpdate();
                     deposit_error.setText("Amount deposit sucessfull");
                     deposit_amount.setText("");
                     deposit_pin.setText("");
                     deposit_username.setText("");
                 }

                 else{
                     deposit_error.setText("Incorrect id or PIN");
                     deposit_username.setText("");
                     deposit_pin.setText("");
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
            deposit_error.setText("Only numbers allowed for pin and amount");
        }
    }

    public void reset(ActionEvent event)throws IOException{
        deposit_username.setText("");
        deposit_pin.setText("");
        deposit_amount.setText("");
    }

}
