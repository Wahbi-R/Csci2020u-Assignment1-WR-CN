import java.io.*;
import java.util.*;


public class WordCounter{
	
	private static Map<String, Double> wordCounts;
	private Map<String, Double> trainHamFreq;
	private Map<String, Double> trainSpamFreq;
	private Map<String, Double> fileIsSpam;
	private Map<String, Double> wordInSpam;
	private Map<String, Double> wordInHam;
	private Map<String, Double> wordInBoth;
	
	public WordCounter(){
		wordCounts = new TreeMap<>();
		trainHamFreq = new TreeMap<>();
		trainSpamFreq = new TreeMap<>();
		fileIsSpam = new TreeMap<>();
		wordInSpam = new TreeMap<>();
		wordInHam = new TreeMap<>();
		wordInBoth = new TreeMap<>();
	}
	
	public void parseFile(File file) throws IOException{
		//System.out.println("Starting parsing the file:" + file.getAbsolutePcmdath());
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
					double previous = wordCounts.get(words.get(i));
					wordCounts.put(words.get(i), previous+1.0);
				}
				else{
					wordCounts.put(words.get(i),1.0);
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
			double previous = wordCounts.get(word);
			wordCounts.put(word, previous+1);
		}else{
			wordCounts.put(word, 1.0);
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
					double count = wordCounts.get(key);
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
		//System.out.println("Hello");
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

	public void printTrainHamFreq(){
		for(String i : wordInHam.keySet()){
			System.out.println("Key" + i + " " + wordInHam.get(i));
		}
	}

	public void printTrainSpamFreq(){
		for(String i : wordInSpam.keySet()){
			System.out.println("Key" + i + " " + wordInSpam.get(i));
		}
	}


	public void putTrainHamFreq(){
		for(String i : trainHamFreq.keySet()){
			wordInHam.put(i, trainHamFreq.get(i)/trainHamFreq.keySet().size());
		}
	}

	public void putTrainSpamFreq(){
		for(String i : trainSpamFreq.keySet()){
			wordInSpam.put(i, trainSpamFreq.get(i)/trainSpamFreq.keySet().size());
		}
	}

	public void findWordBoth(){
		for(String i : trainSpamFreq.keySet()){
			for(String n : trainHamFreq.keySet()){
				if(i == n && wordInHam.get(i) != null) {
					wordInBoth.put(i, wordInSpam.get(i)/(wordInSpam.get(i)+wordInHam.get(i)));
				}
			}
		}
	}

	public double fileIsSpamProbability(){
		double n = 0;
		File file = new File("./data/test/spam");
		Scanner scanner = new Scanner("./data/test/spam");
		if(file.isDirectory()) {
			//parse each file inside the directory
			File[] content = file.listFiles();
			for (File current : content) {
				fileIsSpamProbability(current);
			}
		}
		else{
			while (scanner.hasNext()) {
				String spamWord = scanner.next();
				if (isValidWord(spamWord) && wordInBoth.containsKey(spamWord)) {
					n += (Math.log(1.0 - wordInBoth.get(spamWord)) - Math.log(wordInBoth.get(spamWord)));
				}
			}
		}

		double fileIsSpam = 1/(1 + Math.pow(Math.E,n));
		return fileIsSpam;

	}

	//main method
	public static void main(String[] args) {
		WordCounter trainHamFreq = new WordCounter();
		WordCounter trainSpamFreq = new WordCounter();
		WordCounter trainFinal = new WordCounter();

		fileOut("./data/train/ham", "hamCount.txt");
		fileOut("./data/train/ham2", "ham2Count.txt");
		fileOut("./data/train/spam", "spam2Count.txt");
		trainHamFreq.setHamFreq();
		trainSpamFreq.setSpamFreq();
		//Putting Probability of words in trainHamFreq P(W|H)
		trainHamFreq.putTrainHamFreq();
		//trainHamFreq.printTrainHamFreq();

		//Putting Probability of words in trainSpamFreq P(W|S)
		trainSpamFreq.putTrainSpamFreq();

		//Set every value so far
		trainFinal.wordInHam = trainHamFreq.wordInHam;
		trainFinal.wordInSpam = trainSpamFreq.wordInSpam;
		trainFinal.trainSpamFreq = trainSpamFreq.trainSpamFreq;
		trainFinal.trainHamFreq = trainSpamFreq.trainSpamFreq;

		//trainFinal.printTrainSpamFreq();

		//Word is in both ham and spam and probability P(S|W)
		trainFinal.findWordBoth();

		System.out.println(trainFinal.fileIsSpamProbability());
		//Finding Probability that file is spam

		// Test phase

//		// Create the object for the test phase
//		WordCounter testHamFreq = new WordCounter();
//		WordCounter testSpamFreq = new WordCounter();
//		WordCounter testFinal = new WordCounter();
//
//		fileOut("./data/test/ham", "hamCount.txt");
//		fileOut("./data/test/spam", "spamCount.txt");
		
	}
	
}