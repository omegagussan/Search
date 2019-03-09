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

    //TODO: make this depend on items in stream. Worth checking twice to not degreade filter
    private static final int EXPECTED_INSERTIONS = 1000;
    private static final double COLISSON_RATE = 0.001;

    String readFileAsString(Path path)
    {
        try(Stream<String> lines = Files.lines(path)){
            return lines.collect(Collectors.joining());
        } catch (IOException e) {
            System.err.println("SKIPPING: Could not open file with path: " +  path);
            return null;
        }
    }

    //https://www.baeldung.com/guava-bloom-filter
    BloomFilter<String> readFileAsBloomFilter(Path path)
    {
        try(Stream<String> lines = Files.lines(path)){
            final BloomFilter<String> bloomFilter = BloomFilter.create(Funnels.stringFunnel(Charset.defaultCharset()), EXPECTED_INSERTIONS, COLISSON_RATE);
            lines.map(line -> line.split(" ")).flatMap(Arrays::stream).forEach(bloomFilter::put);
            return bloomFilter;
        } catch (IOException e) {
            System.err.println("SKIPPING: Could not open file with path: " +  path);
            return null;
        }
    }
}
