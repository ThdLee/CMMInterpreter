package com.interpreter.analysis;

import com.interpreter.analysis.node.NNode;
import com.interpreter.analysis.node.TNode;

import java.io.IOException;
import java.util.HashSet;
import java.util.Stack;

public class Parser {
    private static final HashSet<String> AssignSignSet = new HashSet<String>() {{
        add("=");
        add("+="); add("-="); add("*="); add("/="); add("%=");
        add("&&="); add("||=");
    }};

    private static final HashSet<String> Expr1SignSet = new HashSet<String>() {{
        add("||");
    }};
    private static final HashSet<String> Expr2SignSet = new HashSet<String>() {{
        add("&&");
    }};
    private static final HashSet<String> Expr3SignSet = new HashSet<String>() {{
        add("=="); add("!=");
    }};
    private static final HashSet<String> Expr4SignSet = new HashSet<String>() {{
        add(">"); add(">="); add("<"); add("<=");
    }};
    private static final HashSet<String> Expr5SignSet = new HashSet<String>() {{
        add("+"); add("-");
    }};
    private static final HashSet<String> Expr6SignSet = new HashSet<String>() {{
        add("*"); add("/"); add("%");
    }};
    private static final HashSet<String> Expr7SignSet = new HashSet<String>() {{
        add("+"); add("-"); add("!");
    }};

    private Lexer lexer;

    private Token cur = null;
    public Parser(Lexer lexer) {
        this.lexer = lexer;
    }

    private Token nextToken() throws IOException {
        Token token = lexer.read();
        while (token.type == Token.Type.Space || token.type == Token.Type.Annotation) {
            token = lexer.read();
        }
        return token;
    }

    private boolean needSemicolon;
    public AST prog() throws IOException {
        needSemicolon = true;

        AST tree = new AST();
        NNode root = new NNode(NonTerminalSymbol.Root);
        tree.root = root;
        cur = nextToken();
        while (cur.type != Token.Type.EndSymbol) {
            NNode stmt = new NNode(NonTerminalSymbol.Stmt);
            root.addNode(stmt);
            handleStmt(stmt);
        }
        return tree;
    }


    private void handleStmt(NNode root) throws IOException {
        NNode node = new NNode();
        needSemicolon = true;
        if (cur.type == Token.Type.Keyword) {
            switch (cur.value) {
                case "int":
                case "double":
                case "string":
                    node.setSymbol(NonTerminalSymbol.VarDecl);
                    handleVarDecl(node);
                    break;
                case "if":
                    node.setSymbol(NonTerminalSymbol.IFStmt);
                    handleIfStmt(node);
                    break;
                case "while":
                    node.setSymbol(NonTerminalSymbol.WhileStmt);
                    handleWhileStmt(node);
                    break;
                case "read":
                    node.setSymbol(NonTerminalSymbol.ReadStmt);
                    handleReadStmt(node);
                    break;
                case "write":
                    node.setSymbol(NonTerminalSymbol.WriteStmt);
                    handleWriteStmt(node);
                    break;
                case "break":
                    node.setSymbol(NonTerminalSymbol.BreakStmt);
                    handleBreakStmt(node);
                    break;
                case "continue":
                    node.setSymbol(NonTerminalSymbol.ContinueStmt);
                    handleContinueStmt(node);
                    break;
                default:
                    throw new ParserException(cur);
            }
        } else if (cur.type == Token.Type.Identifier) {
            node.setSymbol(NonTerminalSymbol.AssignStmt);
            handleAssignStmt(node);

        }

        if (needSemicolon) {
            if (cur.type != Token.Type.NewStatement || !cur.value.equals(";")) {
                throw new ParserException(cur);
            }
            cur = nextToken();
        }
        root.addNode(node);

    }

    private void handleVarDecl(NNode root) throws IOException {
        NNode type = new NNode(NonTerminalSymbol.Type);
        handleType(type);
        root.addNode(type);

        NNode varList = new NNode(NonTerminalSymbol.VarList);
        handleVarList(varList);
        root.addNode(varList);
    }

    private void handleType(NNode root) throws IOException {
        TNode node = new TNode();
        if (cur.value.equals("int")) {
            node.setSymbol(TerminalSymbol.Int);
        } else if (cur.value.equals("double")) {
            node.setSymbol(TerminalSymbol.Double);
        } else if (cur.value.equals("string")) {
            node.setSymbol(TerminalSymbol.String);
        }
        root.addNode(node);
        cur = nextToken();
        if (cur.type == Token.Type.Sign && cur.value.equals("[")) {
            cur = nextToken();
            NNode index = new NNode(NonTerminalSymbol.Expr1);
            root.addNode(index);
            handleExpr1(index);

            if (cur.type != Token.Type.Sign || !cur.value.equals("]")) {
                throw new ParserException(cur);
            }
            root.addNode(index);
            cur = nextToken();
        }
    }

    private void handleVarList(NNode root) throws IOException {
        if (cur.type != Token.Type.Identifier) {
            throw new ParserException(cur);
        }
        while (cur.type == Token.Type.Identifier) {
            TNode id = new TNode(TerminalSymbol.Identifier, cur.value);
            root.addNode(id);

            cur = nextToken();
            if (cur.type == Token.Type.Sign && cur.value.equals("=")) {
                NNode node = new NNode(NonTerminalSymbol.VarDeclAssign);
                handleVarDeclAssign(node);
                root.addNode(node);
            }
            if (cur.type == Token.Type.Sign && cur.value.equals(",")) {
                cur = nextToken();
            }
        }

    }

    private void handleVarDeclAssign(NNode root) throws IOException {
        TNode sign = new TNode(TerminalSymbol.Sign, "=");
        root.addNode(sign);
        cur = nextToken();

        NNode value = new NNode(NonTerminalSymbol.Expr1);
        handleExpr1(value);
        root.addNode(value);

    }

    private void handleIfStmt(NNode root) throws IOException {
        TNode ifNode = new TNode(TerminalSymbol.If);
        root.addNode(ifNode);

        cur = nextToken();
        if (cur.type != Token.Type.Sign || !cur.value.equals("(")) {
            throw new ParserException(cur);
        }

        cur = nextToken();
        NNode expr = new NNode(NonTerminalSymbol.Expr1);
        handleExpr1(expr);
        root.addNode(expr);

        if (cur.type != Token.Type.Sign || !cur.value.equals(")")) {
            throw new ParserException(cur);
        }

        cur = nextToken();
        NNode block = new NNode(NonTerminalSymbol.StmtBlock);
        handleStmtBlock(block);
        root.addNode(block);

        if (cur.type == Token.Type.Keyword && cur.value.equals("else")) {
            TNode elseNode = new TNode(TerminalSymbol.Else);
            root.addNode(elseNode);

            cur = nextToken();
            NNode elseBlock = new NNode(NonTerminalSymbol.StmtBlock);
            handleStmtBlock(elseBlock);
            root.addNode(elseBlock);

        }
    }

    private void handleWhileStmt(NNode root) throws IOException {
        TNode whileNode = new TNode(TerminalSymbol.While);
        root.addNode(whileNode);

        cur = nextToken();
        if (cur.type != Token.Type.Sign || !cur.value.equals("(")) {
            throw new ParserException(cur);
        }

        cur = nextToken();
        NNode expr = new NNode(NonTerminalSymbol.Expr1);
        handleExpr1(expr);
        root.addNode(expr);

        if (cur.type != Token.Type.Sign || !cur.value.equals(")")) {
            throw new ParserException(cur);
        }

        cur = nextToken();
        NNode block = new NNode(NonTerminalSymbol.StmtBlock);
        handleStmtBlock(block);
        root.addNode(block);
    }

    private void handleStmtBlock(NNode root) throws IOException {
        if (cur.type != Token.Type.Sign || !cur.value.equals("{")) {
            throw new ParserException(cur);
        }

        cur = nextToken();
        while (cur.type != Token.Type.Sign || !cur.value.equals("}")) {
            NNode stmt = new NNode(NonTerminalSymbol.Stmt);
            handleStmt(stmt);
            root.addNode(stmt);
        }
        needSemicolon = false;
        cur = nextToken();
    }

    private void handleAssignStmt(NNode root) throws IOException {
        TNode value = new TNode(TerminalSymbol.Identifier, cur.value);
        root.addNode(value);

        cur = nextToken();
        if (cur.type == Token.Type.Sign &&
                cur.value.equals("[")) {
            cur = nextToken();
            NNode index = new NNode(NonTerminalSymbol.Expr1);
            root.addNode(index);
            handleExpr1(index);

            if (cur.type != Token.Type.Sign ||
                    !cur.value.equals("]")) {
                throw new ParserException(cur);
            }
            cur = nextToken();
        }

        if (cur.type == Token.Type.Sign &&
                AssignSignSet.contains(cur.value)) {
            TNode sign = new TNode(TerminalSymbol.Sign, cur.value);
            root.addNode(sign);

            cur = nextToken();
            NNode expr1 = new NNode(NonTerminalSymbol.Expr1);
            handleExpr1(expr1);
            root.addNode(expr1);
        } else {
            throw new ParserException(cur);
        }

    }

    private void handleExpr1(NNode root) throws IOException {
        NNode node1 = new NNode(NonTerminalSymbol.Expr2);
        handleExpr2(node1);
        root.addNode(node1);

        while (cur.type == Token.Type.Sign && Expr1SignSet.contains(cur.value)) {
            TNode sign = new TNode(TerminalSymbol.Sign, cur.value);
            root.addNode(sign);

            cur = nextToken();
            NNode node2 = new NNode(NonTerminalSymbol.Expr2);
            handleExpr2(node2);
            root.addNode(node2);
        }
    }
    private void handleExpr2(NNode root) throws IOException {
        NNode node1 = new NNode(NonTerminalSymbol.Expr3);
        handleExpr3(node1);
        root.addNode(node1);

        while (cur.type == Token.Type.Sign && Expr2SignSet.contains(cur.value)) {
            TNode sign = new TNode(TerminalSymbol.Sign, cur.value);
            root.addNode(sign);

            cur = nextToken();
            NNode node2 = new NNode(NonTerminalSymbol.Expr3);
            handleExpr3(node2);
            root.addNode(node2);
        }
    }
    private void handleExpr3(NNode root) throws IOException {
        NNode node1 = new NNode(NonTerminalSymbol.Expr4);
        handleExpr4(node1);
        root.addNode(node1);

        while (cur.type == Token.Type.Sign && Expr3SignSet.contains(cur.value)) {
            TNode sign = new TNode(TerminalSymbol.Sign, cur.value);
            root.addNode(sign);

            cur = nextToken();
            NNode node2 = new NNode(NonTerminalSymbol.Expr4);
            handleExpr4(node2);
            root.addNode(node2);
        }
    }
    private void handleExpr4(NNode root) throws IOException {
        NNode node1 = new NNode(NonTerminalSymbol.Expr5);
        handleExpr5(node1);
        root.addNode(node1);

        while (cur.type == Token.Type.Sign && Expr4SignSet.contains(cur.value)) {
            TNode sign = new TNode(TerminalSymbol.Sign, cur.value);
            root.addNode(sign);

            cur = nextToken();
            NNode node2 = new NNode(NonTerminalSymbol.Expr5);
            handleExpr5(node2);
            root.addNode(node2);
        }
    }

    private void handleExpr5(NNode root) throws IOException {
        NNode node1 = new NNode(NonTerminalSymbol.Expr6);
        handleExpr6(node1);
        root.addNode(node1);

        while (cur.type == Token.Type.Sign && Expr5SignSet.contains(cur.value)) {
            TNode sign = new TNode(TerminalSymbol.Sign, cur.value);
            root.addNode(sign);

            cur = nextToken();
            NNode node2 = new NNode(NonTerminalSymbol.Expr6);
            handleExpr6(node2);
            root.addNode(node2);
        }
    }

    private void handleExpr6(NNode root) throws IOException {
        NNode node1 = new NNode(NonTerminalSymbol.Expr7);
        handleExpr7(node1);
        root.addNode(node1);

        while (cur.type == Token.Type.Sign && Expr6SignSet.contains(cur.value)) {
            TNode sign = new TNode(TerminalSymbol.Sign, cur.value);
            root.addNode(sign);

            cur = nextToken();
            NNode node2 = new NNode(NonTerminalSymbol.Expr7);
            handleExpr7(node2);
            root.addNode(node2);
        }
    }

    private void handleExpr7(NNode root) throws IOException {
        while (cur.type == Token.Type.Sign && Expr7SignSet.contains(cur.value)) {
            TNode sign = new TNode(TerminalSymbol.Sign, cur.value);
            root.addNode(sign);

            cur = nextToken();
        }

        NNode node = new NNode(NonTerminalSymbol.Expr8);
        handleExpr8(node);
        root.addNode(node);
    }

    private void handleExpr8(NNode root) throws IOException {
        if (cur.type == Token.Type.Sign && cur.value.equals("(")) {
            cur = nextToken();
            NNode expr = new NNode(NonTerminalSymbol.Expr1);
            handleExpr1(expr);
            root.addNode(expr);

            if (cur.type != Token.Type.Sign || !cur.value.equals(")")) {
                throw new ParserException(cur);
            }
            cur = nextToken();
        } else {
            NNode node = new NNode(NonTerminalSymbol.Value);
            handleValue(node);
            root.addNode(node);
        }
    }

    private void handleValue(NNode root) throws IOException {
        TNode value = new TNode();
        if (cur.type == Token.Type.Identifier) {
            value.setSymbol(TerminalSymbol.Identifier);
            value.setValue(cur.value);
            root.addNode(value);
            cur = nextToken();
            if (cur.type == Token.Type.Sign && cur.value.equals("[")) {
                cur = nextToken();

                NNode index = new NNode(NonTerminalSymbol.Expr1);
                handleExpr1(index);

                if (cur.type != Token.Type.Sign || !cur.value.equals("]")) {
                    throw new ParserException(cur);
                }
                root.addNode(index);
                cur = nextToken();
            }

        } else if (cur.type == Token.Type.Keyword &&
                (cur.value.equals("true") || cur.value.equals("false"))) {
            value.setSymbol(TerminalSymbol.Bool);
            value.setValue(cur.value);
            root.addNode(value);
            cur = nextToken();
        } else if (cur.type == Token.Type.Integer) {
            value.setSymbol(TerminalSymbol.Int);
            value.setValue(cur.value);
            root.addNode(value);
            cur = nextToken();
        } else if (cur.type == Token.Type.Decimal) {
            value.setSymbol(TerminalSymbol.Double);
            value.setValue(cur.value);
            root.addNode(value);
            cur = nextToken();
        } else if (cur.type == Token.Type.String) {
            value.setSymbol(TerminalSymbol.String);
            value.setValue(cur.value);
            root.addNode(value);
            cur = nextToken();
        } else {
            throw new ParserException(cur);
        }
    }

    private void handleReadStmt(NNode root) throws IOException {
        TNode readNode = new TNode(TerminalSymbol.Read);
        root.addNode(readNode);

        cur = nextToken();
        if (cur.type != Token.Type.Sign || !cur.value.equals("(")) {
            throw new ParserException(cur);
        }

        cur = nextToken();
        if (cur.type != Token.Type.Identifier) {
            throw new ParserException(cur, "only identifier can be read");
        }
        TNode idNode = new TNode(TerminalSymbol.Identifier, cur.value);
        root.addNode(idNode);
        cur = nextToken();
        if (cur.type == Token.Type.Sign && cur.value.equals("[")) {
            cur = nextToken();

            NNode index = new NNode(NonTerminalSymbol.Expr1);
            handleExpr1(index);

            if (cur.type != Token.Type.Sign || !cur.value.equals("]")) {
                throw new ParserException(cur);
            }
            root.addNode(index);
            cur = nextToken();
        }

        if (cur.type != Token.Type.Sign || !cur.value.equals(")")) {
            throw new ParserException(cur);
        }
        cur = nextToken();
    }

    private void handleWriteStmt(NNode root) throws IOException {
        TNode writeNode = new TNode(TerminalSymbol.Write);
        root.addNode(writeNode);

        cur = nextToken();
        if (cur.type != Token.Type.Sign || !cur.value.equals("(")) {
            throw new ParserException(cur);
        }

        cur = nextToken();
        NNode expr = new NNode(NonTerminalSymbol.Expr1);
        handleExpr1(expr);
        root.addNode(expr);

        if (cur.type != Token.Type.Sign || !cur.value.equals(")")) {
            throw new ParserException(cur);
        }
        cur = nextToken();
    }

    private void handleBreakStmt(NNode root) throws IOException {
        TNode node = new TNode(TerminalSymbol.Break);
        root.addNode(node);

        cur = nextToken();
    }

    private void handleContinueStmt(NNode root) throws IOException {
        TNode node = new TNode(TerminalSymbol.Continue);
        root.addNode(node);

        cur = nextToken();
    }
}
