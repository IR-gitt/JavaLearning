package com;

import java.io.IOException;

public class SimpleGameRules implements GameRules {
    int ndigits;

    SimpleGameRules(int ndigits) {
        super();
        this.ndigits = ndigits;
    }

    @Override
    public int getdigits() {
        return ndigits;////получать размер чисел
    }

    @Override
    public boolean gamerules() {
        return false;
    }
}
