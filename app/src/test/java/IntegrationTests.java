import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import CalculusCalculator.ChoiceConstants;
import CalculusCalculator.PrettyPrinter;

/**
 * Integration tests
 */
public class IntegrationTests {
  private static String integrate(String exprString) {
    return new PrettyPrinter().print(TestingHelpers.toAst(exprString, ChoiceConstants.INTEGRATION));
  }

  // Simple integration tests
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
    assertEquals("cx", integrate("c"));
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
    assertEquals("2x ^ 2 / 2 + x", integrate("2x + 1"));
  }

  @Test
  void testSubtract() {
    assertEquals("2x ^ 2 / 2 - x", integrate("2x - 1"));
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
    assertEquals("-x + xln(x)", integrate("ln(x)"));
  }

  // U-Substitution tests
  @Test
  void testUSubMul() {
    assertEquals("-cos(x ^ 2)", integrate("2x * sin(x ^ 2)"));
    assertEquals("-cos(x ^ 2)", integrate("sin(x ^ 2) * 2x")); // check that both ways is fine
    assertEquals("sin(x ^ 2)", integrate("2x * cos(x ^ 2)"));
    assertEquals("exp(x ^ 2)", integrate("2x * exp(x ^ 2)"));
    assertEquals("-cos(x ^ 3)", integrate("3x^2 * sin(x^3)"));
    assertEquals("ln(x ^ 2 + 1)", integrate("2x / (x ^ 2 + 1)"));
  }

  @Test
  void testUSubDiv() {
    assertEquals("ln(x ^ 2 + 1)", integrate("2x / (x ^ 2 + 1)"));
    assertEquals("ln(x ^ 3 + 1)", integrate("3x^2 / (x^3 + 1)"));
    assertEquals("ln(sin(x))", integrate("cos(x) / sin(x)"));
    assertEquals("ln(cos(x))", integrate("-sin(x) / cos(x)"));

    // Composite denominators
    assertEquals("4ln(x ^ 2 + 1)", integrate("(8x) / (1 + x ^ 2)"));
    assertEquals("-exp(-x ^ 2)", integrate("(2x) / exp(x ^ 2)"));
    assertEquals("3ln(sin(x))", integrate("(3cos(x)) / sin(x)"));
  }

  @Test
  void testUSubNestedComposites() {
    assertEquals("exp(sin(x))", integrate("exp(sin(x)) * cos(x)"));
    // assertEquals("-cos(x ^ 2)", integrate("sin(x ^ 2) * cos(x ^ 2)")); // requires trig substutition, ignore for now
  }

  @Test
  void testUSubK() {
    // A test in case of fractional k.
    assertEquals("-0.5cos(x ^ 2)", integrate("x * sin(x ^ 2)")); // k = 0.5
    assertEquals("0.5ln(x ^ 2 + 1)", integrate("x / (1 + x ^ 2)"));
  }

  // By parts
  @Test
  void testByParts() {
    assertThrows(UnsupportedOperationException.class, () -> TestingHelpers.toAst("x ^ x", ChoiceConstants.INTEGRATION));
    assertThrows(UnsupportedOperationException.class, () -> integrate("x * exp(x)"));
    assertThrows(UnsupportedOperationException.class, () -> integrate("x * sin(x)"));
    assertThrows(UnsupportedOperationException.class, () -> integrate("ln(x ^ 2 + 1))")); // = x * ln(x^2+1) - 2x + 2arctan(x)
    assertThrows(UnsupportedOperationException.class, () -> integrate("1 / (x ^ 2 + 1)")); // arctan(x)
  }
}
