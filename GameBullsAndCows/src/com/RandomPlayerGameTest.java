package com;

import org.junit.Test;

public class RandomPlayerGameTest {

    @Test
    public void randomPlayerGameTest() throws Exception {
        GameRules rules = new SimpleGameRules(3);
        RandomPlayer p1 = new RandomPlayer("PLAYER_A");
        RandomPlayer p2 = new RandomPlayer("RandomPlayer");
        Communcator c = new Communcator(p1, p2,rules);
        p1.initialize(rules);
        p2.initialize(rules);
        c.GameCycle(rules);
    }
}



