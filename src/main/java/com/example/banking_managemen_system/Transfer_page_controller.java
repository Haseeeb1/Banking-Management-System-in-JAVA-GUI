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

public class Transfer_page_controller implements Initializable {
  @FXML
    private Button goto_dashboard;
    @FXML
    private TextField transfer_amount;

    @FXML
    private Button transfer_enter;
    @FXML
    private Label Date_time_label;
    @FXML
    private Label transfer_error;

    @FXML
    private PasswordField transfer_pin;

    @FXML
    private TextField transfer_reciepient;

    @FXML
    private Button transfer_reset;

    @FXML
    private TextField transfer_username;

    @FXML
    public void entertransfer(ActionEvent event) {
        String id=transfer_username.getText();
        String sendto=transfer_reciepient.getText();

        if(transfer_amount.getText().isBlank() || transfer_pin.getText().isBlank() || transfer_username.getText().isBlank() || transfer_reciepient.getText().isBlank()){
            transfer_error.setText("Text-fields cannot be empty");
        }

        else if(!login_page_controller.list.get((login_page_controller.list.toArray().length)-1).equals(id)){
            transfer_error.setText("Not your id");
        }

        else if(Double.parseDouble(transfer_amount.getText())>25000){
            transfer_error.setText("Limit per transaction is 25000.");
        }

        else if(transfer_amount.getText().matches("[0-9]+")){
            Connection connection = null;
            try{
                PreparedStatement psInsert=null;
                int pin=Integer.parseInt(transfer_pin.getText());
                double amount=Double.parseDouble(transfer_amount.getText());
                try{
                    connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/accounts_table", "root", "Nightmare111");
                    Statement statement = connection.createStatement();

                    String sql = "SELECT * FROM accounts_data_table WHERE (account_id='" + id + "' AND account_pin='" + pin + "')";
                    ResultSet resultSet = statement.executeQuery(sql);

                    Statement statement1 = connection.createStatement();
                    String sql1 = "SELECT * FROM accounts_data_table WHERE account_id='" + sendto + "'";
                    ResultSet resultSet1= statement1.executeQuery(sql1);

                    if(resultSet.next() && resultSet1.next()){

                        Date date = new Date(System.currentTimeMillis());
                        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                        String date1 = formatter.format(date) + "";

                        double curamount = resultSet.getDouble("account_balance");

                        double rec_curamount=resultSet1.getDouble("account_balance");

                        if (curamount > amount) {


                            psInsert = connection.prepareStatement("INSERT INTO transaction_data_table(account_id, transaction_type," +
                                    " transaction_amount, transaction_datetime,account_balance) VALUES (? ,? ,? ,?,?)");
                            psInsert.setString(1, id);
                            psInsert.setString(2, "Cash Transfer");
                            psInsert.setDouble(3, amount);
                            psInsert.setString(4, date1);
                            psInsert.setDouble(5, (curamount-amount));
                            psInsert.executeUpdate();

                            String query1 = "UPDATE accounts_data_table SET account_balance='" + (curamount-amount)  + "' WHERE account_id='" + id + "'";
                            PreparedStatement preparedStmt1 = connection.prepareStatement(query1);
                            preparedStmt1.executeUpdate();

                            String query2 = "UPDATE accounts_data_table SET account_balance='" + (rec_curamount+amount)  + "' WHERE account_id='" + sendto + "'";
                            PreparedStatement preparedStmt2 = connection.prepareStatement(query2);
                            preparedStmt2.executeUpdate();

                            String query3 = "UPDATE accounts_data_table SET account_updates='" + resultSet1.getString("account_updates")+", Received Rs."+amount+" from "+resultSet.getString("account_id")  + "' WHERE account_id='" + sendto + "'";
                            PreparedStatement preparedStmt3 = connection.prepareStatement(query3);
                            preparedStmt3.executeUpdate();

                            String query4 = "UPDATE accounts_data_table SET account_updates='" + resultSet.getString("account_updates")+", Sent Rs."+amount+" to "+resultSet1.getString("account_id")+ "' WHERE account_id='" + id + "'";
                            PreparedStatement preparedStmt4 = connection.prepareStatement(query4);
                            preparedStmt4.executeUpdate();

                            transfer_error.setText("Amount Transfer sucessfull");
                            transfer_amount.setText("");
                            transfer_pin.setText("");
                            transfer_username.setText("");
                            transfer_reciepient.setText("");
                        }

                        else{
                            transfer_error.setText("Not enough balance.");
                            transfer_amount.setText("");
                        }
                    }
                    else{
                        transfer_error.setText("Incorrect id/PIN or reciepient");
                        transfer_username.setText("");
                        transfer_pin.setText("");
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
            transfer_error.setText("Only numbers allowed for pin and amount");
            transfer_amount.setText("");
            transfer_pin.setText("");
        }
    }

    @FXML
    void resettransfer(ActionEvent event) {
        transfer_amount.setText("");
        transfer_pin.setText("");
        transfer_reciepient.setText("");
        transfer_username.setText("");
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


