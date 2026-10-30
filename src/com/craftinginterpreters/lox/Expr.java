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
    static class Dam extends Expr {

        // River expression supplying water to the dam.
        final Expr inflow;

        // Week 11 management rules.
        final List<DamRule> rules;

        // Week 10 configuration values are retained
        // temporarily while the language transitions
        // to the new rule-based design.
        final Object capacity;
        final Object initial;
        final Object low;
        final Object high;
        final Object rain;

        final Object lowRelease;
        final Object normalRelease;
        final Object highRelease;

        // Original Week 10 constructor.
        Dam(
            Expr inflow,
            Object capacity,
            Object initial,
            Object low,
            Object high,
            Object rain,
            Object lowRelease,
            Object normalRelease,
            Object highRelease
        ) {
            this.inflow = inflow;

            // Week 10 dams did not contain rule objects.
            this.rules = null;

            this.capacity = capacity;
            this.initial = initial;
            this.low = low;
            this.high = high;
            this.rain = rain;

            this.lowRelease = lowRelease;
            this.normalRelease = normalRelease;
            this.highRelease = highRelease;
        }

        // Week 11 rule-based constructor.
        //
        // Example:
        //
        // dam(creek) {
        //     release 1 when rain > 10;
        //     release 0.5 otherwise;
        // }
        Dam(
            Expr inflow,
            List<DamRule> rules
        ) {
            this.inflow = inflow;
            this.rules = rules;

            // The old Week 10 fields are not used
            // by the rule-based syntax.
            this.capacity = null;
            this.initial = null;
            this.low = null;
            this.high = null;
            this.rain = null;

            this.lowRelease = null;
            this.normalRelease = null;
            this.highRelease = null;
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