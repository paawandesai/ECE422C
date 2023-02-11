package PA1;

import java.util.Arrays;

/*
 * This file is how you might test out your code.  Don't submit this, and don't
 * have a main method in SortTools.java.
 */



public class Main {
    public static void main(String [] args) {
        // call your test methods here
        // SortTools.isSorted() etc.
    	int [] x = new int[4];
    	x[0]=1;
    	x[1]=3;
    	x[2]=3;
    	x[3]=7;
    	int n=4;
    	int v=3;
    	int [] y = new int[4];
    	y[0]=7;
    	y[1]=3;
    	y[2]=1; 
    	y[3]=3;
    	int n2=3;
    	boolean issorted = SortTools.isSorted(x, n);
    	System.out.print("IsSorted Test: ");
    	System.out.println(issorted);
    	int find = SortTools.find(x, n, v);
    	System.out.print("Find Test: ");
    	System.out.println(find);
    	int [] copyandinsert = SortTools.copyAndInsert(x, 3, 4);
    	System.out.print("CopyandInsert Test: ");
    	System.out.println(Arrays.toString(copyandinsert));
    	System.out.print("Printing x: ");
    	System.out.println(Arrays.toString(x));
    	int insertinplace = SortTools.insertInPlace(x, 3, 4);
    	System.out.print("InsertinPlace Test: ");
    	System.out.println(insertinplace);
    	System.out.print("Printing x: ");
    	System.out.println(Arrays.toString(x));
    	System.out.print("InsertSort Test: ");
    	SortTools.insertSort(y, n2);
    	System.out.print("Printing y: ");
    	System.out.println(Arrays.toString(y));
    	
    	
    }
}
