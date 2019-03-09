package search;

import com.google.common.hash.BloomFilter;

import java.io.File;
import java.io.PrintStream;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;
import java.util.stream.Stream;

public class Search
{

    private static File iDefaultFile;
    private static Scanner iKeyboardScanner;
    private static PrintStream iPrintOut;
    private static ReadUtil iReadUtil = new ReadUtil();

    public static void main(String[] args) {
        final File[] files = getValidDirectory(args).listFiles();
        print("found " +  files.length + " files");
        print("search>");
        final Stream<Pair<String, BloomFilter<String>>> filesContent = getContentFromFilesBloomFilter(files);
        searchWithKeyboard(filesContent);
    }

    static Stream<Pair<String, BloomFilter<String>>> getContentFromFilesBloomFilter(File[] files)
    {
        return Arrays.stream(files)
                .filter(File::isFile)
                .map(file -> new Pair<>(file.getName(), Paths.get(file.getAbsolutePath())))
                .map(pair -> new Pair<>(pair.left, iReadUtil.readFileAsBloomFilter(pair.right)));
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

    /*
     * For tests
     */
    static void setReadUtil(ReadUtil readUtil){
        iReadUtil = readUtil;
    }

    static void searchWithKeyboard(Stream<Pair<String, BloomFilter<String>>> pairStream)
    {
        Scanner keyboard = getKeyboardScanner();
        while (true) {
            String searchLine = keyboard.nextLine().trim();
            if ("quit()".equals(searchLine)){
                break;
            } else if (!"".equals(searchLine)){
                print("search> " + searchLine);
                final String[] searchWords = searchLine.split(" ");
                pairStream
                        .map(pair -> new Pair<>(pair.left, rank(pair.right, searchWords)))
                        .sorted(Comparator.comparingDouble(pair -> (double) ((Pair) pair).right).reversed())
                        .limit(10)
                        .forEach(pair -> print(String.format("%s : %s", pair.left, doubleToProccentString(pair.right))));
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
    static double rank(BloomFilter<String> contentMap, String[] wordsToMatch){
        final long numberOfMatchingWords = Arrays
                .stream(wordsToMatch)
                .filter(contentMap::mightContain)
                .count();
        return numberOfMatchingWords/(double) wordsToMatch.length;
    }

    static String doubleToProccentString(double aDouble){
        return "" + Math.round(aDouble*100) + "%";
    }
}