package messages;

import java.io.IOException;

public class TopOfBookRequestMessage extends Message {

    public TopOfBookRequestMessage() {
        super(MessageType.TOP_OF_BOOK_REQUEST);
    }

    @Override
    public byte[] encode() throws IOException {
        return new Encoder()
                .encodeMessageType(this.messageType)
                .toByteArray();
    }

    public static TopOfBookRequestMessage decode(byte[] messageBytes) {
        Decoder decoder = new Decoder(messageBytes);

        if (decoder.decodeMessageType() != MessageType.TOP_OF_BOOK_REQUEST) {
            throw new IllegalArgumentException();
        }

        return new TopOfBookRequestMessage();
    }
}
