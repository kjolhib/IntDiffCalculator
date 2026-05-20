expression  -> term (('+' | '-') term)*
term        -> power (('*' | '/') power)*
power       -> unary ('^' power)? -- right associative of exponentiation. 2^3^4 is parsed as 2^(3^4)
unary       -> '-' unary | primary
primary     -> NUMBER
            | IDENTIFIER
            | IDENTIFIER '(' expression ')'
            | '(' expression ')'

IDENTIFIER  -> [a-zA-Z]+
NUMBER      -> [0-9]+ ('.' [0-9]+)?