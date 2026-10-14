package com.craftinginterpreters.lox;

import java.util.HashMap;
import java.util.Map;

// Stores the values of variables while a river program is running.
//
// Example:
// var creek = hydro{...};
//
// The name "creek" is stored here together with the evaluated
// flow value produced by the hydro expression.
class Environment {

    // Maps each variable name to its evaluated flow value.
    private final Map<String, Double> values = new HashMap<>();

    // Store a newly declared variable.
    void define(String name, double value) {
        values.put(name, value);
    }

    // Retrieve the value of a previously declared variable.
    double get(Token name) {

        if (values.containsKey(name.lexeme)) {
            return values.get(name.lexeme);
        }

        // A variable should never silently become zero if it
        // has not actually been declared.
        throw new RuntimeException(
            "Undefined variable '" + name.lexeme + "'."
        );
    }
}