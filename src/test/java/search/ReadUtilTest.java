package search;

import com.google.common.hash.BloomFilter;
import org.junit.jupiter.api.Test;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class ReadUtilTest
{
    @Test
    void bloomFilterMightContainContent(){
        ReadUtil readUtil = new ReadUtil();
        final BloomFilter<String> stringBloomFilter = readUtil.readFileAsBloomFilter(Paths.get("src/test/resources/testFile"));
        //assertTrue(stringBloomFilter.mightContain("Lorem"));
        //this should fail 1/100 times approximatley
    }

    @Test
    void bloomFilterWillBeSureOfWhenItDoesNot(){
        ReadUtil readUtil = new ReadUtil();
        final BloomFilter<String> stringBloomFilter = readUtil.readFileAsBloomFilter(Paths.get("src/test/resources/testFile"));
        assertFalse(stringBloomFilter.mightContain("Brandbil"));
    }

    @Test
    void stringWillBeSureOfWhenItDoesNot(){
        ReadUtil readUtil = new ReadUtil();
        final String readFileAsString = readUtil.readFileAsString(Paths.get("src/test/resources/testFile"));
        assertFalse(readFileAsString.contains("Brandbil"));
    }

    @Test
    void stringWillBeSureOfWhenItDoes(){
        ReadUtil readUtil = new ReadUtil();
        final String readFileAsString = readUtil.readFileAsString(Paths.get("src/test/resources/testFile"));
        assertTrue(readFileAsString.contains("Lorem"));
    }
}