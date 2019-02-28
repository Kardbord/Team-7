package messages;

import java.io.IOException;
import java.util.Objects;

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

    @Override
    public String toString() {
        return "RegisterPlayerMessage{" +
                "playerName='" + playerName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        RegisterPlayerMessage that = (RegisterPlayerMessage) o;
        return Objects.equals(playerName, that.playerName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), playerName);
    }
}
