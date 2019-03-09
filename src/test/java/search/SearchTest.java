package search;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.PrintStream;
import java.nio.charset.Charset;
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
    ReadUtil readUtil;

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
    void searchWithKeyboardBrakesWithQuitDoesNotPrint(){
        Search.setPrintOut(mockedPrintStream);
        when(mockedScanner.nextLine()).thenReturn("quit()");

        Search.searchWithKeyboard(getSingeltonFileBloomFilterPair());
        verify(mockedPrintStream, times(0)).println((String) any());
    }


    @Test
    void searchWithKeyboardPrintsSearchingAndArguments(){
        Search.setPrintOut(mockedPrintStream);
        when(mockedScanner.nextLine()).thenReturn("abc").thenReturn("quit()");
        Search.searchWithKeyboard(getSingeltonFileBloomFilterPair());
        verify(mockedPrintStream).println((String) argThat(argument -> "search> abc".equals(argument)));
    }

    @Test
    void searchWithKeyboardPrintsSearchingAndArgumentsAndOneMoreRowPerFile(){
        Search.setPrintOut(mockedPrintStream);
        when(mockedScanner.nextLine()).thenReturn("abc").thenReturn("quit()");
        Search.searchWithKeyboard(getSingeltonFileBloomFilterPair());
        verify(mockedPrintStream, times(2)).println((String) any());
    }

    @Test
    void searchWithKeyboardPrintsSearchingAndArgumentsAndOneMoreRowPerFile2(){
        Search.setPrintOut(mockedPrintStream);
        when(mockedScanner.nextLine()).thenReturn("abc").thenReturn("quit()");
        Search.searchWithKeyboard(getFileBloomFilterPairWithTwoPairs());
        verify(mockedPrintStream, times(3)).println((String) any());
    }

    @Test
    void searchWithKeyboardPrintsExpectedFindingsForMatches(){
        Search.setPrintOut(mockedPrintStream);
        when(mockedScanner.nextLine()).thenReturn("abc").thenReturn("quit()");
        Search.searchWithKeyboard(getSingeltonFileBloomFilterPair());
        verify(mockedPrintStream).println((String) argThat(argument -> "abc : 100%".equals(argument)));
    }

    @Test
    void searchWithKeyboardPrintsExpectedFindingsForNotMatches(){
        Search.setPrintOut(mockedPrintStream);
        when(mockedScanner.nextLine()).thenReturn("abc").thenReturn("quit()");
        Search.searchWithKeyboard(getSingeltonFileBloomFilterPair("./def"));
        verify(mockedPrintStream).println((String) argThat(argument -> "./def : 0%".equals(argument)));
    }

    @Test
    void searchWithKeyboardPrintsExpectedFindingsForPartial(){
        Search.setPrintOut(mockedPrintStream);
        when(mockedScanner.nextLine()).thenReturn("abc def").thenReturn("quit()");
        Search.searchWithKeyboard(getSingeltonFileBloomFilterPair());
        verify(mockedPrintStream).println((String) argThat(argument -> "abc : 50%".equals(argument)));
    }

    @Test
    void searchWithKeyboardLimitsPrintTo10(){
        Search.setPrintOut(mockedPrintStream);
        when(mockedScanner.nextLine()).thenReturn("abc def").thenReturn("quit()");
        Search.searchWithKeyboard(getStreamWithMoreThan10Files());
        //first print is from the "search> abc def"
        verify(mockedPrintStream, times(11)).println((String) any());
    }

    @Test
    void searchWithKeyboardRemovesLeastMatchedItemsIndicatesOrder(){
        Search.setPrintOut(mockedPrintStream);
        when(mockedScanner.nextLine()).thenReturn("10 11 12").thenReturn("quit()");
        Search.searchWithKeyboard(getStreamWithMoreThan10Files());
        //first print is from the "search> abc def"
        verify(mockedPrintStream, times(11)).println((String) any());
        verify(mockedPrintStream).println((String) argThat(("search> 10 11 12"::equals)));
        verify(mockedPrintStream).println((String) argThat(("1 2 3 4 5 6 7 8 9 10 11 12 : 100%"::equals)));
        verify(mockedPrintStream).println((String) argThat(("1 2 3 4 5 6 7 8 9 10 11 : 67%"::equals)));
        verify(mockedPrintStream).println((String) argThat(("1 2 3 4 5 6 7 8 9 10 : 33%"::equals)));

    }

    private Stream<Pair<String, BloomFilter<String>>> getStreamWithMoreThan10Files()
    {
        Stream<Pair<String, BloomFilter<String>>> aggrigate = getSingeltonFileBloomFilterPair("1");
        String name = "1";
        for (int i = 2; i < 13; i++) {
            name = name + " " + i;
            Stream<Pair<String, BloomFilter<String>>> current = xxxxx(name);
            aggrigate = Stream.concat(aggrigate, current);
        }
        return aggrigate;
    }

    private Stream<Pair<String, BloomFilter<String>>> getSingeltonFileBloomFilterPair()
    {
        return getSingeltonFileBloomFilterPair("abc");
    }

    private Stream<Pair<String, BloomFilter<String>>> getSingeltonFileBloomFilterPair(String nameAndContent)
    {
        return Stream.of(new Pair<>(nameAndContent, getBloomFilter(nameAndContent)));
    }

    private Stream<Pair<String, BloomFilter<String>>> getSingeltonFileBloomFilterPair(final BloomFilter<String> bloomFilter, String nameAndContent)
    {
        final BloomFilter<String> bloomFilterCopy = bloomFilter.copy();
        bloomFilterCopy.put(nameAndContent);
        return Stream.of(new Pair<>(nameAndContent, bloomFilterCopy));
    }

    private Stream<Pair<String, BloomFilter<String>>> xxxxx(final String yyyyyy)
    {
        final BloomFilter<String> bloomFilter = BloomFilter.create(Funnels.stringFunnel(Charset.defaultCharset()), 100);
        for (String word : yyyyyy.split(" ")){
            bloomFilter.put(word);
        }
        return Stream.of(new Pair<>(yyyyyy, bloomFilter));
    }

    private BloomFilter<String> getBloomFilter(String nameAndContent){
        final BloomFilter<String> bloomFilter = BloomFilter.create(Funnels.stringFunnel(Charset.defaultCharset()), 100);
        bloomFilter.put(nameAndContent);
        return bloomFilter;
    }

    private Stream<Pair<String, BloomFilter<String>>> getFileBloomFilterPairWithTwoPairs()
    {
        return Stream.concat(getSingeltonFileBloomFilterPair(), getSingeltonFileBloomFilterPair());
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
        assertEquals(1.0, Search.rank(getBloomFilter("a"), new String[]{"a"}));
    }

    @Test
    void rankIfNoWordsMatchIsZero()
    {
        assertEquals(0.0, Search.rank(getBloomFilter("a"), new String[]{"b"}));
    }

    @Test
    void rankIfHalfWordsMatchIsZero()
    {
        assertEquals(0.5, Search.rank(getBloomFilter("a"), new String[]{"b", "a"}));
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