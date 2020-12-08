package edu.spbu.matrix;

class DenseDenseThread extends Thread {
    private final DenseMatrix m1;
    private final DenseMatrix m2;
    private DenseMatrix res;
    private final int p1;
    private final int q1;
    private final int p2;
    private final int q2;

    public DenseDenseThread(final DenseMatrix m1, final DenseMatrix m2, final DenseMatrix res, final int p1, final int q1,
                            final int p2, final int q2) {
        double[][] a = new double[q1 - p1 + 1][m1.columns];
        for (int i = p1; i <= q1; ++i)
            a[i - p1] = m1.matrix[i];
        this.m1 = new DenseMatrix(a);
        double[][] b = new double[m2.rows][q2 - p2 + 1];
        for (int i = 0; i < m2.rows; ++i)
            for (int j = p2; j <= q2; ++j)
                b[i][j - p2] = m2.matrix[i][j];
        this.m2 = new DenseMatrix(b);
        this.res = res;
        this.p1 = p1;
        this.q1 = q1;
        this.p2 = p2;
        this.q2 = q2;
    }

    @Override
    public void run() {
        try {
            DenseMatrix r = m1.mul(m2);
            for (int i = p1; i <= q1; ++i)
                for (int j = p2; j <= q2; ++j)
                    res.matrix[i][j] = r.matrix[i - p1][j - p2];
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
