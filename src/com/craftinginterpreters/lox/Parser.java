package com.craftinginterpreters.lox;

import java.util.List;

import static com.craftinginterpreters.lox.TokenType.*;

class Parser {

    private static class ParseError extends RuntimeException {}

    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

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

    // expression -> route ;
    private Expr expression() {
        return route();
    }

    // route -> confluence ( ">>" confluence )* ;
    private Expr route() {
        Expr expr = confluence();

        while (match(RIGHT_SHIFT)) {
            Token operator = previous();
            Expr right = confluence();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    // confluence -> primary ( "&" primary )* ;
    private Expr confluence() {
        Expr expr = primary();

        while (match(AMPERSAND)) {
            Token operator = previous();
            Expr right = primary();
            expr = new Expr.Binary(expr, operator, right);
        }

        return expr;
    }

    // primary -> hydro | "(" expression ")" ;
    private Expr primary() {
        if (match(HYDRO)) {
            return hydro();
        }

        if (match(LEFT_PAREN)) {
            Expr expr = expression();
            consume(RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }

        throw error(peek(), "Expect river expression.");
    }

    // hydro -> "hydro" "{"
    //          "peak" ":" NUMBER ","
    //          "day" ":" NUMBER ","
    //          "decay" ":" NUMBER
    //          "}" ;
    private Expr hydro() {
        consume(LEFT_BRACE, "Expect '{' after 'hydro'.");

        consume(PEAK, "Expect 'peak'.");
        consume(COLON, "Expect ':' after 'peak'.");
        Object peak = consume(NUMBER, "Expect number after 'peak:'.").literal;

        consume(COMMA, "Expect ',' after peak value.");

        consume(DAY, "Expect 'day'.");
        consume(COLON, "Expect ':' after 'day'.");
        Object day = consume(NUMBER, "Expect number after 'day:'.").literal;

        consume(COMMA, "Expect ',' after day value.");

        consume(DECAY, "Expect 'decay'.");
        consume(COLON, "Expect ':' after 'decay'.");
        Object decay = consume(NUMBER, "Expect number after 'decay:'.").literal;

        consume(RIGHT_BRACE, "Expect '}' after hydro literal.");

        return new Expr.Hydro(peak, day, decay);
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }

        return false;
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) {
            return advance();
        }

        throw error(peek(), message);
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) {
            return false;
        }

        return peek().type == type;
    }

    private Token advance() {
        if (!isAtEnd()) {
            current++;
        }

        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private ParseError error(Token token, String message) {
        Lox.error(token.line, message);
        return new ParseError();
    }
}