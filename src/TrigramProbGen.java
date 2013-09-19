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
	public static Hashtable<Biword, Integer> biwordCount;
	public static Hashtable<Triword, Integer> triwordCount;

	public static Hashtable<String, ArrayList<String>> partsOfSpeech;
	public static Hashtable<String, ArrayList<String>> pos_to_words;

	public static void initialize() throws FileNotFoundException
	{
		biwordCount = new Hashtable<Biword, Integer>();
		triwordCount = new Hashtable<Triword, Integer>();
		partsOfSpeech = new Hashtable<String, ArrayList<String>>();
		pos_to_words = new Hashtable<String, ArrayList<String>>();

		// -- Building the counts
		File f = new File("brown/ca04");
		Scanner posin = new Scanner(f);

		while (posin.hasNext())
		{ // -- Here is where we must build the
			String s = posin.next();
			String[] s_split = s.split("[/+]");
			// -- s_split[1+] contains the pos tag for string
			for (int i = 0; i < s_split.length; i++)
				s_split[i] = s_split[i].toLowerCase();

			add_to_partsOfSpeech(s_split);
			add_to_pos_to_words(s_split);
		}

		posin = new Scanner(f);

		while (posin.hasNext())
		{ // -- Here is where we must build the
			String s = posin.nextLine();
			String[] s_split = s.split(" ");
			// -- Starting from 3 because we want to ignore the tab in the front
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

				update_triword_count(tw);
				update_biword_count(bw);

				if (i == s_split.length - 1)
				{
					Biword biword = new Biword(word_2, word_3);
					update_biword_count(biword);
				}
			}

		}

		System.out.println(pos_to_words);
	}

	public static void main(String[] args) throws FileNotFoundException
	{
		// -- In the initialize phase, we learn the trigram probabilities for
		// parts of speech
		// -- and fill in a hashtable that contains the mapping from each word
		// to a list of
		// -- parts of speech that it is associated with. We also fill up the
		// trigram and bigram counts
		// -- for all these parts of speech combinations.
		initialize();

		// -- What we essentially need to do is to take a particular phrase/
		// sentence, and first detect
		// -- if there is some misspelling. If there is no misspelling, we
		// assume that the sentence is
		// -- correct. We check the misspelling by searching against the default
		// dictionary present in ubuntu
		// -- If there is a misspelling, we need to correct it. So for that, we
		// first need to get a set
		// -- of candidate replacements. In other words, the confusion set that
		// we define is the list of
		// -- words that are close to the misspelt word in terms of the results
		// obtained from the first
		// -- phase of the assignment.
		// -- Once we have a confusion set, we can apply our method.
		// -- Suppose the original sentence was S = w1, w2, w3 ..... wn, and
		// suppose the misspelt word was
		// -- wi. Then we look at the confusion set of wi. We replace every word
		// wi' from the confusion set
		// -- and calculate the probability of the sentence P(S'). The word that
		// is finally replaced/suggested
		// -- is the word with the highest such probability.
		// -- Now we need a method to calculate P(S). This is done using the
		// trigrams in the following way.
		// -- We have P(S) = Sigma P(S,T), where P(S,T) is the probability that
		// blah balh and so on

	}

	public static double prob_of_triword_given_biword(Triword tw, Biword bw)
	{
		if (!tw.contains(bw))
		{
			System.out.println("Biword not in Triword. Fix the bulb");
			System.exit(1);
		}
		if (triwordCount.containsKey(tw))
			return triwordCount.get(tw) + 1 / (double) biwordCount.get(bw);

	}

	public static Probab prob_of_w_given_t(String w, String t)
	{
		// -- If the Hashtable does not contain the tag, then prob is the
		// smoothened one
		if (!pos_to_words.contains(t))
		{
			System.out.println("Tag " + t + " is not in hashtable at all!");
			System.exit(1);
		}
		ArrayList<String> list = pos_to_words.get(t);
		int word_count = 0;
		for (String word : list)
			if (word.equals(w))
				word_count++;

		// -- Here we also account for smoothing
		return (word_count + 1) / (double) (2 * list.size());
	}

	private static void test()
	{
		Biword bw = new Biword("a", "b");
		Biword bw2 = new Biword("a", "b");

		Triword tw = new Triword("a", "b", "c");
		Triword tw2 = new Triword("a", "b", "c");

		update_biword_count(bw2);
		update_biword_count(bw);

		update_triword_count(tw2);
		update_triword_count(tw);

		System.out.println(biwordCount);
		System.out.println(triwordCount);
	}

	private static void update_biword_count(Biword bw)
	{
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

	private static void update_triword_count(Triword tw)
	{
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

	private static void add_to_partsOfSpeech(String[] s)
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

	private static void add_to_pos_to_words(String[] s)
	{
		for (int i = 1; i < s.length; i++)
		{
			if (pos_to_words.containsKey(s[i]))
				pos_to_words.get(s[i]).add(s[0]);
			else
			{
				ArrayList<String> list = new ArrayList<String>();
				list.add(s[0]);
				pos_to_words.put(s[i], list);
			}
		}

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
