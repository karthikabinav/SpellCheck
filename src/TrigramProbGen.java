import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;

import com.sun.corba.se.impl.resolver.SplitLocalResolverImpl;

public class TrigramProbGen
{
	/* Hashtables to keep track of the counts of biwords and triwords */
	public static Hashtable<Biword, Integer> biwordCount;
	public static Hashtable<Triword, Integer> triwordCount;
	public static Dictionary dict;
	public static ConfusionMatrix cm;

	/*
	 * Hashtable to keep track of the parts of speech for a given word and words
	 * for a given part of speech
	 */
	public static Hashtable<String, ArrayList<String>> partsOfSpeech;
	public static Hashtable<String, ArrayList<String>> wordsOfPos;

	/* In this function, we initialize the above four hashtables */
	public static void initialize() throws FileNotFoundException
	{
		// Allocating Memory
		biwordCount = new Hashtable<Biword, Integer>();
		triwordCount = new Hashtable<Triword, Integer>();
		partsOfSpeech = new Hashtable<String, ArrayList<String>>();
		wordsOfPos = new Hashtable<String, ArrayList<String>>();

		// Creating and filling a dictionary
		cm = new ConfusionMatrix();
		cm.init();
		dict = new Dictionary();

		// Reading the corpus
		File filenames = new File("brown/filenames");
		Scanner files_in = new Scanner(filenames);

		// while (files_in.hasNext())
		{
			String next_file = "ce08";
			// String next_file = files_in.next();
			File f = new File("brown/" + next_file);
			Scanner posin = new Scanner(f);

			while (posin.hasNext())
			{
				// Here is where we must build the Parts of speech
				String s = posin.next();
				String[] s_split = s.split("[/+]");
				// s_split[1+] contains the pos tag for string
				for (int i = 0; i < s_split.length; i++)
					s_split[i] = s_split[i].toLowerCase();

				addToPartsOfSpeech(s_split);
				addToWordsOfPos(s_split);
			}

			posin = new Scanner(f);

			while (posin.hasNext())
			{
				// Here is where we must build the triword and biword counts
				String s = posin.nextLine();
				String[] s_split = s.split(" ");
				// Starting from 3 because we want to ignore the tab in the
				// front
				// of the line
				for (int i = 3; i < s_split.length; i++)
				{
					String word_1 = partsOfSpeech.get(
							s_split[i - 2].split("/")[0].toLowerCase()).get(0);
					String word_2 = partsOfSpeech.get(
							s_split[i - 1].split("/")[0].toLowerCase()).get(0);
					String word_3 = partsOfSpeech.get(
							s_split[i].split("/")[0].toLowerCase()).get(0);

					Triword tw = new Triword(word_1, word_2, word_3);
					Biword bw = new Biword(word_1, word_2);

					updateTriwordCount(tw);
					updateBiwordCount(bw);

					if (i == s_split.length - 1)
					{
						Biword biword = new Biword(word_2, word_3);
						updateBiwordCount(biword);
					}
				}

			}
			System.out.println("Done with file " + next_file);
		}
		System.out.println(wordsOfPos);
	}

	public static void main(String[] args) throws FileNotFoundException
	{
		// In the initialize phase, we learn the trigram probabilities for
		// parts of speech
		// and fill in a hashtable that contains the mapping from each word
		// to a list of
		// parts of speech that it is associated with. We also fill up the
		// trigram and bigram counts
		// for all these parts of speech combinations.
		initialize();
		
		// What we essentially need to do is to take a particular phrase/
		// sentence, and first detect
		// if there is some misspelling. If there is no misspelling, we
		// assume that the sentence is
		// correct. We check the misspelling by searching against the default
		// dictionary present in ubuntu
		Scanner jin = new Scanner ( System.in );
		String input = jin.nextLine();
		String[] inputWords = input.split("[ .]");
		
		int misspelt = -1;
		for ( int i = 0; i < inputWords.length; i++ )
		{
			inputWords[i] = inputWords[i].toLowerCase();
			if ( !Dictionary.exists(inputWords[i])) misspelt = i;
		}
		if ( misspelt == -1 ) 
		{
			System.out.println ( "All words are correctly spelt");
			System.exit(0);
		}
		
		ArrayList<String> confusionSet = cm.getSuggestions(inputWords[misspelt]);
		System.out.println (confusionSet);
		
		
		// If there is a misspelling, we need to correct it. So for that, we
		// first need to get a set
		// of candidate replacements. In other words, the confusion set that
		// we define is the list of
		// words that are close to the misspelt word in terms of the results
		// obtained from the first
		// phase of the assignment.
		// Once we have a confusion set, we can apply our method.
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
		
		

	}

	public static Probab probOfTwGivenBw(Triword tw, Biword bw)
	{
		if (!tw.contains(bw))
		{
			System.out.println("Biword not in Triword. Fix the bulb");
			System.exit(1);
		}
		if (triwordCount.containsKey(tw))
			return new Probab ( bigInt(triwordCount.get(tw) + 1) , bigInt( biwordCount.get(bw) + Dictionary.size));
		else
			return new Probab ( bigInt(1) , bigInt( biwordCount.get(bw) + Dictionary.size));

	}

	public static Probab probOfWGivenT(String w, String t)
	{
		// If the tag is not part of our learning, assign equal weights to all
		if (!wordsOfPos.contains(t))
		{
			return new Probab(bigInt(1), bigInt(Dictionary.size));
		}
		ArrayList<String> list = wordsOfPos.get(t);
		int word_count = 0;
		for (String word : list)
			if (word.equals(w))
				word_count++;

		// Here we also account for smoothing
		return new Probab(new BigInteger(Integer.toString(word_count + 1)),
				new BigInteger(Integer.toString(list.size() + Dictionary.size)));
	}

	private static void test()
	{
		Biword bw = new Biword("a", "b");
		Biword bw2 = new Biword("a", "b");

		Triword tw = new Triword("a", "b", "c");
		Triword tw2 = new Triword("a", "b", "c");

		updateBiwordCount(bw2);
		updateBiwordCount(bw);

		updateTriwordCount(tw2);
		updateTriwordCount(tw);

		System.out.println(biwordCount);
		System.out.println(triwordCount);
	}

	private static void updateBiwordCount(Biword bw)
	{
		/*
		 * If the biword hashtable contains the Biword, update the count Else
		 * insert newly into it
		 */
		if (biwordCount.containsKey(bw))
		{
			int count = biwordCount.get(bw);
			count++;
			biwordCount.put(bw, count);
		} else
		{
			biwordCount.put(bw, 1);
		}

	}

	private static void updateTriwordCount(Triword tw)
	{
		/*
		 * If the triword hashtable contains the Triword, update the count Else
		 * insert newly into it
		 */
		if (triwordCount.containsKey(tw))
		{
			int count = triwordCount.get(tw);
			count++;
			triwordCount.put(tw, count);
		} else
		{
			triwordCount.put(tw, 1);
		}

	}

	private static void addToPartsOfSpeech(String[] s)
	{
		for (int i = 1; i < s.length; i++)
		{
			if (partsOfSpeech.contains(s[i]))
				partsOfSpeech.get(s[0]).add(s[i]);
			else
			{
				ArrayList<String> list = new ArrayList<String>();
				list.add(s[i]);
				partsOfSpeech.put(s[0], list);
			}
		}

	}

	private static void addToWordsOfPos(String[] s)
	{
		for (int i = 1; i < s.length; i++)
		{
			if (wordsOfPos.containsKey(s[i]))
				wordsOfPos.get(s[i]).add(s[0]);
			else
			{
				ArrayList<String> list = new ArrayList<String>();
				list.add(s[0]);
				wordsOfPos.put(s[i], list);
			}
		}

	}

	/* BigInteger helper */

	public static BigInteger bigInt(int i)
	{
		return new BigInteger(Integer.toString(i));
	}
}

class Biword
{
	public String a;
	public String b;

	public Biword(String a, String b)
	{
		this.a = a;
		this.b = b;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((a == null) ? 0 : a.hashCode());
		result = prime * result + ((b == null) ? 0 : b.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Biword other = (Biword) obj;
		if (a == null)
		{
			if (other.a != null)
				return false;
		} else if (!a.equals(other.a))
			return false;
		if (b == null)
		{
			if (other.b != null)
				return false;
		} else if (!b.equals(other.b))
			return false;
		return true;
	}

}

class Triword
{
	public String a;
	public String b;
	public String c;

	public Triword(String a, String b, String c)
	{
		this.a = a;
		this.b = b;
		this.c = c;
	}

	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((a == null) ? 0 : a.hashCode());
		result = prime * result + ((b == null) ? 0 : b.hashCode());
		result = prime * result + ((c == null) ? 0 : c.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Triword other = (Triword) obj;
		if (a == null)
		{
			if (other.a != null)
				return false;
		} else if (!a.equals(other.a))
			return false;
		if (b == null)
		{
			if (other.b != null)
				return false;
		} else if (!b.equals(other.b))
			return false;
		if (c == null)
		{
			if (other.c != null)
				return false;
		} else if (!c.equals(other.c))
			return false;
		return true;
	}

	public boolean contains(Biword bw)
	{
		if (bw.a == a && bw.b == b)
			return true;
		return false;
	}

}

class Probab
{
	BigInteger numerator;
	BigInteger denominator;

	public Probab(BigInteger numerator, BigInteger denominator)
	{
		super();
		this.numerator = numerator;
		this.denominator = denominator;
	}

	public void multiply(Probab b)
	{
		this.numerator.multiply(b.numerator);
		this.denominator.multiply(b.denominator);
	}

	public void divide(Probab b)
	{
		this.numerator.multiply(b.denominator);
		this.denominator.multiply(b.numerator);
	}
}
