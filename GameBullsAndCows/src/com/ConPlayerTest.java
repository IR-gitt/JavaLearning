package com;

import org.testng.annotations.Test;
public class ConPlayerTest {
    @Test
    public void conPlayerTest() throws Exception {
        GameRules rules = new SimpleGameRules(3);
        ConPlayer p1 = new ConPlayer("PLAYER_A");
        RandomPlayer p2 = new RandomPlayer("RandomPlayer");
        Communcator c = new Communcator(p1, p2,rules);
        p1.initialize(rules);
        p2.initialize(rules);
        c.GameCycle(rules);
        c.GameCycle(rules);
    }
}
