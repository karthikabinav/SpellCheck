package Bayes;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

class StringCounter implements Serializable {
	ArrayList<String> words;
	int count;
}

public class CollocationLearning {

	public static HashMap<String, StringCounter> collocation = new HashMap<String, StringCounter>();
	private static int k = 20;

	private static ArrayList<String> setIntersection(ArrayList<String> A,
			ArrayList<String> B) {
		ArrayList<String> C = new ArrayList<String>();
		for (String a : A) {
			for (String b : B) {
				if (a.equals(b)) {
					C.add(a);
					break;
				}
			}
		}
		return C;
	}

	@SuppressWarnings("unchecked")
	public static void Learn() throws FileNotFoundException {
		Scanner s = new Scanner(new File(Helpers.Main.corpusFile));

		ArrayList<String> prevk = new ArrayList<String>();
		while (s.hasNext()) {
			String curWord = s.next();
			String word = curWord.split("/")[0].toLowerCase();

			if (collocation.containsKey(word)) {
				StringCounter sc = collocation.get(word);
				ArrayList<String> value = sc.words;
				collocation.remove(word);
				sc.words = setIntersection(prevk, value);
				sc.count++;
				collocation.put(word, sc);
			} else {
				StringCounter sc = new StringCounter();
				sc.words = (ArrayList<String>) prevk.clone();
				sc.count = 1;
				collocation.put(word, sc);
			}

			if (prevk.size() == k) {
				int i;
				for (i = 1; i < prevk.size(); i++) {
					collocation.get(prevk.get(0)).words.add(prevk.get(i));
				}
				for (i = 0; i < prevk.size() - 1; i++) {
					prevk.set(i, prevk.get(i + 1));
				}
				prevk.set(i, word);
			} else
				prevk.add(word);

		}

		/*
		 * Set<java.util.Map.Entry<String, StringCounter>> hash = collocation
		 * .entrySet(); for (java.util.Map.Entry<String, StringCounter> e :
		 * hash) { if (e.getValue().count <= 3) continue;
		 * System.out.println(e.getKey()); for (String p : e.getValue().words) {
		 * System.out.print(p + " "); } System.out.println(); }
		 */
		s.close();

	}
}
