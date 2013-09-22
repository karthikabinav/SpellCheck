package Helpers;

import java.math.BigInteger;

/*
 * Levenshtein distance Class
 * Authors : Abhiram R ( CS10B060 )
 * 			 Smit Mehta ( CS10B024 )
 * 			 Karthik Abinav S ( CS10B057 )
 * 
 * */

public class LevenshteinDistance
{

	public static BigInteger prob[][] = new BigInteger[1005][1005];
	public static int length1;
	public static int length2;

	public int getLDnoConfusion(Object object1, Object object2)
	{
		String firstString = (String) object1;
		String secondString = (String) object2;
		
		char char1;
		char char2;
		
		int cost;
		
		int length1 = firstString.length();
		int length2 = secondString.length();
		
		if(length1 == 0) return length2;
		if(length2 == 0) return length1;
		
		int[][] distance = new int[length1+1][length2+1];
		
		for (int i = 0; i <= length1; i++)
	        distance[i][0] = i;
	    for (int j = 0; j <= length2; j++)
	        distance[0][j] = j;
	    
	    for (int i = 1; i <= length1; i++)
        {
            char1 = firstString.charAt (i - 1);
            for (int j = 1; j <= length2; j++)
            {
                char2 = secondString.charAt(j - 1);
                if (char1 == char2) cost = 0;
                else cost = 1;
                    
                distance[i][j] = Math.min(Math.min(distance[i-1][j]+1, distance[i][j-1]+1), distance[i-1][j-1] + cost);
            }
        }
	    return distance[length1][length2];
	}
	
	public int getLD(Object object1, Object object2, ConfusionMatrix matrix)
	{
		String firstString = (String) object1;
		String secondString = (String) object2;

		// System.out.println(matrix.AddMatrix[0][0]);
		char char1;
		char char2;

		int cost;

		length1 = firstString.length();
		length2 = secondString.length();

		if (length1 == 0)
			return length2;
		if (length2 == 0)
			return length1;
		if (length1 >= 1005 || length2 >= 1005)
			return -1;

		for (int i = 0; i <= length1; i++)
		{
			for (int j = 0; j <= length2; j++)
				prob[i][j] = new BigInteger("1");
		}
		int[][] distance = new int[length1 + 1][length2 + 1];

		for (int i = 1; i <= length1; i++)
		{
			distance[i][0] = i;
			if (i == 1)
				prob[i][0] = prob[i - 1][0].multiply(new BigInteger(
						matrix.DelMatrix[26][firstString.charAt(i - 1) - 'a']
								+ ""));
			else
				prob[i][0] = prob[i - 1][0]
						.multiply(new BigInteger(
								matrix.DelMatrix[firstString.charAt(i - 2) - 'a'][firstString
										.charAt(i - 1) - 'a'] + ""));

		}
		for (int j = 1; j <= length2; j++)
		{
			distance[0][j] = j;

			if (j == 1)
				prob[0][j] = prob[0][j - 1].multiply(new BigInteger(
						matrix.AddMatrix[26][secondString.charAt(j - 1) - 'a']
								+ ""));
			else
				prob[0][j] = prob[0][j - 1]
						.multiply(new BigInteger(matrix.AddMatrix[secondString
								.charAt(j - 2) - 'a'][secondString
								.charAt(j - 1) - 'a']
								+ ""));
		}

		for (int i = 1; i <= length1; i++)
		{
			char1 = firstString.charAt(i - 1);
			for (int j = 1; j <= length2; j++)
			{
				char2 = secondString.charAt(j - 1);
				if (char1 == char2)
					cost = 0;
				else
					cost = 1;

				int val1 = distance[i - 1][j] + 1; // deletion
				int val2 = distance[i][j - 1] + 1; // Addition
				int val3 = distance[i - 1][j - 1] + cost; // Subsituition
				if (val1 <= val2 && val1 <= val3)
				{
					distance[i][j] = val1;
					int c1 = firstString.charAt(i - 1) - 'a';
					int c2;
					if (j == length2)
						c2 = 26;
					else
						c2 = secondString.charAt(j) - 'a';

					prob[i][j] = prob[i - 1][j].multiply(new BigInteger(
							matrix.DelMatrix[c2][c1] + ""));

				} else if (val2 <= val1 && val2 <= val3)
				{
					distance[i][j] = val2;
					int c1, c2;
					if (i == length1)
						c1 = 26;
					else
						c1 = firstString.charAt(i) - 'a';

					c2 = secondString.charAt(j - 1) - 'a';
					prob[i][j] = prob[i][j - 1].multiply(new BigInteger(
							matrix.AddMatrix[c1][c2] + ""));

				} else
				{
					distance[i][j] = val3;

					prob[i][j] = prob[i - 1][j - 1]
							.multiply(new BigInteger(
									matrix.SubMatrix[firstString.charAt(i - 1) - 'a'][secondString
											.charAt(j - 1) - 'a'] + ""));

				}

			}
		}
		return distance[length1][length2];
	}
}
