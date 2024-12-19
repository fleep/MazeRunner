package org.fleep.mazerunner;

import javafx.util.Pair;

public class XY extends Pair<Integer, Integer> {
    public XY(Integer x, Integer y) {
        super(x, y);
    }

    public int getX() {
        return getKey();
    }

    public int getY() {
        return getValue();
    }

    @Override
    public String toString() {
        return "(" + getX() + "," + getY() + ")";
    }
}
