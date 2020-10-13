package edu.spbu;

import edu.spbu.MatrixGenerator;
import edu.spbu.matrix.DenseMatrix;
import edu.spbu.matrix.Matrix;
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
    Matrix m = m3.mul(m4);
    assertTrue(m3.equals(m));
  }

  @Test
  public void testMul() {
    Matrix m3 = new DenseMatrix("m3.txt");
    Matrix m5 = new DenseMatrix("m5.txt");
    Matrix m = m3.mul(m5);
    Matrix m6 = new DenseMatrix("m6.txt");
    assertTrue(m.equals(m6));
  }
}
