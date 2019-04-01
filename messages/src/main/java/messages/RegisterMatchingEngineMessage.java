package messages;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

public class RegisterMatchingEngineMessage extends Message {

    private String symbol;

    public RegisterMatchingEngineMessage(String symbol) {
        super(MessageType.REGISTER_MATCHING_ENGINE);
        this.symbol = symbol;
    }

    RegisterMatchingEngineMessage(UUID uuid, String symbol) {
        super(MessageType.REGISTER_MATCHING_ENGINE, uuid);
        this.symbol = symbol;
    }

    @Override
    public byte[] encode() throws IOException {
        return new Encoder()
                .encodeMessageType(messageType)
                .encodeUUID(conversationId)
                .encodeString(symbol)
                .toByteArray();
    }

    public static RegisterMatchingEngineMessage decode(byte[] messageBytes) {
        Decoder decoder = new Decoder(messageBytes);

        if (decoder.decodeMessageType() != MessageType.REGISTER_MATCHING_ENGINE) {
            throw new IllegalArgumentException();
        }

        UUID uuid = decoder.decodeUUID();
        String symbol = decoder.decodeString();

        return new RegisterMatchingEngineMessage(uuid, symbol);
    }

    public String getSymbol() {
        return symbol;
    }

    @Override
    public String toString() {
        return "RegisterMatchingEngineMessage{" +
                "symbol='" + symbol + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        RegisterMatchingEngineMessage that = (RegisterMatchingEngineMessage) o;
        return Objects.equals(symbol, that.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), symbol);
    }
}
