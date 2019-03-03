package userInterface;

import javafx.scene.text.Font;
import javafx.scene.text.Text;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class ClientApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Pane pane = new Pane();

        initScreen(pane);
        Scene scene = new Scene(pane);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Virtual Stock Trader");
        primaryStage.setWidth(600);
        primaryStage.setHeight(600);
        primaryStage.show();
    }

    private void initScreen(Pane pane) {
        Text welcomeText = new Text("Welcome to the Virtual Stock Exchange!");
        welcomeText.setX(50);
        welcomeText.setY(100);
        welcomeText.setFont(new Font(24));
        pane.getChildren().add(welcomeText);
    }
}
