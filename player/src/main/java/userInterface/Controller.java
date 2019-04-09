package userInterface;

import communicators.UdpCommunicator;
import gateway.TopOfBookEntry;
import javafx.application.Platform;
import javafx.collections.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import player.Player;
import portfolio.PortfolioEntry;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.DatagramChannel;

import messages.SubmitOrderMessage.OrderType;

public class Controller {

    private static Logger log = LogManager.getFormatterLogger(Controller.class.getName());

    private Player player;
    private ObservableList<String> symbols = FXCollections.observableArrayList();
    private String selectedSymbol;
    private OrderType orderType;
    private ObservableMap<String, TopOfBookEntry> topOfBookMap;
    private ObservableMap<String, PortfolioEntry> portfolioMap;
//    private int quantity;
//    private int price;


    @FXML private MenuItem quit;
    @FXML private Label serverAddress;
    @FXML private Label nameLabel;
    @FXML private Label idLabel;
    @FXML private Label cashLabel;
    @FXML private ListView<String> orderBookAsks;
    @FXML private ListView<String> orderBookBids;
    @FXML private ListView<String> symbolList;
    @FXML private ListView<String> portfolioList;
    @FXML private ListView<String> restingOrdersList;
    @FXML private RadioButton buyBtn;
    @FXML private RadioButton sellBtn;
    @FXML private RadioButton cancelOrderBtn;
    @FXML private TextField orderQty;
    @FXML private TextField orderPrice;
    @FXML private Button orderBtn;
    @FXML private ToggleGroup orderTypes;



    public Controller() {
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
            orderBookAsks.getItems().clear();
        });
        orderTypes.selectedToggleProperty().addListener(((obs_v, old_toggle, new_toggle) -> {
            if (orderTypes.getSelectedToggle() != null) {
                setOrderType(new_toggle);
                fillOrderInfo();
            }
        }));
        orderBookAsks.getSelectionModel().selectedItemProperty().addListener(((observable, oldVal, newVal) -> {
            fillOrderInfo();
        }));
        orderType = OrderType.BUY;
    }


    void initPlayer(String name, String server) throws IOException{
        this.player = new Player(name, new UdpCommunicator(DatagramChannel.open(), new InetSocketAddress(0)), server);
        topOfBookMap = player.getTopOfBookMap();
        topOfBookMap.addListener((MapChangeListener<String, TopOfBookEntry>) change -> updateTopOfBook());

        portfolioMap = player.getPortfolioMap();
        portfolioMap.addListener((MapChangeListener<String, PortfolioEntry>) change -> {
            updatePortfolio();
            updateInfo();
        });

        symbols = player.getSymbolList();
        symbols.addListener((ListChangeListener<String>) change -> updateSymbols());
    }

    // This still needs work
    private void updateSymbols() {
        Platform.runLater(() -> symbols.forEach(s -> {
            if (!symbolList.getItems().contains(s))
                symbolList.getItems().add(s);
        }));

    }
    void updateInfo() {
        this.serverAddress.setText(player.getServerSocketAddress().toString());
        this.nameLabel.setText("Name: " + player.getName());
        this.idLabel.setText("ID: " + player.getPlayerId());
        this.cashLabel.setText("Cash: " + player.getCash());

    }

    private void updatePortfolio() {
        Platform.runLater(() -> {
            try {
                portfolioList.getItems().clear();
                log.info("Updating Portfolio");
                portfolioMap.forEach((k, v) -> {
                    log.info("%s -> %s", k, v.toString());
                    portfolioList.getItems().add(v.toString());
                });
            } catch (NullPointerException e) {
                log.error("Failed to update portfolio: %s", e.getMessage());
            }
        });

    }

    @FXML
    private void submitOrder(ActionEvent e) {
        try {
            int price = Integer.parseInt(orderPrice.getText());
            int quantity = Integer.parseInt(orderQty.getText());
            if (player.getCash() - (price * quantity) < 0) {
                showAlert("Insufficient Funds", "You do not have enough funds to place this order");
            } else if (selectedSymbol == null ) {
                showAlert("No symbol selected", "Please Select a Symbol");
            } else{
                player.submitOrder((short) player.getPlayerId(), orderType, (short) quantity, price, selectedSymbol);
            }
        } catch (NumberFormatException err) {
            showAlert("Invalid value entered ", err.getMessage());
        } catch (NullPointerException err) {
            log.error(err.getMessage());
        }
    }

    private void setOrderType(Toggle selected) {
        if (selected == buyBtn) {
            orderType = OrderType.BUY;
        } else if (selected == sellBtn) {
            orderType = OrderType.SELL;
        }
    }

    private void updateTopOfBook() {
        Platform.runLater(() -> {
            try {
                orderBookAsks.getItems().add(topOfBookMap.get(selectedSymbol).toString());
            } catch (NullPointerException e) {
                log.error(e.getMessage());
            }
        });
    }

    private void fillOrderInfo() {
        int quantity = 0, price = 0;

        if (orderType == OrderType.BUY) {
            quantity = topOfBookMap.get(selectedSymbol).getAskQuantity();
            price = topOfBookMap.get(selectedSymbol).getAskPrice();
        } else if (orderType == OrderType.SELL) {
            quantity = topOfBookMap.get(selectedSymbol).getBidQuantity();
            price = topOfBookMap.get(selectedSymbol).getBidPrice();
        } else {
            // for cancelling an order
        }
        orderQty.setText(Integer.toString(quantity));
        orderPrice.setText(Integer.toString(price));
    }

    private void showAlert(String header, String msg) {
        Alert alert = new Alert(Alert.AlertType.WARNING, msg);
        alert.setHeaderText(header);
        alert.showAndWait();
        alert.setOnCloseRequest(e -> alert.hide());
    }
}