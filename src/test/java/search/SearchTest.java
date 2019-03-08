package search;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchTest
{
    @Mock
    File mockedFile;
    @Mock
    Scanner mockedScanner;
    @Mock
    PrintStream mockedPrintStream;
    @Mock
    Search.ReadUtil readUtil;

    @BeforeEach
    void setupMock(){
        Search.setDefaultFile(mockedFile);
        Search.setKeyboardScanner(mockedScanner);
        Search.setReadUtil(readUtil);
    }

    @AfterEach
    void tearDownMock(){
        Search.setDefaultFile(null);
        Search.setKeyboardScanner(null);
    }

//    @Test
//    void mainPrintsCorrectLengthOfFiles(){
//        Search.setDefaultFile(mockedFile);
//        when(mockedFile.isDirectory()).thenReturn(true);
//        Search.setPrintOut(mockedPrintStream);
//        when(mockedScanner.nextLine()).thenReturn("quit()");
//
//        when(mockedFile.list()).thenReturn(new String[]{"1", "2", "3"});
//        Search.main(new String[]{"./validPath"});
//        verify(mockedPrintStream).println((String) argThat(argument -> "found 3 files".equals(argument)));
//    }
//
//    @Test
//    void mainPrintsFirstSearch(){
//        Search.setDefaultFile(mockedFile);
//        when(mockedFile.isDirectory()).thenReturn(true);
//        Search.setPrintOut(mockedPrintStream);
//        when(mockedScanner.nextLine()).thenReturn("quit()");
//
//        when(mockedFile.list()).thenReturn(new String[]{"1", "2", "3"});
//        Search.main(new String[]{"./validPath"});
//        verify(mockedPrintStream).println((String) argThat(argument -> "search>".equals(argument)));
//    }

    @Test
    void getContentFromFilesGetsCorrectItem()
    {
        when(mockedFile.getAbsolutePath()).thenReturn("./absolutePath");
        when(mockedFile.getName()).thenReturn("1");
        when(mockedFile.isFile()).thenReturn(true);

        when(readUtil.readFileAsString(any())).thenReturn("a");
        File[] testFile= new File[]{mockedFile};
        final List<Search.Pair> contentFromFiles = Search.getContentFromFiles(testFile);
        assertTrue(contentFromFiles.size() > 0);
        assertEquals("a", contentFromFiles.get(0).right);
        assertEquals("1", contentFromFiles.get(0).left);
    }

    @Test
    void getContentFromFilesGetsManyCorrectItem()
    {
        when(mockedFile.getAbsolutePath()).thenReturn("./absolutePath");
        when(mockedFile.getName()).thenReturn("1").thenReturn("2").thenReturn("3");
        when(mockedFile.isFile()).thenReturn(true);

        when(readUtil.readFileAsString(any())).thenReturn("a").thenReturn("b").thenReturn("c");
        File[] testFile= new File[]{mockedFile, mockedFile, mockedFile};
        final List<Search.Pair> contentFromFiles = Search.getContentFromFiles(testFile);
        assertTrue(contentFromFiles.size() > 0);
        assertEquals("a", contentFromFiles.get(0).right);
        assertEquals("1", contentFromFiles.get(0).left);
        assertEquals("b", contentFromFiles.get(1).right);
        assertEquals("2", contentFromFiles.get(1).left);
        assertEquals("c", contentFromFiles.get(2).right);
        assertEquals("3", contentFromFiles.get(2).left);
    }

    @Test
    void searchWithKeyboardBrakesWithQuitDoesNotPrint(){
        Search.setPrintOut(mockedPrintStream);
        when(mockedScanner.nextLine()).thenReturn("quit()");

        List<Search.Pair> pairList= Collections.singletonList(new Search.Pair("./abc", "./abc"));
        Search.searchWithKeyboard(pairList);
        verify(mockedPrintStream, times(0)).println((String) any());
    }

    @Test
    void searchWithKeyboardPrintsSearchingAndArguments(){
        Search.setPrintOut(mockedPrintStream);
        when(mockedScanner.nextLine()).thenReturn("abc").thenReturn("quit()");
        List<Search.Pair> pairList= Collections.singletonList(new Search.Pair("./abc", "./abc"));
        Search.searchWithKeyboard(pairList);
        verify(mockedPrintStream).println((String) argThat(argument -> "search> abc".equals(argument)));
    }

    @Test
    void searchWithKeyboardPrintsSearchingAndArgumentsAndOneMoreRowPerFile(){
        Search.setPrintOut(mockedPrintStream);
        when(mockedScanner.nextLine()).thenReturn("abc").thenReturn("quit()");
        List<Search.Pair> pairList= Collections.singletonList(new Search.Pair("./abc", "./abc"));
        Search.searchWithKeyboard(pairList);
        verify(mockedPrintStream, times(2)).println((String) any());
    }

    @Test
    void searchWithKeyboardPrintsSearchingAndArgumentsAndOneMoreRowPerFile2(){
        Search.setPrintOut(mockedPrintStream);
        when(mockedScanner.nextLine()).thenReturn("abc").thenReturn("quit()");
        List<Search.Pair> pairList= Arrays.asList(new Search.Pair("./abc", "./abc"), new Search.Pair("./abc", "./abc"));
        Search.searchWithKeyboard(pairList);
        verify(mockedPrintStream, times(3)).println((String) any());
    }

    @Test
    void searchWithKeyboardPrintsExpectedFindingsForMatches(){
        Search.setPrintOut(mockedPrintStream);
        when(mockedScanner.nextLine()).thenReturn("abc").thenReturn("quit()");
        List<Search.Pair> pairList= Collections.singletonList(new Search.Pair("./abc", "./abc"));
        Search.searchWithKeyboard(pairList);
        verify(mockedPrintStream).println((String) argThat(argument -> "./abc : 100%".equals(argument)));
    }

    @Test
    void searchWithKeyboardPrintsExpectedFindingsForNotMatches(){
        Search.setPrintOut(mockedPrintStream);
        when(mockedScanner.nextLine()).thenReturn("abc").thenReturn("quit()");
        List<Search.Pair> pairList= Collections.singletonList(new Search.Pair("./def", "./def"));
        Search.searchWithKeyboard(pairList);
        verify(mockedPrintStream).println((String) argThat(argument -> "./def : 0%".equals(argument)));
    }

    @Test
    void searchWithKeyboardPrintsExpectedFindingsForPartial(){
        Search.setPrintOut(mockedPrintStream);
        when(mockedScanner.nextLine()).thenReturn("abc def").thenReturn("quit()");
        List<Search.Pair> pairList= Collections.singletonList(new Search.Pair("./abc", "./abc"));
        Search.searchWithKeyboard(pairList);
        verify(mockedPrintStream).println((String) argThat(argument -> "./abc : 50%".equals(argument)));
    }

    @Test
    void searchWithKeyboardLimitsPrintTo10(){
        Search.setPrintOut(mockedPrintStream);
        when(mockedScanner.nextLine()).thenReturn("abc def").thenReturn("quit()");
        List<Search.Pair> pairListWithMoreThan10Files= getPairListWithMoreThan10Files();
        Search.searchWithKeyboard(pairListWithMoreThan10Files);
        //first print is from the "search> abc def"
        verify(mockedPrintStream, times(11)).println((String) any());
    }

    @Test
    void searchWithKeyboardRemovesLeastMatchedItemsIndicatesOrder(){
        Search.setPrintOut(mockedPrintStream);
        when(mockedScanner.nextLine()).thenReturn("10 11 12").thenReturn("quit()");
        List<Search.Pair> pairListWithMoreThan10Files= getPairListWithMoreThan10Files();
        Search.searchWithKeyboard(pairListWithMoreThan10Files);
        //first print is from the "search> abc def"
        verify(mockedPrintStream, times(11)).println((String) any());
        verify(mockedPrintStream).println((String) argThat(("search> 10 11 12"::equals)));
        verify(mockedPrintStream).println((String) argThat(("12 : 100%"::equals)));
        verify(mockedPrintStream).println((String) argThat(("11 : 67%"::equals)));
        verify(mockedPrintStream).println((String) argThat(("10 : 33%"::equals)));

    }

    private List<Search.Pair> getPairListWithMoreThan10Files()
    {
        return Arrays.asList(new Search.Pair("1", "1"),
                new Search.Pair("1", "1"),
                new Search.Pair("2", "1 2"),
                new Search.Pair("3", "1 2 3"),
                new Search.Pair("4", "1 2 3 4"),
                new Search.Pair("5", "1 2 3 4 5"),
                new Search.Pair("6", "1 2 3 4 5 6"),
                new Search.Pair("7", "1 2 3 4 5 6 7"),
                new Search.Pair("8", "1 2 3 4 5 6 7 8"),
                new Search.Pair("9", "1 2 3 4 5 6 7 8 9"),
                new Search.Pair("10", "1 2 3 4 5 6 7 8 9 10"),
                new Search.Pair("11", "1 2 3 4 5 6 7 8 9 10 11"),
                new Search.Pair("12", "1 2 3 4 5 6 7 8 9 10 11 12"));
    }

    @Test
    void getValidDirectoryOfEmpty(){
        assertThrows(IllegalArgumentException.class, ()-> Search.getValidDirectory(new String[]{}));
    }

    @Test
    void getValidDirectoryOfNotADirectory(){
        assertThrows(IllegalArgumentException.class, ()-> Search.getValidDirectory(new String[]{}));
    }

    @Test
    void getValidDirectoryOfNonDirectoryFile(){
        Search.setDefaultFile(mockedFile);
        when(mockedFile.isDirectory()).thenReturn(false);
        assertThrows(IllegalArgumentException.class, ()-> Search.getValidDirectory(new String[]{"./valid_directory"}));
    }

    @Test
    void getValidDirectoryOfValidDirectoryDoesNotThrow(){
        Search.setDefaultFile(mockedFile);
        when(mockedFile.isDirectory()).thenReturn(true);
        Search.getValidDirectory(new String[] {"./valid_directory"});
    }

    @Test
    void rankIfAllWordsMatchIsOne()
    {
        assertEquals(1.0, Search.rank("a", new String[]{"a"}));
    }

    @Test
    void rankIfNoWordsMatchIsZero()
    {
        assertEquals(0.0, Search.rank("a", new String[]{"b"}));
    }

    @Test
    void rankIfHalfWordsMatchIsZero()
    {
        assertEquals(0.5, Search.rank("a", new String[]{"b", "a"}));
    }

    @Test
    void doubleToProccentStringOf0()
    {
        assertEquals("0%", Search.doubleToProccentString(0.0));
    }

    @Test
    void doubleToProccentStringOf1()
    {
        assertEquals("100%", Search.doubleToProccentString(1.0));
    }

    @Test
    void doubleToProccentStringOf2()
    {
        assertEquals("200%", Search.doubleToProccentString(2.0));
    }

    @Test
    void doubleToProccentStringOfNegative1()
    {
        assertEquals("-100%", Search.doubleToProccentString(-1.0));
    }
}