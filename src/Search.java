import java.io.File;
import java.util.Scanner;

public class Search
{
    public static void main(String[] args) {
        if( args.length == 0){
            throw new IllegalArgumentException("No directory given to index.");
        }
        final File indexableDirectory = new File(args[0]);
        if (indexableDirectory.isDirectory()){
            final File[] files = indexableDirectory.listFiles();
            System.out.println("found " +  files.length + " files");
            Scanner keyboard = new Scanner(System.in);
            while (true) {
                System.out.print("search> ");
                String line = keyboard.nextLine();
                //find files with name matching word in line
            }
        }
    }
}