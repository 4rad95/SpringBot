package org.binance.springbot.analytic;

import org.ta4j.core.Bar;

public class OrderBlock {
    private int move;
    private int numCandela;
    private Bar bar;

    public OrderBlock(int move, int numCandela, Bar bar) {
        this.move = move;
        this.numCandela = numCandela;
        this.bar = bar;
    }

    public int getMove() {
        return move;
    }

    public void setMove(int move) {
        this.move = move;
    }

    public int getNumCandela() {
        return numCandela;
    }

    public void setNumCandela(int numCandela) {
        this.numCandela = numCandela;
    }

    public Bar getBar() {
        return bar;
    }

    public void setBar(Bar bar) {
        this.bar = bar;
    }
}
