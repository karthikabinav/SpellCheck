package Bayes;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import Helpers.ConfusionMatrix;
import Helpers.Dictionary;
import Helpers.Global;
import Helpers.LevenshteinDistance;

//Class that takes the test data and returns the result based on Bayesian probabilities
public class TestData {
	// Input a sentence with possibly a spelling mistake
	// Output the correct sentence with the spelling mistake corrected
	private static ConfusionMatrix matrix;
	private static LevenshteinDistance LD;
	private static PartsOfSpeech post;

	public TestData() throws FileNotFoundException {
		matrix = new ConfusionMatrix();
		LD = new LevenshteinDistance();
		post = new PartsOfSpeech();
	}

	private static ArrayList<String> getCandidates(String input) {
		ArrayList<String> suggestions = new ArrayList<String>();

		for (int i = Math.max(input.length() - 3, 1); i < input.length() + 3; i++) {
			ArrayList<String> possibility_list = Dictionary.dictionary.get(i);
			for (String s : possibility_list) {
				int edit_dist = LD.getLD(s, input, matrix);

				if (edit_dist < Global.MAX_DIST) {
					suggestions.add(s);

				}
			}
		}
		return suggestions;
	}

	public static String correct(String input) throws FileNotFoundException {

		String correction = input;
		StringTokenizer tokenizer = new StringTokenizer(input, " !?.;,");

		String misSpelt = "";
		String misSpeltPPOS = "";
		ArrayList<String> contextWord = new ArrayList<String>();
		ArrayList<String> coOccurenceWord = new ArrayList<String>();

		ArrayList<String> contextPOS = new ArrayList<String>();
		ArrayList<String> coOccurencePOS = new ArrayList<String>();

		boolean beforeOccurence = true; // Assume a sentence has just one
										// spelling error
		// TODO: For more than one a repeated guess and correct learn method
		// will be followed
		while (tokenizer.hasMoreElements()) {
			String token = tokenizer.nextToken();

			ArrayList<String> possibility_list = Dictionary.dictionary
					.get(token.length());
			for (String str : possibility_list) {
				int edit_dist = LD.getLD(str, token, matrix);

				if (edit_dist == 0) {
					if (beforeOccurence)
						contextWord.add(token);
					String POS;
					if (post.wordPOSMap.containsKey(token))
						POS = post.wordPOSMap.get(token);
					else {
						post.wordPOSMap.put(token, "UND"); // code for undefined
						POS = "UND";
					}
					if (beforeOccurence)
						contextPOS.add(POS);
					coOccurenceWord.add(token);
					coOccurencePOS.add(POS);
				} else {
					beforeOccurence = false;
					misSpelt = token;

					if (post.wordPOSMap.containsKey(token))
						misSpeltPPOS = post.wordPOSMap.get(token);
					else {
						post.wordPOSMap.put(token, "UND"); // code for undefined
						misSpeltPPOS = "UND";
					}
				}

			}

		}

		// Once the list of word to be corrected, context words, collocation
		// words, context POS and
		// collocation POS is found, we need to find the candidates and then
		// find the respective probabilities

		// Getting candidates baseed on Edit distance
		ArrayList<String> candidates = getCandidates(misSpelt);

		return correction;
	}

	public static void main(String args[]) throws FileNotFoundException {
		correct("How's Going?");
	}
}
