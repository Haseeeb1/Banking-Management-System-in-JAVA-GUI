package com.example.banking_managemen_system;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.ConnectionPendingException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.sql.*;
import java.util.ResourceBundle;

public class signup_page_controller implements Initializable {
    @FXML
    private Button signin_switch_button;

    @FXML
    private  static Stage stage;

    @FXML
    private FXMLLoader fxmlLoader;

    @FXML
    private static Scene scene;

    @FXML
    private TextField signup_favpet,signup_id,signup_reg;

    @FXML
    private PasswordField signup_pass,signup_confirmpass,signup_pin;

    @FXML
    private Label Date_time_label;
    @FXML
    private Label signup_error,password_error;

    public void signinswitch(ActionEvent event)throws IOException{
       login_page_controller.changescene("login_page",event);
    }
    public void signup_button(ActionEvent event)throws SQLException{

        String id = signup_id.getText();
        String favpet = signup_favpet.getText();
        String pass = signup_pass.getText();
        String confirmpass = signup_confirmpass.getText();
        String reg=signup_reg.getText();
        double initial_balance=0;
        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String date1=formatter.format(date)+"";

            if (signup_id.getText().length()==0 || signup_favpet.getText().length()==0 || signup_pass.getText().length()==0 ||
                    signup_confirmpass.getText().length()==0 || signup_pin.getText().length()==0){
                signup_error.setText("Text-Field cannot be empty");
            }

           else  if(!signup_id.getText().matches(".+@gmail.com")){
              signup_error.setText("Invalid username:Should be a gmail account");
              signup_id.setText("");
            }

            else if(signup_reg.getText().length()==7 && signup_reg.getText().matches("[0-9]+")){
            if (!signup_confirmpass.getText().equals(signup_pass.getText())) {
                password_error.setText("Passwords do not match");
                signup_error.setText("");
            } else if (signup_pass.getText().length() < 6) {
                password_error.setText("Too short");
                signup_error.setText("");
                signup_pass.setText("");
                signup_confirmpass.setText("");
            } else if (signup_pass.getText().length() > 20) {
                password_error.setText("Too long");
                signup_error.setText("");
                signup_pass.setText("");
                signup_confirmpass.setText("");
            } else if ((signup_pin.getText().length() == 4)) {
                Connection connection = null;
                PreparedStatement psInsert = null;
                PreparedStatement pscheckuserexists = null;
                ResultSet resultSet = null;
                try {
                    int PIN = Integer.parseInt(signup_pin.getText());
                    connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/accounts_table", "root", "Nightmare111");
                    pscheckuserexists = connection.prepareStatement("SELECT * FROM accounts_data_table WHERE account_id = ?");
                    pscheckuserexists.setString(1, id);
                    resultSet = pscheckuserexists.executeQuery();

                    if (resultSet.isBeforeFirst()) {
                        System.out.println("already exist");
                        signup_error.setText("USER already exists. Try another ");
                        password_error.setText("");
                        signup_id.setText("");
                    } else {
                        psInsert = connection.prepareStatement("INSERT INTO accounts_data_table (account_id , account_pass , account_pin ," +
                                " account_balance ,account_favpet,account_rceatedon,account_bills_reg) VALUES (? ,? ,? ,?,?,?,?)");
                        psInsert.setString(1, id);
                        psInsert.setString(2, pass);
                        psInsert.setInt(3, PIN);
                        psInsert.setDouble(4, initial_balance);
                        psInsert.setString(5, favpet);
                        psInsert.setString(6, date1);
                        psInsert.setString(7,reg);
                        psInsert.executeUpdate();

                        signup_error.setText("SIGN-UP successful");
                        signup_id.setText("");
                        signup_pin.setText("");
                        signup_pass.setText("");
                        signup_confirmpass.setText("");
                        signup_pin.setText("");
                        signup_favpet.setText("");
                        signup_reg.setText("");
                    }
                } catch (SQLException e) {
                    signup_pin.setText("");
                    signup_error.setText("Only Numbers allowed");
                    password_error.setText("");
                    e.printStackTrace();
                } finally {
                    if (resultSet != null) {
                        try {
                            resultSet.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    if (pscheckuserexists != null) {
                        try {
                            pscheckuserexists.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                    if (psInsert != null) {
                        try {
                            psInsert.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }

                    if (connection != null) {
                        try {
                            connection.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

                else{
                    signup_error.setText("PIN cannot be more than 4 digits");
                    signup_pin.setText("");
                    password_error.setText("");
                }
            }

            else{
                signup_error.setText("invalid Registeration number");
                password_error.setText("");
                signup_reg.setText("");
            }
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














