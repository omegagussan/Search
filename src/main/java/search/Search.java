package search;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Search
{
    static class Pair{
        final Object right;
        final Object left;
        Pair(Object left, Object right){
            this.right = right;
            this.left = left;
        }
    }

    static class ReadUtil{
        String readFileAsString(String path)
        {
            try(Stream<String> lines = Files.lines(Paths.get(path))){
                return lines.collect(Collectors.joining());
            } catch (IOException e) {
                System.err.println("SKIPPING: Could not open file with path: " +  path);
                return null;
            }
        }
    }

    private static File iDefaultFile;
    private static Scanner iKeyboardScanner;
    private static PrintStream iPrintOut;
    private static ReadUtil iReadUtil = new ReadUtil();

    public static void main(String[] args) {
        final File[] files = getValidDirectory(args).listFiles();
        print("found " +  files.length + " files");
        print("search>");
        final List<Pair> filesContent = getContentFromFiles(files);
        searchWithKeyboard(filesContent);
    }

    static List<Pair> getContentFromFiles(File[] files)
    {
        return Arrays.stream(files)
                    .filter(File::isFile)
                    .map(file -> new Pair(file.getName(), file.getAbsolutePath()))
                    .map(pair -> new Pair(pair.left, iReadUtil.readFileAsString((String) pair.right)))
                    .collect(Collectors.toList());
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

    static void searchWithKeyboard(List<Pair> pairList)
    {
        Scanner keyboard = getKeyboardScanner();
        while (true) {
            String line = keyboard.nextLine();
            if ("quit()".equals(line)){
                break;
            }else if (line != null && !"".equals(line)){
                print("search> " + line);
                final String[] words = line.split(" ");
                pairList.stream()
                        .map(pair -> new Pair(pair.left, rank((String) pair.right, words)))
                        .sorted(Comparator.comparingDouble(pair -> {
                            Pair p = (Pair) pair;
                            return (double) p.right;
                        }).reversed())
                        .limit(10)
                        .forEach(pair -> print(String.format("%s : %s", pair.left, doubleToProccentString((double) pair.right))));
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
    static double rank(String fileName, String[] wordsToMatch){
        final long occurances = Arrays.stream(wordsToMatch).filter(fileName::contains).count();
        return occurances/(double) wordsToMatch.length;
    }

    static String doubleToProccentString(double aDouble){
        return "" + Math.round(aDouble*100) + "%";
    }
}