package messages;

import java.io.IOException;

public class RegisterMatchingEngineMessage extends Message {

    private String symbol;

    public RegisterMatchingEngineMessage(String symbol) {
        super(MessageType.REGISTER_MATCHING_ENGINE);
        this.symbol = symbol;
    }

    @Override
    public byte[] encode() throws IOException {
        return new Encoder()
                .encodeMessageType(messageType)
                .encodeString(symbol)
                .toByteArray();
    }

    public static RegisterMatchingEngineMessage decode(byte[] messageBytes) {
        Decoder decoder = new Decoder(messageBytes);

        if (decoder.decodeMessageType() != MessageType.REGISTER_MATCHING_ENGINE) {
            throw new IllegalArgumentException();
        }

        String symbol = decoder.decodeString();

        return new RegisterMatchingEngineMessage(symbol);
    }

    public String getSymbol() {
        return symbol;
    }
}
