package com;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public  class CompPlayer implements AbstractPlayer {
        private String myname;
        String mynumber;
        String moveNumber;
        GameRules myRules;
        Response reply;
        NumberGenerator ng;
        ArrayList arrayList1 = new ArrayList();
        //в сооветствии с правилами задумать число и занести его в mynumber // в оба числа заносится первое значениее
        CompPlayer(String name) {
            myname = name;
        }
        public int rnd1(int max) {
            int s = (int) (Math.random() * (max + 1));
            return s;
        }
        @Override
        public void initialize(GameRules rules) {
            List<Integer> list1 = new ArrayList(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
            int nnNumber = rules.getdigits();
            ArrayList<Integer> list2 = new ArrayList();
            for (int f = 0; f < nnNumber; f++) {
                int rand1 = rnd1(9-f);
                int getNumber = list1.get(rand1);
                list2.add(getNumber);
                list1.remove(rand1);
            }
            StringBuilder u = new StringBuilder();
            for (int q: list2) {
                u.append(Integer.toString(q));
                System.out.println();//радном кроме предидуего числа
            }
            setMynumber(u.toString());
        }
        @Override
        public Challenge getChallenge(GameRules rules) {
            // АЛГОРИТМ ИИ ОТВЕТА ИИ ЧИСЛА
            NumberGenerator numberGenerator = new NumberGenerator();
            String[] a;
            a = numberGenerator.generateNumber().split(",");
            for (String add : a) {
                arrayList1.add(add);
            }
            if (reply.getBulls() == 0 | reply.getСows() == 0)
                arrayList1.remove("0123");
            moveNumber = (String) arrayList1.get(0);
            return new Challenge(moveNumber);
        }
              /*  try (FileWriter writer = new FileWriter("10.txt", false)) {
                    writer.write(String.valueOf(arrayList1));
                } catch (IOException ex) {
                    System.out.println("S");
                }
              */  //ходит 0 числом,если нет коров remove(число = ходу)012
            /*
            int rules1 = rules.getdigits();
            List<Integer> list1 = new ArrayList(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9));
            int nnNumber = 3;
            ArrayList<Integer> list2 = new ArrayList();
            for (int f = 0; f < rules1; f++) {
                int rand1 = rnd1(9 - f);
                int getNumber = list1.get(rand1);
                list2.add(getNumber);
                list1.remove(rand1);
            }
            StringBuilder u = new StringBuilder();
            for (int q : list2) {
                u.append(Integer.toString(q));
                //радном кроме предидуего числа
            }*/
                // moveNumber = u.toString();//просим игроков ввести число,в соответсвии с правилами(проверка на соответствие с правилами(в гейм рулес))

            // ДОБАВИТЬ ЕЩЕ ОДНО СРАВНИВАЕМОЕ ЧИСЛО
        @Override
        public Response getResponse (Challenge c){
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
        public String getName(){
            return myname; //вместо нул вернуть имя
        }
        @Override
        public void processResponse (Challenge c, Response r){
            //заносим количество коров и быков игрока,в (Responce) количество (получаем ответ на челлендж)(процесс ответа должен говорить сколько быков и коров и давать новое число)
        }
        void setMynumber (String a){
            mynumber = a;
            System.out.println(a);
        }
     /*   void numbers() throws InterruptedException{
            Thread th = new Thread();
            th.start();
            th.join();
     */
    }

