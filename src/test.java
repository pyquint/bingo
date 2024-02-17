import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class test {

    @Test
    public void numberRepr() {
        assertEquals('!', BINGO.getNumberRepr(1));
        assertEquals('*', BINGO.getNumberRepr(10));
        assertEquals('A', BINGO.getNumberRepr(33));
        assertEquals('k', BINGO.getNumberRepr(75));
    }

    @Test
    public void reprNumber() {
        assertEquals(24, BINGO.getReprNumber('8'));
        assertEquals(1, BINGO.getReprNumber('!'));
    }
}
