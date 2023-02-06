// SortTools.java
/*
 * EE422C Project 1 submission by
 * Replace <...> with your actual data.
 * <Paawan Desai>
 * <pkd397>
 * <17150>
 * Spring 2023
 * Slip days used: 0
 */

package assignment1;

import java.lang.reflect.Array;
import java.util.Arrays;

public class SortTools {
	/**
	 * Return whether the first n elements of x are sorted in non-decreasing order.
	 * 
	 * @param x is the array
	 * @param n is the size of the input to be checked
	 * @return true if array is sorted
	 */
	public static boolean isSorted(int[] x, int n) {
		// stub only, you write this!
		// TODO: complete it
		if (x.length == 0 || n == 0) {
			return false;
		}
		for (int i = 1; i < n; i++) {
			if (x[i - 1] > x[i]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Return an index of value v within the first n elements of x.
	 * 
	 * @param x is the array
	 * @param n is the size of the input to be checked
	 * @param v is the value to be searched for
	 * @return any index k such that k < n and x[k] == v, or -1 if no such k exists
	 */
	public static int find(int[] x, int n, int v) {
		// stub only, you write this!
		// TODO: complete it
		int mid = 0;
		int low = 0;
		int high = n - 1;
		while (high >= low) {
			mid = (high + low) / 2;
			if (x[mid] < v) {
				low = mid + 1;
			} else if (x[mid] > v) {
				high = mid - 1;
			} else
				return mid;
		}
		return -1;

	}

	/**
	 * Return a sorted, newly created array containing the first n elements of x and
	 * ensuring that v is in the new array.
	 * 
	 * @param x is the array
	 * @param n is the number of elements to be copied from x
	 * @param v is the value to be added to the new array if necessary
	 * @return a new array containing the first n elements of x as well as v
	 */
	public static int[] copyAndInsert(int[] x, int n, int v) {
		// stub only, you write this!
		// TODO: complete it

		if (isSorted(x, n) == false || x.length < 0 || n > x.length || n < 0) {
			return null;
		}
		int[] y = new int[n + 1];
		int temp = 0;
		for (int i = 0; i < n; i++) {
			if (x[i] == v) {
				y = Arrays.copyOfRange(x, 0, n);
				return y;
			}
			if (v >= x[i] && v <= x[i + 1]) {
				temp = i + 1;
				break;
			}
		}
		for (int j = 0; j < n + 1; j++) {
			if (j <= temp - 1) {
				y[j] = x[j];
			} else if (j == temp) {
				y[j] = v;
			} else
				y[j] = x[j - 1];
		}
		return y;
	}

	/**
	 * Insert the value v in the first n elements of x if it is not already there,
	 * ensuring those elements are still sorted.
	 * 
	 * @param x is the array
	 * @param n is the number of elements in the array
	 * @param v is the value to be added
	 * @return n if v is already in x, otherwise returns n+1
	 */
	public static int insertInPlace(int[] x, int n, int v) {
		// stub only, you write this!
		// TODO: complete it
		if (isSorted(x, n) == false || n > x.length || x == null) {
			return -1;
		}
		int temp = 0;
		int[] y = new int[n + 1];
		for (int i = 0; i < n + 1; i++) {
			if (x[i] == v) {
				return n;
			}
			if (x[i] < v && x[i + 1] > v) {
				temp = i + 1;
				break;
			}
		}
		for (int j = 0; j <= n + 1; j++) {
			if (j == temp) {
				x[j] = v;
			}
			if (j > temp)
				y[j - 1] = x[j - 1];
		}
		y = x;
		return n + 1;

	}

	/**
	 * Sort the first n elements of x in-place in non-decreasing order using
	 * insertion sort.
	 * 
	 * @param x is the array to be sorted
	 * @param n is the number of elements of the array to be sorted
	 */
	public static void insertSort(int[] x, int n) {
		// stub only, you write this!
		// TODO: complete it
		int i, j, temp;
		for (i = 1; i <= n; i++) {
			j = i;
			while (j > 0 && x[j] < x[j - 1]) {
				temp = x[j];
				x[j] = x[j - 1];
				x[j - 1] = temp;
				--j;
			}
		}
	}
}
