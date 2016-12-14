package com.company;

import org.ejml.data.Complex64F;
import org.ejml.simple.SimpleEVD;
import org.ejml.simple.SimpleMatrix;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * Created by  nex on 30.11.16.
 */

public class EllipsoidQuantile {

    public static Object[] ellipsoidQuantile(SimpleMatrix data, double quantile) {
        // data: sample per row, dimension per column (i.e. numpy default)
        // TODO: might be optimised
        assert 0 < quantile;
        assert quantile < 1;

        SimpleMatrix cov = cov(data);
        SimpleMatrix inv_cov = cov.invert();
        SimpleMatrix mean = new SimpleMatrix(data.numCols(), 1);
        for (int c = 0; c < data.numCols(); c++) {
            mean.set(c, 0, data.extractVector(false, c).elementSum() / data.numRows());
        }

        SimpleEVD EVD_cov = cov.eig();
        SimpleMatrix[] eig_vects = new SimpleMatrix[EVD_cov.getNumberOfEigenvalues()];
        for (int i = 0; i < eig_vects.length; i++) {
            eig_vects[i] = EVD_cov.getEigenVector(i);
        }

        // distances from the mean in the 'spherical' space
        SimpleMatrix xcen = data.copy();
        for (int r = 0; r < xcen.numRows(); r++) {
            SimpleMatrix xcen_row = xcen.extractVector(true, r).minus(mean.transpose());
            for (int c = 0; c < xcen.numCols(); c++) {
                xcen.set(r, c, xcen_row.get(0, c));
            }
        }
        // extractDiag = yes, we do calculate n^2 things instead of n. Might be optimised later.
        SimpleMatrix circled_data_tmp = xcen.mult(inv_cov).mult(xcen.transpose()).extractDiag();

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

        TreeSet<Double> sortedKeys = (TreeSet<Double>) new TreeSet<Double>(n_at_distance.keySet()).descendingSet();
        double[] qlevel = new double[sortedKeys.size() + 1];
        qlevel[0] = 0;
        int idx = 1;
        Iterator<Double> itr = sortedKeys.iterator();
        while (itr.hasNext()) {
            int how_many = n_at_distance.get(itr.next());
            qlevel[idx] = qlevel[idx - 1] + how_many;
            idx++;
        }

        for (int i = 0; i < qlevel.length; i++) {
            qlevel[i] = (n - qlevel[i]) / n;
        }
        qlevel = Arrays.copyOfRange(qlevel, 1, qlevel.length);

        // ========================

        int index_outer = 1;
        for (int i = 0; i < qlevel.length; i++) {
            if (qlevel[i] > quantile) {
                index_outer++;
            } else break;
        }
        int index_inner = index_outer + 1;

        double[] temp = new double[qlevel.length + 1];
        temp[0] = 1;
        for (int i = 1; i < temp.length; i++) {
            temp[i] = qlevel[i - 1];
        }
        qlevel = temp;


        Double[] circled_java_is_stupid = new Double[circled_data.length];
        for (int i = 0; i < circled_data.length; i++) {
            circled_java_is_stupid[i] = (Double) circled_data[i];
        }
        // maybe two lines below might be optimised
        TreeSet<Double> distances_tmp = (TreeSet<Double>) new TreeSet<Double>(Arrays.asList(circled_java_is_stupid)).descendingSet();
        Double[] distances = distances_tmp.toArray(new Double[distances_tmp.size()]); // stackoverflow magic

        double q_out = qlevel[index_outer - 1];
        double d_out = distances[index_outer - 1];

        double q_inn = 0;
        double d_inn = 0;

        if (index_outer != distances.length) {
            q_inn = qlevel[index_inner - 1];
            d_inn = distances[index_inner - 1];
        }

        double quantile_distance = d_inn + (d_out - d_inn) * ((quantile - q_inn) / (q_out - q_inn));
        quantile_distance = quantile_distance * quantile_distance;

        SimpleMatrix new_covariance = cov.scale(quantile_distance);

        // ========================

        SimpleEVD EVD_new_cov = new_covariance.eig();
        Complex64F[] new_covariance_eigen_values = new Complex64F[EVD_new_cov.getNumberOfEigenvalues()];
        for (int i = 0; i < new_covariance_eigen_values.length; i++) {
            new_covariance_eigen_values[i] = EVD_new_cov.getEigenvalue(i);
        }

        double[] radii = new double[new_covariance_eigen_values.length];
        for (int i = 0; i < radii.length; i++) {
            radii[i] = Math.sqrt(new_covariance_eigen_values[i].getReal());
        }

        return new Object[]{mean, radii, eig_vects};
    }

    static SimpleMatrix cov(SimpleMatrix X) {
        // all credits to nok: https://gist.github.com/nok/73d07cc644a390fad9e9

        int n_samples = X.numRows();
        SimpleMatrix Xt = X.transpose();
        int m_dimensions = Xt.numRows();

        // Means:
        SimpleMatrix means = new SimpleMatrix(m_dimensions, 1);
        for (int r = 0; r < m_dimensions; r++) {
            means.set(r, 0, Xt.extractVector(true, r).elementSum() / n_samples);
        }

        // Covariance matrix:
        SimpleMatrix cov_matrix = new SimpleMatrix(m_dimensions, m_dimensions);
        for (int r = 0; r < m_dimensions; r++) {
            for (int c = 0; c < m_dimensions; c++) {
                if (r > c) {
                    cov_matrix.set(r, c, cov_matrix.get(c, r));
                } else {
                    double cov = Xt.extractVector(true, r).minus(means.get((r), 0)).dot(Xt.extractVector(true, c).minus(means.get((c), 0)).transpose());
                    cov_matrix.set(r, c, (cov / (n_samples - 1)));
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
