module player {
    requires javafx.fxml;
    requires javafx.controls;
    requires org.apache.logging.log4j;

    requires communication.subsytem;
    requires messages;
    requires exchange.gateway;

    opens userInterface to javafx.fxml;
    exports player;
    exports userInterface;
}