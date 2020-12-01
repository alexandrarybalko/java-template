package edu.spbu;

import edu.spbu.MatrixGenerator;
import edu.spbu.matrix.DenseMatrix;
import edu.spbu.matrix.Matrix;
import edu.spbu.matrix.SparseMatrix;
import org.junit.After;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MatrixGeneratorTest
{
  String fileName="test.txt";
  @After
  public void cleanUp() {
    File f = new File(fileName);
    if(f.exists())
      f.delete();
  }

  @Test
  public void testGenerate() throws Exception {
    new MatrixGenerator(1,3,fileName,10).generate();
    BufferedReader reader = new BufferedReader(new FileReader(fileName));
    int lineCount=0;
    int emptyLineCount=0;
    for (String line = reader.readLine();line !=null; line = reader.readLine()) {
      if (line.equals("0 0 0 0 0 0 0 0 0 0")) {
        emptyLineCount++;
      } else {
        assertEquals(10, line.split(" ").length);
      }
      lineCount++;
    }
    assertTrue(emptyLineCount>1);
    assertEquals(10,lineCount);
  }

  @Test
  public void testMulUnit() {
    Matrix m3 = new DenseMatrix("m3.txt");
    Matrix m4 = new DenseMatrix("m4.txt");
    assertEquals(m3, m3.mul(m4));
  }

  @Test
  public void testMulDD() {
    Matrix m3 = new DenseMatrix("m3.txt");
    Matrix m5 = new DenseMatrix("m5.txt");
    Matrix m6 = new DenseMatrix("m6.txt");
    assertEquals(m3.mul(m5), m6);
  }

  @Test
  public void testMulSS() {
    Matrix m7 = new SparseMatrix("m7.txt");
    Matrix m9 = new SparseMatrix("m9.txt");
    assertEquals(m7.mul(m7), m9);
  }

  @Test
  public void testMulDS() {
    Matrix m7 = new SparseMatrix("m7.txt");
    Matrix m8 = new DenseMatrix("m8.txt");
    Matrix m11 = new DenseMatrix("m11.txt");
    assertEquals((m8).mul(m7), m11);
  }

  @Test
  public void testMulSD() {
    Matrix m7 = new SparseMatrix("m7.txt");
    Matrix m8 = new DenseMatrix("m8.txt");
    Matrix m10 = new DenseMatrix("m10.txt");
    assertEquals(m7.mul(m8), m10);
  }

  @Test
  public void equalsDS() {
    Matrix m7 = new SparseMatrix("m7.txt");
    Matrix m7_ = new DenseMatrix("m7.txt");
    assertEquals(m7_, m7);
  }

  @Test
  public void equalsSS() {
    Matrix m7 = new SparseMatrix("m7.txt");
    Matrix m7_ = new SparseMatrix("m7.txt");
    assertEquals(m7_, m7);
  }

  @Test
  public void dmulDD() {
    Matrix m3 = new DenseMatrix("m3.txt");
    Matrix m5 = new DenseMatrix("m5.txt");
    Matrix m6 = new DenseMatrix("m6.txt");
    assertEquals(m3.dmul(m5), m6);
  }

  @Test
  public void dmulDS() {
    Matrix m7 = new SparseMatrix("m7.txt");
    Matrix m8 = new DenseMatrix("m8.txt");
    Matrix m11 = new DenseMatrix("m11.txt");
    assertEquals((m8).dmul(m7), m11);
  }

  @Test
  public void dmulSD() {
    Matrix m7 = new SparseMatrix("m7.txt");
    Matrix m8 = new DenseMatrix("m8.txt");
    Matrix m10 = new DenseMatrix("m10.txt");
    assertEquals(m7.dmul(m8), m10);
  }

  @Test
  public void dmulSS() {
    Matrix m7 = new SparseMatrix("m7.txt");
    Matrix m9 = new SparseMatrix("m9.txt");
    assertEquals(m7.dmul(m7), m9);
  }

  @Test
  public void dmulSS2() {
    Matrix m7 = new SparseMatrix("m7.txt");
    Matrix m9 = new SparseMatrix("m9.txt");
    Matrix m12 = new SparseMatrix("m12.txt");
    assertEquals(m7.dmul(m9), m12);
  }
}
