package Messages;

import java.io.IOException;

public class PlayerRegisteredMessage extends Message{

    private short playerId;
    private int initialCash;

    PlayerRegisteredMessage(short playerId, int initialCash){
        super(MessageType.PLAYER_REGISTERED);
        this.playerId = playerId;
        this.initialCash = initialCash;
    }

    public static PlayerRegisteredMessage decode(byte[] messageBytes) {
        Decoder decoder = new Decoder(messageBytes);

        if(decoder.decodeMessageType() != MessageType.PLAYER_REGISTERED) {
            throw new IllegalArgumentException();
        }

        short playerId = decoder.decodeShort();
        int initialCash = decoder.decodeInt();

        return new PlayerRegisteredMessage(playerId, initialCash);
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
                .encodeShort(playerId)
                .encodeInt(initialCash)
                .toByteArray();
    }
}
