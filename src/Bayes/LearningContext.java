package Bayes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Set;

/*
 * This class will be used to learn the context of a particular word.
 *  
 */

// Class to tag the part of speech
class POST {
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((POSTag == null) ? 0 : POSTag.hashCode());
		result = prime * result + ((word == null) ? 0 : word.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		POST other = (POST) obj;
		if (POSTag == null) {
			if (other.POSTag != null)
				return false;
		} else if (!POSTag.equals(other.POSTag))
			return false;
		if (word == null) {
			if (other.word != null)
				return false;
		} else if (!word.equals(other.word))
			return false;
		return true;
	}

	String word;
	String POSTag;
}

public class LearningContext {
	public static HashMap<POST, ArrayList<POST>> context = new HashMap<POST, ArrayList<POST>>();
	private final static int k = 10; // To check the depth of the context in the

	// training

	@SuppressWarnings("unchecked")
	public static void main(String args[]) {
		Scanner s = new Scanner(System.in);

		ArrayList<POST> prevk = new ArrayList<POST>();
		while (s.hasNext()) {
			String curWord = s.next();
			POST obj = new POST();
			obj.word = curWord.split("/")[0].toLowerCase();
			obj.POSTag = curWord.split("/")[1];
			if (context.containsKey(obj)) {
				ArrayList<POST> temp = context.get(obj);
				for (POST p : prevk) {
					temp.add(p);
				}
				context.remove(obj);
				context.put(obj, (ArrayList<POST>) temp.clone());
			} else
				context.put(obj, (ArrayList<POST>) prevk.clone());
			if (prevk.size() == k) {
				int i;
				for (i = 0; i < prevk.size() - 1; i++) {
					prevk.set(i, prevk.get(i + 1));
				}
				prevk.set(i, obj);
			} else
				prevk.add(obj);

		}
		Set<java.util.Map.Entry<POST, ArrayList<POST>>> hash = context
				.entrySet();
		for (java.util.Map.Entry<POST, ArrayList<POST>> e : hash) {
			System.out.println(e.getKey().word + " " + e.getKey().POSTag);
			for (POST p : e.getValue()) {
				System.out.print(p.word + " " + p.POSTag + " ");
			}
			System.out.println();
		}
		s.close();
	}
}
