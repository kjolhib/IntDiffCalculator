
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import IntDiffCalculator.ChoiceConstants;
import IntDiffCalculator.PrettyPrinter;

/**
 * Differentiation tests.
 * Note, outptus are **NOT** fully simplfiied. Only near terms are simplified, and like terms are not collected.
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
    assertEquals("((4 * x) + (x * 4))", differentiate("x * 4x"));
  }

  @Test
  void testPower() {
    assertEquals("0", differentiate("582 ^ 2"));
    assertEquals("(4 * (x ^ 3))", differentiate("x ^ 4"));
  }

  @Test
  void testTrigExp() {
    assertEquals("((cos(x) + -sin(x)) + (1 / (cos(x) ^ 2)))", differentiate("sin(x) + cos(x) + tan(x)"));
    assertEquals("((cos(x) * cos(x)) + (sin(x) * -sin(x)))", differentiate("sin(x) * cos(x)"));
    assertEquals("((6 * (x ^ 5)) / (x ^ 6))", differentiate("ln(x ^ (4 + 2))"));
  }

  @Test
  void testMisc() {
    assertEquals("(((4 * x) + (x * 4)) * (4 ^ 3))", differentiate("x * 4x * 4 ^ 3")); // 256x^2
    assertEquals("(((cos(x) * x) + sin(x)) + ((5 * (x ^ 4)) * (4 ^ 3)))", differentiate("sin(x) * x + x ^ (3 + 2) * (4 ^ (4 - 1))")); // xsin(x) + 60x
    assertEquals("(((((1 + (4 ^ 2)) / (x + (x * (4 ^ 2)))) * sin((x + 8))) + (ln((x + (x * (4 ^ 2)))) * cos((x + 8)))) + ((1 / (cos(((x / 4) + 2)) ^ 2)) * (4 / (4 ^ 2))))", differentiate("ln(x + x * (4 ^ 2)) * sin(x + 8 * 1) + tan(x / 4 + 2)")); // log(17x)sin(x + 8) + tan((x + 8) / 4)
  }
}
