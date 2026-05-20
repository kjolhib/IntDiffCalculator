package IntDiffCalculator;

import java.util.ArrayList;
import java.util.List;

public class Lexer {
  private final String source;
  private int pos = 0;

  public Lexer(String source) {
    this.source = source.trim();
  }

  /**
   * Transforms source string into a list of tokens.
   * @return List<Token>: a list of tokens that represents the source string. E.g. "3x + 1" tokenises to: NUMBER(3), IDENTIFIER(x), PLUS(+), NUMBER(1).
   */
  public List<Token> tokenise() {
    List<Token> tokens = new ArrayList<>();
    while (pos < source.length()) {
      skipWhitespace();
      
      if (pos >= source.length()) {
        // past the end of the string, break out of the loop
        break;
      };

      tokens.add(nextToken());
    }
    tokens.add(new Token(TokenType.EOF, ""));
    return tokens;
  }

  /**
   * Continually scans the source string while it is a digit or a dot.
   * @return Token: a NUMBER type token with the value being the substring containing the actual number. E.g. "2.194" would produce a token of type NUMBER with a value of "2.194".
   */
  private Token readNumber() {
    int start = pos;
    
    while (pos < source.length() && isDigitOrDot(source.charAt(pos))) {
      // keep scanning whilst characters match
      pos++;
    }

    // return the substring from start to end
    return new Token(TokenType.NUMBER, source.substring(start, pos));
  }

  /**
   * Continually scans the source string while it is a letter.
   * @return Token: an IDENTIFIER type token similarly to readNumber.
   */
  private Token readIdent() {
    int start = pos;

    while (pos < source.length() && Character.isLetter(source.charAt(pos))) {
      // keep scanning whilst characters match
      pos++;
    }

    // return the substring from start to end
    return new Token(TokenType.IDENTIFIER, source.substring(start, pos));
  }

  /**
   * Inspects the current character, and decides which type of token to produce.
   * @return Token: the next token in the source string
   */
  private Token nextToken() {
    char currentChar = source.charAt(pos);
    
    // Handle number/variable reading
    if (isDigitOrDot(currentChar)) {
      return readNumber();
    }

    if (Character.isLetter(currentChar)) {
      return readIdent();
    }

    return switch (currentChar) {
      case '+' -> {pos++; yield new Token(TokenType.PLUS, "+");}
      case '-' -> {pos++; yield new Token(TokenType.MINUS, "-");}
      case '*' -> {pos++; yield new Token(TokenType.STAR, "*");}
      case '/' -> {pos++; yield new Token(TokenType.SLASH, "/");}
      case '^' -> {pos++; yield new Token(TokenType.CARET, "^");}
      case '(' -> {pos++; yield new Token(TokenType.LPAREN, "(");}
      case ')' -> {pos++; yield new Token(TokenType.RPAREN, ")");}
      default -> throw new IllegalArgumentException("Unexpected character: " + currentChar);
    };
  }

  private void skipWhitespace() {
    while (pos < source.length() && Character.isWhitespace(source.charAt(pos))) {
      pos++;
    }
  }

  private boolean isDigitOrDot(char c) {
    return Character.isDigit(c) || c == '.';
  }
}
