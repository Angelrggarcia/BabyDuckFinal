import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.nio.file.*;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("=== INICIO DE EJECUCIÓN ===");

            // Leer archivo de entrada
            String filePath = "entrada.txt";
            String programa = Files.readString(Paths.get(filePath));

            System.out.println("=== CÓDIGO FUENTE ===");
            System.out.println(programa);
            System.out.println("=====================");

            CharStream input = CharStreams.fromString(programa);
            miniCppLexer lexer = new miniCppLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            miniCppParser parser = new miniCppParser(tokens);

            ParseTree tree = parser.program();
            System.out.println("Árbol de parsing:\n" + tree.toStringTree(parser));
            System.out.println("=====================");
            System.out.println("Resultado:\n");

            MyVisitor visitor = new MyVisitor();
            visitor.visit(tree);

        } catch (Exception e) {
            System.err.println("Error durante la ejecución:");
            e.printStackTrace();
        }
    }
}