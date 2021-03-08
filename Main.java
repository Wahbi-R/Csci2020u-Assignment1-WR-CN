public class Main {
    private static void trainHamFreq(){
        String[] run = new String[2];
        run[0] = file1;
        run[1] = fileOutput;
        WordCounter.main(run);
    }

    private static void trainSpamFreq(String file1, String fileOutput){
        String[] run = new String[2];
        run[0] = file1;
        run[1] = fileOutput;
        WordCounter.main(run);
    }


    public static void main(String[] args) {
        //trainHamFreq("./data/train/spam", "spamCount.txt");

        //trainSpamFreq("spam", "spamCount.txt");
    }
}
