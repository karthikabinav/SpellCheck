import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;

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
		while (posin.hasNext())
			partsOfSpeech.add(posin.next());*/

		// -- Building the counts
		File f = new File("brown/ca01");
		Scanner posin = new Scanner(f);

		while (posin.hasNext())
		{
			// -- Here is where we must build the hashtable
			String s = posin.next();
			String[] s_split = s.split("[/+]");
			// -- s_split[1+] contains the pos tag
			for ( int i = 1; i < s_split.length; i++)
				s_split[i] = s_split[i].toUpperCase();	
			

			add_to_hashtable( s_split );

		}
		
		posin.reset();
		
		while (posin.hasNext())
		{
			// -- Here is where we must build the hashtable
			String s = posin.nextLine();
			for ( int i = 2; i < s.split(" ").length; i++)

		}
		
		System.out.println ( partsOfSpeech);

	}

	private static void add_to_hashtable(String[] s)
	{
		for (int i = 1; i < s.length; i++)
		{
			if ( partsOfSpeech.contains(s[i]))
			partsOfSpeech.get(s[0]).add(s[i]);
			else
			{
				ArrayList<String> list = new ArrayList<String>();
				list.add(s[i]);
				partsOfSpeech.put(s[0], list );
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
	public boolean equals(Object o)
	{
		if (!(o instanceof Biword))
			return false;
		Biword bw = (Biword) o;
		if (bw.a == a && bw.b == b)
			return true;
		return false;
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
	public boolean equals(Object o)
	{
		if (!(o instanceof Triword))
			return false;
		Triword tw = (Triword) o;
		if (tw.a == a && tw.b == b && tw.c == c)
			return true;
		return false;
	}

	public boolean contains(Biword bw)
	{
		if (bw.a == a && bw.b == b)
			return true;
		return false;
	}
}
