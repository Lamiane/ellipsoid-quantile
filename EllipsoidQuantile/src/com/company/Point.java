package com.company;

import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CovarianceOps;
import org.ejml.simple.SimpleMatrix;


/**
 * Created by nex on 30.11.16.
 */

public class Point {
    public float x;
    public float y;

    public Object ellipsoidQuantile(DenseMatrix64F data, float quantil){
        // assumption: points are 2D
        assert 0 < quantil;
        assert quantil < 1;
        return null;
    }

    public static void cov(SimpleMatrix X){
        // all credits to nok: https://gist.github.com/nok/73d07cc644a390fad9e9
        // for some weird reason nok_cov(X) = 0.8*numpy.cov(X)

        int n = X.numRows();
        SimpleMatrix Xt = X.transpose();
        int m = Xt.numRows();

        // Means:
        SimpleMatrix x = new SimpleMatrix(m, 1);
        for(int r=0; r<m; r++ ){
            x.set(r, 0, Xt.extractVector(true, r).elementSum() / n);
        }
        // System.out.println(x);

        // Covariance matrix:
        SimpleMatrix S = new SimpleMatrix(m, m);
        for(int r=0; r<m; r++){
            for(int c=0; c<m; c++){
                if(r > c){
                    S.set(r, c, S.get(c, r));
                } else {
                    double cov = Xt.extractVector(true, r).minus( x.get((r), 0) ).dot(Xt.extractVector(true, c).minus( x.get((c), 0) ).transpose());
                    S.set(r, c, (cov / n));
                }
            }
        }
        // System.out.println(S);

        // Plotting:
        for(int r=0; r<m; r++){
            for(int c=0; c<m; c++) { System.out.print(S.get(r, c) + "\t\t\t"); }
            System.out.print("\n");
        }



    }

}
