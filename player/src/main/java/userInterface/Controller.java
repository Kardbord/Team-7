package userInterface;

import gateway.TopOfBookEntry;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import player.Player;

import java.io.IOException;

public class Controller {

    private static Logger log = LogManager.getFormatterLogger(Controller.class.getName());

    private Player player;
    private ObservableList<String> symbols = FXCollections.observableArrayList();
    private ObservableList<TopOfBookEntry> orderBookList = FXCollections.observableArrayList();
    private String selectedSymbol;
    private static final int TOP_OF_BOOK_REFRESH_RATE = 1500;

    @FXML private MenuItem quit;
    @FXML private Label serverAddress;
    @FXML private GridPane portfolio;
    @FXML private Label nameLabel;
    @FXML private Label idLabel;
    @FXML private Label cashLabel;
    @FXML private ListView<String> orderBookAsks;
    @FXML private ListView<String> orderBookBids;
    @FXML private ListView<String> symbolList;
    @FXML private RadioButton buyBtn;
    @FXML private RadioButton sellBtn;
    @FXML private RadioButton cancelOrderBtn;
    @FXML private TextField orderQty;
    @FXML private TextField orderPrice;
    @FXML private Button orderBtn;
    @FXML private ToggleGroup orderTypes;



    public Controller() {
        symbols.add("MSFT");
        symbols.add("AMZN");
        symbols.add("GOOG");
    }

    @FXML
    public void initialize() {
        quit.setOnAction(e -> System.exit(0));
        orderBtn.setOnAction(this::submitOrder);
        symbolList.setItems(symbols);
        symbolList.getSelectionModel().selectFirst();
        selectedSymbol = symbolList.getSelectionModel().getSelectedItem();
        symbolList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            selectedSymbol = newValue;
        });
        updateTopOfBook();
    }

    void updateInfo() {
        this.serverAddress.setText(player.getServerSocketAddress().toString());
        this.nameLabel.setText("Name: " + player.getName());
        this.idLabel.setText("ID: " + player.getPlayerId());
        this.cashLabel.setText("Cash: " + player.getCash());

    }


    void setPlayer(String name, String server) throws IOException{
        this.player = new Player(name, server);
    }

    @FXML
    private void submitOrder(ActionEvent e) {
        try {
            int price = Integer.parseInt(orderPrice.getText());
            int quantity = Integer.parseInt(orderQty.getText());
            if (player.getCash() - price < 0) {
                showAlert("Insufficient Funds", "You do not have enough funds to place this order");
            } else if (selectedSymbol == null ) {
                showAlert("No symbol selected", "Please Select a Symbol");
            } else{
                player.submitOrder((short) player.getPlayerId(), (short) 1, (short) quantity, price, selectedSymbol);
                updateInfo();
            }
        } catch (NumberFormatException err) {
            showAlert("Invalid value entered ", err.getMessage());
        }
    }

    @FXML
    public void setOrderType() {

    }

    private void updateTopOfBook() {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(TOP_OF_BOOK_REFRESH_RATE);
                    orderBookList.add(player.getTopOfBookEntry(selectedSymbol));
                    orderBookList.forEach(entry -> {
                        String askPrice = Integer.toString(entry.getAskPrice());
                        String askQty = Integer.toString(entry.getAskQuantity());
                        orderBookAsks.getItems().add(askPrice + " " + askQty);
                    });

                } catch (InterruptedException e) {
                    log.error("Top of Book request interrupted -> %s", e.getMessage());
                } catch (NullPointerException e) {
                    log.error("No Top of Book data for selected symbol: %s", selectedSymbol);
                }
            }
        }).start();

    }
    private void showAlert(String header, String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING, msg);
        alert.setHeaderText(header);
        alert.showAndWait();
        alert.setOnCloseRequest(e -> alert.hide());
    }
}
