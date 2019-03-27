module exchange.gateway {
    requires org.apache.logging.log4j;

    requires communication.subsytem;
    requires messages;
    exports gateway;
}