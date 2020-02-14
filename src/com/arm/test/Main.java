package com.arm.test;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader l = new FXMLLoader(getClass().getResource("sample.fxml"));
        Parent root = l.load();
        primaryStage.setTitle("NAVin Map Manager");
        primaryStage.setScene(new Scene(root, 1200, 700));
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(650);
        primaryStage.show();
        ((Controller)l.getController()).curr_stg=primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}