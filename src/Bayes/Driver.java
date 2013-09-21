package Bayes;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import Helpers.Dictionary;
import Helpers.LevenshteinDistance;

public class Driver
{

	// Credits : http://www.wikihow.com/Serialize-an-Object-in-Java
	public static void serialize(Object object, String s)
	{
		try
		{
			// Serialize data object to a file
			ObjectOutputStream out = new ObjectOutputStream(
					new FileOutputStream(s + ".ser"));
			out.writeObject(object);
			out.close();

			// Serialize data object to a byte array
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			out = new ObjectOutputStream(bos);
			out.writeObject(object);
			out.close();

			// Get the bytes of the serialized object
			byte[] buf = bos.toByteArray();
		} catch (IOException e)
		{
		}
	}

	// http://www.wikihow.com/Serialize-an-Object-in-Java
	public static Object readSerialized(String s) throws ClassNotFoundException
	{
		try
		{
			FileInputStream door = new FileInputStream(s + ".ser");
			ObjectInputStream reader = new ObjectInputStream(door);
			return reader.readObject();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;

	}

	public Driver() throws IOException, ClassNotFoundException
	{
		Dictionary d = new Dictionary();
		d.fillDictionary();

		TestData.init();
		// Reader.ReadToFile();

		/*
		 * LearningContext.Learn(); serialize(LearningContext.context,
		 * "Context"); System.out.println("Phase 1 complete");
		 * CollocationLearning.Learn();
		 * serialize(CollocationLearning.collocation, "Collocation");
		 * System.out.println("Phase 2 complete");
		 * PartsOfSpeech.populatePOSMap(); serialize(PartsOfSpeech.wordPOSMap,
		 * "POS"); System.out.println("Phase 3 complete");
		 * System.out.println("End of learning phase");
		 */

		PartsOfSpeech.wordPOSMap = (HashMap<String, String>) readSerialized("POS");
		LearningContext.context = (HashMap<POST, ArrayList<POST>>) readSerialized("Context");
		CollocationLearning.collocation = (HashMap<String, StringCounter>) readSerialized("Collocation");

		
		System.out.println ( "Driver initialization complete");
		// String sentence = "Let there be peece of mind";
		//
		// ArrayList<Tuple> corrections = TestData.correct(sentence);
		// for (Tuple cand : corrections) {
		// System.out.println(cand.word + " " + cand.rank);
		// }

	}

}
