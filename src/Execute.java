import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

import Bayes.Driver;
import Bayes.TestData;
import Bayes.Tuple;
import Helpers.Dictionary;

public class Execute
{

	/**
	 * @param args
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException,
			ClassNotFoundException
	{
		Driver d = new Driver();

		TrigramProbGen t = new TrigramProbGen();

		// What we essentially need to do is to take a particular phrase/
		// sentence, and first detect
		// if there is some misspelling. If there is no misspelling, we
		// assume that the sentence is
		// correct. We check the misspelling by searching against the default
		// dictionary present in ubuntu
		Scanner jin = new Scanner(System.in);
		String input = jin.nextLine();
		String[] inputWords = input.split("[ .]");

		int misspelt = -1;
		for (int i = 0; i < inputWords.length; i++)
		{
			inputWords[i] = inputWords[i].toLowerCase();
			if (!Dictionary.exists(inputWords[i]))
				misspelt = i;
		}
		if (misspelt == -1)
		{
			System.out.println("All words are correctly spelt");
			System.exit(0);
		}
		// If there is a misspelling, we need to correct it. So for that, we
		// first need to get a set
		// of candidate replacements. In other words, the confusion set that
		// we define is the list of
		// words that are close to the misspelt word in terms of the results
		// obtained from the first
		// phase of the assignment.
		// Once we have a confusion set, we can apply our method.
		ArrayList<String> confusionSet = TrigramProbGen.cm
				.getSuggestions(inputWords[misspelt]);
		System.out.println(confusionSet);

		// Suppose the original sentence was S = w1, w2, w3 ..... wn, and
		// suppose the misspelt word was
		// wi. Then we look at the confusion set of wi. We replace every word
		// wi' from the confusion set
		// and calculate the probability of the sentence P(S'). The word that
		// is finally replaced/suggested
		// is the word with the highest such probability.
		// Now we need a method to calculate P(S). This is done using the
		// trigrams in the following way.
		// We have P(S) = Sigma P(S,T), where P(S,T) is the probability that
		// blah balh and so on

		String combined = "";
		for (int i = 0; i < inputWords.length; i++)
			combined += inputWords[i] + " ";

		ArrayList<Tuple> corrections = TestData.correct(combined);
		
		// for (Tuple cand : corrections)
		// {
		// System.out.println(cand.word + " " + cand.rank);
		// }
		for (String s : confusionSet)
		{
			//System.out.print(s + " : ");

			inputWords[misspelt] = s;
			Probab p = TrigramProbGen.probOfSentence(inputWords);
			for (Tuple cand : corrections)
			{
				if (cand.word == s)
					cand.trigram_rank = p.numerator; 
			}

		}
		normalizeTuple(corrections);
		for (Tuple cand : corrections)
		{
			System.out.println ( cand.word + " : " + cand.overall_rank);
		}
		

	}
	
	public static void normalizeTuple ( ArrayList<Tuple> corrections )
	{
		long rank = 1;
		BigInteger trigram_rank = BigInteger.ONE;
		BigInteger long_max = new BigInteger(Long.toString(Long.MAX_VALUE));
		
		for ( Tuple cand : corrections )
		{
			if ( cand.rank > rank )
				rank = cand.rank;
			
			if ( cand.trigram_rank.compareTo(trigram_rank) > 0 )
			{
				trigram_rank = cand.trigram_rank;
			}
		}
		for ( Tuple cand : corrections )
		{
			cand.rank = cand.rank * (Long.MAX_VALUE / rank);
			
			cand.trigram_rank = cand.trigram_rank.multiply(long_max).divide(trigram_rank);
		}
		for ( Tuple cand : corrections )
		{
			cand.overall_rank = cand.trigram_rank.add(new BigInteger(Long.toString(cand.rank)));
		}
		Collections.sort(corrections);
		
	}
}
