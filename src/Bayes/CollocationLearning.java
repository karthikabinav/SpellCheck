package Bayes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

public class CollocationLearning {

	public static HashMap<String, Set<String>> collocation = new HashMap<String, Set<String>>();
	private static int k = 20;

	public static void main(String args[]) {
		Scanner s = new Scanner(System.in);

		Set<String> prevk = new HashSet();
		while (s.hasNext()) {
			String curWord = s.next();
			String word = curWord.split("/")[0].toLowerCase();

			if (collocation.containsKey(word)) {

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
