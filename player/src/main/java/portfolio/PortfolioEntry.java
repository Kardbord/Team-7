package portfolio;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PortfolioEntry {

    private static Logger log = LogManager.getFormatterLogger(PortfolioEntry.class.getName());

    private String symbol;
    private int positions;
    private int equity;

    public PortfolioEntry(String symbol, int positions, int equity) {
        this.symbol = symbol;
        this.positions = positions;
        this.equity = equity * positions;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getEquity() {
        return equity;
    }

    public int getPositions() {
        return positions;
    }

    public void setEquity(int equity) {
        this.equity = equity;
    }

    public void setPositions(int positions) {
        this.positions = positions;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public void updatePositions(int positions) {
        this.positions += positions;
    }

    public void updateEquity(int equity) {
        this.equity = equity * positions;
    }

    @Override
    public String toString() {
        return String.format("%s:\t Shares: %d\t Equity: %d", symbol, positions, equity);
    }
}
