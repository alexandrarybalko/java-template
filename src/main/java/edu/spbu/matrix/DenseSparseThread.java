package edu.spbu.matrix;

class DenseSparseThread extends Thread {
    private final DenseMatrix m1;
    private final SparseMatrix m2;
    private DenseMatrix res;
    private final int p;
    private final int q;

    public DenseSparseThread(final DenseMatrix m1, final SparseMatrix m2, final DenseMatrix res, final int p, final int q) {
        double[][] a = new double[q - p + 1][m1.columns];
        for (int i = p; i <= q; ++i)
            a[i - p] = m1.matrix[i];
        this.m1 = new DenseMatrix(a);
        this.m2 = m2;
        this.res = res;
        this.p = p;
        this.q = q;
    }

    @Override
    public void run() {
        try {
            DenseMatrix r = m1.mul(m2);
            for (int i = p; i <= q; ++i)
                res.matrix[i] = r.matrix[i - p];
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
