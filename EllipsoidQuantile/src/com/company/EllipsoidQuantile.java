package com.company;

import org.ejml.data.DenseMatrix64F;
import org.ejml.ops.CovarianceOps;
import org.ejml.simple.SimpleBase;
import org.ejml.simple.SimpleEVD;
import org.ejml.simple.SimpleMatrix;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by  nex on 30.11.16.
 */

public class EllipsoidQuantile {

    public Object ellipsoidQuantile(SimpleMatrix data, float quantil){
        // data: sample per row, dimension per column (i.e. numpy default)
        //TODO po przetestowaniu zoptymalizowac //mniej linii, mniej obiektow, mniej wszystkiego
        assert 0 < quantil;
        assert quantil < 1;

        SimpleMatrix cov = cov(data);
        SimpleMatrix inv_cov = cov.invert();
        SimpleMatrix mean = new SimpleMatrix(data.numCols(), 1);
        for(int c=0; c<data.numCols(); c++ ){
            mean.set(c, 0, data.extractVector(false, c).elementSum() / data.numRows());
        }

        SimpleEVD EVD = cov.eig(); // wektory potrzebne na samym koncu do zwracania

        // distances from the mean in the 'circular' space
        SimpleMatrix xcen = data.copy();
        for (int r=0; r < xcen.numRows(); r++){
            SimpleMatrix xcen_row = xcen.extractVector(true, r).minus(mean);
            for(int c=0; c<xcen.numCols(); c++){
                xcen.set(r, c, xcen_row.get(0, c));
            }
        }

        SimpleMatrix circled_data_tmp = xcen.transpose().mult(inv_cov).mult(xcen);
        circled_data_tmp = circled_data_tmp.elementPower(0.5);
        double[] circled_data = circled_data_tmp.getMatrix().data;
        Arrays.sort(circled_data);
        int n = circled_data.length;

        HashMap<Double, Integer> n_at_distance = new HashMap<>();
        for (double distance : circled_data) {
            if (n_at_distance.get(distance) == null) {
                n_at_distance.put(distance, 1);
            } else {
                n_at_distance.put(distance, n_at_distance.get(distance) + 1);
            }
        }

        TreeSet<Double> sortedKeys = (TreeSet<Double>)new TreeSet<Double>(n_at_distance.keySet()).descendingSet();
        double[] qlevel = new double[sortedKeys.size()+1];
        qlevel[0] = 0;
        int idx = 1;
        Iterator<Double> itr = sortedKeys.iterator();
        while(itr.hasNext()){
            int how_many = n_at_distance.get(itr.next());
            qlevel[idx] = qlevel[idx - 1] + how_many;
        }

        for(int i=0; i<qlevel.length; i++) {
            qlevel[i] = (n - qlevel[i]) / n;
        }
        qlevel = Arrays.copyOfRange(qlevel, 0, qlevel.length - 1);

        // ========================









        return null;
    }

    static SimpleMatrix cov(SimpleMatrix X){
        // all credits to nok: https://gist.github.com/nok/73d07cc644a390fad9e9
        // TODO: needs testing

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

        return cov_matrix;

//        // Plotting:
//        for(int r=0; r<m_dimensions; r++){
//            for(int c=0; c<m_dimensions; c++) { System.out.print(cov_matrix.get(r, c) + "\t\t\t"); }
//            System.out.print("\n");
//        }
    }

}
