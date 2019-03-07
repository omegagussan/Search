import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SearchTest
{

    @Test
    public void main()
    {
    }

    @Test
    public void rank()
    {
    }

    @Test
    public void doubleToProccentStringOf0()
    {
        assertEquals("0%", Search.doubleToProccentString(0.0));
    }

    @Test
    public void doubleToProccentStringOf1()
    {
        assertEquals("100%", Search.doubleToProccentString(1.0));
    }

    @Test
    public void doubleToProccentStringOf2()
    {
        assertEquals("200%", Search.doubleToProccentString(2.0));
    }

    @Test
    public void doubleToProccentStringOfNegative1()
    {
        assertEquals("-100%", Search.doubleToProccentString(-1.0));
    }
}