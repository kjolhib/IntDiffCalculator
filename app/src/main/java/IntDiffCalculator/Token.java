package IntDiffCalculator;

/**
 * Token class representing a token in the input expression. Each token has a type and an optional value (for numbers and identifiers).
 * @param type The type of the token (e.g., NUMBER, IDENTIFIER, OPERATOR, LPAREN, RPAREN, EOF).
 */
public record Token(TokenType type, String value) {

  @Override
  public String toString() {
    return type + "(" + value + ")";
  }
}
