package com;

public interface AbstractPlayer {
    void initialize(GameRules rules);// инициализация метода

    Challenge getChallenge(GameRules rules);//получаает челендж,сделать ход

    //создать ответ на числа игроков количество ибыков и коров
    Response getResponse(Challenge c);

    void processResponse(Challenge c, Response r);// получаает ответ на челлендж(количество быков и коров)

    // консоль плеер
    String getName();
}
