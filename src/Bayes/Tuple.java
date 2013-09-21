package Bayes;

import java.math.BigInteger;

public class Tuple implements Comparable<Tuple>
{
	public String word;
	public long rank;
	public BigInteger trigram_rank;
	
	public BigInteger overall_rank;

	@Override
	public int compareTo(Tuple o)
	{
		return overall_rank.compareTo(o.overall_rank);
	}
}
