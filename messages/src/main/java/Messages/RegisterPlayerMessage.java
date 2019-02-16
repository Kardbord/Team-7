package Messages;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class RegisterPlayerMessage extends Message{
    public RegisterPlayerMessage() {
        super(MessageType.REGISTER_PLAYER);
    }

    @Override
    public void setStateFrom(ByteBuffer buff) {

    }

    @Override
    public ByteArrayOutputStream encode() throws IOException {
        return null;
    }
}
