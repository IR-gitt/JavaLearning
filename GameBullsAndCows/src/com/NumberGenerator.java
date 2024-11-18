package com;
import java.util.*;
// Импортируем все из java.util для использования Scanner и коллекций.

public class NumberGenerator {
    // Импортируем необходимые библиотеки
    // Объявляем переменные
    Response response; // Переменная для хранения ответа
    StringBuilder u = new StringBuilder(); // Переменная для построения строки

    // Метод для рекурсивной печати всех элементов массива с разделителем
    public <integer> void printAllRecursive(integer[] elements, String delimiter) {
        printAllRecursive(elements.length, elements, delimiter);
    }

    // Перегруженный метод для рекурсивной печати элементов
    public <integer> void printAllRecursive(int n, integer[] elements, String delimiter) {
        if (n == 1) {
            // Если длина массива 1, печатаем массив
            printArray(elements, delimiter);
        } else {
            // Иначе рекурсивно вызываем метод для n-1 элементов
            for (int i = 0; i < n - 1; ++i) {
                printAllRecursive(n - 1, elements, delimiter);
                if (n % 2 == 0) {
                    // Если n четное, меняем местами элементы i и n-1
                    swap(elements, i, n - 1);
                } else {
                    // Если n нечетное, меняем местами элементы 0 и n-1
                    swap(elements, 0, n - 1);
                }
            }
            // Рекурсивно вызываем метод для n-1 элементов
            printAllRecursive(n - 1, elements, delimiter);
        }
    }

    // Метод для обмена местами двух элементов массива
    private <integer> void swap(integer[] elements, int a, int b) {
        integer tmp = elements[a]; // Сохраняем элемент a во временную переменную
        elements[a] = elements[b]; // Перемещаем элемент b в a
        elements[b] = tmp; // Перемещаем временный элемент в b
    }

    // Метод для печати массива с разделителем
    private <integer> void printArray(integer[] elements, String delimiter) {
        String sum = ""; // Переменная для хранения объединенных элементов массива
        for (int i = 0; i < 4; ++i) {
            sum += elements[i]; // Объединяем элементы массива в строку
        }
        u.append(sum).append(delimiter); // Добавляем объединенную строку и разделитель в StringBuilder
    }

    // Метод для генерации числа
    public String generateNumber() {
        Integer[] elements = new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9}; // Создаем массив целых чисел
        printAllRecursive(elements, ","); // Вызываем метод для рекурсивной печати элементов массива
        return String.valueOf(u); // Возвращаем результат в виде строки
    }
}

