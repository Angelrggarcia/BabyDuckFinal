import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        try {
            // 1. Read the input source code from the file
            String fileName = "entrada.txt";  // The file containing the source code
            String code = new String(Files.readAllBytes(Paths.get(fileName)));

            // 2. Create a CharStream from the input code
            CharStream inputStream = CharStreams.fromString(code);

            // 3. Create a lexer and tokenize the input code
            miniCppLexer lexer = new miniCppLexer(inputStream);
            CommonTokenStream tokens = new CommonTokenStream(lexer);

            // 4. Create a parser that will parse the tokens
            miniCppParser parser = new miniCppParser(tokens);

            // 5. Parse the input code to generate the AST (program)
            ParseTree tree = parser.program();

            // 6. Create the SemanticAnalyzer instance
            FunctionDirectory funcDir = new FunctionDirectory();
            SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer(funcDir);


            // 7. Perform semantic analysis by visiting the AST
            semanticAnalyzer.visit(tree);

            System.out.println("--- SymbolTable justo antes de imprimir cuádruplos ---");
            System.out.println(semanticAnalyzer.getQuadrupleGenerator().getMemoryManager().getSymbolTable());

            // 8. Print quadruples (if you added generation)
            semanticAnalyzer.getQuadrupleGenerator().printQuadruples();
            semanticAnalyzer.getQuadrupleGenerator().printQuadruplesWithAddresses();

            // 9. If no exception is thrown, the code is semantically correct
            System.out.println("Semantic analysis completed successfully.");

            // 10. Ejecuta los cuádruplos usando la máquina virtual
            List<Quadruple> quads = semanticAnalyzer.getQuadrupleGenerator().getQuadruplesWithVirtualAddresses();
            Map<Integer, Object> consts = semanticAnalyzer.getQuadrupleGenerator().getMemoryManager().getConstantValues();
            Map<String, Integer> funcDirectory = semanticAnalyzer.getQuadrupleGenerator().getFunctionDirectory();


            VirtualMachine vm = new VirtualMachine(quads, consts, funcDirectory);
            System.out.println("--- Ejecutando el programa en la máquina virtual ---");
            vm.execute();


        } catch (IOException e) {
            // Handle file read errors
            System.err.println("Error reading the input file: " + e.getMessage());
        } catch (Exception e) {
            // Handle other errors (e.g., semantic errors)
            System.err.println("Semantic error: " + e.getMessage());
        }

    }
}
