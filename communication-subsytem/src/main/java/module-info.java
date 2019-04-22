module communication.subsytem {
    requires messages;
    requires org.apache.logging.log4j;

    exports communicators;
    exports dispatcher;
    exports conversation;
    exports security;

}