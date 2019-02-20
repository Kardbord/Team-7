package messages;

import java.io.IOException;

public class AckMessage extends Message {

    public AckMessage() {
        super(MessageType.ACK);
    }

    @Override
    public byte[] encode() throws IOException {
        return new Encoder()
                .encodeMessageType(this.messageType)
                .toByteArray();
    }

    public static AckMessage decode(byte[] messageBytes) {
        Decoder decoder = new Decoder(messageBytes);

        if (decoder.decodeMessageType() != MessageType.ACK) {
            throw new IllegalArgumentException();
        }

        return new AckMessage();
    }
}
