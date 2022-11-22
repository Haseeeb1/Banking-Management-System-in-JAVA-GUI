package com.example.banking_managemen_system;

import javafx.animation.AnimationTimer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class transaction_history_controller implements Initializable {


    @FXML
    private TableView<history> t_table;

    @FXML
    private TableColumn<history, Double> amount;

    @FXML
    private TableColumn<history, Double> balance;

    @FXML
    private TableColumn<history, String> date;
    @FXML
    private Label Date_time_label;
    @FXML
    private TableColumn<history, String> type;


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


        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            ObservableList<history> list = FXCollections.observableArrayList();
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/accounts_table", "root", "Nightmare111");
            Statement statement = connection.createStatement();
            String sql = "SELECT * FROM transaction_data_table WHERE account_id='" + login_page_controller.list.get((login_page_controller.list.toArray().length) - 1) + "'";
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                String type = resultSet.getString("transaction_type");
                String date = resultSet.getString("transaction_datetime");
                Double amount = resultSet.getDouble("transaction_amount");
                Double balance = resultSet.getDouble("account_balance");
                list.add(new history(type, date, amount, balance));
            }

            type.setCellValueFactory(new PropertyValueFactory<history, String>("type"));
            date.setCellValueFactory(new PropertyValueFactory<history, String>("date"));
            balance.setCellValueFactory(new PropertyValueFactory<history, Double>("balance"));
            amount.setCellValueFactory(new PropertyValueFactory<history, Double>("amount"));

            t_table.setItems(list);

        } catch (Exception e) {
            System.out.println("Error");
        } finally {
            if (connection != null ) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void dashboard_changescene(ActionEvent event) throws IOException {
        login_page_controller.changescene("main_page", event);
    }

}
