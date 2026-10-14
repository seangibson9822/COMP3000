package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.List;

import static com.craftinginterpreters.lox.TokenType.*;

class Parser {

    private static class ParseError extends RuntimeException {}

    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    // Original single-expression parser.
    // This is kept temporarily so the existing Week 7 evaluator
    // can continue to work while statements are being added.
    Expr parse() {
        try {
            Expr expr = expression();

            if (!isAtEnd()) {
                throw error(peek(), "Expect end of expression.");
            }

            return expr;
        } catch (ParseError error) {
            return null;
        }
    }

    // Parses a complete program containing multiple statements.
    // This will become the main parser entry point once statement
    // evaluation is connected to the interpreter.
    List<Stmt> parseProgram() {
        List<Stmt> statements = new ArrayList<>();

        while (!isAtEnd()) {
            statements.add(declaration());
        }

        return statements;
    }

    // expression -> route ;
    private Expr expression() {
        return route();
    }

    // declaration -> "var" IDENTIFIER "=" expression ";"
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

    // Parses a variable declaration.
    //
    // Example:
    // var creek = hydro{peak:5, day:1, decay:0.5};
    private Stmt varDeclaration() {

        // Read the variable name.
        Token name = consume(
            IDENTIFIER,
            "Expect variable name."
        );

        // Every variable declaration currently requires
        // an initial river expression.
        consume(
            EQUAL,
            "Expect '=' after variable name."
        );

        Expr initializer = expression();

        consume(
            SEMICOLON,
            "Expect ';' after variable declaration."
        );

        return new Stmt.Var(name, initializer);
    }

    // statement -> "print" "(" expression ")" ";"
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

        Expr value = expression();

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

    // Allows a normal river expression to be used as a statement.
    //
    // Example:
    // hydro{peak:5, day:1, decay:0.5};
    private Stmt expressionStatement() {

        Expr expr = expression();

        consume(
            SEMICOLON,
            "Expect ';' after expression."
        );

        return new Stmt.Expression(expr);
    }

    // route -> confluence ( ">>" confluence )* ;
    private Expr route() {
        Expr expr = confluence();

        while (match(RIGHT_SHIFT)) {
            Token operator = previous();
            Expr right = confluence();

            expr = new Expr.Binary(
                expr,
                operator,
                right
            );
        }

        return expr;
    }

    // confluence -> primary ( "&" primary )* ;
    private Expr confluence() {
        Expr expr = primary();

        while (match(AMPERSAND)) {
            Token operator = previous();
            Expr right = primary();

            expr = new Expr.Binary(
                expr,
                operator,
                right
            );
        }

        return expr;
    }

    // primary -> hydro
    //          | IDENTIFIER
    //          | "(" expression ")" ;
    private Expr primary() {

        // Parse a hydro river-response literal.
        if (match(HYDRO)) {
            return hydro();
        }

        // An identifier refers to a previously declared variable.
        if (match(IDENTIFIER)) {
            return new Expr.Variable(previous());
        }

        // Parentheses group an expression.
        if (match(LEFT_PAREN)) {
            Expr expr = expression();

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

        // Read the peak flow response.
        consume(
            PEAK,
            "Expect 'peak'."
        );

        consume(
            COLON,
            "Expect ':' after 'peak'."
        );

        Object peak = consume(
            NUMBER,
            "Expect peak value."
        ).literal;

        consume(
            COMMA,
            "Expect ',' after peak value."
        );

        // Read the day on which the peak flow occurs.
        consume(
            DAY,
            "Expect 'day'."
        );

        consume(
            COLON,
            "Expect ':' after 'day'."
        );

        Object day = consume(
            NUMBER,
            "Expect day value."
        ).literal;

        consume(
            COMMA,
            "Expect ',' after day value."
        );

        // Read the proportion of flow retained
        // on each following day.
        consume(
            DECAY,
            "Expect 'decay'."
        );

        consume(
            COLON,
            "Expect ':' after 'decay'."
        );

        Object decay = consume(
            NUMBER,
            "Expect decay value."
        ).literal;

        // Area defaults to 1.0 so Assignment 1 programs
        // without an area parameter remain valid.
        Object area = 1.0;

        // If another comma follows decay, parse
        // the optional catchment area.
        if (match(COMMA)) {

            consume(
                AREA,
                "Expect 'area' after ','."
            );

            consume(
                COLON,
                "Expect ':' after 'area'."
            );

            area = consume(
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

    // Checks whether the current token matches any
    // of the supplied token types.
    private boolean match(TokenType... types) {

        for (TokenType type : types) {

            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    // Requires a particular token and consumes it.
    // If it is not present, a parse error is produced.
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

    // Looks at the current token without consuming it.
    private boolean check(TokenType type) {

        if (isAtEnd()) {
            return false;
        }

        return peek().type == type;
    }

    // Move to the next token.
    private Token advance() {

        if (!isAtEnd()) {
            current++;
        }

        return previous();
    }

    // Check whether the parser has reached EOF.
    private boolean isAtEnd() {
        return peek().type == EOF;
    }

    // Return the current token without consuming it.
    private Token peek() {
        return tokens.get(current);
    }

    // Return the most recently consumed token.
    private Token previous() {
        return tokens.get(current - 1);
    }

    // Report a parser error through the existing
    // Lox error-reporting system.
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