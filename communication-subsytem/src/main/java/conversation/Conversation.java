package conversation;

import messages.*;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class Conversation {

    private Conversation(Builder builder) {

    }

    public static class Builder {
        public <T extends Message> CanAcceptIncoming<T> receive(Class<T> clazz, Protocol protocol, int port) {
            return new CanAcceptIncoming<>();
        }

        public Builder execute(Runnable runnable) {
            return this;
        }

        public Builder executeSend(Supplier<Message> messageSupplier, Protocol protocol, String address, int port) {
            return this;
        }

        public Conversation build() {
            return new Conversation(this);
        }

        private static class CanAcceptIncoming<T> extends Builder {
            public Builder execute(Consumer<T> incomingConsumer) {
                return this;
            }

            public Builder executeSend(Function<T, Message> incomingConsumerMessageSupplier, Protocol protocol, String address, int port) {
                return this;
            }
        }
    }
}
