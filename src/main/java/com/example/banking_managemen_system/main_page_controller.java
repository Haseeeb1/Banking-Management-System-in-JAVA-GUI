package com.example.banking_managemen_system;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.ResourceBundle;
public class main_page_controller implements  Initializable{
    @FXML
    private Button logout_main, transfer_main, deposit_main, _withdraw_main, bills_main,transaction;

    @FXML
    private Label lastlogin_main, username_main,main_current,main_lastw,main_lastwd,main_lastd,main_lastdd;

    @FXML
    private TextArea main_textarea;

    @FXML
    private ImageView bell;

    @FXML
    private Label Date_time_label;
    @FXML
    private ImageView img;
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


        bell.setOnMouseEntered(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                main_textarea.setVisible(true);
                img.setVisible(true);
                bell.setVisible(false);
            }
        });

        main_textarea.setOnMouseExited(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                main_textarea.setVisible(false);
                img.setVisible(false);
                bell.setVisible(true);
            }
        });
        bell.setVisible(true);
        img.setVisible(false);
        main_textarea.setVisible(false);
        Connection connection=null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/accounts_table", "root", "Nightmare111");
            Statement statement = connection.createStatement();

            String sql = "SELECT * FROM accounts_data_table WHERE account_id='" + login_page_controller.list.get((login_page_controller.list.toArray().length)-1) + "'";
            ResultSet resultSet = statement.executeQuery(sql);
            username_main.setText(login_page_controller.list.get((login_page_controller.list.toArray().length)-1));

            if (resultSet.next()) {

                String n=resultSet.getString("account_updates");

                if(n==null){
                    main_textarea.setText("No new notifications");
                }
                else {
                    for (int i = 0; i < n.length(); i++) {
                        if (n.charAt(i) == ',' || n.charAt(i) == ';') {
                            main_textarea.appendText("\n");
                        } else {
                            main_textarea.appendText(String.valueOf(n.charAt(i)));
                        }
                    }
                }

                if(main_textarea.getText().length()>250){
                    String query5 = "UPDATE accounts_data_table SET account_updates='" + "ALL:" + "' WHERE account_id='" + login_page_controller.list.get((login_page_controller.list.toArray().length)-1) + "'";
                    PreparedStatement preparedStmt5 = connection.prepareStatement(query5);
                    preparedStmt5.executeUpdate();
                }

                    main_current.setText(resultSet.getDouble("account_balance")+"");
                    main_lastd.setText(resultSet.getString("account_lastd_amount"));
                    main_lastdd.setText(resultSet.getString("account_lastd_date"));
                    main_lastw.setText(resultSet.getString("account_lastw_amount"));
                    main_lastwd.setText(resultSet.getString("account_lastw_date"));

                    if (resultSet.getString("accounts_seclastlogin") != null){
                        lastlogin_main.setText(resultSet.getString("accounts_seclastlogin"));
                    }

                    else if(resultSet.getString("accounts_seclastlogin") == null){
                        lastlogin_main.setText(resultSet.getString(""));
                    }
                }
            }

        catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        }

    public void changeto_withdraw(ActionEvent event) throws IOException {
        login_page_controller.changescene("withdraw_page", event);
    }
    public void changeto_deposit(ActionEvent event) throws IOException {
        login_page_controller.changescene("deposit_page", event);
    }

    public void changeto_transfer(ActionEvent event) throws IOException {
        login_page_controller.changescene("transfer_page", event);
    }
    public void changeto_bills(ActionEvent event) throws IOException {
        login_page_controller.changescene("utilitybills_page", event);
    }

    public void changeto_login(ActionEvent event) throws IOException {
        Alert alert =new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("Are you sure you want to log out?");
        alert.setResizable(false);

        Optional<ButtonType> result = alert.showAndWait();
        if(!result.isPresent()){}
        else if(result.get() == ButtonType.OK){
            login_page_controller.changescene("login_page", event);
        }
        else if(result.get() == ButtonType.CANCEL){}

    }
    public void changeto_transaction(ActionEvent event) throws IOException {
        login_page_controller.changescene("transaction_history", event);
    }
}
