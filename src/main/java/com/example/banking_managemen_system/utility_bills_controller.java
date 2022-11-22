package com.example.banking_managemen_system;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.skin.LabeledSkinBase;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.ResourceBundle;

public class utility_bills_controller implements Initializable {

    @FXML
    private TextField bills_reg;

    @FXML
    private TextField bills_amount;

    @FXML
    private ChoiceBox<String> bills_choice;

    @FXML
    private Button enter_bills,goto_dashboard,reset_bills;

    @FXML
    private PasswordField bills_pin;
    @FXML
    private Label Date_time_label;
    @FXML
    private Label bills_error;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                Date_time_label
                        .setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEEE-MMM-d-yyyy HH:mm:ss")));
            }
        };
        timer.start();
        bills_choice.getItems().addAll("Electricity", "Gas","Internet","Water Supply");
        bills_choice.setValue("Electricity");
    }

    public void enterbills(ActionEvent event){

        String id=bills_reg.getText();

        if(bills_amount.getText().isBlank() || bills_pin.getText().isBlank() || bills_reg.getText().isBlank()) {
            bills_error.setText("Text-Fields cannot be empty");
        }

        else if(bills_amount.getText().matches("[0-9]+")){
            Connection connection = null;
            try{
                PreparedStatement psInsert=null;
                int pin=Integer.parseInt(bills_pin.getText());
                double amount=Double.parseDouble(bills_amount.getText());
                try{
                    connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/accounts_table", "root", "Nightmare111");
                    Statement statement = connection.createStatement();

                    String sql = "SELECT * FROM accounts_data_table WHERE (account_bills_reg='" + id + "' AND account_pin='" + pin + "')";
                    ResultSet resultSet = statement.executeQuery(sql);

                    if(resultSet.next()){


                        Date date = new Date(System.currentTimeMillis());
                        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                        String date1 = formatter.format(date) + "";

                        double curamount = resultSet.getDouble("account_balance");

                        if (curamount > amount) {

                            psInsert = connection.prepareStatement("INSERT INTO transaction_data_table(account_id, transaction_type," +
                                    " transaction_amount, transaction_datetime,account_balance) VALUES (? ,? ,? ,?,?)");
                            psInsert.setString(1, resultSet.getString("account_id"));
                            if(bills_choice.getValue().equals("Electricity")) {
                                psInsert.setString(2, "Electricity-bill");
                                String query1 = "UPDATE accounts_data_table SET account_updates='" + resultSet.getString("account_updates")+", Electricity Department:; Bill paid✓ Rs "+amount  + "' WHERE account_bills_reg='" + id + "'";
                                PreparedStatement preparedStmt1 = connection.prepareStatement(query1);
                                preparedStmt1.executeUpdate();
                            }
                            else if(bills_choice.getValue().equals("Gas")) {
                                psInsert.setString(2, "Gas-bill");
                                String query1 = "UPDATE accounts_data_table SET account_updates='" + resultSet.getString("account_updates")+", Gas Department:; Bill paid✓ Rs"+amount  + "' WHERE account_bills_reg='" + id + "'";
                                PreparedStatement preparedStmt1 = connection.prepareStatement(query1);
                                preparedStmt1.executeUpdate();
                            }
                            else if(bills_choice.getValue().equals("Internet")) {
                                psInsert.setString(2, "Internet-bill");
                                String query1 = "UPDATE accounts_data_table SET account_updates='" + resultSet.getString("account_updates")+ ", Internet Department:; Bill paid✓ Rs"+amount + "' WHERE account_bills_reg='" + id + "'";
                                PreparedStatement preparedStmt1 = connection.prepareStatement(query1);
                                preparedStmt1.executeUpdate();
                            }
                            else if(bills_choice.getValue().equals("Water Supply")) {
                                psInsert.setString(2, "Water Supply-bill");
                                String query1 = "UPDATE accounts_data_table SET account_updates='" + resultSet.getString("account_updates")+ ", Water Suppyly Department:; Bill paid✓ Rs"+amount + "' WHERE account_bills_reg='" + id + "'";
                                PreparedStatement preparedStmt1 = connection.prepareStatement(query1);
                                preparedStmt1.executeUpdate();
                            }

                            psInsert.setDouble(3, amount);
                            psInsert.setString(4, date1);
                            psInsert.setDouble(5, (curamount-amount));
                            psInsert.executeUpdate();

                            String query2 = "UPDATE accounts_data_table SET account_balance='" + (curamount-amount)  + "' WHERE account_bills_reg='" + id + "'";
                            PreparedStatement preparedStmt2 = connection.prepareStatement(query2);
                            preparedStmt2.executeUpdate();

                            bills_error.setText("Bill Payment sucessfull");
                            bills_amount.setText("");
                            bills_pin.setText("");
                            bills_reg.setText("");
                            bills_choice.setValue("Electricity");
                        }

                        else{
                            bills_error.setText("Not enough balance.");
                            bills_amount.setText("");
                        }
                    }

                    else{
                        bills_error.setText("Incorrect PIN or Reg.No");
                        bills_reg.setText("");
                        bills_pin.setText("");
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
            bills_error.setText("Only numbers allowed for pin and amount");
            bills_amount.setText("");
            bills_pin.setText("");
        }

    }

    public void dashboard_changescene(ActionEvent event) throws IOException {
        login_page_controller.changescene("main_page", event);
    }

    public void resetbills(ActionEvent event)throws  IOException{
        bills_reg.setText("");
        bills_choice.setValue("Electricity");
        bills_pin.setText("");
        bills_amount.setText("");
    }




}