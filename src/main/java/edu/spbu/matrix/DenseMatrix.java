package edu.spbu.matrix;

import java.io.*;
import java.util.*;

/**
 * Плотная матрица
 */
public class DenseMatrix implements Matrix {
    private int rows;
    private int columns;
    public double[][] matrix;

    public static void main(String[] args) {
        Matrix m4 = new DenseMatrix("m4.txt");
        m4.print("print.txt");
    }

    /**
     * загружает матрицу из файла
     *
     * @param fileName
     */
    public DenseMatrix(String fileName) {
        int m = 1, n = 0;
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
        if (o instanceof DenseMatrix) {
            DenseMatrix res = new DenseMatrix(new double[this.rows][((DenseMatrix) o).columns]);
            try {
                res = mul((DenseMatrix) o);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            return res;
        } else return null;
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

    /**
     * многопоточное умножение матриц
     *
     * @param o
     * @return
     */
    @Override
    public Matrix dmul(Matrix o) {
        return null;
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
        else return false;
    }

    public boolean equals(DenseMatrix o) {
        if (this.rows != o.rows && this.columns != o.columns)
            return false;
        else {
            int i = 0, j = 0;
            boolean p = true;
            for (; i < this.rows && p; ++i)
                for (; j < o.columns && p; ++j)
                    if (this.matrix[i][j] != o.matrix[i][j]) p = false;
            return p;
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
