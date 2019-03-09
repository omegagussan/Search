package search;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class ReadUtil
{
    String readFileAsString(Path path)
    {
        try(Stream<String> lines = Files.lines(path)){
            return lines.collect(Collectors.joining());
        } catch (IOException e) {
            System.err.println("SKIPPING: Could not open file with path: " +  path);
            return null;
        }
    }

    BloomFilter<String> readFileAsBloomFilter(Path path, int expectedInsertions)
    {
        try(Stream<String> lines = Files.lines(path)){
            //https://www.baeldung.com/guava-bloom-filter
            BloomFilter<String> bloomFilter =  BloomFilter.create(Funnels.stringFunnel(Charset.defaultCharset()), expectedInsertions, 0.001);
            lines.map(line -> line.split(" ")).flatMap(Arrays::stream).forEach(bloomFilter::put);
            return bloomFilter;
        } catch (IOException e) {
            System.err.println("SKIPPING: Could not open file with path: " +  path);
            return null;
        }
    }
}
