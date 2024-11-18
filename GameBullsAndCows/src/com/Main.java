package com;

public class Main {

    /**
     * Implementation of the game bulls and cows in the terminal
     */
    public static void main(String[] args) throws Exception {
        GameRules rules = new SimpleGameRules(3);
        RandomPlayer p1 = new RandomPlayer("PLAYER_A");
        ConPlayer p2 = new ConPlayer("RandomPlayer");
        Communcator c = new Communcator(p1, p2,rules);
        p1.initialize(rules);
        p2.initialize(rules);
        Communcator.GameStatus result = Communcator.GameStatus.GAME_CONTINUE;
        while(result== Communcator.GameStatus.GAME_CONTINUE) {
            result = c.GameCycle(rules);
        }
        System.out.println("Game result is: " + result);
    }
}

