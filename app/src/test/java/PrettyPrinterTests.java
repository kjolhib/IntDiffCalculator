import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import IntDiffCalculator.PrettyPrinter;

public class PrettyPrinterTests {
  private final PrettyPrinter printer = new PrettyPrinter();

  private String printParse(String input) {
    return printer.print(TestingHelpers.parse(input));
  }

  @Test
  void testSimpleNumber() {
    assertEquals("3", printParse("3"));
  }

  @Test
  void testNonSingleDigit() {
    assertEquals("342", printParse("342"));
    assertEquals("88", printParse("88"));
    assertEquals("3.4", printParse("3.4"));
  }

  @Test
  void testSimpleVariable() {
    assertEquals("x", printParse("x"));
    assertEquals("(42 * x)", printParse("42x"));
    assertEquals("(3.4 * x)", printParse("3.4x"));
  }

  @Test
  void testSimpleAddition() {
    assertEquals("(2 + 3)", printParse("2 + 3"));
    assertEquals("(x + 5)", printParse("x + 5"));
    assertEquals("(3.4 + 25.1)", printParse("3.4 + 25.1"));
  }

  @Test
  void testSimpleMultiplication() {
    assertEquals("(2 * 3)", printParse("2 * 3"));
    assertEquals("(x * 5)", printParse("x * 5"));
    assertEquals("(3.4 * 25.1)", printParse("3.4 * 25.1"));
  }

  @Test
  void testPrecedence() {
    assertEquals("(a * x)", printParse("ax"));
    assertEquals("((3 + x) * 4)", printParse("(3+x)*4"));
    assertEquals("(3 + (4 * x))", printParse("3 + 4 * x"));
    assertEquals("(2 ^ (5 ^ 4))", printParse("2^5^4"));
  }

  @Test
  void testImplicitMultiplication() {
    assertEquals("((2 + 3) * x)", printParse("(2+3)x"));
    assertEquals("(3 * (x + 2))", printParse("3(x+2)"));
    assertEquals("(2 * (3 * (x + 2)))", printParse("2*3(x+2)"));
  }
}
