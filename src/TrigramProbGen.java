import java.util.Hashtable;

public class TrigramProbGen
{
	public static Hashtable<Biword, Integer> biwordCount;
	public static Hashtable<Triword, Integer> triwordCount;

	public static void main(String[] args)
	{
		biwordCount =  new Hashtable<Biword, Integer>();
		triwordCount = new Hashtable<Triword, Integer>();
		
		
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

	public boolean contains(Biword bw)
	{
		if (bw.a == a && bw.b == b)
			return true;
		return false;
	}
}
