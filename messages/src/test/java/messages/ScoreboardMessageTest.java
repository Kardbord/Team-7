package messages;

import net.bytebuddy.utility.RandomString;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.UUID;

import static org.junit.Assert.*;

public class ScoreboardMessageTest {

    @Test
    public void encodesIntoExpected() throws IOException {
        Message.MessageType expectedMessageType = Message.MessageType.SCOREBOARD;
        UUID expectedUUID = UUID.randomUUID();
        ArrayList<ScoreboardMessage.ScoreboardEntry> expectedScoreboard = new ArrayList<>();

        for (int i = 0; i < 10; ++i) {
            Random rand = new Random();
            String randomName = RandomString.make(rand.nextInt(10) + 1);
            int randomNetWorth = rand.nextInt(5000);
            float randomROI = rand.nextFloat();
            expectedScoreboard.add(new ScoreboardMessage.ScoreboardEntry(randomName, randomNetWorth, randomROI));
        }
        Collections.sort(expectedScoreboard);

        Message.Encoder encoder = new Message.Encoder()
                .encodeMessageType(expectedMessageType)
                .encodeUUID(expectedUUID)
                .encodeShort((short) expectedScoreboard.size());
        for (ScoreboardMessage.ScoreboardEntry scoreboardEntry : expectedScoreboard) {
            scoreboardEntry.encode(encoder);
        }

        byte[] expectedMessageBytes = encoder.toByteArray();

        byte[] actualMessageBytes = new ScoreboardMessage(expectedUUID, expectedScoreboard).encode();

        assertArrayEquals(expectedMessageBytes, actualMessageBytes);

    }

    @Test
    public void decodesIntoExpected() throws IOException {
        Message.MessageType expectedMessageType = Message.MessageType.SCOREBOARD;
        UUID expectedUUID = UUID.randomUUID();
        ArrayList<ScoreboardMessage.ScoreboardEntry> expectedScoreboard = new ArrayList<>();

        for (int i = 0; i < 10; ++i) {
            Random rand = new Random();
            String randomName = RandomString.make(rand.nextInt(10) + 1);
            int randomNetWorth = rand.nextInt(5000);
            float randomROI = rand.nextFloat();
            expectedScoreboard.add(new ScoreboardMessage.ScoreboardEntry(randomName, randomNetWorth, randomROI));
        }
        Collections.sort(expectedScoreboard);

        Message.Encoder encoder = new Message.Encoder()
                .encodeMessageType(expectedMessageType)
                .encodeUUID(expectedUUID)
                .encodeShort((short) expectedScoreboard.size());
        for (ScoreboardMessage.ScoreboardEntry scoreboardEntry : expectedScoreboard) {
            scoreboardEntry.encode(encoder);
        }

        byte[] messageBytes = encoder.toByteArray();

        ScoreboardMessage victim = ScoreboardMessage.decode(messageBytes);

        assertEquals(expectedMessageType, victim.getMessageType());
        assertEquals(expectedScoreboard, victim.getScoreboard());
    }

}