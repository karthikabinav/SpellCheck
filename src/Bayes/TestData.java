package Bayes;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import Helpers.ConfusionMatrix;
import Helpers.Dictionary;
import Helpers.Global;
import Helpers.LevenshteinDistance;

// Class that takes the test data and returns the result based on Bayesian
// probabilities
public class TestData
{
	// Input a sentence with possibly a spelling mistake
	// Output the correct sentence with the spelling mistake corrected
	private static ConfusionMatrix matrix;
	private static LevenshteinDistance LD;

	public static void init() throws FileNotFoundException
	{
		matrix = new ConfusionMatrix();
		LD = new LevenshteinDistance();
	}

	// intersection of the words context with the current context
	// TODO: the function definition
	private static long getIntersection(String word, String POSTag,
			ArrayList<String> contextWord, ArrayList<String> contextPOS,
			ArrayList<String> coOccurenceWord)
	{

		POST p = new POST();
		p.word = word;
		p.POSTag = POSTag;

		long rank = 0;
		ArrayList<POST> thisWordContext = LearningContext.context.get(p);
		if (thisWordContext != null)
		{
			for (POST loop : thisWordContext)
			{

				for (String s : contextWord)
				{
					if (s.equals(loop.word))
						rank++;
				}
				for (String s : contextPOS)
				{
					if (s.equals(loop.POSTag))
						rank++;
				}
			}
		}

		StringCounter thisWordCollocation = CollocationLearning.collocation
				.get(word);

		long colCount = 0;
		if (thisWordCollocation != null)
		{
			for (String loop : thisWordCollocation.words)
			{

				for (String s : coOccurenceWord)
				{
					if (s.equals(loop))
						colCount++;
				}
			}
		}
		colCount *= (thisWordCollocation.count);
		rank += colCount;
		return rank;
	}

	private static ArrayList<String> getCandidates(String input)
	{
		ArrayList<String> suggestions = new ArrayList<String>();

		for (int i = Math.max(input.length() - 3, 1); i < input.length() + 3; i++)
		{
			ArrayList<String> possibility_list = Dictionary.dictionary.get(i);
			for (String s : possibility_list)
			{
				int edit_dist = LD.getLD(s, input, matrix);

				if (edit_dist < Global.MAX_DIST)
				{
					suggestions.add(s);

				}
			}
		}
		return suggestions;
	}

	public static ArrayList<Tuple> correct(String input)
			throws FileNotFoundException
	{
//		System.out.println("TestData -- " + input);

		StringTokenizer tokenizer = new StringTokenizer(input, " !?.;,");

		String misSpelt = "";
		String misSpeltPPOS = "";
		ArrayList<String> contextWord = new ArrayList<String>();
		ArrayList<String> coOccurenceWord = new ArrayList<String>();

		ArrayList<String> contextPOS = new ArrayList<String>();

		boolean beforeOccurence = true; // Assume a sentence has just one
										// spelling error
		// TODO: For more than one a repeated guess and correct learn method
		// will be followed
		while (tokenizer.hasMoreElements())
		{
			String token = tokenizer.nextToken();

			ArrayList<String> possibility_list = Dictionary.dictionary
					.get(token.length());

			boolean found = false;
			for (String str : possibility_list)
			{
				str = str.replaceAll("[^a-zA-Z]", "");
				token = token.replaceAll("[^a-zA-Z]", "");
				token = token.toLowerCase();

				int edit_dist = LD.getLD(str, token, matrix);
				if (edit_dist == 0)
				{
					found = true;
					break;
				}
			}
			if (!found)
			{
				misSpelt = token;
				beforeOccurence = false;

			} else
			{
				if (beforeOccurence)
					contextWord.add(token);
				String POS;
				if (PartsOfSpeech.wordPOSMap.containsKey(token))
					POS = PartsOfSpeech.wordPOSMap.get(token);
				else
				{
					PartsOfSpeech.wordPOSMap.put(token, "UND"); // code for
					POS = "UND";
				}
				if (beforeOccurence)
					contextPOS.add(POS);
				coOccurenceWord.add(token);

			}

		}

		// Once the list of word to be corrected, context words, collocation
		// words, context POS and
		// collocation POS is found, we need to find the candidates and then
		// find the respective probabilities

		// Getting candidates baseed on Edit distance
//		System.out.println("Misspelt " + misSpelt);
		ArrayList<String> candidates = getCandidates(misSpelt);

		ArrayList<Tuple> correction = new ArrayList<Tuple>();
		for (String cand : candidates)
		{
			misSpeltPPOS = PartsOfSpeech.wordPOSMap.get(cand);
			long rank = 0;
			if (misSpeltPPOS != null)
				rank = getIntersection(cand, misSpeltPPOS, contextWord,
						contextPOS, coOccurenceWord);
			Tuple t = new Tuple();
			t.word = cand;
			t.rank = rank;

			// if (rank != 0)
			correction.add(t);
		}

		return correction;
	}
}
