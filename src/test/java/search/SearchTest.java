package search;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @BeforeEach
    void setupMock(){
        Search.setDefaultFile(mockedFile);
        Search.setKeyboardScanner(mockedScanner);
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