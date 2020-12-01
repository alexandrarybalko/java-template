package edu.spbu.matrix;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Разряженная матрица
 */
public class SparseMatrix implements Matrix
{
  public ArrayList<Double> value;
  public ArrayList<Integer> p_row;
  public ArrayList<Integer> p_column;
  public int rows;
  public int columns;

  public static void main(String[] args) {
    Matrix m7 = new SparseMatrix("m7.txt");
    Matrix m9 = new SparseMatrix("m9.txt");
    m7.dmul(m7).print("print.txt");
  }

  /**
   * загружает матрицу из файла
   * @param fileName
   */
  public SparseMatrix(String fileName) {
    ArrayList<Double> v = new ArrayList<>();
    ArrayList<Integer> r = new ArrayList<>();
    ArrayList<Integer> c = new ArrayList<>();
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
    try (Scanner s = new Scanner(new FileReader(new File(fileName)))){
      int i = 0;
      while (s.hasNext()) {
        int j;
        for (j = 0; j < m; ++j) {
          double x = s.nextDouble();
          if (x != 0) {
            v.add(x);
            r.add(i);
            c.add(j);
          }
        }
        ++i;
      }
    } catch (IOException e) {
      System.out.println("Error with reading the file");
      e.printStackTrace();
    }
    this.value = v;
    this.p_row = r;
    this.p_column = c;
    this.columns = m;
    this.rows = n;
  }

  public SparseMatrix(ArrayList<Double> value, ArrayList<Integer> p_row, ArrayList<Integer> p_column, int rows, int
                      columns) {
    this.value = value;
    this.p_row = p_row;
    this.p_column = p_column;
    this.rows = rows;
    this.columns = columns;
  }

  public SparseMatrix(int n, int r, int c) {
    this.rows = r;
    this.columns = c;
    this.value = new ArrayList<>();
    this.p_row = new ArrayList<>();
    this.p_column = new ArrayList<>();
    for (int i = 0; i < n; ++i) {
      this.value.add(0.0);
      this.p_row.add(0);
      this.p_column.add(0);
    }
  }

  public SparseMatrix transpose() {
      SparseMatrix res = new SparseMatrix(this.value.size(), this.columns, this.rows);
      int count[] = new int[this.columns]; // array of numbers of non-zero elements in each column
      for (int i = 0; i < this.columns; i++) {
        count[i] = 0;
      }
      for (int i = 0; i < this.value.size(); i++) {
        count[this.p_column.get(i)] += 1;
      }
      int[] index1 = new int[this.columns]; // array of number of non-zero elements in previous columns
      int[] index2 = new int[this.value.size()]; // array of number of non-zero elements in column before this element
      index1[0] = 0;
      index2[0] = 0;
      for (int i = 1; i < this.columns; i++)
        index1[i] = index1[i - 1] + count[i - 1];
      for (int i = 0; i < this.columns; ++i) {
        int t = 0;
        for (int j = 0; j < this.value.size(); ++j)
          if (i == this.p_column.get(j)) {
            index2[j] = t;
            ++t;
          }
      }
      for (int i = 0; i < this.value.size(); ++i) {
        res.value.set(index1[this.p_column.get(i)] + index2[i], this.value.get(i));
        res.p_row.set(index1[this.p_column.get(i)] + index2[i], this.p_column.get(i));
        res.p_column.set(index1[this.p_column.get(i)] + index2[i], this.p_row.get(i));
      }
      return res;
    }
  /**
   * однопоточное умнджение матриц
   * должно поддерживаться для всех 4-х вариантов
   *
   * @param o
   * @return
   */
  @Override public Matrix mul(Matrix o) {
    if (o instanceof DenseMatrix) {
      Matrix res = new DenseMatrix(new double[this.rows][((DenseMatrix) o).columns]);
      try {
        res = mul((DenseMatrix) o);
      } catch (Exception e) {
        System.out.println(e.getMessage());
      }
      return res;
    }
    else {
      Matrix res = new SparseMatrix(new ArrayList<Double>(), new ArrayList<Integer>(), new ArrayList<Integer>(), this.rows, ((SparseMatrix) o).columns);
      try {
        res = mul((SparseMatrix) o);

      } catch (Exception e) {
        System.out.println(e.getMessage());
      }
      return res;
    }
  }

  public SparseMatrix mul(SparseMatrix o) throws Exception {
    if (this.columns != o.rows) throw new Exception("These matrixes cannot be multiplied");
    ArrayList<Double> v = new ArrayList<>();
    ArrayList<Integer> r = new ArrayList<>();
    ArrayList<Integer> c = new ArrayList<>();
    SparseMatrix o_trans = o.transpose();
    int[] count_this = new int[this.rows]; // array of numbers of non-zero elements in each row in this
    for (int i = 0; i < this.rows; ++i)
      count_this[i] = 0;
    for (int i = 0; i < this.p_row.size(); ++i)
      count_this[this.p_row.get(i)] += 1;
    int[] count_o = new int[o_trans.rows]; // array of numbers of non-zero elements in each row in o_trans
    for (int i = 0; i < o_trans.rows; ++i)
      count_o[i] = 0;
    for (int i = 0; i < o_trans.p_row.size(); ++i)
      count_o[o_trans.p_row.get(i)] += 1;
    int p = 0, q = 0;
    for (int i = 0; i < this.rows; ++i)
      if (count_this[i] != 0) {
        for (int j = 0; j < o_trans.rows; ++j)
          if (count_o[j] != 0) {
            double s_prod = 0;
            for (int k = p; k < p + count_this[i]; ++k)
              for (int l = q; l < q + count_o[j]; ++l) {
                if (this.p_column.get(k) == o_trans.p_column.get(l)) {
                  s_prod += this.value.get(k) * o_trans.value.get(l);
                  l = q + count_o[j];
                }
              }
            if (s_prod != 0) {
              v.add(s_prod);
              r.add(i);
              c.add(j);
            }
            q += count_o[j];
          }
        p += count_this[i];
        q = 0;
      }
    return new SparseMatrix(v, r, c, this.rows, o.columns);
  }


  public DenseMatrix mul(DenseMatrix o) throws Exception {
    if (this.columns != o.rows) throw new Exception("These matrixes cannot be multiplied");
    double[][] res = new double[this.rows][o.columns];
    double[] zero_row = new double[o.columns];
    for (int i = 0; i < o.columns; ++i)
      zero_row[i] = 0;
    int[] count_this = new int[this.rows]; // array of numbers of non-zero elements in each row in this
    for (int i = 0; i < this.rows; ++i)
      count_this[i] = 0;
    for (int i = 0; i < this.p_row.size(); ++i)
      count_this[this.p_row.get(i)] += 1;
    int p = 0;
    for (int i = 0; i < this.rows; ++i)
      if (count_this[i] != 0) {
        double s_prod = 0;
        for (int j = 0; j < o.columns; ++j) {
          for (int k = p; k < p + count_this[i]; ++k)
            s_prod += this.value.get(k) * o.matrix[this.p_column.get(k)][j];
          res[i][j] = s_prod;
          s_prod = 0;
        }
        p += count_this[i];
      }
      else res[i] = zero_row;
    return new DenseMatrix(res);
  }

  /**
   * многопоточное умножение матриц
   *
   * @param o
   * @return
   */
  @Override public Matrix dmul(Matrix o) {
    if (o instanceof DenseMatrix) {
      DenseMatrix res = new DenseMatrix(new double[this.rows][((DenseMatrix) o).columns]);
      try {
        SparseDenseThread t1 = new SparseDenseThread(this, (DenseMatrix) o, res, 0, ((DenseMatrix) o).columns / 4 - 1);
        SparseDenseThread t2 = new SparseDenseThread(this, (DenseMatrix) o, res, ((DenseMatrix) o).columns / 4, 2 * (((DenseMatrix) o).columns / 4) - 1);
        SparseDenseThread t3 = new SparseDenseThread(this, (DenseMatrix) o, res, 2 * (((DenseMatrix) o).columns / 4), 3 * (((DenseMatrix) o).columns / 4) - 1);
        SparseDenseThread t4 = new SparseDenseThread(this, (DenseMatrix) o, res, 3 * (((DenseMatrix) o).columns / 4), ((DenseMatrix) o).columns - 1);
        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t1.join();
        t2.join();
        t3.join();
        t4.join();
      } catch (Exception e) {
        System.out.println(e.getMessage());
      }
      return res;
    } else {
      SparseMatrix res = new SparseMatrix(new ArrayList<Double>(), new ArrayList<Integer>(), new ArrayList<Integer>(), this.rows, ((SparseMatrix) o).columns);
      try {
        int[] arr = new int[this.rows]; //array of number of non-zero elements in each row in this
        for (int i = 0; i < this.rows; ++i)
          arr[i] = 0;
        for (int i = 0; i < this.p_row.size(); ++i)
          arr[this.p_row.get(i)] += 1;
        int k = 0; //number of not empty rows
        for (int i = 0; i < this.rows; ++i)
          if (arr[i] != 0) ++k;
        SparseSparseThread[] t = new SparseSparseThread[k];
        int j = 0, p = 0;
        for (int i = 0; i < this.rows; ++i) {
          if (arr[i] != 0) {
            t[j] = new SparseSparseThread(this, (SparseMatrix) o, res, p, p + arr[i] - 1);
            p += arr[i];
            ++j;
          }
        }
        for (int i = 0; i < k; ++i)
          t[i].start();
        for (int i = 0; i < k; ++i)
          t[i].join();
      } catch (Exception e) {
        System.out.println(e.getMessage());
      }
      return res;
    }
  }

  /**
   * спавнивает с обоими вариантами
   * @param o
   * @return
   */
  @Override public boolean equals(Object o) {
    if (o instanceof DenseMatrix)
      return ((DenseMatrix) o).equals(this);
    else return this.equals((SparseMatrix) o);
  }

  public boolean equals(SparseMatrix o) {
    if (this.value.size() != o.value.size()) return false;
    for (int i = 0; i < this.value.size(); ++i)
      if (Math.abs(this.value.get(i) - o.value.get(i)) > 1e-5 || this.p_row.get(i) != o.p_row.get(i) || this.p_column.get(i) != o.p_column.get(i))
        return false;
    return true;
  }

  @Override public void print(String fileName) {
    try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
      int p = 0;
      for (int i = 0; i < this.rows; ++i) {
        for (int j = 0; j < this.columns; ++j)
          if (p < this.value.size()) {
            if (i == this.p_row.get(p) && j == this.p_column.get(p)) {
              bw.write(Double.toString(this.value.get(p)) + " ");
              ++p;
            } else bw.write("0.0" + " ");
          } else bw.write("0.0" + " ");
        bw.write("\n");
      }
    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }
}
