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
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class forgot_password_controller implements Initializable {
    @FXML
    private TextField forgot_id,forgot_pet;

    @FXML
    private Label Date_time_label;
    @FXML
    private Button forgot_submit,forgot_signin;

    @FXML
    private Label forgot_showpass,error_label;
    public void forgot_change(ActionEvent event)throws IOException{
        login_page_controller.changescene("login_page",event);
    }

    public void submitinfo(ActionEvent event)throws IOException{

        if(forgot_id.getText().isBlank() || forgot_pet.getText().isBlank()){
            error_label.setText("Text-fields cannot be empty");
        }

        else {
            String id=forgot_id.getText();
            String pet=forgot_pet.getText();
            Connection connection=null;

            try {
                connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/accounts_table", "root", "Nightmare111");
                Statement statement = connection.createStatement();

                String sql = "SELECT * FROM accounts_data_table WHERE (account_id='" + id + "' AND account_favpet='" + pet + "')";
                ResultSet resultSet = statement.executeQuery(sql);

                if (resultSet.next()) {
                    String sql2="SELECT account_pass FROM accounts_data_table WHERE (account_id='" + id + "' AND account_favpet='" + pet + "')";
                    forgot_showpass.setText(resultSet.getString("account_pass"));
                    error_label.setText("");
                }
                else{
                    error_label.setText("Invalid Details");
                }
                }
            catch (SQLException e) {
                e.printStackTrace();
            }

            finally {
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
