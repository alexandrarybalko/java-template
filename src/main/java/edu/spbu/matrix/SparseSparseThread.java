package edu.spbu.matrix;

import java.util.ArrayList;

public class SparseSparseThread extends Thread {
    private final SparseMatrix m1;
    private final SparseMatrix m2;
    private double[] res;
    private final int p;
    private final int q;

    public SparseSparseThread(final SparseMatrix mm1, final SparseMatrix m2, final double[] res, final int p, final int q) {
        ArrayList<Double> v = new ArrayList<>();
        ArrayList<Integer> r = new ArrayList<>();
        ArrayList<Integer> c = new ArrayList<>();
        for (int i = p; i <= q; ++i) {
            v.add(mm1.value.get(i));
            r.add(mm1.p_row.get(0));
            c.add(mm1.p_column.get(i));
        }
        this.m1 = new SparseMatrix(v, r, c, 1, mm1.columns);
        this.m2 = m2;
        this.res = res;
        this.p = p;
        this.q = q;
    }

    @Override
    public void run() {
        try {
            SparseMatrix r = m1.mul(m2);
            int j = 0;
            for (int i = 0; i < res.length; ++i)
                if (r.value.size() == 0 || j == r.p_column.size() || r.p_column.get(j) != i)
                    res[i] = 0;
                else {
                    res[i] = r.value.get(j);
                    ++j;
                }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
