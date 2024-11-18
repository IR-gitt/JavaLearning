package com;

import org.junit.Assert;
import org.junit.Test;

public class SimplePlayerTest {
    private void assertResponse(AbstractPlayer p, String challenge, int cows, int bulls) {
        Challenge c = new Challenge(challenge);
        Response expected = new Response(cows,bulls);
        Response actual = p.getResponse(c);
        Assert.assertEquals( expected, actual);
    }
    @Test
    public void shouldInitialize() throws Exception {
        AbstractPlayer player = new SimplePlayer("PlayerA","098");
        GameRules rules = new SimpleGameRules(3);
        player.initialize(rules);
        assertResponse(player, "098", 0,3); //000




    }
}
