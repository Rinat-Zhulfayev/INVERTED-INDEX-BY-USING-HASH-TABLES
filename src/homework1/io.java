package homework1;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class io {
	private final static int BUFFER_SIZE = 4096;
	public static String DELIMITERS = "[-+=" + " " + "\r\n " + "1234567890" + "’'\"" + "(){}<>\\[\\]" + ":" + ","
			+ "‒–—―" + "…" + "!" + "." + "«»" + "-‐" + "?" + "‘’“”" + ";" + "/" + "⁄" + "␠" + "·" + "&" + "@" + "*"
			+ "\\" + "•" + "^" + "¤¢$€£¥₩₪" + "†‡" + "°" + "¡" + "¿" + "¬" + "#" + "№" + "%‰‱" + "¶" + "′" + "§" + "~"
			+ "¨" + "_" + "|¦" + "⁂" + "☞" + "∴" + "‽" + "※" + "]";

	public static int modeSelection(String promt) { //input from user
		Scanner sc = new Scanner(System.in);
		int mode = 0;
		while (true) {
			try {
				System.out.println(promt);
				mode = sc.nextInt();
			} catch (Exception e) {
				System.out.println("Invalid input");
				continue;
			}
			if (mode != 1 && mode != 2) {
				System.out.println("Invalid input");
				continue;
			} else
				break;
		}
		return mode;
	}

	public static void unzip(String zipFilePath, String destDirectory) throws IOException {
		File destDir = new File(destDirectory);
		if (!destDir.exists()) {
			destDir.mkdir();
		}
		ZipInputStream zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
		ZipEntry entry = zipIn.getNextEntry();
		// iterates over entries in the zip file
		while (entry != null) {
			String filePath = destDirectory + File.separator + entry.getName();
			if (!entry.isDirectory()) {
				// if the entry is a file, extracts it
				extractFile(zipIn, filePath);
			} else {
				// if the entry is a directory, make the directory
				File dir = new File(filePath);
				dir.mkdirs();
			}
			zipIn.closeEntry();
			entry = zipIn.getNextEntry();
		}
		zipIn.close();
	}

	private static void extractFile(ZipInputStream zipIn, String filePath) throws IOException {
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
		byte[] bytesIn = new byte[BUFFER_SIZE];
		int read = 0;
		while ((read = zipIn.read(bytesIn)) != -1) {
			bos.write(bytesIn, 0, read);
		}
		bos.close();
	}

	public static File[] readFolder(String path) { // list files in folder
		File bigfolder = new File(path);
		File[] listOfFolders = bigfolder.listFiles();

		return listOfFolders;
	}

	public static String readFile(File textFile) throws IOException { 
		BufferedReader bf;
		String text = "";
		try {
			bf = new BufferedReader(new FileReader(textFile));
			text = "";
			for (String line; (line = bf.readLine()) != null;) {
				text += line.toLowerCase().trim() + ".";
			}

			bf.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return text;

	}

	public static ArrayList<String> stopWords() {
		ArrayList<String> stop_words = new ArrayList<String>();
		try {
			File stops = new File("stop_words_en.txt");
			Scanner sc = new Scanner(stops);
			while (sc.hasNextLine()) {
				sc.delimiter();
				stop_words.add(sc.next());
			}
			stop_words.add("");
			stop_words.add("\n");
			sc.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return stop_words;
	}

}
