package edu.spbu.sort;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by artemaliev on 07/09/15.
 */
public class IntSort {

  public static void sort (int[] arr) {
    if (arr.length != 0) qsort(arr, 0, arr.length - 1);
  }

  public static void qsort (int[] arr, int left, int right) {
    int x = arr[(right + left)/2], t;
    int i = left, j = right;
    while (i <= j) {
      while (arr[i] < x) ++i;
      while (arr[j] > x) --j;
      if (i <= j) {
        t = arr[i];
        arr[i] = arr[j];
        arr[j] = t;
        ++i; --j;
      }
    }
    if (left < i - 1) qsort(arr, left, i - 1);
    if (right > i) qsort(arr, i, right);
  }

  public static void sort (List<Integer> list) {
    Collections.sort(list);
  }

}
