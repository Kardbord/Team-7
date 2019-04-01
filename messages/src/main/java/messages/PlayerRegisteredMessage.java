package messages;

import java.io.IOException;
import java.util.Objects;
import java.util.UUID;

public class PlayerRegisteredMessage extends Message{

    private short playerId;
    private int initialCash;

    @Override
    public String toString() {
        return "PlayerRegisteredMessage{" +
                "playerId=" + playerId +
                ", initialCash=" + initialCash +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        PlayerRegisteredMessage that = (PlayerRegisteredMessage) o;
        return playerId == that.playerId &&
                initialCash == that.initialCash;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), playerId, initialCash);
    }

    public PlayerRegisteredMessage(UUID uuid, short playerId, int initialCash){
        super(MessageType.PLAYER_REGISTERED, uuid);
        this.playerId = playerId;
        this.initialCash = initialCash;
    }

    public static PlayerRegisteredMessage decode(byte[] messageBytes) {
        Decoder decoder = new Decoder(messageBytes);

        if(decoder.decodeMessageType() != MessageType.PLAYER_REGISTERED) {
            throw new IllegalArgumentException();
        }

        UUID uuid = decoder.decodeUUID();
        short playerId = decoder.decodeShort();
        int initialCash = decoder.decodeInt();

        return new PlayerRegisteredMessage(uuid, playerId, initialCash);
    }

    public short getPlayerId() {
        return playerId;
    }

    public int getInitialCash() {
        return initialCash;
    }

    @Override
    public byte[] encode() throws IOException {
        return new Encoder()
                .encodeMessageType(messageType)
                .encodeUUID(conversationId)
                .encodeShort(playerId)
                .encodeInt(initialCash)
                .toByteArray();
    }
}
