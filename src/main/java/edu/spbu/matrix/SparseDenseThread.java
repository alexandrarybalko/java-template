package edu.spbu.matrix;

public class SparseDenseThread extends Thread {
    private final SparseMatrix m1;
    private final DenseMatrix m2;
    private DenseMatrix res;
    private final int p;
    private final int q;

    public SparseDenseThread(final SparseMatrix m1, final DenseMatrix m2, final DenseMatrix res, final int p, final int q) {
        this.m1 = m1;
        double[][] b = new double[m2.rows][q - p + 1];
        for (int i = 0; i < m2.rows; ++i)
            for (int j = p; j <= q; ++j)
                b[i][j - p] = m2.matrix[i][j];
        this.m2 = new DenseMatrix(b);
        this.res = res;
        this.p = p;
        this.q = q;
    }

    @Override
    public void run() {
        try {
            DenseMatrix r = m1.mul(m2);
            for (int i = 0; i < res.rows; ++i)
                for (int j = p; j <= q; ++j)
                    res.matrix[i][j] = r.matrix[i][j - p];
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
