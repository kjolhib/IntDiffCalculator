
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import IntDiffCalculator.ChoiceConstants;
import IntDiffCalculator.PrettyPrinter;

/**
 * Differentiation tests.
 */
public class DifferentiationTests {
  private static String differentiate(String exprString) {
    return new PrettyPrinter().print(TestingHelpers.toAst(exprString, ChoiceConstants.DIFFERENTIATION));
  }

  @Test
  void testConstant() {
    assertEquals("0", differentiate("582"));
  }

  @Test
  void testSimpleVar() {
    assertEquals("1", differentiate("x"));
  }

  @Test
  void testAddition() {
    assertEquals("0", differentiate("582 + 2"));
    assertEquals("1", differentiate("x + 4"));
    assertEquals("5", differentiate("x + 4x"));
  }
  
  @Test
  void testSubtraction() {
    assertEquals("0", differentiate("582 - 2"));
    assertEquals("1", differentiate("x - 4"));
    assertEquals("-3", differentiate("x - 4x"));
  }

  @Test
  void testMultiplication() {
    assertEquals("0", differentiate("582 * 2"));
    assertEquals("4", differentiate("x * 4"));
    assertEquals("8x", differentiate("x * 4x"));
  }

  @Test
  void testPower() {
    assertEquals("0", differentiate("582 ^ 2"));
    assertEquals("4x ^ 3", differentiate("x ^ 4"));
  }

  @Test
  void testTrigExp() {
    assertEquals("1 / cos(x) ^ 2 + cos(x) - sin(x)", differentiate("sin(x) + cos(x) + tan(x)"));
    assertEquals("cos(x) * cos(x) + sin(x) * -sin(x)", differentiate("sin(x) * cos(x)"));
    assertEquals("6x ^ 5 / x ^ 6", differentiate("ln(x ^ (4 + 2))"));
  }

  @Test
  void testMisc() {
    assertEquals("8x * 4 ^ 3", differentiate("x * 4x * 4 ^ 3")); // d/dx(256x^2)
    assertEquals("5x ^ 4 * 4 ^ 3 + cos(x) * x + sin(x)", differentiate("sin(x) * x + x ^ (3 + 2) * (4 ^ (4 - 1))")); // d/dx(xsin(x) + 60x)
    assertEquals("(1 + 4 ^ 2) / (x + x * 4 ^ 2) * sin(8 + x) + 1 / cos(2 + x / 4) ^ 2 * 4 / 4 ^ 2 + ln(x + x * 4 ^ 2) * cos(8 + x)",
      differentiate("ln(x + x * (4 ^ 2)) * sin(x + 8 * 1) + tan(x / 4 + 2)")); // d/dx(log(17x)sin(x + 8) + tan((x + 8) / 4))
  }
}
