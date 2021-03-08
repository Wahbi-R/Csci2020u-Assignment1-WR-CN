import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class WordCounter extends Application{
	public void initialize(){
//		TableView<TestFile> table;
//		//Name Columns
//		TableColumn<TestFile, String> fileColumn = new TableColumn<>("File");
//		fileColumn.setCellValueFactory(new PropertyValueFactory<>("filename"));
//
//		TableColumn<TestFile, String> actualClassColumn = new TableColumn<>("Actual Class");
//		actualClassColumn.setCellValueFactory(new PropertyValueFactory<>("actualClass"));
//
//		TableColumn<TestFile, String> spamProbabilityColumn = new TableColumn<>("Spam Probability");
//		spamProbabilityColumn.setCellValueFactory(new PropertyValueFactory<>("spamProbability"));
//
//		table = new TableView<>();
//		table.setItems(getTestFiles());
//		table.getColumns().addAll(fileColumn, actualClassColumn, spamProbabilityColumn);
//		VBox vBox = new VBox();
//		vBox.getChildren().addAll();
	}



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

	public double fileIsSpamProbability(File file)throws IOException{
		double n = 0;
			Scanner scanner = new Scanner(file);
			while (scanner.hasNext()) {
				String spamWord = scanner.next();
				if (isValidWord(spamWord) && wordInBoth.containsKey(spamWord)) {
					n += (Math.log(1.0 - wordInBoth.get(spamWord)) - Math.log(wordInBoth.get(spamWord)));

				}
			}

		System.out.println(n);
		double fileIsSpam = 1/(1 + Math.pow(Math.E,n));
		//System.out.println(fileIsSpam);
		DecimalFormat df = new DecimalFormat("0.00000");
		df.format(fileIsSpam);
		return fileIsSpam;

	}

	public ObservableList<TestFile> getTestFiles(WordCounter testWords) throws IOException {
		ObservableList<TestFile> testFileList = FXCollections.observableArrayList();
		double tempNum = 0.0;
		DecimalFormat df = new DecimalFormat("0.00000");
		File dataDir = new File("./data/test/ham");
		if(dataDir.isDirectory()) {
			//parse each file inside the directory
			File[] content = dataDir.listFiles();
			for (File current : content) {
				tempNum = testWords.fileIsSpamProbability(current);
				testFileList.add(new TestFile(current.getName(), tempNum, dataDir.getName()));
			}

		}
//		testFileList.add(new TestFile(current.getName(), tempNum, content.getName()));
		return testFileList;
	}

	@Override
	public void start(Stage primaryStage) throws IOException {
		WordCounter trainHamFreq = new WordCounter();
		WordCounter trainSpamFreq = new WordCounter();
		WordCounter trainFinal = new WordCounter();
		WordCounter test = new WordCounter();

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
		trainFinal.trainSpamFreq = trainSpamFreq.trainSpamFreq;
		trainFinal.trainHamFreq = trainSpamFreq.trainSpamFreq;
		trainFinal.wordInHam = trainHamFreq.wordInHam;
		trainFinal.wordInSpam = trainSpamFreq.wordInSpam;

		//trainFinal.printTrainSpamFreq();

		//Word is in both ham and spam and probability P(S|W)
		trainFinal.findWordBoth();

		//Get all of the TestFiles


		//TEST PHASE
		//Finding Probability that file is spam
		test = trainFinal;
		primaryStage.setTitle("Assignment 1");

		TableView<TestFile> table;
		//Name Columns
		TableColumn<TestFile, String> fileColumn = new TableColumn<>("File");
		fileColumn.setMinWidth(200);
		fileColumn.setCellValueFactory(new PropertyValueFactory<>("filename"));

		TableColumn<TestFile, String> actualClassColumn = new TableColumn<>("Actual Class");
		actualClassColumn.setMinWidth(400);
		actualClassColumn.setCellValueFactory(new PropertyValueFactory<>("actualClass"));

		TableColumn<TestFile, String> spamProbabilityColumn = new TableColumn<>("Spam Probability");
		spamProbabilityColumn.setMinWidth(400);
		spamProbabilityColumn.setCellValueFactory(new PropertyValueFactory<>("spamProbability"));
		//Table Setup
		table = new TableView<>();
		table.setItems(getTestFiles(test));
		table.getColumns().addAll(fileColumn, actualClassColumn, spamProbabilityColumn);

		VBox vBox = new VBox();
		vBox.getChildren().addAll(table);
		GridPane grid = new GridPane();
		grid.setPadding(new Insets(0, 0, 0, 0));
		grid.setVgap(8);
		grid.setVgap(10);

		//Accuracy Label
		Label accuracyLabel = new Label("Accuracy: ");
		GridPane.setConstraints(accuracyLabel, 0, 4);

		//Accuracy input
		TextField accuracyInput = new TextField("3");
		GridPane.setConstraints(accuracyInput, 1, 4);

		grid.getChildren().addAll(accuracyLabel, accuracyInput);
		grid.setAlignment(Pos.BOTTOM_LEFT);
		grid.add(vBox, 0,1);
		Scene scene = new Scene(grid);
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	//main method
	public static void main(String[] args){
		launch(args);
		//fileIsSpamProbability("./data/test/ham");
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