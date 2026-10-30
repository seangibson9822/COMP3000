package com.craftinginterpreters.lox;

import java.util.List;

abstract class Expr {

    interface Visitor<R> {
        R visitBinaryExpr(Binary expr);
        R visitGroupingExpr(Grouping expr);
        R visitLiteralExpr(Literal expr);
        R visitUnaryExpr(Unary expr);

        // Custom river-language expressions.
        R visitHydroExpr(Hydro expr);
        R visitVariableExpr(Variable expr);
        R visitDamExpr(Dam expr);
    }

    static class Binary extends Expr {

        final Expr left;
        final Token operator;
        final Expr right;

        Binary(
            Expr left,
            Token operator,
            Expr right
        ) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitBinaryExpr(this);
        }
    }

    static class Grouping extends Expr {

        final Expr expression;

        Grouping(Expr expression) {
            this.expression = expression;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitGroupingExpr(this);
        }
    }

    static class Literal extends Expr {

        final Object value;

        Literal(Object value) {
            this.value = value;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitLiteralExpr(this);
        }
    }

    static class Unary extends Expr {

        final Token operator;
        final Expr right;

        Unary(
            Token operator,
            Expr right
        ) {
            this.operator = operator;
            this.right = right;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitUnaryExpr(this);
        }
    }

    // Represents a hydro river-response expression.
    static class Hydro extends Expr {

        final Object peak;
        final Object day;
        final Object decay;
        final Object area;

        Hydro(
            Object peak,
            Object day,
            Object decay,
            Object area
        ) {
            this.peak = peak;
            this.day = day;
            this.decay = decay;
            this.area = area;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitHydroExpr(this);
        }
    }

    // Represents the use of a previously declared variable.
    //
    // Example:
    // var creek = hydro{...};
    // print(creek);
    static class Variable extends Expr {

        final Token name;

        Variable(Token name) {
            this.name = name;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitVariableExpr(this);
        }
    }

    // Represents one management rule inside a dam.
    //
    // Example:
    // release 1 when rain > 10;
    //
    // source:
    // rain or inflow
    //
    // operator:
    // >, >=, < or <=
    //
    // threshold:
    // the number being compared against
    //
    // release:
    // proportion of water released
    static class DamRule {

        final Token source;
        final Token operator;
        final Object threshold;
        final Object release;

        // True for the final:
        // release ... otherwise;
        final boolean otherwise;

        DamRule(
            Token source,
            Token operator,
            Object threshold,
            Object release,
            boolean otherwise
        ) {
            this.source = source;
            this.operator = operator;
            this.threshold = threshold;
            this.release = release;
            this.otherwise = otherwise;
        }
    }

    // Represents a human-controlled dam.
    //
    // A dam receives an incoming river expression and
    // applies its management rules in source order.
    //
    // Example:
    //
    // dam(creek) {
    //     release 1 when rain > 10;
    //     release 0.5 otherwise;
    // }
    static class Dam extends Expr {

        // River expression supplying water to the dam.
        final Expr inflow;

        // Ordered management rules controlling
        // how much of the inflow is released.
        final List<DamRule> rules;

        Dam(
            Expr inflow,
            List<DamRule> rules
        ) {
            this.inflow = inflow;
            this.rules = rules;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitDamExpr(this);
        }
    }

    abstract <R> R accept(
        Visitor<R> visitor
    );
}