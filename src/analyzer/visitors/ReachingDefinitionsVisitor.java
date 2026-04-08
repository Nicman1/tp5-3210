package analyzer.visitors;

import analyzer.ast.*;

import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This Java code defines a visitor class called ReachingDefinitionsVisitor for a custom parser.
 * The visitor class traverses an abstract syntax tree (AST) and generates optimised code (Single Assignment and
 * Dead-code elimination). The code includes implementations for various types of assignment statements,
 * such as direct assignment, unary assignment, and assignment with arithmetic operations.
 * */
public class ReachingDefinitionsVisitor implements ParserVisitor {
    private PrintWriter m_writer = null;
    private final ArrayList<String> RETURNS = new ArrayList<>();
    private ArrayList<CodeLine> CODE = new ArrayList<>();

    public ReachingDefinitionsVisitor(PrintWriter writer) {
        m_writer = writer;
    }

    @Override
    public Object visit(SimpleNode node, Object data) {
        return null;
    }

    @Override
    public Object visit(ASTProgram node, Object data) {
        node.childrenAccept(this, null);

        computeReachingDefinitions();
        printCode();

        computeSingleAssignment();
        eliminateDeadCode();
        printOptimisedCode();

        return null;
    }

    @Override
    public Object visit(ASTReturnStmt node, Object data) {
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            RETURNS.add(((ASTIdentifier) node.jjtGetChild(i)).getValue());
        }
        return null;
    }

    @Override
    public Object visit(ASTBlock node, Object data) {
        node.childrenAccept(this, null);
        return null;
    }

    @Override
    public Object visit(ASTStmt node, Object data) {
        node.childrenAccept(this, null);
        return null;
    }

    @Override
    public Object visit(ASTAssignStmt node, Object data) {
        String assign = (String) node.jjtGetChild(0).jjtAccept(this, null);
        String left = (String) node.jjtGetChild(1).jjtAccept(this, null);
        String right = (String) node.jjtGetChild(2).jjtAccept(this, null);
        String op = node.getOp();

        CODE.add(new CodeLine(op, assign, left, right));

        return null;
    }

    @Override
    public Object visit(ASTAssignUnaryStmt node, Object data) {
        String assign = (String) node.jjtGetChild(0).jjtAccept(this, null);
        String right = (String) node.jjtGetChild(1).jjtAccept(this, null);

        CODE.add(new CodeLine("-", assign,  right, ""));

        return null;
    }

    @Override
    public Object visit(ASTAssignDirectStmt node, Object data) {
        String assign = (String) node.jjtGetChild(0).jjtAccept(this, null);
        String right = (String) node.jjtGetChild(1).jjtAccept(this, null);

        CODE.add(new CodeLine("+", assign, right, ""));

        return null;
    }

    @Override
    public Object visit(ASTExpr node, Object data) {
        return node.jjtGetChild(0).jjtAccept(this, null);
    }

    @Override
    public Object visit(ASTIntValue node, Object data) {
        return "#" + node.getValue();
    }

    @Override
    public Object visit(ASTIdentifier node, Object data) {
        return node.getValue();
    }

    /**
     * Computes the GEN sets for each line of code.
     */
    private void computeGenSets() {
        // TODO exo 1
    }

    /**
     * Computes the KILL sets for each line of code.
     */
    private void computeKillSets() {
        // TODO exo 1
    }

    /**
     * Computes the Reaching Definitions Analysis for the code.
     */
    private void computeReachingDefinitions() {
        // TODO exo 2
    }

    /**
     * Optimizes the code by eliminating unnecessary assignments.
     */
    public void computeSingleAssignment() {
        // TODO exo 3
    }

    /**
     * Eliminates dead code from the code.
     */
    public void eliminateDeadCode() {
        // TODO exo 3
    }


    public void printCode() {
        int i = 0;
        for (CodeLine code : CODE) {
            String line = code.ASSIGN + " = " + code.left;
            if (!code.right.isEmpty() && !code.right.equals("#0")) {
                line += " " + code.op + " " + code.right;
            }
            m_writer.println("// Bloc " + i);
            m_writer.println(line);
            m_writer.println("// ValDef_IN  : " + sortedDefinitions(code.ValDef_IN));
            m_writer.println("// ValDef_OUT : " + sortedDefinitions(code.ValDef_OUT));
            m_writer.println();
            i++;
        }
    }

    public void printOptimisedCode() {
        m_writer.println("###############################################");
        m_writer.println("Optimised code:");
        for (CodeLine code : CODE) {
            String line =  code.GEN + ": " + code.ASSIGN + " = " + code.left;
            if (!code.right.isEmpty()) {
                line += " " + code.op + " " + code.right;
            }
            m_writer.println(line);
        }
        m_writer.println("###############################################");
    }


    // Helper function to convert a set of Definition objects to a sorted list of strings
    private List<String> sortedDefinitions(Set<Definition> definitions) {
        return definitions.stream()
                .map(Definition::toString)
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * A struct to store the data of a code line.
     */
    public class CodeLine {
        public String op;
        public String ASSIGN;
        public String left;
        public String right;
        public Definition GEN;
        public Set<Definition> KILL;
        public Set<Definition> ValDef_IN;
        public Set<Definition> ValDef_OUT;

        public CodeLine(String op, String ASSIGN, String left, String right) {
            this.op = op;
            this.ASSIGN = ASSIGN;
            this.left = left;
            this.right = right;
            this.KILL = new HashSet<>();
            this.ValDef_IN = new HashSet<>();
            this.ValDef_OUT = new HashSet<>();
        }
    }
    public int statementNumber = 0;

    /**
     * A struct to store the data of a definition.
     */
    public class Definition {
        public String identifier;
        public String op;
        public String ASSIGN;
        public String left;
        public String right;

        public Definition(String op, String ASSIGN, String left, String right) {
            this.identifier = "d_" + statementNumber++;
            this.op = op;
            this.ASSIGN = ASSIGN;
            this.left = left;
            this.right = right;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Definition) {
                Definition other = (Definition) obj;
                return this.identifier.equals(other.identifier);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return identifier.hashCode();
        }

        @Override
        public String toString() {
            return identifier;
        }
    }
}