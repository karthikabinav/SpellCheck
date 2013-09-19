import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;

import com.sun.corba.se.impl.resolver.SplitLocalResolverImpl;

public class TrigramProbGen
{
	public static Hashtable<Biword, Integer> biwordCount;
	public static Hashtable<Triword, Integer> triwordCount;

	public static Hashtable<String, ArrayList<String>> partsOfSpeech;

	public static void main(String[] args) throws FileNotFoundException
	{
		biwordCount = new Hashtable<Biword, Integer>();
		triwordCount = new Hashtable<Triword, Integer>();
		partsOfSpeech = new Hashtable<String, ArrayList<String>>();

		// -- Reading the POS tags
		/*
		 * while (posin.hasNext()) partsOfSpeech.add(posin.next());
		 */

		// -- Building the counts

		File f = new File("brown/ca02");
		Scanner posin = new Scanner(f);

		while (posin.hasNext())
		{ // -- Here is where we must build the
			String s = posin.next();
			String[] s_split = s.split("[/+]"); // -- s_split[1+] contains the
												// pos tag for (int i =
			for (int i = 1; i < s_split.length; i++)
				s_split[i] = s_split[i].toUpperCase();

			add_to_hashtable(s_split);

		}

		posin = new Scanner(f);

		while (posin.hasNext())
		{ // -- Here is where we must build the
			String s = posin.nextLine();
			String[] s_split = s.split(" ");
			//-- Starting from 3 because we want to ignore the tab in the front of the line
			for (int i = 3; i < s_split.length; i++)
			{
				String word_1 = partsOfSpeech.get(s_split[i - 2].split("/")[0])
						.get(0);
				String word_2 = partsOfSpeech.get(s_split[i - 1].split("/")[0])
						.get(0);
				String word_3 = partsOfSpeech.get(s_split[i].split("/")[0])
						.get(0);

				Triword tw = new Triword(word_1, word_2, word_3);
				Biword bw = new Biword(word_1, word_2);
				
				update_triword_count(tw);
				update_biword_count(bw);
				
				if ( i == s_split.length - 1 )
				{
					Biword biword = new Biword ( word_2, word_3 );
					update_biword_count ( biword );
				}
			}

		}

		System.out.println(triwordCount);
		System.out.println(biwordCount);
		
		


	}
	
	private static void test ()
	{
		Biword bw = new Biword("a", "b");
		Biword bw2 = new Biword ( "a", "b");
		
		Triword tw = new Triword("a", "b", "c");
		Triword tw2 = new Triword("a", "b", "c");
		
		
		update_biword_count(bw2);
		update_biword_count(bw);
		
		update_triword_count(tw2);
		update_triword_count(tw);
		
		System.out.println ( biwordCount );
		System.out.println ( triwordCount);
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

	private static void add_to_hashtable(String[] s)
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
