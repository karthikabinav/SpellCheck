import java.io.FileNotFoundException;
import java.io.File;
import java.util.Scanner;

public class ConfusionMatrix {
	public int AddMatrix[][] = new int[27][27];
	public int SubMatrix[][] = new int[27][27];
	public int DelMatrix[][] = new int[27][27];
	public int RevMatrix[][] = new int[27][27];

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
