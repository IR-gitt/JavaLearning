package learning;

import java.util.Random;
import java.util.Scanner;

public class GameGuessNumber {
    public static void main(String[] args) {
        // Создаем игрока
        Player player = new Player("Igor");

        // Создаем коммуникатор с игроком и запускаем его
        Communicator communicator = new Communicator(player);
        communicator.start();
    }

    // Класс для хранения информации об игроке
    public static class Player {
        private String playerName;

        // Геттер для получения имени игрока
        public String getPlayerName() {
            return playerName;
        }

        // Конструктор для инициализации имени игрока
        public Player(String playerName) {
            this.playerName = playerName;
        }
    }

    // Класс для коммуникации с игроком
    public static class Communicator {
        private final int targetNumber;
        private int attempts = 0;
        Player player;

        // Конструктор, который задает случайное целевое число и связывает игрока
        public Communicator(Player player) {
            Random random = new Random();
            this.targetNumber = random.nextInt(100) + 1;
            this.player = player;
        }

        // Метод для начала игры
        public void start() {
            // Статус игры: игра началась
            String status = GameStatus.GAME_STARTED;
            System.out.println("Привет," + player.getPlayerName() + "\n" + status);
            Scanner scanner = new Scanner(System.in);
            int userGuess;

            // Цикл игры
            while (status.equals(GameStatus.GAME_STARTED)) {
                System.out.print("Введите число: ");
                userGuess = scanner.nextInt();
                attempts++;

                // Проверка числа
                if (userGuess < targetNumber) {
                    System.out.println(GameStatus.TOO_LOW);
                } else if (userGuess > targetNumber) {
                    System.out.println(GameStatus.TOO_HIGH);
                } else {
                    status = GameStatus.CORRECT;
                }
            }

            // Вывод количества попыток и закрытие сканера
            System.out.println(GameStatus.ATTEMPTS + attempts);
            scanner.close();
        }
    }

    // Класс для хранения статусов игры
    class GameStatus {
        public static final String GAME_STARTED = "Я загадал число от 1 до 100. Попробуйте его угадать.";
        public static final String TOO_LOW = "Слишком мало";
        public static final String TOO_HIGH = "Слишком много";
        public static final String CORRECT = "Вы угадали!";
        public static final String ATTEMPTS = "Количество попыток: ";
    }
}

