package messages;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

public class RegisterPlayerMessage extends Message {

    private String playerName;

    public RegisterPlayerMessage(String playerName) {
        super(MessageType.REGISTER_PLAYER);
        this.playerName = playerName;
    }

    RegisterPlayerMessage(UUID uuid, String playerName) {
        super(MessageType.REGISTER_PLAYER, uuid);
        this.playerName = playerName;
    }

    public static RegisterPlayerMessage decode(byte[] messageBytes) {
        Decoder decoder = new Decoder(messageBytes);

        if (decoder.decodeMessageType() != MessageType.REGISTER_PLAYER) {
            throw new IllegalArgumentException();
        }

        UUID uuid = decoder.decodeUUID();
        String playerName = decoder.decodeString();

        return new RegisterPlayerMessage(uuid, playerName);
    }

    @Override
    public byte[] encode() throws IOException {
        return new Encoder()
                .encodeMessageType(messageType)
                .encodeUUID(conversationId)
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
