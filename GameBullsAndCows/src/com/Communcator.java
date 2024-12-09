package com;

import java.util.logging.Logger;

public class Communcator {
    public enum GameStatus {
        GAME_CONTINUE("Игра продолжается"),
        WIN_A("1ST PLayer wins"),
        WIN_B("2ND Player wins"),
        DRAW("Game draws");
        private String humanReadable = "";

        private GameStatus(String humanReadable) {
            this.humanReadable = humanReadable;
        }
    }
    private GameRules test;
    String name;
    GameCount count;
    Logger logger = Logger.getAnonymousLogger();
    private AbstractPlayer playerA;
    private AbstractPlayer playerB;

    public Communcator(AbstractPlayer pa, AbstractPlayer pb, GameRules t) {
        System.out.println("Hello! Enter value of " + t.getdigits() + " character");
        this.playerA = pa;
        this.playerB = pb;
        this.test = t;
    }

    public GameStatus GameCycle(GameRules rules) {
        GameStatus status = GameStatus.GAME_CONTINUE;
        Challenge moveA = playerA.getChallenge(rules);//
        logger.info("Player A challenge = " + moveA.num);
        Response a = playerB.getResponse(moveA);
        playerA.processResponse(moveA, a);
        logger.info("Player B responce = " + moveA.num + a.toString());
        //плеер б отвечает
        Challenge moveB = playerB.getChallenge(rules);
        logger.info("Player B challenge = " + moveB.num);
        Response b = playerA.getResponse(moveB);
        logger.info("Player A responce = " + moveB.num + b.toString());
        playerB.processResponse(moveB, b);
        if (a.getBulls() == rules.getdigits())
            status = GameStatus.WIN_A;
        if (b.getBulls() == rules.getdigits())
            status = GameStatus.WIN_B;
        if (a.getBulls() == b.getBulls() & b.getBulls() == rules.getdigits())
            status = GameStatus.DRAW;
        return status;
    }

    public void getReply() {
        return;
    }
}//комбинаторный вариантб генерируем все возможные комбинаации без н повторений, равновероятны, первым ходом заагадываем либую кобинааццию, проходим по массиву которые не соответсвуют ответу


//
//gameCicle();//конструктор player challange
//reply();
