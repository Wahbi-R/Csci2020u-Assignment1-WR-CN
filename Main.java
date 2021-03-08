import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

public class Main extends Application{
	private static Map<String, Double> wordCounts;
	private Map<String, Double> trainHamFreq;
	private Map<String, Double> trainSpamFreq;
	private Map<String, Double> fileIsSpam;
	private Map<String, Double> wordInSpam;
	private Map<String, Double> wordInHam;
	private Map<String, Double> wordInBoth;

	public double numTruePositives = 0;
	public double numFalsePositives = 0;
	public double numTrueNegatives = 0;
	public double accuracy ;
	public double precision ;
	public double numTestFiles;

	// constructor function
	public Main(){

		wordCounts = new TreeMap<>();
		trainHamFreq = new TreeMap<>();
		trainSpamFreq = new TreeMap<>();
		fileIsSpam = new TreeMap<>();
		wordInSpam = new TreeMap<>();
		wordInHam = new TreeMap<>();
		wordInBoth = new TreeMap<>();
	}
	// parse through every file in the folder and count how many files does each different word shows up
	public void parseFile(File file) throws IOException{
	    // initialize hash set to store every word only once every file
		HashSet<String> existingWords = new HashSet<String>();
		if(file.isDirectory()){
			//parse each file inside the directory
			File[] content = file.listFiles();
			for(File current: content){
				parseFile(current);
			}
		}else{ // If there is only one file in the directory
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
			    // if the word is valid and it already shows up in other files, increase the number of appearance by 1
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
	// check if a word is a proper word
	private boolean isValidWord(String word){
		String allLetters = "^[a-zA-Z]+$";
		// returns true if the word is composed by only letters otherwise returns false;
		return word.matches(allLetters);
	}

	// Create the output file with the words and their appearance in all files
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
					// testing minimum number of occurences
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
    // calls parseFile and outputWordCount function (Used to make files for testing purposes)
	public static void fileOut(String fileName1, String fileName2){
		File dataDir = new File(fileName1);
		File outFile = new File(fileName2);

		Main wordCounter = new Main();
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

	// calls the parseFile function and returns the map wordCounts
	public static Map hashOut(String fileName1){
		File dataDir = new File(fileName1);

		Main wordCounter = new Main();

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

	// set the key value pair for trainHamFreq
	public void setHamFreq(){
		trainHamFreq = hashOut("./data/train/ham");
	}

    // set the key value pair for trainSpamFreq
	public void setSpamFreq(){
		trainSpamFreq = hashOut("./data/train/spam");
	}

    // Calculate the probability that a word is in ham files
	public void putTrainHamFreq(){
		for(String i : trainHamFreq.keySet()){
			wordInHam.put(i, trainHamFreq.get(i)/trainHamFreq.keySet().size());
		}
	}
    // Calculate the probability that a word is in spam files
	public void putTrainSpamFreq(){
		for(String i : trainSpamFreq.keySet()){
			wordInSpam.put(i, trainSpamFreq.get(i)/trainSpamFreq.keySet().size());
		}
	}

    // Calculate the probability that a file is a spam p(S|W)
	public void findWordBoth(){
		for(String i : trainSpamFreq.keySet()){
			for(String n : trainHamFreq.keySet()){
				if(i == n && wordInHam.get(i) != null) { // Checks if the word is in both ham and spam
					wordInBoth.put(i, wordInSpam.get(i)/(wordInSpam.get(i)+wordInHam.get(i)));
				}
			}
		}
	}
    // Calculate the probability that a file is a spam P(S|F)
	public double fileIsSpamProbability(File file)throws IOException{
		double n = 0;
		double threshold = 0.5; // threshold value to distinguish
                                // between numTruePositives, numFalsePositives and numTrueNegatives
			Scanner scanner = new Scanner(file);
			while (scanner.hasNext()) {
				String spamWord = scanner.next();
				if (isValidWord(spamWord) && wordInBoth.containsKey(spamWord)) {
					n += (Math.log(1.0 - wordInBoth.get(spamWord)) - Math.log(wordInBoth.get(spamWord)));

				}
			}
		double fileIsSpam = 1/(1 + Math.pow(Math.E,n));
		if (file.getParent().contains("spam") && fileIsSpam > threshold){
			numTruePositives += 1;
		}
		if (file.getParent().contains("ham") && fileIsSpam > threshold){
			numFalsePositives += 1;
		}
		if (file.getParent().contains("ham") && fileIsSpam < threshold){
			numTrueNegatives += 1;
		}
		numTestFiles += 1;

		DecimalFormat df = new DecimalFormat("0.00000");
		df.format(fileIsSpam); // the probability that a file is a spam

		return fileIsSpam;
	}

	// return a list of TestFile objects after finding the probabilities individually
	public ObservableList<TestFile> getTestFiles(Main testWords, File dataDir) throws IOException {
		ObservableList<TestFile> testFileList = FXCollections.observableArrayList();
		double tempNum = 0.0;
		DecimalFormat df = new DecimalFormat("0.00000");
		if(dataDir.isDirectory()) {
			//parse each file inside the directory
			File[] content = dataDir.listFiles();
			for (File current : content) {
				tempNum = testWords.fileIsSpamProbability(current); //Gets the probability
				testFileList.add(new TestFile(current.getName(), tempNum, dataDir.getName())); //adds to testFile (FileName, Probability, Directory)
			}
		}
		return testFileList;
	}

	@Override
	public void start(Stage primaryStage) throws IOException {
	    // Instantiate Main objects
		Main trainHamFreq = new Main();
		Main trainSpamFreq = new Main();
		Main trainFinal = new Main();
		Main test;

		// create output files for given absolute paths
		fileOut("./data/train/ham", "hamCount.txt");
		fileOut("./data/train/ham2", "ham2Count.txt");
		fileOut("./data/train/spam", "spam2Count.txt");
		trainHamFreq.setHamFreq();
		trainSpamFreq.setSpamFreq();
		//Putting Probability of words in trainHamFreq P(W|H)
		trainHamFreq.putTrainHamFreq();

		//Putting Probability of words in trainSpamFreq P(W|S)
		trainSpamFreq.putTrainSpamFreq();

		//Set every value so far into combined object
		trainFinal.trainSpamFreq = trainSpamFreq.trainSpamFreq;
		trainFinal.trainHamFreq = trainSpamFreq.trainSpamFreq;
		trainFinal.wordInHam = trainHamFreq.wordInHam;
		trainFinal.wordInSpam = trainSpamFreq.wordInSpam;


		//Word is in both ham and spam and probability P(S|W)
		trainFinal.findWordBoth();

		//TEST PHASE
		//Finding Probability that file is spam
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setInitialDirectory(new File("."));
		File mainDirectory = directoryChooser.showDialog(primaryStage);
		test = trainFinal;
		primaryStage.setTitle("Assignment 1 Spam Detection Solution");
		//Creating table
		TableView<TestFile> table;

		//Setting up File Column
		TableColumn<TestFile, String> fileColumn = new TableColumn<>("File");
		fileColumn.setMinWidth(200);
		fileColumn.setCellValueFactory(new PropertyValueFactory<>("filename"));

		//Setting up Actual Class Column
		TableColumn<TestFile, String> actualClassColumn = new TableColumn<>("Actual Class");
		actualClassColumn.setMinWidth(400);
		actualClassColumn.setCellValueFactory(new PropertyValueFactory<>("actualClass"));

		//Setting up Spam Probability Column
		TableColumn<TestFile, String> spamProbabilityColumn = new TableColumn<>("Spam Probability");
		spamProbabilityColumn.setMinWidth(400);
		spamProbabilityColumn.setCellValueFactory(new PropertyValueFactory<>("spamProbability"));

		//Table Setup
		table = new TableView<>();
		table.setItems(getTestFiles(test, mainDirectory));
		table.getColumns().addAll(fileColumn, actualClassColumn, spamProbabilityColumn);

		// calculate accuracy and precision
		test.accuracy = (test.numTruePositives + test.numTrueNegatives)/test.numTestFiles;
		test.precision = test.numTruePositives/ (test.numFalsePositives + test.numTruePositives);

		//Making vBox for Table
		VBox vBox = new VBox();
		vBox.getChildren().addAll(table);

		//Making GridPane for Accuracy and Precision sections
		GridPane grid = new GridPane();
		grid.setPadding(new Insets(00, 10, 10, 10));
		grid.setVgap(5);
		grid.setHgap(100);

		//Accuracy Label
		Label accuracyLabel = new Label("Accuracy: ");
		GridPane.setConstraints(accuracyLabel, 0, 4);

		//Accuracy input
		DecimalFormat df = new DecimalFormat("0.00000");
		TextField accuracyInput = new TextField(String.valueOf(df.format(test.accuracy)));
		
		accuracyInput.setPrefSize(100, 20);
		accuracyInput.setMaxSize(100,20);
		GridPane.setConstraints(accuracyInput, 0, 5);

		//Precision Label
		Label precisionLabel = new Label("Precision: ");
		GridPane.setConstraints(precisionLabel, 0, 6);

		//Precision input
		TextField precisionInput = new TextField(String.valueOf(df.format(test.precision)));
		precisionInput.setPrefSize(100, 20);
		precisionInput.setMaxSize(100,20);
		GridPane.setConstraints(precisionInput, 0, 7);

		//Setting up scene
		grid.getChildren().addAll(accuracyLabel, accuracyInput, precisionLabel, precisionInput);
		grid.setAlignment(Pos.BOTTOM_LEFT);
		grid.add(vBox, 0,1);
		Scene scene = new Scene(grid);
		scene.getStylesheets().add("Colors.css");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	//main method
	public static void main(String[] args){
		launch(args);
	}
	
}