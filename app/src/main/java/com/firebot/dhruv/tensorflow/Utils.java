package com.firebot.dhruv.tensorflow;

import android.util.Log;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Utils {

	public static int getIndexOfLargest( float[] array )
	{
		if ( array == null || array.length == 0 ) return -1; // null or empty

		int largest = 0;
		for ( int i = 1; i < array.length; i++ )
		{
			if ( array[i] > array[largest] ) largest = i;
		}
		return largest; // position of the first largest found
	}


	public static void duplicateCount(int[][] data)
	{
		HashMap<Integer, Integer> repetitions = new HashMap<>();

		for (int i = 0; i < 257; ++i) {
			for(int j=0; j<257; ++j) {
				int item = data[i][j];

				if (repetitions.containsKey(item))
					repetitions.put(item, repetitions.get(item) + 1);
				else
					repetitions.put(item, 1);
			}
		}

		// Now let's print the repetitions out
		StringBuilder sb = new StringBuilder();

		int overAllCount = 0;

		for (Map.Entry<Integer, Integer> e : repetitions.entrySet()) {
//			if (e.getValue() > 1) {
			overAllCount += 1;

			sb.append("\n");
			sb.append(e.getKey());
			sb.append(": ");
			sb.append(e.getValue());
			sb.append(" times");
//			}
		}

		if (overAllCount > 0) {
			sb.insert(0, " repeated numbers:");
			sb.insert(0, overAllCount);
			sb.insert(0, "There are ");
		}

		Log.d("Data",sb.toString());

	}


	public static void fillZeroes(int[][] array) {
		if (array == null) {
			return;
		}

		int r;
		for (r = 0; r < array.length; r++) {
			Arrays.fill(array[r], 0);
		}
	}
}
