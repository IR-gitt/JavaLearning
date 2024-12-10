package com;


public class SimplePlayer implements AbstractPlayer {
    private String myname;
    String mynumber; //в сооветствии с правилами задумать число и занести его в mynumber // в оба числа заносится первое значениее
    SimplePlayer(String name, String number) {
        myname = name;
        mynumber = number;
    }
    @Override
    public void initialize(GameRules rules){ //нужно задумать число// в заносится число(игрок вносит число)
    }
    @Override
    public Challenge getChallenge(GameRules rules) {
        //просим игроков ввести число,в соответсвии с правилами(проверка на соответствие с правилами(в гейм рулес))
        return new Challenge(mynumber);
    }
    @Override
    public void processResponse(Challenge c, Response r) { //заносим количество коров и быков игрока,в (Responce) количество (получаем ответ на челлендж)
    }
    @Override
    public Response getResponse(Challenge c) {
        int bulls = 0;
        int cows = 0;
        String hisnumber = c.getChallenge();
        for (int i = 0; i < mynumber.length(); i++) {
            for (int j = 0; j < hisnumber.length(); j++) {
                if (mynumber.charAt(i) != hisnumber.charAt(j)) continue;
                if (i == j) {
                    bulls++;
                } else {
                    cows++;
                }
            }
        }
        return new Response(cows, bulls);
    }
        @Override
        public String getName () {
            return myname; //вместо нул вернуть имя
        }
    }

