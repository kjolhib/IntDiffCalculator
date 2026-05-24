import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import IntDiffCalculator.ChoiceConstants;
import IntDiffCalculator.PrettyPrinter;

/**
 * Integration tests
 */
public class IntegrationTests {
  private static String integrate(String exprString) {
    return new PrettyPrinter().print(TestingHelpers.toAst(exprString, ChoiceConstants.INTEGRATION));
  }

  @Test
  void testConstant() {
    assertEquals("3x", integrate("3"));
    assertEquals("3x ^ 2 / 2", integrate("3 * x"));
  }

  @Test
  void testZero() {
    assertEquals("0", integrate("0"));
  }

  @Test
  void testOne() {
    assertEquals("x", integrate("1"));
  }

  @Test
  void testNonWrtVars() {
    assertEquals("c * x", integrate("c"));
  }

  @Test
  void testPower() {
    assertEquals("x ^ 3 / 3", integrate("x^2"));
  }

  @Test
  void testNeg() {
    assertEquals("x ^ -3 / -3", integrate("x^(-4)")); // documented as a quirk with pretty printer, slightly ambiguous (x ^ -3 instead of x ^ (-3))
    assertEquals("x ^ -3 / -3", integrate("1/x^(4)")); // equivalent to neg powers
    assertEquals("-x ^ 2 / 2", integrate("-x"));
  }

  @Test
  void testSum() {
    assertEquals("x + 2x ^ 2 / 2", integrate("2x + 1"));
  }

  @Test
  void testSubtract() {
    assertEquals("-x + 2x ^ 2 / 2", integrate("2x - 1"));
  }

  @Test
  void testSin() {
    assertEquals("-cos(x)", integrate("sin(x)"));
    assertEquals("-2cos(x)", integrate("2sin(x)"));
  }

  @Test
  void testCos() {
    assertEquals("sin(x)", integrate("cos(x)"));
    assertEquals("2sin(x)", integrate("2cos(x)"));
  }

  @Test
  void testTan() {
    assertEquals("-ln(cos(x))", integrate("tan(x)"));
  }

  @Test
  void testExp() {
    assertEquals("exp(x)", integrate("exp(x)"));
  }

  @Test 
  void testLn() {
    assertEquals("ln(x)", integrate("1/x"));
    assertEquals("-x + x * ln(x)", integrate("ln(x)"));
  }

  // Unsupported operations
  @Test
  void testUSub() {
    assertThrows(UnsupportedOperationException.class, () -> TestingHelpers.toAst("x * sin(x)", ChoiceConstants.INTEGRATION));
  }

  @Test
  void testVarExp() {
    assertThrows(UnsupportedOperationException.class, () -> TestingHelpers.toAst("x ^ x", ChoiceConstants.INTEGRATION));
  }
}
