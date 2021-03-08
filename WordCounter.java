import java.io.*;
import java.util.*;


public class WordCounter{
	
	private static Map<String, Integer> wordCounts;
	private Map<String, Integer> trainHamFreq;
	private Map<String, Integer> trainSpamFreq;
	
	public WordCounter(){
		wordCounts = new TreeMap<>();
		trainHamFreq = new TreeMap<>();
		trainSpamFreq = new TreeMap<>();
	}
	
	public void parseFile(File file) throws IOException{
		//System.out.println("Starting parsing the file:" + file.getAbsolutePath());
		HashSet<String> existingWords = new HashSet<String>();
		if(file.isDirectory()){
			//parse each file inside the directory
			File[] content = file.listFiles();
			for(File current: content){
				parseFile(current);
			}
		}else{
			existingWords.clear();
			Scanner scanner = new Scanner(file);
			// scanning token by token
			while (scanner.hasNext()){
				String  token = scanner.next();
				if (isValidWord(token)){
					existingWords.add(token);
				}
			}
			ArrayList<String> words = new ArrayList<String>();
			for(String i: existingWords){
				words.add(i);
			}
			
			for(int i=0;i< words.size();i++){
				if(isValidWord(words.get(i)) && wordCounts.containsKey(words.get(i))){
					int previous = wordCounts.get(words.get(i));
					wordCounts.put(words.get(i), previous+1);
				}
				else{
					wordCounts.put(words.get(i),1);
				}
			}	
		}		
		
	}
	
	private boolean isValidWord(String word){
		String allLetters = "^[a-zA-Z]+$";
		// returns true if the word is composed by only letters otherwise returns false;
		return word.matches(allLetters);
			
	}
	
	private void countWord(String word){
		if(wordCounts.containsKey(word)){
			int previous = wordCounts.get(word);
			wordCounts.put(word, previous+1);
		}else{
			wordCounts.put(word, 1);
		}
	}
	
	public void outputWordCount(int minCount, File output) throws IOException{
		System.out.println("Saving word counts to file:" + output.getAbsolutePath());
		System.out.println("Total words:" + wordCounts.keySet().size());
		
		if (!output.exists()){
			output.createNewFile();
			if (output.canWrite()){
				PrintWriter fileOutput = new PrintWriter(output);
				
				Set<String> keys = wordCounts.keySet();
				Iterator<String> keyIterator = keys.iterator();
				
				while(keyIterator.hasNext()){
					String key = keyIterator.next();
					int count = wordCounts.get(key);
					// testing minimum number of occurances
					if(count>=minCount){					
						fileOutput.println(key + ": " + count);
					}
				}
				
				fileOutput.close();
			}
		}else{
			System.out.println("Error: the output file already exists: " + output.getAbsolutePath());
		}
		
	}

	public static void fileOut(String fileName1, String fileName2){
		File dataDir = new File(fileName1);
		File outFile = new File(fileName2);

		WordCounter wordCounter = new WordCounter();
		System.out.println("Hello");
		try{
			wordCounter.parseFile(dataDir);
			wordCounter.outputWordCount(1, outFile);
		}catch(FileNotFoundException e){
			System.err.println("Invalid input dir: " + dataDir.getAbsolutePath());
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	public static Map hashOut(String fileName1){
		File dataDir = new File(fileName1);

		WordCounter wordCounter = new WordCounter();
		System.out.println("Hello");
		try{
			wordCounter.parseFile(dataDir);

		}catch(FileNotFoundException e){
			System.err.println("Invalid input dir: " + dataDir.getAbsolutePath());
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
		return wordCounter.wordCounts;
	}

	public void setHamFreq(){
		trainHamFreq = hashOut("./data/train/ham");
	}

	public void setSpamFreq(){
		trainSpamFreq = hashOut("./data/train/spam");
	}

	//main method
	public static void main(String[] args) {
		WordCounter trainHamFreq = new WordCounter();
		WordCounter trainSpamFreq = new WordCounter();
		//if(args.length < 2){
		//	System.err.println("Usage: java WordCounter <inputDir> <outfile>");
		//	System.exit(0);
		//}
		//trainHamFreq
		fileOut("./data/train/ham", "hamCount.txt");
		fileOut("./data/train/ham2", "ham2Count.txt");
		fileOut("./data/train/spam", "spam2Count.txt");
		trainHamFreq.setHamFreq();
		trainSpamFreq.setSpamFreq();


		
	}
	
}