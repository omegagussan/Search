import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class SearchTest
{

    @Test
    void main()
    {
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