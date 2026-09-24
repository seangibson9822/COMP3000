package com.craftinginterpreters.lox;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class Lox {

    static boolean hadError = false;

    public static void main(String[] args) throws IOException {

        if (args.length != 1) {
            System.out.println("Usage: jlox <file>");
            System.exit(64);
        }

        runFile(args[0]);
    }

    private static void runFile(String path) throws IOException {

        byte[] bytes = Files.readAllBytes(Paths.get(path));

        run(new String(bytes, StandardCharsets.UTF_8));

        if (hadError) {
            System.exit(65);
        }
    }

   private static void run(String source) {
    Scanner scanner = new Scanner(source);
    List<Token> tokens = scanner.scanTokens();

    Parser parser = new Parser(tokens);
    Expr expression = parser.parse();

    if (hadError) {
        return;
    }

    System.out.println(new AstPrinter().print(expression));
}

    static void error(int line, String message) {

        report(line, "", message);
    }

    private static void report(
        int line,
        String where,
        String message
    ) {

        System.err.println(
            "[line " + line + "] Error"
            + where + ": " + message
        );

        hadError = true;
    }
}