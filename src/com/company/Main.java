package com.company;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    public static void main(String[] args) {
        args[0] = "D:\\11.txt";
        args[1] = "D:\\12.txt";
        args[2] = "D:\\13.txt";
        ResultStatistics resultStatistics = new ResultStatistics();
        int countOfThreads = 10;
        ExecutorService executor = Executors.newFixedThreadPool(countOfThreads);

        Expression func = (sum, n)-> { if (n>0 && n%2 == 0) {
                                            return (sum+n);}
                                        else {
                                            return sum;} };

        for (String fileName:
                args) {
            CountStatistic reading = new CountStatistic(fileName, resultStatistics, func);
            Thread readingThread = new Thread(reading);//не устанавливается имя!!
            executor.execute(readingThread);
        }
        executor.shutdown();//подчистка после вечеринки ресурсов
        while (!executor.isTerminated()) { //ждем что все подчистили
        }

        CountStatistic.PrintStatistics(resultStatistics);
    }

}

interface Expression{
    int sum(int sum, int n);
}
