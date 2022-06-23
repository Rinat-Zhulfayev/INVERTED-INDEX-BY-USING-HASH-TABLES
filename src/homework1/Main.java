package homework1;

import java.io.*;
import java.util.*;
import java.security.InvalidKeyException;
import java.time.Duration;
import java.time.Instant;

public class Main {

	public static void main(String[] args) throws IOException, InvalidKeyException {

		//int collisionHandlingMode = io.modeSelection("Please select Collision Handling Mode: " + "\n" // mode selection
		//		+ "1 - for Linear Probing; 2 - for Double Hashing");
		int hashFunctionMode = io.modeSelection("Please select Hash Function Mode: " + "\n"
				+ "1 - for Simple Summation Function (SSF); 2 - Polynomial Accumulation Function (PAF) ");
		double load_factor = 0.8;

		String dir = "C:\\Users\\rzhul\\eclipse-workspace\\Inverted_Index\\bbc.zip"; 
		String destdir = "C:\\Users\\rzhul\\eclipse-workspace\\Inverted_Index";
		io.unzip(dir, destdir);															// unzip zip file

//		if(collisionHandlingMode==1) {
//			HashTableLinearProbing<String, HashTableLinearProbing<String, Integer>> map = new HashTableLinearProbing<>(17,load_factor,hashFunctionMode);
//		}
//		else {
//			HashTableDoubleHashing<String, HashTableDoubleHashing<String, Integer>> map = new HashTableDoubleHashing<>(17,load_factor,hashFunctionMode);
//		}

		// Via if else statement created map became invisible.
		HashTableLinearProbing<String, HashTableLinearProbing<String, Integer>> map = new HashTableLinearProbing<>(17,load_factor, hashFunctionMode);
		                    // select for linear probing
		// HashTableDoubleHashing<String, HashTableDoubleHashing<String, Integer>> map = new HashTableDoubleHashing<>(17,load_factor, hashFunctionMode);
							// select for double hashing Change variable bellow too

		ArrayList<String> stop_words = io.stopWords();

		File[] listOfFolders = io.readFolder("bbc");
		File[] listOfFiles;
		File textFile;
		String text = "";
		String[] splitted;
		List<String> wordList;
		String word;

		Instant inst_start, inst_end;
		inst_start = Instant.now();
		for (int i = 0; i < listOfFolders.length; i++) {
			listOfFiles = io.readFolder(listOfFolders[i].toString());

			for (int j = 0; j < listOfFiles.length; j++) {

				textFile = new File(listOfFiles[j].toString());
				String nametxt = textFile.toString();

				text = io.readFile(textFile);

				text = text.toLowerCase().trim();
				splitted = text.split(io.DELIMITERS);

				wordList = new ArrayList<>();
				wordList.addAll(Arrays.asList(splitted));

				wordList.removeAll(stop_words);

				for (int q = 0; q < wordList.size(); q++) {

					word = wordList.get(q);
					HashTableLinearProbing<String, Integer> value = new HashTableLinearProbing<>(17, load_factor,hashFunctionMode);// activate for linear probing
					//HashTableDoubleHashing<String, Integer> value = new HashTableDoubleHashing<>(17,load_factor, hashFunctionMode); //activate double hashing
					int freq = 0;
					if (map.hasKey(word) && map.get(word).hasKey(nametxt)) {
						freq = map.get(word).get(nametxt) + 1;
						value = map.get(word);
						value.add(nametxt, freq);
						map.add(word, value);
					} else if (map.hasKey(word) && !map.get(word).hasKey(nametxt)) {
						freq++;
						value = map.get(word);
						value.add(nametxt, freq);
						map.add(word, value);
					} else {
						freq++;
						value.add(nametxt, freq);
						map.add(word, value);
					}

				}
				wordList.clear();
			}

		}
		inst_end = Instant.now();

		System.out.println("Total number of the collisions: " + map.getNumberOfTheCollisions());
		System.out.println("Total indexing time: " + Duration.between(inst_start, inst_end).toMillis() + " miliseconds");

		File search_file = new File("search.txt");
		text = io.readFile(search_file);
		String[] search = text.split(io.DELIMITERS);

		List<Long> time_for_search = new ArrayList<>();
		for (int i = 0; i < search.length; i++) {
			long start = System.nanoTime();
			map.get(search[i]);
			long end = System.nanoTime();
			long time = end - start;
			time_for_search.add(time);
		}
		Collections.sort(time_for_search);

		System.out.println("Min time: " + time_for_search.get(0) + " nanoseconds");
		System.out.println("Max time: " + time_for_search.get(time_for_search.size() - 1) + " nanoseconds");

		long sum = time_for_search.stream().mapToLong(Long::longValue).sum();
		double average = sum / time_for_search.size();
		System.out.println("Average time: " + average + " nanoseconds");

	}

}
