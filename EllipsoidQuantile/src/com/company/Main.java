package com.company;

import org.ejml.simple.SimpleMatrix;

public class Main {

    static void print(Object[] results) {
        System.out.println((SimpleMatrix) results[0]);

        System.out.print("radii:\t");
        for (double x : (double[]) results[1]) {
            System.out.print(x + "\t");
        }
        System.out.println("\n\n");
        for (SimpleMatrix x : (SimpleMatrix[]) results[2]) {
            System.out.println(x);
        }
    }

    public static void main(String[] args) {
        // write your code here

        SimpleMatrix data = new SimpleMatrix(new double[][]{{1, 3}, {4, 4}, {5, 6}, {5, 15}, {20, 10}});
        Object[] results = EllipsoidQuantile.ellipsoidQuantile(data, 0.9);
        print(results);

        System.out.println("\n\n===============================\n\n");
        // Singular Matrix Exception as expected
//        data = new SimpleMatrix(new double[][]{{1, 1}, {0, 0}, {2, 2}});
//        results = EllipsoidQuantile.ellipsoidQuantile(data, 0.9);
//        print(results);

        System.out.println("\n\n===============================\n\n");

        data = new SimpleMatrix(new double[][]{{0, -1}, {-1, 0}, {1, 0}, {0, 1}});
        results = EllipsoidQuantile.ellipsoidQuantile(data, 0.9);
        print(results);

        System.out.println("\n\n===============================\n\n");

        results = EllipsoidQuantile.ellipsoidQuantile(data, 0.8);
        print(results);

        System.out.println("\n\n===============================\n\n");

        results = EllipsoidQuantile.ellipsoidQuantile(data, 0.75);
        print(results);

        System.out.println("\n\n===============================\n\n");

        data = new SimpleMatrix(new double[][]{{0, -1}, {1, 3}, {-4, 0}, {5, 1}, {6, 7}, {11, 2}, {3, 3}, {1, 7}});
        results = EllipsoidQuantile.ellipsoidQuantile(data, 0.9);
        print(results);

        System.out.println("\n\n===============================\n\n");

        results = EllipsoidQuantile.ellipsoidQuantile(data, 0.8);
        print(results);

        System.out.println("\n\n===============================\n\n");

        data = new SimpleMatrix(new double[][]{{1, 1, 1}, {2, 3, 4}, {6, 7, 8}, {1, 0, 0}, {-2, 3, 4}, {9, -10, 1}});
        results = EllipsoidQuantile.ellipsoidQuantile(data, 0.8);
        print(results);


    }
}
