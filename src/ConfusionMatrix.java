import java.io.FileNotFoundException;
import java.io.File;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Scanner;

public class ConfusionMatrix {
	public int AddMatrix[][] = new int[27][27];
	public int SubMatrix[][] = new int[27][27];
	public int DelMatrix[][] = new int[27][27];
	public int RevMatrix[][] = new int[27][27];

	public double addProb[][] = new double[27][27];
	public double subProb[][] = new double[27][27];
	public double delProb[][] = new double[27][27];
	public double revProb[][] = new double[27][27];

	public long addCount;
	public long subCount;
	public long delCount;
	public long revCount;

	public void init() throws FileNotFoundException {
		Dictionary my_dictionary = new Dictionary();
		my_dictionary.fillDictionary();

		this.readAddMatrix("addXY");
		this.readSubMatrix("subXY");
		this.readDelMatrix("delXY");
		this.readRevMatrix("revXY");
		this.smoothMatrix();
		this.convertProb();
	}

	public ArrayList<String> getSuggestions(String input) {
		// A list for the output
		ArrayList<String> output_list = new ArrayList<String>();
		LevenshteinDistance LD = new LevenshteinDistance();

		// Giving an error in the length by 3 units
		for (int i = Math.max(input.length() - 3, 1); i < input.length() + 3; i++) {
			ArrayList<String> possibility_list = Dictionary.dictionary.get(i);
			for (String s : possibility_list) {
				int edit_dist = LD.getLD(s, input, this);
				if (edit_dist == 0) {
					System.out.println("Word is already correct");
					System.exit(0);
				}
				if (edit_dist < Global.MAX_DIST) {
					output_list.add(s);
				}
			}
		}

		return output_list;

	}

	public void printAddMatrix() {
		int i, j;
		for (i = 0; i < 27; i++) {
			for (j = 0; j < 26; j++)
				System.out.print(AddMatrix[i][j] + " ");
			System.out.println();
		}

	}

	public void printSubMatrix() {
		int i, j;
		for (i = 0; i < 26; i++) {
			for (j = 0; j < 26; j++)
				System.out.print(SubMatrix[i][j] + " ");
			System.out.println();
		}
	}

	public void printDelMatrix() {
		int i, j;
		for (i = 0; i < 27; i++) {
			for (j = 0; j < 26; j++)
				System.out.print(DelMatrix[i][j] + " ");
			System.out.println();

		}

	}

	public void printRevMatrix() {
		int i, j;
		for (i = 0; i < 26; i++) {
			for (j = 0; j < 26; j++)
				System.out.print(RevMatrix[i][j] + " ");
			System.out.println();
		}
	}

	public void readAddMatrix(String fileName) throws FileNotFoundException {
		File f = new File(fileName);
		Scanner fin = new Scanner(f);

		int i, j;
		for (i = 0; i < 27; i++) {
			for (j = 0; j < 26; j++)
				AddMatrix[i][j] = fin.nextInt();

		}
		fin.close();
	}

	public void readSubMatrix(String fileName) throws FileNotFoundException {
		File f = new File(fileName);
		Scanner fin = new Scanner(f);

		int i, j;
		for (i = 0; i < 26; i++) {
			for (j = 0; j < 26; j++)
				SubMatrix[i][j] = fin.nextInt();

		}
		fin.close();
	}

	public void readDelMatrix(String fileName) throws FileNotFoundException {
		File f = new File(fileName);
		Scanner fin = new Scanner(f);

		int i, j;
		for (i = 0; i < 27; i++) {
			for (j = 0; j < 26; j++)
				DelMatrix[i][j] = fin.nextInt();

		}
		fin.close();
	}

	public void readRevMatrix(String fileName) throws FileNotFoundException {
		File f = new File(fileName);
		Scanner fin = new Scanner(f);

		int i, j;
		for (i = 0; i < 26; i++) {
			for (j = 0; j < 26; j++)
				RevMatrix[i][j] = fin.nextInt();

		}
		fin.close();
	}

	// Convert counts to prob
	public void convertProb() {
		long count = 0;
		int i, j;
		for (i = 0; i < 27; i++) {
			for (j = 0; j < 26; j++) {
				count += AddMatrix[i][j];
			}
		}
		addCount = count;
		for (i = 0; i < 27; i++) {
			for (j = 0; j < 26; j++) {
				addProb[i][j] = AddMatrix[i][j] * 1.0 / count;
			}
		}
		count = 0;
		for (i = 0; i < 27; i++) {
			for (j = 0; j < 26; j++) {
				count += DelMatrix[i][j];
			}
		}
		delCount = count;
		for (i = 0; i < 27; i++) {
			for (j = 0; j < 26; j++) {
				delProb[i][j] = DelMatrix[i][j] * 1.0 / count;
			}
		}
		count = 0;
		for (i = 0; i < 26; i++) {
			for (j = 0; j < 26; j++) {
				count += SubMatrix[i][j];
			}
		}
		subCount = count;
		for (i = 0; i < 26; i++) {
			for (j = 0; j < 26; j++) {
				subProb[i][j] = SubMatrix[i][j] * 1.0 / count;
			}
		}
		count = 0;
		for (i = 0; i < 26; i++) {
			for (j = 0; j < 26; j++) {
				count += RevMatrix[i][j];
			}
		}
		revCount = count;
		for (i = 0; i < 26; i++) {
			for (j = 0; j < 26; j++) {
				revProb[i][j] = RevMatrix[i][j] * 1.0 / count;
			}
		}

	}

	// Laplace smoothing.
	public void smoothMatrix() {
		int i, j;
		boolean smoothRequired = false;
		// The Add Matrix
		for (i = 0; i < 27; i++) {
			for (j = 0; j < 26; j++) {
				if (AddMatrix[i][j] == 0)
					smoothRequired = true;

			}
			if (smoothRequired)
				break;
		}
		if (smoothRequired) {
			for (i = 0; i < 27; i++) {
				for (j = 0; j < 26; j++) {
					AddMatrix[i][j]++;
				}
			}
		}

		smoothRequired = false;
		// The Del Matrix
		for (i = 0; i < 26; i++) {
			for (j = 0; j < 27; j++) {
				if (DelMatrix[i][j] == 0)
					smoothRequired = true;

			}
			if (smoothRequired)
				break;
		}
		if (smoothRequired) {
			for (i = 0; i < 26; i++) {
				for (j = 0; j < 27; j++) {
					DelMatrix[i][j]++;
				}
			}
		}

		smoothRequired = false;
		// The Sub Matrix
		for (i = 0; i < 26; i++) {
			for (j = 0; j < 26; j++) {
				if (SubMatrix[i][j] == 0)
					smoothRequired = true;

			}
			if (smoothRequired)
				break;
		}
		if (smoothRequired) {
			for (i = 0; i < 26; i++) {
				for (j = 0; j < 27; j++) {
					SubMatrix[i][j]++;
				}
			}
		}

		smoothRequired = false;
		// The Rev Matrix
		for (i = 0; i < 26; i++) {
			for (j = 0; j < 27; j++) {
				if (RevMatrix[i][j] == 0)
					smoothRequired = true;

			}
			if (smoothRequired)
				break;
		}
		if (smoothRequired) {
			for (i = 0; i < 26; i++) {
				for (j = 0; j < 27; j++) {
					RevMatrix[i][j]++;
				}
			}
		}

	}

}
