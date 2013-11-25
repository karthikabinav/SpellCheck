package Helpers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Reader {

	private static String inputFile = "merged";

	public static void ReadToFile() throws IOException {
		Scanner read = new Scanner(new File(inputFile));
		File f = new File(Main.corpusFile);
		BufferedWriter fout = new BufferedWriter(new FileWriter(f));

		while (read.hasNext()) {
			String s = read.next();
			fout.write(s);
			fout.write(" ");
		}
		read.close();
		fout.close();
	}

}
