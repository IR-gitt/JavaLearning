package com.onlyOneMethod;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class ArrayOperations {
    public ArrayOperations() {
    }

    // Расширение класса для создания асинхронности
    private static class QuickSortTask extends RecursiveAction {
        private final int[] array;
        private final int low, high;

        public QuickSortTask(int[] array, int low, int high) {
            this.array = array;
            this.low = low;
            this.high = high;
        }

        // Создание асинхронности
        @Override
        protected void compute() {
            if (low < high) {
                int pivot = partition(array, low, high);

                ArrayOperations.QuickSortTask leftTask = new ArrayOperations.QuickSortTask(array, low, pivot - 1);
                ArrayOperations.QuickSortTask rightTask = new ArrayOperations.QuickSortTask(array, pivot + 1, high);

                invokeAll(leftTask, rightTask);
            }
        }

        // Разделение массива (Возвращаем опорный элемент для разделения массива)
        private int partition(int[] array, int low, int high) {
            int pivot = array[high];
            int i = low - 1;

            for (int j = low; j < high; j++) {
                if (array[j] <= pivot) {
                    i++;
                    swap(array, i, j);
                }
            }

            swap(array, i + 1, high);
            return i + 1;
        }

        private void swap(int[] array, int i, int j) {
            int temp = array[i];
            array[i] = array[j];
            array[j] = temp;
        }
    }

    public int[] asyncQuickSort(int[] array) {
        ForkJoinPool pool = new ForkJoinPool();
        pool.invoke(new ArrayOperations.QuickSortTask(array, 0, array.length - 1));
        System.out.println();
        return array;
    }

    // вывод числа N в соотвествии с числом введенным пользователем (условно 0ая позиция отсутствует)
    public int selectNValueArray(int[] array, int n){
        return array[n-1];
    }
}



