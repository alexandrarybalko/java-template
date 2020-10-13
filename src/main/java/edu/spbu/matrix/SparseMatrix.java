package edu.spbu.matrix;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Разряженная матрица
 */
public class SparseMatrix implements Matrix
{
  public ArrayList<Double> value;
  private ArrayList<Integer> row;
  private ArrayList<Integer> column;
  /**
   * загружает матрицу из файла
   * @param fileName
   */
  public SparseMatrix(String fileName) throws IOException{
    ArrayList<Double> v = new ArrayList<>();
    ArrayList<Integer> r = new ArrayList<>();
    ArrayList<Integer> c = new ArrayList<>();
    try (Scanner s = new Scanner(new FileReader(new File(fileName)))){
      while (s.hasNext()) {
        int i = 0;
        while (s.next() != "\n") {
          int j = 0;
          if (s.nextDouble() != 0) {
            v.add(s.nextDouble());
            r.add(i);
            c.add(j);
          }
          ++j;
        }
        ++i;
      }
    } catch (IOException e) {
      System.out.println("Error with reading the file");
      e.printStackTrace();
    }
    this.value = v;
    this.row = r;
    this.column = c;
  }
  /**
   * однопоточное умнджение матриц
   * должно поддерживаться для всех 4-х вариантов
   *
   * @param o
   * @return
   */
  @Override public Matrix mul(Matrix o)
  {
    return null;
  }

  /**
   * многопоточное умножение матриц
   *
   * @param o
   * @return
   */
  @Override public Matrix dmul(Matrix o)
  {
    return null;
  }

  /**
   * спавнивает с обоими вариантами
   * @param o
   * @return
   */
  @Override public boolean equals(Object o) {
    return false;
  }

  @Override public void print(String fileName) { }
}
