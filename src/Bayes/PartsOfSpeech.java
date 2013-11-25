package Bayes;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class PartsOfSpeech {

	public static HashMap<String, String> wordPOSMap = new HashMap<String, String>();

	public static void populatePOSMap() throws FileNotFoundException {
		Scanner s = new Scanner(new File(Helpers.Main.corpusFile));

		while (s.hasNext()) {
			String r = s.next();
			String key = "";
			String value = "";
			try {
				key = r.split("/")[0].toLowerCase();
				value = r.split("/")[1];
			} catch (Exception e) {
				value = "UND";
			}

			wordPOSMap.put(key, value);

		}
		s.close();
	}
}
