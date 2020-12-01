package edu.spbu.matrix;

import java.io.*;
import java.util.*;

/**
 * Плотная матрица
 */
public class DenseMatrix implements Matrix {
    public int rows;
    public int columns;
    public double[][] matrix;

    public static void main(String[] args) {
        Matrix m7 = new SparseMatrix("m7.txt");
        Matrix m8 = new DenseMatrix("m8.txt");
        Matrix res = m8.dmul(m7);
        res.print("print.txt");
    }

    /**
     * загружает матрицу из файла
     *
     * @param fileName
     */
    public DenseMatrix(String fileName) {
        int m = 1, n = 0; // m - columns, n - rows
        try (Scanner s = new Scanner(new FileReader(new File(fileName)))) {
            String str = s.nextLine();
            for (int k = 0; k < str.length(); ++k)
                if (str.charAt(k) == ' ') ++m;
            ++n;
            while (s.hasNextLine()) {
                ++n;
                s.nextLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        double[][] mat = new double[n][m];
        try (Scanner s1 = new Scanner(new FileReader(new File(fileName)))) {
            int i = 0, j = 0;
            for (; i < n; ++i) {
                for (; j < m; ++j)
                    mat[i][j] = s1.nextDouble();
                if (i != n - 1) s1.nextLine();
                j = 0;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        this.rows = n;
        this.columns = m;
        this.matrix = mat;
    }

    public DenseMatrix(double[][] m) {
        this.rows = m.length;
        this.columns = m[0].length;
        this.matrix = m;
    }

    /**
     * однопоточное умнджение матриц
     * должно поддерживаться для всех 4-х вариантов
     *
     * @param o
     * @return
     */
    @Override
    public Matrix mul(Matrix o) {
        DenseMatrix res;
        if (o instanceof DenseMatrix) res = new DenseMatrix(new double[this.rows][((DenseMatrix) o).columns]);
        else res = new DenseMatrix(new double[this.rows][((SparseMatrix) o).columns]);
        try {
            if (o instanceof DenseMatrix) res = mul((DenseMatrix) o);
            else res = mul((SparseMatrix) o);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return res;
    }


    public DenseMatrix mul(DenseMatrix o) throws Exception {
        if (this.columns != o.rows) throw new Exception("These matrixes cannot be multiplied");
        double[][] m = new double[this.rows][o.columns];
        int i = 0, j = 0, k = 0, n = this.columns;
        double d = 0;
        if (n % 2 == 0) {
            double[] r = new double[this.rows];
            double[] c = new double[o.columns];
            for (; i < this.rows; ++i) {
                for (; j < n / 2; ++j)
                    d += this.matrix[i][2 * j] * this.matrix[i][2 * j + 1];
                r[i] = d;
                d = 0;
                j = 0;
            }
            i = 0;
            for (; i < o.columns; ++i) {
                for (; j < n / 2; ++j)
                    d += o.matrix[2 * j][i] * o.matrix[2 * j + 1][i];
                c[i] = d;
                d = 0;
                j = 0;
            }
            i = 0;
            for (; i < this.rows; ++i) {
                for (; j < o.columns; ++j) {
                    for (; k < n / 2; ++k)
                        d += (this.matrix[i][2 * k] + o.matrix[2 * k + 1][j]) * (this.matrix[i][2 * k + 1] + o.matrix[2 * k][j]);
                    m[i][j] = d - r[i] - c[j];
                    d = 0;
                    k = 0;
                }
                j = 0;
            }
        } else {
            double[] r = new double[this.rows];
            double[] c = new double[o.columns];
            for (; i < this.rows; ++i) {
                for (; j < (n - 1) / 2; ++j)
                    d += this.matrix[i][2 * j] * this.matrix[i][2 * j + 1];
                r[i] = d;
                d = 0;
                j = 0;
            }
            i = 0;
            for (; i < o.columns; ++i) {
                for (; j < (n - 1) / 2; ++j)
                    d += o.matrix[2 * j][i] * o.matrix[2 * j + 1][i];
                c[i] = d;
                d = 0;
                j = 0;
            }
            i = 0;
            for (; i < this.rows; ++i) {
                for (; j < o.columns; ++j) {
                    for (; k < (n - 1) / 2; ++k)
                        d += (this.matrix[i][2 * k] + o.matrix[2 * k + 1][j]) * (this.matrix[i][2 * k + 1] + o.matrix[2 * k][j]);
                    m[i][j] = d - r[i] - c[j] + this.matrix[i][n - 1] * o.matrix[n - 1][j];
                    d = 0;
                    k = 0;
                }
                j = 0;
            }
        }
        return new DenseMatrix(m);
    }

    public DenseMatrix mul(SparseMatrix o) throws Exception {
        if (this.columns != o.rows) throw new Exception("These matrixes cannot be multiplied");
        double[][] res = new double[this.rows][o.columns];
        SparseMatrix o_trans = o.transpose();
        int[] count_o = new int[o_trans.rows]; // array of numbers of non-zero elements in each row in o_trans
        for (int i = 0; i < o_trans.rows; ++i)
            count_o[i] = 0;
        for (int i = 0; i < o_trans.p_row.size(); ++i)
            count_o[o_trans.p_row.get(i)] += 1;
        int q = 0;
        for (int j = 0; j < o.columns; ++j)
            if (count_o[j] != 0) {
                double s_prod = 0;
                for (int i = 0; i < this.rows; ++i) {
                    for (int k = q; k < q + count_o[j]; ++k)
                        s_prod += o_trans.value.get(k) * this.matrix[i][o_trans.p_column.get(k)];
                    res[i][j] = s_prod;
                    s_prod = 0;
                }
                q += count_o[j];
            }
            else for (int i = 0; i < this.rows; ++i)
                res[i][j] = 0;
        return new DenseMatrix(res);
    }

    /**
     * многопоточное умножение матриц
     *
     * @param o
     * @return
     */
    @Override
    public Matrix dmul(Matrix o) {
        DenseMatrix res;
        if (o instanceof DenseMatrix) res = new DenseMatrix(new double[this.rows][((DenseMatrix) o).columns]);
        else res = new DenseMatrix(new double[this.rows][((SparseMatrix) o).columns]);
        try {
            if (o instanceof DenseMatrix) {
                DenseDenseThread t1 = new DenseDenseThread(this, (DenseMatrix) o, res, 0, res.rows / 2 - 1, 0, res.columns / 2 - 1); //upper left
                DenseDenseThread t2 = new DenseDenseThread(this, (DenseMatrix) o, res, res.rows / 2, res.rows - 1, 0, res.columns / 2 - 1); //bottom left
                DenseDenseThread t3 = new DenseDenseThread(this, (DenseMatrix) o, res, 0, res.rows / 2 - 1, res.columns / 2, res.columns - 1); //upper right
                DenseDenseThread t4 = new DenseDenseThread(this, (DenseMatrix) o, res, res.rows / 2, res.rows - 1, res.columns / 2, res.columns - 1); //bottom right
                t1.start();
                t2.start();
                t3.start();
                t4.start();
                t1.join();
                t2.join();
                t3.join();
                t4.join();
            }
            else {
                DenseSparseThread t1 = new DenseSparseThread(this, (SparseMatrix) o, res, 0, this.rows / 4 - 1);
                DenseSparseThread t2 = new DenseSparseThread(this, (SparseMatrix) o, res, this.rows / 4, 2 * (this.rows / 4) - 1);
                DenseSparseThread t3 = new DenseSparseThread(this, (SparseMatrix) o, res, 2 * (this.rows / 4), 3 * (this.rows / 4) - 1);
                DenseSparseThread t4 = new DenseSparseThread(this, (SparseMatrix) o, res, 3 * (this.rows / 4), this.rows - 1);
                t1.start();
                t2.start();
                t3.start();
                t4.start();
                t1.join();
                t2.join();
                t3.join();
                t4.join();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return res;
    }

        /**
         * спавнивает с обоими вариантами
         *
         * @param o
         * @return
         */
    @Override
    public boolean equals(Object o) {
        if (o instanceof DenseMatrix)
            return this.equals((DenseMatrix) o);
        else return this.equals((SparseMatrix) o);
    }

    public boolean equals(DenseMatrix o) {
        if (this.rows != o.rows && this.columns != o.columns)
            return false;
        else {
            int i = 0, j = 0;
            for (; i < this.rows; ++i)
                for (; j < o.columns; ++j)
                    if (this.matrix[i][j] != o.matrix[i][j]) return false;
            return true;
        }
    }

    public boolean equals(SparseMatrix o) {
        if (this.rows != o.rows && this.columns != o.columns)
            return false;
        else {
            int p = 0;
            for (int i = 0; i < this.rows; ++i)
                for (int j = 0; j < this.columns; ++j)
                    if (p < o.value.size()) {
                        if (this.matrix[i][j] == 0) {
                            if (i == o.p_row.get(p) && j == o.p_column.get(p))
                                return false;
                        } else {
                            if (this.matrix[i][j] != o.value.get(p) || i != o.p_row.get(p) || j != o.p_column.get(p))
                                return false;
                            ++p;
                        }
                    }
                    else if (this.matrix[i][j] != 0) return false;
            return true;
        }
    }

    @Override public void print(String fileName) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
            int i = 0, j = 0;
            for (; i < this.rows; ++i) {
                for (; j < this.columns - 1; ++j)
                    bw.write(Double.toString(this.matrix[i][j]) + " ");
                bw.write(Double.toString(this.matrix[i][j]) + "\n");
                j = 0;
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}

