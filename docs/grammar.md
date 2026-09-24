expression  → route ;

route       → confluence ( ">>" confluence )* ;

confluence  → primary ( "&" primary )* ;

primary     → hydro
            | "(" expression ")" ;

hydro       → "hydro" "{"
              "peak" ":" NUMBER ","
              "day" ":" NUMBER ","
              "decay" ":" NUMBER
              "}" ;


# River Language Grammar

## Week 5 Grammar

The language is currently an expression language. A river system is represented by combining *hydro* literals with confluence and downstream operators.


expression  → route ;

route       → confluence ( ">>" confluence )* ;

confluence  → primary ( "&" primary )* ;

primary     → hydro
            | "(" expression ")" ;

hydro       → "hydro" "{"
              "peak" ":" NUMBER ","
              "day" ":" NUMBER ","
              "decay" ":" NUMBER
              "}" ;

## Operator Meaning

*&* represents a confluence where two river flows meet.

A & B


*>>* represents one river flow moving downstream into another river section.

A >> B


*&* has higher precedence than *>>*, therefore:

A & B >> C

is parsed as:

(A & B) >> C


Parentheses can be used to explicitly control the structure of a river system.