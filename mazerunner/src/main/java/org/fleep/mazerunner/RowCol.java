package org.fleep.mazerunner;

import javafx.util.Pair;

public class RowCol extends Pair<Integer, Integer> {
    public RowCol(Integer row, Integer col) {
        super(row, col);
    }

    public int getRow() {
        return getKey();
    }

    public int getCol() {
        return getValue();
    }

    @Override
    public String toString() {
        return "(" + getRow() + "," + getCol() + ")";
    }

    @Override
    public boolean equals(Object o) {
        RowCol other = (RowCol) o;
        return getRow() == other.getRow() && getCol() == other.getCol();
    }
}
