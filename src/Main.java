import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;


public class Main
{

	public static void main(String[] args) throws FileNotFoundException
	{
		// Initializing instances of the necessary classes
		LevenshteinDistance LD = new LevenshteinDistance();
		Dictionary my_dictionary = new Dictionary ();
		// Filling the dictionary
		my_dictionary.fillDictionary();
		
		/*
		 * 1. Get input
		 * 2. Search the dictionary for length-3 to length+3
		 * 3. Run LD on them and get the top 15 words.
		 * */
		// Taking the user input 
		Scanner jin = new Scanner ( System.in );
		String user_input = jin.next();
		
		// A list for the output
		ArrayList<String> output_list = new ArrayList<String>();
		
		// Giving an error in the length by 3 units
		for ( int i = user_input.length() - 3; i < user_input.length() + 3; i++ )
		{
			ArrayList<String> possibility_list = my_dictionary.dictionary.get(i);
			for ( String s : possibility_list )
			{
				int edit_dist = LD.getLD( s, user_input);
				if (  edit_dist < Global.MAX_DIST && edit_dist != 0 )
					output_list.add( s );
			}
		}
		
		System.out.println ( output_list );
		
	}

}
