# Grammar

```ebnf
expression  -> term (('+' | '-') term)*
term        -> implicit (('*' | '/') implicit)*
implicit    -> power (IDENTIFIER | '(' expression ')')*
power       -> unary ('^' power)?
unary       -> '-' unary | primary
primary     -> NUMBER
            | IDENTIFIER
            | IDENTIFIER '(' expression ')'
            | '(' expression ')'

IDENTIFIER  -> [a-zA-Z]+
NUMBER      -> [0-9]+ ('.' [0-9]+)?
```
# Precedence and Associativity
## Associativity
Strictly left-associative.
- $x + 2 + 4$ is evaluated $(x + 2) + 4$.
- $1/2x$ is evaluated $(1/2) * x$.
Similarly with all other cases where brackets are **not** specified.

## Precedence
**IMPORTANT**: I made the design decision for implicit operations to be *higher* than explicit operations. $\text{Implicit} > \text{Explicit}$.
Ie. The expression $x * y(z + 1)$ would evaluate as $x * (y * (z + 1))$ in the internal AST representation.
This is mostly to avoid ambiguous expressions such as that viral $8\div 2(2+2)$ problem or whatever that nonsense was. If you need to divide by something, please just use brackets or fractions. It is just completely unambiguous and the reason $\div$ pretty much is nonexistent in and past high school. 
Not jabbing anyone, but just something I had a mini-crisis about as I pondered my entire mathematical foundation lmao.

All other precedence remains relatively standard;
- Brackets
- Orders (exponents)
- Division
- Multiplication
- Addition
- Subtraction

Note, Division/Multiplication and Addition/Subtraction have the same precedence and are evaluated **left to right**. It is ordered this way, for those who don't know, is because BODMAS is easier to remember ^_^.
