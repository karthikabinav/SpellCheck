package Helpers;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Scanner;

class Pair {
	public String word;
	public BigInteger prob;

}

public class Main {
	public static String corpusFile = "corpus";

	public static void main(String[] args) throws FileNotFoundException {
		// Initializing instances of the necessary classes
		LevenshteinDistance LD = new LevenshteinDistance();
		ConfusionMatrix matrix = new ConfusionMatrix();
		matrix.init();

		/*
		 * 1. Get input 2. Search the dictionary for length-3 to length+3 3. Run
		 * LD on them and get the top 15 words.
		 */
		// Taking the user input
		Scanner jin = new Scanner(System.in);
		String user_input = jin.next();
		user_input = user_input.toLowerCase();

		// A list for the output
		ArrayList<String> output_list = new ArrayList<String>();
		ArrayList<Pair> suggestions = new ArrayList<Pair>();

		// Giving an error in the length by 3 units
		for (int i = Math.max(user_input.length() - 3, 1); i < user_input
				.length() + 3; i++) {
			ArrayList<String> possibility_list = Dictionary.dictionary.get(i);
			for (String s : possibility_list) {
				int edit_dist = LD.getLD(s, user_input, matrix);
				Pair p = new Pair();
				// int edit_dist = 0;
				if (edit_dist == 0) {
					System.out.println("Word is already correct");
					System.exit(0);
				}
				if (edit_dist < Global.MAX_DIST) {
					output_list.add(s);
					p.prob = LD.prob;
					p.word = s;
					suggestions.add(p);
				}
			}
		}
		// Once we have the output list, we need to assign probabilities to them
		// The one with the highest probability will be displayed as correction
		// System.out.println(LD.prob);
		for (Pair p : suggestions) {
			System.out.println("Word: " + p.word + " " + "Prob: " + p.prob);
		}

		// Probabilites given in terms of BigInteger numbers. The one with the
		// highest will be
		// chosen as the suggestion
		jin.close();
	}

}
