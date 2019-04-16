package messages;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;

public class ScoreboardMessage extends Message {

    private ArrayList<ScoreboardEntry> scoreboard;

    public ScoreboardMessage(UUID uuid, ArrayList<ScoreboardEntry> scoreboard) {
        super(MessageType.SCOREBOARD, uuid);
        this.scoreboard = scoreboard;
    }

    @Override
    public byte[] encode() throws IOException {
        Encoder encoder = new Encoder()
                .encodeMessageType(this.messageType)
                .encodeUUID(this.conversationId)
                .encodeShort((short) scoreboard.size());

        Collections.sort(this.scoreboard);
        for (ScoreboardEntry scoreboardEntry : scoreboard) {
            scoreboardEntry.encode(encoder);
        }

        return encoder.toByteArray();
    }

    public static ScoreboardMessage decode(byte[] messageBytes) {
        Decoder decoder = new Decoder(messageBytes);
        if (decoder.decodeMessageType() != MessageType.SCOREBOARD) {
            throw new IllegalArgumentException();
        }

        UUID uuid = decoder.decodeUUID();

        short numScoreboardEntries = decoder.decodeShort();

        ArrayList<ScoreboardEntry> scoreboard = new ArrayList<>(numScoreboardEntries);

        for (int i = 0; i < numScoreboardEntries; ++i) {
            scoreboard.add(ScoreboardEntry.decode(decoder));
        }

        return new ScoreboardMessage(uuid, scoreboard);
    }

    public ArrayList<ScoreboardEntry> getScoreboard() {
        return scoreboard;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ScoreboardMessage that = (ScoreboardMessage) o;
        return messageType == that.getMessageType() &&
                scoreboard == that.getScoreboard();
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), scoreboard);
    }

    public static class ScoreboardEntry implements Comparable<ScoreboardEntry> {

        private String playerName;

        private int netWorth;

        /**
         * A percentage calculated as (currentInvestmentsValue / costOfInvestments)
         */
        private float returnOnInvestment;

        public ScoreboardEntry(String playerName, int netWorth, float returnOnInvestment) {
            this.playerName = playerName;
            this.netWorth = netWorth;
            this.returnOnInvestment = returnOnInvestment;
        }

        public void encode(Encoder encoder) throws IOException {
            encoder
                    .encodeString(this.playerName)
                    .encodeInt(this.netWorth)
                    .encodeFloat(this.returnOnInvestment);
        }

        public static ScoreboardEntry decode(Decoder decoder) {
            String playerName = decoder.decodeString();
            int netWorth = decoder.decodeInt();
            float returnOnInvestment = decoder.decodeFloat();

            return new ScoreboardEntry(playerName, netWorth, returnOnInvestment);
        }

        public int getNetWorth() {
            return netWorth;
        }

        public String getPlayerName() {
            return playerName;
        }

        public float getReturnOnInvestment() {
            return returnOnInvestment;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ScoreboardEntry that = (ScoreboardEntry) o;
            return this.playerName.equals(that.getPlayerName()) &&
                    this.netWorth == that.getNetWorth() &&
                    this.returnOnInvestment == that.getReturnOnInvestment();
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), playerName, netWorth, returnOnInvestment);
        }

        @Override
        public int compareTo(ScoreboardEntry other) {
            return Integer.compare(other.getNetWorth(), this.netWorth);
        }
    }
}
