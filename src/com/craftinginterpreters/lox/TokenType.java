package com.craftinginterpreters.lox;

enum TokenType {

    // Single-character tokens.
    LEFT_PAREN, RIGHT_PAREN,
    LEFT_BRACE, RIGHT_BRACE,
    COMMA, DOT, MINUS, PLUS, SEMICOLON, SLASH, STAR,
    COLON, AMPERSAND,

    // One or two character tokens.
    BANG, BANG_EQUAL,
    EQUAL, EQUAL_EQUAL,
    GREATER, GREATER_EQUAL,
    LESS, LESS_EQUAL, RIGHT_SHIFT,

    // Literals.
    IDENTIFIER, STRING, NUMBER,

    // Standard Lox keywords.
    AND, CLASS, ELSE, FALSE, FUN, FOR, IF, NIL, OR,
    PRINT, RETURN, SUPER, THIS, TRUE, VAR, WHILE,

    // Water-flow language keywords.
    HYDRO, PEAK, DAY, DECAY, AREA,

    // Week 10 dam keywords.
    DAM, CAPACITY, INITIAL, LOW, HIGH, RAIN,
    LOW_RELEASE, NORMAL_RELEASE, HIGH_RELEASE,

    // Week 11 rule-based dam syntax.
    WHEN, OTHERWISE, RELEASE, INFLOW,

    EOF
}