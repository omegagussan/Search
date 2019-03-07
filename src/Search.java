import java.io.File;
import java.util.Arrays;
import java.util.Scanner;

public class Search
{
    public static void main(String[] args) {
        if( args.length == 0){
            throw new IllegalArgumentException("No directory given to index.");
        }
        final File indexableDirectory = new File(args[0]);
        if (indexableDirectory.isDirectory()){
            final String[] fileNames = indexableDirectory.list();
            System.out.println("found " +  fileNames.length + " files");

            Scanner keyboard = new Scanner(System.in);
            while (true) {
                System.out.print("search> ");
                String line = keyboard.nextLine();
                final String[] words = line.split(" ");
                Arrays.stream(fileNames).map(fileName -> {
                    System.out.println(fileName + " : " + doubleToProccentString(rank(fileName, words)));
                    return null;
                });
            }
        }
        return;
    }

    /*
     * The rank score must be 100% if a file contains all the words
     * It must be 0% if it contains none of the words
     * It should be between 0 and 100 if it contains only some of the words
     */
    protected static double rank(String fileName, String[] wordsToMatch){
        final long occurances = Arrays.stream(wordsToMatch).filter(fileName::contains).count();
        return occurances/(double) wordsToMatch.length;
    }

    protected static String doubleToProccentString(double aDouble){
        return "" + Math.round(aDouble*100) + "%";
    }
}