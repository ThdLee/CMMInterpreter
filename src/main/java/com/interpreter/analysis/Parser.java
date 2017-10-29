package com.interpreter.analysis;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;

public class Parser {
    private static final HashSet<String> AssignSignSet = new HashSet<String>() {{
        add("+="); add("-="); add("*="); add("/="); add("%=");
        add("&&="); add("||=");
    }};

    private Stack<Token> stack;
    private Lexer lexer;

    private Token cur = null;
    public Parser(Lexer lexer) {
        this.stack = new Stack<>();
        this.lexer = lexer;
    }

    private Token nextToken() throws IOException, LexerException {
        if (cur == null) return lexer.read();
        stack.push(cur);
        return lexer.read();
    }

    public void prog() throws IOException, LexerException {
        ArrayList<AST> list = new ArrayList<>();
        cur = nextToken();
        while (cur.type != Token.Type.EndSymbol) {
            if (cur.type == Token.Type.Annotation) continue;
            AST tree = new AST();
            handleStmt(tree);
            list.add(tree);
            if (cur.type != Token.Type.Sign || !cur.value.equals(";")) {
                
            }
        }

    }

    private void handleStmt(AST tree) throws IOException, LexerException {
        Node node = new Node(NonTerminalSymbol.Stmt);
        tree.root = node;
        if (cur.type == Token.Type.Keyword) {
            if (cur.value.equals("int") ||
                    cur.value.equals("double")) {
                node.setNSymbol(NonTerminalSymbol.VarDecl);
                handleVarDecl(node);
            } else if (cur.value.equals("if")) {
                node.setNSymbol(NonTerminalSymbol.IFStmt);
                cur = nextToken();
                handleIfStmt(node);
            } else if (cur.value.equals("while")) {
                node.setNSymbol(NonTerminalSymbol.WhileStmt);
                cur = nextToken();
                handleWhileStmt(node);
            } else if (cur.value.equals("read")) {

            } else if (cur.value.equals("write")) {

            } else {

            }
        } else if (cur.type == Token.Type.Identifier) {
            node.setNSymbol(NonTerminalSymbol.AssignStmt);
            handleAssignStmt(node);

        }
    }

    private void handleStmtBlock(Node root) throws IOException, LexerException {

    }

    private void handleVarDecl(Node root) throws IOException, LexerException {
        Node type = new Node(NonTerminalSymbol.Type);
        handleType(type);

        Node varList = new Node(NonTerminalSymbol.VarList);
        handleVarList(varList);
        cur = nextToken();
    }

    private void handleType(Node root) throws IOException, LexerException {
        Node node = new Node();
        if (cur.value.equals("int")) {
            node.setTSymbol(TerminalSymbol.Int);
        } else if (cur.value.equals("double")) {
            node.setTSymbol(TerminalSymbol.Double);
        }
        root.addNode(node);
        cur = nextToken();
        if (cur.type == Token.Type.Sign && cur.value.equals("[")) {
            cur = nextToken();
            Node index = new Node(TerminalSymbol.Index, cur.getValue());

            cur = nextToken();
            if (cur.type != Token.Type.Sign || !cur.value.equals("]")) {

            }
            root.addNode(node);
            cur = nextToken();
        }
    }

    private void handleVarList(Node root) throws IOException, LexerException {
        if (cur.type != Token.Type.Identifier) {

        }
        while (cur.type != Token.Type.Identifier) {
            Node id = new Node(TerminalSymbol.Identifier);
            root.addNode(id);

            cur = nextToken();
            if (cur.type == Token.Type.Sign && cur.value.equals("=")) {
                Node node = new Node(NonTerminalSymbol.VarDeclAssign);
                root.addNode(node);
                cur = nextToken();
                handleVarList(node);
            } else if (cur.type == Token.Type.Sign && cur.value.equals(",")) {
                cur = nextToken();
            }
        }

    }

    private void handleVarDeclAssign(Node root) throws IOException, LexerException {
        Node sign = new Node(TerminalSymbol.Sign, "=");
        root.addNode(sign);
        cur = nextToken();

        Node value = new Node(NonTerminalSymbol.Expr2);
        root.addNode(value);
        handleExpr2(value);

    }

    private void handleIfStmt(Node root) {

    }

    private void handleWhileStmt(Node root) {

    }

    private void handleAssignStmt(Node root) throws IOException, LexerException {
        Node value = new Node(TerminalSymbol.Identifier, cur.value);
        root.addNode(value);

        cur = nextToken();
        if (cur.type == Token.Type.Sign &&
                cur.value.equals("[")) {
            cur = nextToken();
            if (cur.type == Token.Type.Integer) {
                Node index = new Node(TerminalSymbol.Index, cur.value);
                root.addNode(index);
            } else {

            }
            cur = nextToken();
            if (cur.type != Token.Type.Sign ||
                    !cur.value.equals("[")) {

            }
            cur = nextToken();
        }

        if (cur.type == Token.Type.Sign &&
                AssignSignSet.contains(cur.value)) {
            Node sign = new Node(TerminalSymbol.Sign, cur.value);
            root.addNode(sign);

            cur = nextToken();
            Node expr1 = new Node(NonTerminalSymbol.Expr1);
            root.addNode(expr1);
            handleExpr1(expr1);
        } else {

        }

    }

    private void handleExpr1(Node root) throws IOException, LexerException {
        Node node1 = new Node(NonTerminalSymbol.Expr2);
        root.addNode(node1);
        handleExpr2(node1);

        if (cur.type == Token.Type.Sign && cur.value.equals("||")) {
            Node sign = new Node(TerminalSymbol.Sign, "||");
            root.addNode(sign);

            cur = nextToken();
            Node node2 = new Node(NonTerminalSymbol.Expr2);
            root.addNode(node2);
            handleExpr2(node2);
        }
    }
    private void handleExpr2(Node root) throws IOException, LexerException {
        Node node1 = new Node(NonTerminalSymbol.Expr3);
        root.addNode(node1);
        handleExpr3(node1);

        if (cur.type == Token.Type.Sign && cur.value.equals("&&")) {
            Node sign = new Node(TerminalSymbol.Sign, "&&");
            root.addNode(sign);

            cur = nextToken();
            Node node2 = new Node(NonTerminalSymbol.Expr3);
            root.addNode(node2);
            handleExpr3(node2);
        }
    }
    private void handleExpr3(Node root) throws IOException, LexerException {
        Node node1 = new Node(NonTerminalSymbol.Expr4);
        root.addNode(node1);
        handleExpr4(node1);

        if (cur.type == Token.Type.Sign && (
                cur.value.equals("==") ||
                cur.value.equals("!="))) {
            Node sign = new Node(TerminalSymbol.Sign, cur.value);
            root.addNode(sign);

            cur = nextToken();
            Node node2 = new Node(NonTerminalSymbol.Expr4);
            root.addNode(node2);
            handleExpr4(node2);
        }
    }
    private void handleExpr4(Node root) throws IOException, LexerException {
        Node node1 = new Node(NonTerminalSymbol.Expr5);
        root.addNode(node1);
        handleExpr5(node1);

        if (cur.type == Token.Type.Sign && (
                cur.value.equals(">") ||
                        cur.value.equals(">=") ||
                        cur.value.equals("<") ||
                        cur.value.equals("<="))) {
            Node sign = new Node(TerminalSymbol.Sign, cur.value);
            root.addNode(sign);

            cur = nextToken();
            Node node2 = new Node(NonTerminalSymbol.Expr5);
            root.addNode(node2);
            handleExpr5(node2);
        }
    }

    private void handleExpr5(Node root) throws IOException, LexerException {
        Node node1 = new Node(NonTerminalSymbol.Expr6);
        root.addNode(node1);
        handleExpr6(node1);

        if (cur.type == Token.Type.Sign && (
                cur.value.equals("+") || cur.value.equals("-"))) {
            Node sign = new Node(TerminalSymbol.Sign, cur.value);
            root.addNode(sign);

            cur = nextToken();
            Node node2 = new Node(NonTerminalSymbol.Expr6);
            root.addNode(node2);
            handleExpr6(node2);
        }
    }

    private void handleExpr6(Node root) throws IOException, LexerException {
        Node node1 = new Node(NonTerminalSymbol.Expr7);
        root.addNode(node1);
        handleExpr7(node1);

        if (cur.type == Token.Type.Sign && (
                cur.value.equals("*") ||
                        cur.value.equals("/") ||
                        cur.value.equals("%"))) {
            Node sign = new Node(TerminalSymbol.Sign, cur.value);
            root.addNode(sign);

            cur = nextToken();
            Node node2 = new Node(NonTerminalSymbol.Expr7);
            root.addNode(node2);
            handleExpr7(node2);
        }
    }

    private void handleExpr7(Node root) throws IOException, LexerException {
        if (cur.type == Token.Type.Sign && (
                cur.value.equals("+") ||
                        cur.value.equals("-") ||
                        cur.value.equals("!"))) {
            Node sign = new Node(TerminalSymbol.Sign, cur.value);
            root.addNode(sign);

            cur = nextToken();
        }

        Node node = new Node(NonTerminalSymbol.Expr8);
        root.addNode(node);
        handleExpr8(node);
    }

    private void handleExpr8(Node root) throws IOException, LexerException {
        if (cur.type == Token.Type.Sign && cur.value.equals("(")) {
            cur = nextToken();
            Node expr = new Node(NonTerminalSymbol.Expr1);
            root.addNode(expr);

            cur = nextToken();
            if (cur.type != Token.Type.Sign || cur.value.equals(")")) {

            }
        } else {
            Node node = new Node(NonTerminalSymbol.Value);
            root.addNode(node);
            handleValue(node);
        }
    }

    private void handleValue(Node root) throws IOException, LexerException {
        Node value = new Node();
        if (cur.type == Token.Type.Identifier) {
            value.setTSymbol(TerminalSymbol.Identifier);
        } else if (cur.type == Token.Type.Keyword &&
                (cur.value.equals("true") || cur.value.equals("false"))) {
            value.setTSymbol(TerminalSymbol.Bool);
        } else if (cur.type == Token.Type.Integer) {
            value.setTSymbol(TerminalSymbol.Int);
        } else if (cur.type == Token.Type.Decimal) {
            value.setTSymbol(TerminalSymbol.Double);
        } else {

        }
        value.setValue(cur.value);
        cur = nextToken();
    }
}
