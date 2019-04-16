package userInterface;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientApp extends Application {

    private static Logger log = LogManager.getFormatterLogger(ClientApp.class.getName());

    private Controller controller = new Controller();
    private FXMLLoader fxmlLoader;

    @Override
    public void start(Stage primaryStage) throws Exception {
        fxmlLoader = new FXMLLoader(getClass().getResource("/userInterface/portfolio.fxml"));
        fxmlLoader.setController(controller);
        Scene scene = new Scene(loginScreen(primaryStage));

        primaryStage.setScene(scene);
        primaryStage.setTitle("Virtual Stock Trader");
        primaryStage.show();
        primaryStage.centerOnScreen();
        primaryStage.setOnCloseRequest(e -> System.exit(0));
    }

    private GridPane loginScreen(Stage primaryStage){
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        Text sceneTitle = new Text("Join Exchange");
        sceneTitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(sceneTitle, 0, 0, 2, 1);

        Label userName = new Label("Name:");
        grid.add(userName, 0, 1);
        TextField userTextField = new TextField();

        Label serverLabel = new Label("Server Address:");
        grid.add(serverLabel, 0, 2);
        TextField serverField = new TextField("127.0.0.1");

        Button btn = new Button("Register");
        btn.setOnAction(actionEvent -> {
            if (userTextField.getText() != null && !userTextField.getText().isEmpty()) {
                try {
                    controller.initPlayer((userTextField.getText()), serverField.getText());
                    primaryStage.setScene(showPortfolio());
                } catch (IOException e) {
                    log.error("Invalid server address.");
                    e.getMessage();
                    serverField.clear();
                    serverField.setPromptText("Invalid address...");
                }
            } else {
                userTextField.setPromptText("Name is required...");
            }
        });
        HBox hbBtn = new HBox();
        hbBtn.setAlignment(Pos.BOTTOM_CENTER);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 4);
        grid.add(userTextField, 1, 1);
        grid.add(serverField, 1,2);

        return grid;
    }

    private Scene showPortfolio() {
        Scene scene;
        try {
            Parent root = fxmlLoader.load();
            scene = new Scene(root);
            controller.updateInfo();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
        return scene;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
