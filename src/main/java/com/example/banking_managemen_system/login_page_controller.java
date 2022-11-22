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
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;
import java.util.ResourceBundle;
public class login_page_controller implements Initializable {

    @FXML
    private Label captcha, signin_error, captcha_error;

    @FXML
    private Button signup_button, forgotpass_button, login_button;

    @FXML
    private static Stage stage;

    @FXML
    private static Scene scene;

    @FXML
    private FXMLLoader fxmlLoader;

    @FXML
    private  TextField login_id;
    @FXML
    private TextField captcha_check;

    @FXML
    private PasswordField login_password;
    @FXML
    private Label Date_time_label;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                Date_time_label.setText(LocalDateTime.now().format(DateTimeFormatter.ofPattern("EEEE-MMM-d-yyyy HH:mm:ss")));
            }
        };
        timer.start();
    }


    public static void changescene(String fxml_file, ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource(fxml_file + ".fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(fxmlLoader.load());
        if (fxml_file == "login_page") {
            login_page_controller Controller = fxmlLoader.getController();
            Controller.changelabeltext();
        }
        stage.centerOnScreen();
        stage.setScene(scene);
        stage.setResizable(false);
        stage.setTitle("APOLLO");
        stage.getIcons().add(new Image("C:\\Users\\Lenovo\\IdeaProjects\\banking_managemen_system\\.idea\\images\\bank final.png"));
        stage.show();
    }

    public void changelabeltext() {
        Random random = new Random();
        char char1 = (char) (random.nextInt(26) + 'a');
        char char2 = (char) (random.nextInt(26) + 'a');
        int number = (int) (Math.random() * 100);
        int number2 = (int) (Math.random() * 100);
        captcha.setText(number + "" + char1 + "" + number2 + "" + char2);
    }

    public void signup(ActionEvent event) throws IOException {
        changescene("signup_page", event);
    }

    public void forgotpass(ActionEvent event1) throws IOException {
        changescene("forgot_password", event1);
    }

    @FXML
   public static ArrayList<String> list=new ArrayList<>();

    public void login(ActionEvent event) throws IOException {
        String id = login_id.getText();
        String pass = login_password.getText();
        String captcha_text = captcha.getText();
        String match_captcha_text = captcha_check.getText();

        Date date = new Date(System.currentTimeMillis());
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String date1 = formatter.format(date) + "";

        if (login_id.getText().isBlank() || login_password.getText().isBlank()) {
            signin_error.setText("Text-fields Cannot be empty!");
        } else if (captcha_check.getText().isBlank()) {
            captcha_error.setText("Enter verification text");
        } else if (!captcha_check.getText().equals(captcha.getText())) {
            captcha_check.setText("");
            changelabeltext();
            captcha_error.setText("ERROR! Try again");
        } else if (captcha_check.getText().equals(captcha.getText())) {
            list.add(id);
            Connection connection = null;

            try {
                connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/accounts_table", "root", "Nightmare111");
                Statement statement = connection.createStatement();

                String sql = "SELECT * FROM accounts_data_table WHERE (account_id='" + id + "' AND account_pass='" + pass + "')";
                ResultSet resultSet = statement.executeQuery(sql);

                String sql2 = "  UPDATE accounts_data_table SET account_lastlogin = '" + date1 + "' WHERE account_id = '" + id + "'";

                if (resultSet.next()) {
                    String sec=resultSet.getString("account_lastlogin");
                    try {
                        String query1 = "UPDATE accounts_data_table SET accounts_seclastlogin='" + sec + "' where account_id='" + id + "'";
                        PreparedStatement preparedStmt1 = connection.prepareStatement(query1);
                        preparedStmt1.executeUpdate();
                    }
                    catch(Exception e){

                    }
                    String query = "UPDATE accounts_data_table SET account_lastlogin='" + date1 + "' where account_id='" + id + "'";
                    PreparedStatement preparedStmt = connection.prepareStatement(query);
                    preparedStmt.executeUpdate();

                    changescene("main_page", event);
                    resultSet.close();

                } else {
                    signin_error.setText("Invalid username or password");
                    captcha_check.setText("");
                    changelabeltext();
                    login_id.setText("");
                    login_password.setText("");
                }
            } catch (SQLException e) {
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
    }
}



