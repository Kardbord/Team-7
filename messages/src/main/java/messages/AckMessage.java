package messages;

import java.io.IOException;
import java.util.UUID;

public class AckMessage extends Message {

    public AckMessage(UUID uuid) {
        super(MessageType.ACK, uuid);
    }

    @Override
    public byte[] encode() throws IOException {
        return new Encoder()
                .encodeMessageType(this.messageType)
                .encodeUUID(this.conversationId)
                .toByteArray();
    }

    public static AckMessage decode(byte[] messageBytes) {
        Decoder decoder = new Decoder(messageBytes);

        if (decoder.decodeMessageType() != MessageType.ACK) {
            throw new IllegalArgumentException();
        }

        UUID uuid = decoder.decodeUUID();

        return new AckMessage(uuid);
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
