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
            String fileName = "entrada.txt";
            String code = new String(Files.readAllBytes(Paths.get(fileName)));

            CharStream inputStream = CharStreams.fromString(code);

            miniCppLexer lexer = new miniCppLexer(inputStream);
            CommonTokenStream tokens = new CommonTokenStream(lexer);

            miniCppParser parser = new miniCppParser(tokens);

            ParseTree tree = parser.program();

            FunctionDirectory funcDir = new FunctionDirectory();
            SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer(funcDir);

            semanticAnalyzer.visit(tree);

            System.out.println("--- SymbolTable justo antes de imprimir cuádruplos ---");

            semanticAnalyzer.getQuadrupleGenerator().printQuadruples();
            semanticAnalyzer.getQuadrupleGenerator().printQuadruplesWithAddresses();

            System.out.println("Semantic analysis completed successfully.");

            List<Quadruple> quads = semanticAnalyzer.getQuadrupleGenerator().getQuadruplesWithVirtualAddresses();
            Map<Integer, Object> consts = semanticAnalyzer.getQuadrupleGenerator().getMemoryManager().getConstantValues();
            Map<String, Integer> funcDirectory = semanticAnalyzer.getQuadrupleGenerator().getFunctionDirectory();


            VirtualMachine vm = new VirtualMachine(quads, consts, funcDirectory);
            System.out.println("--- Ejecutando el programa en la máquina virtual ---");
            vm.execute();


        } catch (IOException e) {
            System.err.println("Error reading the input file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Semantic error: " + e.getMessage());
        }

    }
}
