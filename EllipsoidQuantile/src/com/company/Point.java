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

        int n_samples = X.numRows();
        SimpleMatrix Xt = X.transpose();
        int m_dimensions = Xt.numRows();

        // Means:
        SimpleMatrix means = new SimpleMatrix(m_dimensions, 1);
        for(int r=0; r<m_dimensions; r++ ){
            means.set(r, 0, Xt.extractVector(true, r).elementSum() / n_samples);
        }

        // Covariance matrix:
        SimpleMatrix cov_matrix = new SimpleMatrix(m_dimensions, m_dimensions);
        for(int r=0; r<m_dimensions; r++){
            for(int c=0; c<m_dimensions; c++){
                if(r > c){
                    cov_matrix.set(r, c, cov_matrix.get(c, r));
                } else {
                    double cov = Xt.extractVector(true, r).minus( means.get((r), 0) ).dot(Xt.extractVector(true, c).minus( means.get((c), 0) ).transpose());
                    cov_matrix.set(r, c, (cov / (n_samples-1)));
                }
            }
        }

//        // Plotting:
//        for(int r=0; r<m_dimensions; r++){
//            for(int c=0; c<m_dimensions; c++) { System.out.print(cov_matrix.get(r, c) + "\t\t\t"); }
//            System.out.print("\n");
//        }
    }

}
