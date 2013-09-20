import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Scanner;

public class Dictionary {
	public static Hashtable<Integer, ArrayList<String>> dictionary;
	public static HashSet <String> dictionarySet;
	public static int size;

	public Dictionary() {
		dictionary = new Hashtable<Integer, ArrayList<String>>();
		dictionarySet = new HashSet<String>();
		size = 0;
	}

	public void fillDictionary() throws FileNotFoundException {
		File f = new File("/usr/share/dict/words");
		Scanner fin = new Scanner(f);

		while (fin.hasNext()) {
			String input = fin.next();
			boolean pack_word = false;
			for (int i = 0; i < input.length(); i++) {
				// XXX May use some other condition here
				if (input.charAt(i) < 'a' || input.charAt(i) > 'z') {
					pack_word = true;
					break;
				}
			}
			if (pack_word)
				continue;

			addToDictionary(input);
			size++;
		}
		fin.close();
		// System.out.println ( dictionary );

	}

	public void addToDictionary(String a) {
		if (dictionary.containsKey(a.length())) {
			dictionary.get(a.length()).add(a);
		} else {
			ArrayList<String> list = new ArrayList<String>();
			list.add(a);
			dictionary.put(a.length(), list);
		}
		dictionarySet.add(a);

	}
	
	public static boolean exists ( String a )
	{
		if ( dictionarySet.contains(a)) return true;
		else return false;
	}
	
}
