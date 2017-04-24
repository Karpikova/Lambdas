package com.company;

/*
 * ${Classname}
 * 
 * Version 1.0 
 * 
 * 24.04.2017
 * 
 * Karpikova
 */
public class ResultStatistics {
    private static volatile int sum;

    public static int getSum() {
        return sum;
    }

    public static void setSum(int sum) {
        ResultStatistics.sum = sum;
    }
}
