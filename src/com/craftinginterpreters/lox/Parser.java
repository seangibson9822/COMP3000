package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.List;

import static com.craftinginterpreters.lox.TokenType.*;

class Parser {

    private static class ParseError
        extends RuntimeException {}

    private final List<Token> tokens;

    private int current = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    // Original single-expression parser.
    // This is retained for compatibility with
    // the earlier Assignment 1 parser.
    Expr parse() {

        try {

            Expr expr = expression();

            if (!isAtEnd()) {
                throw error(
                    peek(),
                    "Expect end of expression."
                );
            }

            return expr;

        } catch (ParseError error) {
            return null;
        }
    }

    // Parses a complete program containing
    // multiple declarations and statements.
    List<Stmt> parseProgram() {

        List<Stmt> statements =
            new ArrayList<>();

        while (!isAtEnd()) {
            statements.add(
                declaration()
            );
        }

        return statements;
    }

    // expression -> route ;
    private Expr expression() {
        return route();
    }

    // declaration -> "var" IDENTIFIER
    //                "=" expression ";"
    //              | statement ;
    private Stmt declaration() {

        try {

            if (match(VAR)) {
                return varDeclaration();
            }

            return statement();

        } catch (ParseError error) {
            return null;
        }
    }

    // Parses:
    //
    // var creek = hydro{...};
    private Stmt varDeclaration() {

        Token name = consume(
            IDENTIFIER,
            "Expect variable name."
        );

        consume(
            EQUAL,
            "Expect '=' after variable name."
        );

        Expr initializer =
            expression();

        consume(
            SEMICOLON,
            "Expect ';' after variable declaration."
        );

        return new Stmt.Var(
            name,
            initializer
        );
    }

    // statement -> "print"
    //              "(" expression ")" ";"
    //            | expression ";" ;
    private Stmt statement() {

        if (match(PRINT)) {
            return printStatement();
        }

        return expressionStatement();
    }

    // Parses:
    //
    // print(expression);
    private Stmt printStatement() {

        consume(
            LEFT_PAREN,
            "Expect '(' after 'print'."
        );

        Expr value =
            expression();

        consume(
            RIGHT_PAREN,
            "Expect ')' after value."
        );

        consume(
            SEMICOLON,
            "Expect ';' after print statement."
        );

        return new Stmt.Print(value);
    }

    // Allows a river expression to be used
    // as a normal statement.
    //
    // Example:
    // hydro{peak:5, day:1, decay:0.5};
    private Stmt expressionStatement() {

        Expr expr =
            expression();

        // Require semicolons between statements,
        // but allow the final expression in an old
        // Assignment 1 file to omit one.
        if (!isAtEnd()) {

            consume(
                SEMICOLON,
                "Expect ';' after expression."
            );
        }

        return new Stmt.Expression(expr);
    }

    // route -> confluence
    //          ( ">>" confluence )* ;
    private Expr route() {

        Expr expr =
            confluence();

        while (match(RIGHT_SHIFT)) {

            Token operator =
                previous();

            Expr right =
                confluence();

            expr =
                new Expr.Binary(
                    expr,
                    operator,
                    right
                );
        }

        return expr;
    }

    // confluence -> primary
    //               ( "&" primary )* ;
    private Expr confluence() {

        Expr expr =
            primary();

        while (match(AMPERSAND)) {

            Token operator =
                previous();

            Expr right =
                primary();

            expr =
                new Expr.Binary(
                    expr,
                    operator,
                    right
                );
        }

        return expr;
    }

    // primary -> hydro
    //          | dam
    //          | IDENTIFIER
    //          | "(" expression ")" ;
    private Expr primary() {

        // Parse a normal river response.
        if (match(HYDRO)) {
            return hydro();
        }

        // Parse a human-controlled dam.
        if (match(DAM)) {
            return dam();
        }

        // An identifier refers to a
        // previously declared variable.
        if (match(IDENTIFIER)) {

            return new Expr.Variable(
                previous()
            );
        }

        // Parentheses group an expression.
        if (match(LEFT_PAREN)) {

            Expr expr =
                expression();

            consume(
                RIGHT_PAREN,
                "Expect ')' after expression."
            );

            return new Expr.Grouping(expr);
        }

        throw error(
            peek(),
            "Expect river expression."
        );
    }

    // hydro -> "hydro" "{"
    //          "peak" ":" NUMBER ","
    //          "day" ":" NUMBER ","
    //          "decay" ":" NUMBER
    //          ( "," "area" ":" NUMBER )?
    //          "}" ;
    private Expr hydro() {

        consume(
            LEFT_BRACE,
            "Expect '{' after 'hydro'."
        );

        // Peak flow response.
        consume(
            PEAK,
            "Expect 'peak'."
        );

        consume(
            COLON,
            "Expect ':' after 'peak'."
        );

        Object peak =
            consume(
                NUMBER,
                "Expect peak value."
            ).literal;

        consume(
            COMMA,
            "Expect ',' after peak value."
        );

        // Day on which peak flow occurs.
        consume(
            DAY,
            "Expect 'day'."
        );

        consume(
            COLON,
            "Expect ':' after 'day'."
        );

        Object day =
            consume(
                NUMBER,
                "Expect day value."
            ).literal;

        consume(
            COMMA,
            "Expect ',' after day value."
        );

        // Proportion retained on each
        // following day.
        consume(
            DECAY,
            "Expect 'decay'."
        );

        consume(
            COLON,
            "Expect ':' after 'decay'."
        );

        Object decay =
            consume(
                NUMBER,
                "Expect decay value."
            ).literal;

        // Existing Assignment 1 programs
        // default to an area of 1.
        Object area = 1.0;

        // Optional catchment area.
        if (match(COMMA)) {

            consume(
                AREA,
                "Expect 'area' after ','."
            );

            consume(
                COLON,
                "Expect ':' after 'area'."
            );

            area =
                consume(
                    NUMBER,
                    "Expect area value."
                ).literal;
        }

        consume(
            RIGHT_BRACE,
            "Expect '}' after hydro literal."
        );

        return new Expr.Hydro(
            peak,
            day,
            decay,
            area
        );
    }

    // dam -> "dam" "(" expression ")" "{"
    //        damRule+
    //        "}" ;
    //
    // damRule -> "release" NUMBER "when"
    //            damSource comparison NUMBER ";"
    //          | "release" NUMBER
    //            "otherwise" ";" ;
    //
    // damSource -> "rain"
    //            | "inflow" ;
    //
    // comparison -> ">"
    //             | ">="
    //             | "<"
    //             | "<=" ;
    private Expr dam() {

        // dam(...) receives a river
        // expression as its inflow.
        consume(
            LEFT_PAREN,
            "Expect '(' after 'dam'."
        );

        Expr inflow =
            expression();

        consume(
            RIGHT_PAREN,
            "Expect ')' after dam inflow."
        );

        consume(
            LEFT_BRACE,
            "Expect '{' after dam inflow."
        );

        // Rules are retained in source order
        // because the first matching rule can
        // later determine the dam output.
        List<Expr.DamRule> rules =
            new ArrayList<>();

        boolean foundOtherwise =
            false;

        while (
            !check(RIGHT_BRACE)
            && !isAtEnd()
        ) {

            // Every management rule starts
            // with its release fraction.
            consume(
                RELEASE,
                "Expect 'release' at start of dam rule."
            );

            Object release =
                consume(
                    NUMBER,
                    "Expect release fraction."
                ).literal;

            // Conditional rule:
            //
            // release 1 when rain > 10;
            if (match(WHEN)) {

                // Otherwise must remain the
                // final rule in the block.
                if (foundOtherwise) {

                    throw error(
                        peek(),
                        "No dam rules allowed after 'otherwise'."
                    );
                }

                Token source;

                // Week 11 rules can inspect
                // rainfall or incoming flow.
                if (match(RAIN, INFLOW)) {

                    source =
                        previous();

                } else {

                    throw error(
                        peek(),
                        "Expect 'rain' or 'inflow' after 'when'."
                    );
                }

                Token operator;

                if (
                    match(
                        GREATER,
                        GREATER_EQUAL,
                        LESS,
                        LESS_EQUAL
                    )
                ) {

                    operator =
                        previous();

                } else {

                    throw error(
                        peek(),
                        "Expect comparison operator in dam rule."
                    );
                }

                Object threshold =
                    consume(
                        NUMBER,
                        "Expect comparison value."
                    ).literal;

                consume(
                    SEMICOLON,
                    "Expect ';' after dam rule."
                );

                rules.add(
                    new Expr.DamRule(
                        source,
                        operator,
                        threshold,
                        release,
                        false
                    )
                );
            }

            // Fallback rule:
            //
            // release 0.5 otherwise;
            else if (match(OTHERWISE)) {

                foundOtherwise =
                    true;

                consume(
                    SEMICOLON,
                    "Expect ';' after otherwise rule."
                );

                rules.add(
                    new Expr.DamRule(
                        null,
                        null,
                        null,
                        release,
                        true
                    )
                );

                // Otherwise is always the
                // final management rule.
                if (!check(RIGHT_BRACE)) {

                    throw error(
                        peek(),
                        "'otherwise' must be the final dam rule."
                    );
                }
            }

            else {

                throw error(
                    peek(),
                    "Expect 'when' or 'otherwise' after release fraction."
                );
            }
        }

        // A dam cannot contain an
        // empty management block.
        if (rules.isEmpty()) {

            throw error(
                peek(),
                "Dam must contain at least one release rule."
            );
        }

        // Every dam requires a fallback
        // so its behaviour is always defined.
        if (!foundOtherwise) {

            throw error(
                peek(),
                "Dam requires an 'otherwise' release rule."
            );
        }

        consume(
            RIGHT_BRACE,
            "Expect '}' after dam rules."
        );

        return new Expr.Dam(
            inflow,
            rules
        );
    }

    // Checks whether the current token
    // matches any supplied type.
    private boolean match(
        TokenType... types
    ) {

        for (TokenType type : types) {

            if (check(type)) {

                advance();

                return true;
            }
        }

        return false;
    }

    // Requires a particular token.
    private Token consume(
        TokenType type,
        String message
    ) {

        if (check(type)) {
            return advance();
        }

        throw error(
            peek(),
            message
        );
    }

    // Look at the current token
    // without consuming it.
    private boolean check(
        TokenType type
    ) {

        if (isAtEnd()) {
            return false;
        }

        return peek().type
            == type;
    }

    // Move to the next token.
    private Token advance() {

        if (!isAtEnd()) {
            current++;
        }

        return previous();
    }

    // Check for EOF.
    private boolean isAtEnd() {

        return peek().type
            == EOF;
    }

    // Current token.
    private Token peek() {

        return tokens.get(
            current
        );
    }

    // Most recently consumed token.
    private Token previous() {

        return tokens.get(
            current - 1
        );
    }

    // Report a parser error using
    // the existing Lox error system.
    private ParseError error(
        Token token,
        String message
    ) {

        Lox.error(
            token.line,
            message
        );

        return new ParseError();
    }
}