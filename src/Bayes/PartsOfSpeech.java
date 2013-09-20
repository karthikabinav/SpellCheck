package Bayes;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class PartsOfSpeech {

	public HashMap<String, String> wordPOSMap;

	public void populatePOSMap() throws FileNotFoundException {
		Scanner s = new Scanner(new File(Helpers.Main.corpusFile));

		while (s.hasNext()) {
			String r = s.next();
			wordPOSMap.put(r.split("/")[0], r.split("/")[1]);

		}
		s.close();
	}
}
