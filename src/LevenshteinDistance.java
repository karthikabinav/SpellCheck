/*
 * Levenshtein distance Class
 * Authors : Abhiram R ( CS10B060 )
 * 			 Smit Mehta ( CS10B024 )
 * 			 Karthik Abinav S ( CS10B057 )
 * 
 * */

public class LevenshteinDistance 
{
	public int getLD(Object object1, Object object2)
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
}
