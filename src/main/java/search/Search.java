package search;

import java.io.File;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Scanner;

public class Search
{
    private static File iDefaultFile;
    private static Scanner iKeyboardScanner;
    private static PrintStream iPrintOut;

    public static void main(String[] args) {
        final String[] fileNames = getValidDirectory(args).list();
        print("found " +  fileNames.length + " files");
        print("search>");
        searchWithKeyboard(fileNames);
    }

    static File getValidDirectory(String[] args)
    {
        if( args.length == 0) throw new IllegalArgumentException("No directory given to index.");

        final File indexableDirectory = getNewFile(args[0]);

        if (!indexableDirectory.isDirectory()) throw new IllegalArgumentException("Path given is not a directory");

        return indexableDirectory;
    }

    private static File getNewFile(String arg)
    {
        if (iDefaultFile != null) return iDefaultFile;
        return new File(arg);
    }

    /*
     * For tests
     */
    static void setDefaultFile(File aFile){
        iDefaultFile = aFile;
    }

    /*
     * For tests
     */
    static void setKeyboardScanner(Scanner aScanner){
        iKeyboardScanner = aScanner;
    }

    static void searchWithKeyboard(String[] fileNames)
    {
        Scanner keyboard = getKeyboardScanner();
        while (true) {
            String line = keyboard.nextLine();
            if ("quit()".equals(line)){
                break;
            }else if (line != null && !"".equals(line)){
                print("search> " + line);
                final String[] words = line.split(" ");
                Arrays.stream(fileNames).forEach(fileName -> print(fileName + " : " + doubleToProccentString(rank(fileName, words))));
            }
        }
    }

    private static void print(String toPrint){
        if (iPrintOut == null){
            iPrintOut = System.out;
        }
        iPrintOut.println(toPrint);
    }

    static void setPrintOut(PrintStream aPrintStream){
        iPrintOut = aPrintStream;
    }

    private static Scanner getKeyboardScanner()
    {
        if (iKeyboardScanner == null) iKeyboardScanner = new Scanner(System.in);
        return iKeyboardScanner;
    }

    /*
     * The rank score must be 100% if a iDefaultFile contains all the words
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