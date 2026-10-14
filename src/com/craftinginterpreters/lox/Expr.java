package com.craftinginterpreters.lox;

abstract class Expr {

    interface Visitor<R> {
        R visitBinaryExpr(Binary expr);
        R visitGroupingExpr(Grouping expr);
        R visitLiteralExpr(Literal expr);
        R visitUnaryExpr(Unary expr);

        // Custom river-language expression.
        R visitHydroExpr(Hydro expr);

        // Refers to a previously declared variable.
        R visitVariableExpr(Variable expr);

        R visitDamExpr(Dam expr);
    }

    static class Binary extends Expr {
        final Expr left;
        final Token operator;
        final Expr right;

        Binary(Expr left, Token operator, Expr right) {
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

        Unary(Token operator, Expr right) {
            this.operator = operator;
            this.right = right;
        }

        @Override
        <R> R accept(Visitor<R> visitor) {
            return visitor.visitUnaryExpr(this);
        }
    }

    static class Hydro extends Expr {
        final Object peak;
        final Object day;
        final Object decay;
        final Object area;

        Hydro(Object peak, Object day, Object decay, Object area) {
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
    //
    // The identifier "creek" becomes an Expr.Variable node.
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

    // Represents a human-controlled dam.
//
// Example:
// dam(creek) {
//     capacity: 100,
//     initial: 40,
//     low: 30,
//     high: 80,
//     rain: 20,
//     lowRelease: 0,
//     normalRelease: 0.5,
//     highRelease: 1
// }
static class Dam extends Expr {

    // River expression supplying water to the dam.
    final Expr inflow;

    // Dam configuration values.
    final Object capacity;
    final Object initial;
    final Object low;
    final Object high;
    final Object rain;

    // Fractions of inflow released under
    // different operating conditions.
    final Object lowRelease;
    final Object normalRelease;
    final Object highRelease;

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
        this.capacity = capacity;
        this.initial = initial;
        this.low = low;
        this.high = high;
        this.rain = rain;
        this.lowRelease = lowRelease;
        this.normalRelease = normalRelease;
        this.highRelease = highRelease;
    }

    @Override
    <R> R accept(Visitor<R> visitor) {
        return visitor.visitDamExpr(this);
    }
}

    abstract <R> R accept(Visitor<R> visitor);
}