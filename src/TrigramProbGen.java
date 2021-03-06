
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;

import Helpers.ConfusionMatrix;
import Helpers.Dictionary;


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
	public static void initialize() throws FileNotFoundException, ClassNotFoundException
	{
		// Allocating Memory
		biwordCount = new Hashtable<Biword, Integer>();
		triwordCount = new Hashtable<Triword, Integer>();
		partsOfSpeech = new Hashtable<String, ArrayList<String>>();
		wordsOfPos = new Hashtable<String, ArrayList<String>>();

		partsOfSpeech = (Hashtable<String, ArrayList<String>>) readSerialized("partsOfSpeech");
		wordsOfPos = (Hashtable<String, ArrayList<String>>) readSerialized("wordsOfPos");
		
		// Creating and filling a dictionary
		cm = new ConfusionMatrix();
		dict = new Dictionary();

		// Reading the corpus
		/*File filenames = new File("brown/filenames");
		Scanner files_in = new Scanner(filenames);

		while (files_in.hasNext())
		{
			//String next_file = "ce08";
			 String next_file = files_in.next();
			File f = new File("brown/" + next_file);
			Scanner posin = new Scanner(f);

//			while (posin.hasNext())
//			{
//				// Here is where we must build the Parts of speech
//				String s = posin.next();
//				String[] s_split = s.split("[/+]");
//				// s_split[1+] contains the pos tag for string
//				for (int i = 0; i < s_split.length; i++)
//					s_split[i] = s_split[i].toLowerCase();
//
//				addToPartsOfSpeech(s_split);
//				addToWordsOfPos(s_split);
//			}
//
//			posin = new Scanner(f);

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
		

		serialize(biwordCount, "BiwordCount");
		serialize(triwordCount, "TriwordCount");*/
		
		biwordCount = (Hashtable<Biword, Integer>) readSerialized("BiwordCount");
		triwordCount = (Hashtable<Triword, Integer>) readSerialized("TriwordCount");
		
		//System.out.println(biwordCount);
	}

	public TrigramProbGen() throws FileNotFoundException, ClassNotFoundException
	{
		// In the initialize phase, we learn the trigram probabilities for
		// parts of speech
		// and fill in a hashtable that contains the mapping from each word
		// to a list of
		// parts of speech that it is associated with. We also fill up the
		// trigram and bigram counts
		// for all these parts of speech combinations.
		initialize();
		
		System.out.println ( "Trigram initialization complete.");

	}

	public static String noun = "nn";

	public static Probab probOfSentence(String[] sentence)
	{
		Probab p = new Probab(bigInt(1), bigInt(1));

		for (int i = 0; i < sentence.length; i++)
		{
			if (partsOfSpeech.containsKey(sentence[i]))
				p.multiply(probOfWGivenT(sentence[i],
						partsOfSpeech.get(sentence[i]).get(0)));
			else
				p.multiply(probOfWGivenT(sentence[i], noun));
		}

		for (int i = 2; i < sentence.length; i++)
		{
			String a = partsOfSpeech.containsKey(sentence[i - 2]) ? partsOfSpeech
					.get(sentence[i - 2]).get(0) : noun;
			String b = partsOfSpeech.containsKey(sentence[i - 1]) ? partsOfSpeech
					.get(sentence[i - 1]).get(0) : noun;
			String c = partsOfSpeech.containsKey(sentence[i]) ? partsOfSpeech
					.get(sentence[i]).get(0) : noun;

			p.multiply(probOfTwGivenBw(new Triword(a, b, c), new Biword(a, b)));
		}

		return p;
	}

	public static Probab probOfTwGivenBw(Triword tw, Biword bw)
	{
		if (!tw.contains(bw))
		{
			System.out.println("Biword not in Triword. Fix the bulb");
			System.exit(1);
		}
		if (triwordCount.containsKey(tw))
			return new Probab(bigInt(triwordCount.get(tw) + 1),
					bigInt(biwordCount.get(bw) + Dictionary.size));
		else
		{
			if (biwordCount.containsKey(bw))
				return new Probab(bigInt(1), bigInt(biwordCount.get(bw)
						+ Dictionary.size));
			else
				return new Probab(bigInt(1), bigInt(Dictionary.size));
		}
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
		/*
		 * Biword bw = new Biword("a", "b"); Biword bw2 = new Biword("a", "b");
		 * Triword tw = new Triword("a", "b", "c"); Triword tw2 = new
		 * Triword("a", "b", "c"); updateBiwordCount(bw2);
		 * updateBiwordCount(bw); updateTriwordCount(tw2);
		 * updateTriwordCount(tw); System.out.println(biwordCount);
		 * System.out.println(triwordCount);
		 */

		Probab p = new Probab(bigInt(24), bigInt(30));
		Probab p2 = new Probab(bigInt(2), bigInt(3));

		p.add(p2);
		p.print();
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

	// Credits : http://www.wikihow.com/Serialize-an-Object-in-Java
	public static void serialize(Object object, String s)
	{
		try
		{
			// Serialize data object to a file
			ObjectOutputStream out = new ObjectOutputStream(
					new FileOutputStream(s + ".ser"));
			out.writeObject(object);
			out.close();

			// Serialize data object to a byte array
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			out = new ObjectOutputStream(bos);
			out.writeObject(object);
			out.close();

			// Get the bytes of the serialized object
			byte[] buf = bos.toByteArray();
		} catch (IOException e)
		{
		}
	}

	// http://www.wikihow.com/Serialize-an-Object-in-Java
	public static Object readSerialized(String s) throws ClassNotFoundException
	{
		try
		{
			FileInputStream door = new FileInputStream(s + ".ser");
			ObjectInputStream reader = new ObjectInputStream(door);
			Object x = new Object();
			x =(Object) reader.readObject();
			return x;
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;

	}

}

class Biword implements Serializable
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

class Triword implements Serializable
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
		this.numerator = this.numerator.multiply(b.numerator);
		this.denominator = this.denominator.multiply(b.denominator);
	}

	public void add(Probab b)
	{
		BigInteger num = numerator;
		BigInteger den = denominator;

		this.numerator = num.multiply(b.denominator).add(
				den.multiply(b.numerator));
		this.denominator = (den.multiply(b.denominator));
		// XXX
	}

	public void divide(Probab b)
	{
		this.numerator = this.numerator.multiply(b.denominator);
		this.denominator = this.denominator.multiply(b.numerator);
	}

	public void print()
	{
		System.out.println(this.numerator);
	}
}
