package Helpers;
import java.math.BigInteger;

/*
 * Levenshtein distance Class
 * Authors : Abhiram R ( CS10B060 )
 * 			 Smit Mehta ( CS10B024 )
 * 			 Karthik Abinav S ( CS10B057 )
 * 
 * */

public class LevenshteinDistance {
	public BigInteger prob = new BigInteger("1");

	public int getLD(Object object1, Object object2, ConfusionMatrix matrix) {
		String firstString = (String) object1;
		String secondString = (String) object2;

		prob = new BigInteger("1");

		// System.out.println(matrix.AddMatrix[0][0]);
		char char1;
		char char2;

		int cost;

		int length1 = firstString.length();
		int length2 = secondString.length();

		if (length1 == 0)
			return length2;
		if (length2 == 0)
			return length1;

		int[][] distance = new int[length1 + 1][length2 + 1];

		for (int i = 0; i <= length1; i++)
			distance[i][0] = i;
		for (int j = 0; j <= length2; j++)
			distance[0][j] = j;

		for (int i = 1; i <= length1; i++) {
			char1 = firstString.charAt(i - 1);
			for (int j = 1; j <= length2; j++) {
				char2 = secondString.charAt(j - 1);
				if (char1 == char2)
					cost = 0;
				else
					cost = 1;

				int val1 = distance[i - 1][j] + 1; // deletion
				int val2 = distance[i][j - 1] + 1; // Addition
				int val3 = distance[i - 1][j - 1] + cost; // Subsituition
				if (val1 <= val2 && val1 <= val3) {
					distance[i][j] = val1;
					int c1 = firstString.charAt(i - 1) - 'a';
					int c2;
					if (j == length2)
						c2 = 26;
					else
						c2 = secondString.charAt(j) - 'a';

					prob = prob.multiply(new BigInteger(
							matrix.DelMatrix[c1][c2] + ""));
				} else if (val2 <= val1 && val2 <= val3) {
					distance[i][j] = val2;
					int c1, c2;
					if (i == length1)
						c1 = 26;
					else
						c1 = firstString.charAt(i) - 'a';

					c2 = secondString.charAt(j - 1) - 'a';
					prob = prob.multiply(new BigInteger(
							matrix.AddMatrix[c1][c2] + ""));

				} else {
					distance[i][j] = val3;

					prob = prob
							.multiply(new BigInteger(
									matrix.SubMatrix[firstString.charAt(i - 1) - 'a'][secondString
											.charAt(j - 1) - 'a'] + ""));

				}

			}
		}
		return distance[length1][length2];
	}
}
