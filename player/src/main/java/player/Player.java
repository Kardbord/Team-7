package player;


public class Player {

    private String name;
    private int playerId;
    private int cash;

    public Player(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getPlayerId() {
        return playerId;
    }

    public int getCash() {
        return cash;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public void setCash(int cash) {
        this.cash = cash;
    }
}
