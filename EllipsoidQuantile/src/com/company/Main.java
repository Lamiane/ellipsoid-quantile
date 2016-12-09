package com.company;

import org.ejml.simple.SimpleMatrix;

public class Main {

    public static void main(String[] args) {
	// write your code here

        double data[][] = new double[][]{
                { 90, 60, 90 },
                { 90, 90, 30 },
                { 60, 60, 60 },
                { 60, 60, 90 },
                { 30, 30, 30 }
        };

        SimpleMatrix X = new SimpleMatrix(data);
        Point.cov(X);

        // 504.0			360.0			180.0
        // 360.0			360.0			0.0
        // 180.0			0.0  			720.0

        System.out.println("");


        double data2[][] = new double[][]{
                {2,3}, {4, 5}, {3, 4}, {5, 7}, {2,8}
        };

        SimpleMatrix X2 = new SimpleMatrix(data2);
        Point.cov(X2);

    }
}
