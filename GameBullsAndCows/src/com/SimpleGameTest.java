package com;

import org.junit.Test;

public class SimpleGameTest {
    @Test
    public void communicatorTest() throws Exception {
        GameRules rules = new SimpleGameRules(3);
        AbstractPlayer p1 = new SimplePlayer("PLAYER_A", "123");
        AbstractPlayer p2 = new SimplePlayer("PLAYER_B", "123");
        Communcator c = new Communcator(p1, p2, rules);

        p1.initialize(rules);//
        p2.initialize(rules);
        c.GameCycle(rules);
    }
}
