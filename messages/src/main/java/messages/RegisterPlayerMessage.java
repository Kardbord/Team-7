package messages;

import java.io.IOException;

public class RegisterPlayerMessage extends Message {

    private String playerName;

    public RegisterPlayerMessage(String playerName) {
        super(MessageType.REGISTER_PLAYER);
        this.playerName = playerName;
    }

    public static RegisterPlayerMessage decode(byte[] messageBytes) {
        Decoder decoder = new Decoder(messageBytes);

        if (decoder.decodeMessageType() != MessageType.REGISTER_PLAYER) {
            throw new IllegalArgumentException();
        }

        String playerName = decoder.decodeString();

        return new RegisterPlayerMessage(playerName);
    }

    @Override
    public byte[] encode() throws IOException {
        return new Encoder()
                .encodeMessageType(messageType)
                .encodeString(playerName)
                .toByteArray();
    }


    public String getPlayerName() {
        return playerName;
    }
}
