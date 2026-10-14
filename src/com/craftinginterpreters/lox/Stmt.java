package com.craftinginterpreters.lox;

// Base class for statements in the language.
//
// Assignment 1 only needed expressions. Week 9 introduces
// statements so that programs can contain variable declarations
// and explicit print commands.
abstract class Stmt {

    // Visitor interface used later by the interpreter
    // to execute each type of statement.
    interface Visitor<R> {
        R visitExpressionStmt(Expression stmt);
        R visitPrintStmt(Print stmt);
        R visitVarStmt(Var stmt);
    }

    // A normal expression used as a statement.
    //
    // Example:
    // hydro{peak:5, day:1, decay:0.5};
    static class Expression extends Stmt {
        final Expr expression;

        Expression(Expr expression) {
            this.expression = expression;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitExpressionStmt(this);
        }
    }

    // Explicitly prints the value of an expression.
    //
    // Example:
    // print(outputRiver);
    static class Print extends Stmt {
        final Expr expression;

        Print(Expr expression) {
            this.expression = expression;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitPrintStmt(this);
        }
    }

    // Declares a named variable and stores its initial value.
    //
    // Example:
    // var creek = hydro{peak:5, day:1, decay:0.5};
    static class Var extends Stmt {
        final Token name;
        final Expr initializer;

        Var(Token name, Expr initializer) {
            this.name = name;
            this.initializer = initializer;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitVarStmt(this);
        }
    }

    abstract <R> R accept(Visitor<R> visitor);
}