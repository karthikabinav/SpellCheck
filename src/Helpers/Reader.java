package Helpers;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Reader {

	public static void ReadToFile() throws IOException {
		Scanner read = new Scanner(System.in);
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

	public static void main(String args[]) throws IOException {
		ReadToFile();
	}
}
