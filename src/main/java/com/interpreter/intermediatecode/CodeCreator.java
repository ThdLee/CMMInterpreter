package com.interpreter.intermediatecode;

import com.interpreter.analysis.NonTerminalSymbol;
import com.interpreter.analysis.TerminalSymbol;
import com.interpreter.analysis.node.NNode;
import com.interpreter.analysis.node.Node;
import com.interpreter.analysis.node.TNode;
import com.interpreter.debug.Debug;
import com.interpreter.intermediatecode.CodeChunk.Command;
import com.interpreter.intermediatecode.CodeChunk.ImmediateNumber;


class CodeCreator {
    final static CodeCreator instance = new CodeCreator();

    void handleRoot(Node root, Context context) {

        NNode n = (NNode) root;
        for (Node node : n.getChildren()) {
            handleStmt((NNode) node, context);
        }
    }
    private void handleStmt(NNode root, Context context) {
        Debug.instance.mapRecorder(root.getLine(), context.recorder);
        for (Node node : root.getChildren()) {
            NNode n = (NNode) node;
            switch (n.getSymbol()) {
                case VarDecl:
                    handleVarDecl((NNode) node, context);
                    break;
                case IFStmt:
                    handleIFStmt((NNode) node, context);
                    break;
                case WhileStmt:
                    handleWhileStmt((NNode) node, context);
                    break;
                case ReadStmt:
                    handleReadStmt((NNode) node, context);
                    break;
                case WriteStmt:
                    handleWriteStmt((NNode) node, context);
                    break;
                case BreakStmt:
                    handleBreakStmt((NNode) node, context);
                    break;
                case ContinueStmt:
                    handleContinueStmt((NNode) node, context);
                    break;
                case AssignStmt:
                    handleAssignStmt((NNode) node, context);
                    break;
                default:
                    throw new IntermediateException("unknown non-terminal symbol '" + n.getSymbol() + "'");
            }
        }
    }

    private void handleVarDecl(NNode root, Context context) {
        NNode typeNode = (NNode) root.getChildren().get(0);
        NNode varListNode = (NNode) root.getChildren().get(1);

        TNode node = (TNode) typeNode.getChildren().get(0);
        PrimaryType type;
        switch (node.getSymbol()) {
            case Int:
                type = PrimaryType.Int;
                break;
            case Bool:
                type = PrimaryType.Bool;
                break;
            case String:
                type = PrimaryType.String;
                break;
            case Double:
                type = PrimaryType.Double;
                break;
            default:
                throw new IntermediateException("unknown type '" + node.getSymbol() + "'");
        }
        int arrayIndex = -1;
        if (typeNode.getChildren().size() >= 2) {
            NNode n = (NNode) typeNode.getChildren().get(1);
            arrayIndex = context.variablePool.createIndex();
            handleExpr(n, arrayIndex, context);
        }


        for (int i = 0; i < varListNode.getChildren().size(); i++) {
            int var = -1;
            boolean newArray = false;
            Node varNode = varListNode.getChildren().get(i);
            if (varNode instanceof TNode && ((TNode) varNode).getSymbol() == TerminalSymbol.Identifier) {
                var = context.variablePool.createIndex();
                String id = ((TNode) varNode).getValue();
                if (context.recorder.localContains(id)) {
                    throw new IntermediateException("'" + id + "' has defined");
                }
                if (arrayIndex != -1) {
                    context.chunk.push(Command.NewArray, var, arrayIndex);
                    context.recorder.define(id, var, PrimaryType.Array, type);
                    context.variablePool.freeIndex(arrayIndex);
                    newArray = true;
                } else {
                    context.recorder.define(id, var, type);
                    context.chunk.push(Command.Mov, var, initialize(type));
                }
            }
            boolean assign = false;
            if (i+1 < varListNode.getChildren().size()) {
                varNode = varListNode.getChildren().get(i+1);
                if (varNode instanceof NNode) {
                    int val = context.variablePool.createIndex();
                    handleVarDeclAssign((NNode) varNode, val, context);
                    context.chunk.push(Command.Mov, var, val);
                    context.variablePool.freeIndex(val);
                    i++;
                    assign = true;
                }
            }
        }

    }

    private void handleVarDeclAssign(NNode root, int res, Context context) {
        NNode node = (NNode) root.getChildren().get(1);
        handleExpr(node, res, context);
    }
    private void handleIFStmt(NNode root, Context context) {
        int endHolder = context.positionPlaceholder.createPosition();

        NNode condition = (NNode) root.getChildren().get(1);
        int var = context.variablePool.createIndex();
        handleExpr(condition, var, context);
        if (root.getChildren().size() > 3) {
            int nextHolder = context.positionPlaceholder.createPosition();
            context.chunk.push(Command.JmpUnless, nextHolder, var);
            Context childContext = context.link();
            NNode node = (NNode) root.getChildren().get(2);
            handleStmtBlock(node, childContext);
            context.chunk.push(Command.Jmp, endHolder);
            context.positionPlaceholder.setPosition(nextHolder, context.chunk.getCurrentPostion());
            NNode elseNode = (NNode) root.getChildren().get(4);
            Context elseChildContext = context.link();
            handleStmtBlock(elseNode, elseChildContext);
            context.positionPlaceholder.setPosition(endHolder, context.chunk.getCurrentPostion());
        } else  {
            context.chunk.push(Command.JmpUnless, endHolder, var);
            Context childContext = context.link();
            NNode node = (NNode) root.getChildren().get(2);
            handleStmtBlock(node, childContext);
            context.positionPlaceholder.setPosition(endHolder, context.chunk.getCurrentPostion());
        }
        context.variablePool.freeIndex(var);
    }
    private void handleStmtBlock(NNode root, Context context) {
        for (Node node : root.getChildren()) {
            handleStmt((NNode) node, context);
        }
    }
    private void handleWhileStmt(NNode root, Context context) {
        int checkConditionLocation = context.positionPlaceholder.createPosition();
        int endHolder = context.positionPlaceholder.createPosition();

        context.jumpStack.push(endHolder, checkConditionLocation);

        Context childContext = context.link();
        childContext.positionPlaceholder.setPosition(checkConditionLocation, childContext.chunk.getCurrentPostion());
        NNode condition = (NNode) root.getChildren().get(1);
        int var = childContext.variablePool.createIndex();
        handleExpr(condition, var, context);
        childContext.chunk.push(Command.JmpUnless, endHolder, var);
        childContext.variablePool.freeIndex(var);
        NNode node = (NNode) root.getChildren().get(2);

        handleStmtBlock(node, childContext);
        context.positionPlaceholder.setPosition(endHolder, childContext.chunk.getCurrentPostion());
        context.jumpStack.pop();
        context.chunk.push(Command.Jmp, checkConditionLocation);
        context.positionPlaceholder.setPosition(endHolder, context.chunk.getCurrentPostion());

    }
    private void handleBreakStmt(NNode root, Context context) {
        context.chunk.push(Command.Jmp, context.jumpStack.getCurrentBreakLocation());
    }
    private void handleContinueStmt(NNode root, Context context) {
        context.chunk.push(Command.Jmp, context.jumpStack.getCurrentContinueLocation());
    }
    private void handleReadStmt(NNode root, Context context) {
        TNode node = (TNode) root.getChildren().get(1);
        int id = context.recorder.getVarIndex(node.getValue());
        if (root.getChildren().size() > 2) {
            int var = context.variablePool.createIndex();
            int i = context.variablePool.createIndex();
            NNode index = (NNode) root.getChildren().get(2);
            handleExpr(index, i, context);
            context.chunk.push(Command.Get, var, id, i);
            context.chunk.push(Command.Read, var);
            context.chunk.push(Command.Set, id, var, i);
            context.variablePool.freeIndex(i);
            context.variablePool.freeIndex(var);
        } else {
            context.chunk.push(Command.Read, id);
        }

    }

    private void handleWriteStmt(NNode root, Context context) {
        NNode node = (NNode) root.getChildren().get(1);
        int var = context.variablePool.createIndex();
        handleExpr(node, var, context);
        context.chunk.push(Command.Write, var);
        context.variablePool.freeIndex(var);
    }
    private void handleAssignStmt(NNode root, Context context) {
        int i = 0;
        TNode idNode = (TNode) root.getChildren().get(i++);
        String id = idNode.getValue();
        if (!context.recorder.contains(id)) throw new IntermediateException("undefined '" + id + "'");

        int res;

        Node node = root.getChildren().get(i++);
        boolean hasIndex = false;
        int index = 0;
        if (node instanceof NNode) {
            hasIndex = true;
            index = context.variablePool.createIndex();
            handleExpr((NNode) node, index, context);
            node = root.getChildren().get(i++);
            res = context.variablePool.createIndex();
        } else {
            res = context.recorder.getVarIndex(id);
        }
        TNode sign = (TNode) node;
        NNode expr = (NNode) root.getChildren().get(i);
        switch (sign.getValue()) {
            case "=": {
                int num = context.variablePool.createIndex();
                handleExpr(expr, num, context);
                context.chunk.push(Command.Mov, res, num);
                context.variablePool.freeIndex(num);
                break;
            }
            case "+=": {
                int num = context.variablePool.createIndex();
                handleExpr(expr, num, context);
                context.chunk.push(Command.Add, res, num);
                context.variablePool.freeIndex(num);
                break;
            }
            case "-=": {
                int num = context.variablePool.createIndex();
                handleExpr(expr, num, context);
                context.chunk.push(Command.Sub, res, num);
                context.variablePool.freeIndex(num);
                break;
            }
            case "*=": {
                int num = context.variablePool.createIndex();
                handleExpr(expr, num, context);
                context.chunk.push(Command.Mul, res, num);
                context.variablePool.freeIndex(num);
                break;
            }
            case "/=": {
                int num = context.variablePool.createIndex();
                handleExpr(expr, num, context);
                context.chunk.push(Command.Div, res, num);
                context.variablePool.freeIndex(num);
                break;
            }
            case "%=": {
                int num = context.variablePool.createIndex();
                handleExpr(expr, num, context);
                context.chunk.push(Command.Mod, res, num);
                context.variablePool.freeIndex(num);
                break;
            }
            case "&&=": {
                int num = context.variablePool.createIndex();
                handleExpr(expr, num, context);
                context.chunk.push(Command.And, res, num);
                context.variablePool.freeIndex(num);
                break;
            }
            case "||=": {
                int num = context.variablePool.createIndex();
                handleExpr(expr, num, context);
                context.chunk.push(Command.Or, res, num);
                context.variablePool.freeIndex(num);
                break;
            }
            default:
                throw new IntermediateException("unknown operation '" + sign.getValue() + "'");
        }

        if (hasIndex) {
            int var = context.recorder.getVarIndex(id);
            context.chunk.push(Command.Set, var, res, index);
            context.variablePool.freeIndex(res);
            context.variablePool.freeIndex(index);
        }

    }
    private void handleExpr(NNode root, int res, Context context) {
        if (root.getSymbol() == NonTerminalSymbol.Expr7) {
            handleExpr7(root, res, context);
            return;
        } else if (root.getSymbol() == NonTerminalSymbol.Expr8) {
            handleExpr8(root, res, context);
            return;
        }
        int size = root.getChildren().size();
        if (size == 1) {
            handleExpr((NNode) root.getChildren().get(0), res, context);
        } else {
            NNode node = (NNode) root.getChildren().get(0);
            handleExpr(node, res, context);
            for (int i = 1; i < size; i++) {
                TNode sign = (TNode) root.getChildren().get(i++);
                node = (NNode) root.getChildren().get(i);
                int num = context.variablePool.createIndex();
                handleExpr(node, num, context);
                switch (sign.getValue()) {
                    case "+":
                        context.chunk.push(Command.Add, res, num);
                        break;
                    case "-":
                        context.chunk.push(Command.Sub, res, num);
                        break;
                    case "*":
                        context.chunk.push(Command.Mul, res, num);
                        break;
                    case "/":
                        context.chunk.push(Command.Div, res, num);
                        break;
                    case "%":
                        context.chunk.push(Command.Mod, res, num);
                        break;
                    case ">":
                        context.chunk.push(Command.Gt, res, num);
                        break;
                    case ">=":
                        context.chunk.push(Command.Gte, res, num);
                        break;
                    case "<":
                        context.chunk.push(Command.Lt, res, num);
                        break;
                    case "<=":
                        context.chunk.push(Command.Lte, res, num);
                        break;
                    case "&&":
                        context.chunk.push(Command.And, res, num);
                        break;
                    case "||":
                        context.chunk.push(Command.Or, res, num);
                        break;
                    case "==":
                        context.chunk.push(Command.Equal, res, num);
                        break;
                    case "!=":
                        context.chunk.push(Command.NotEqual, res, num);
                        break;
                    default:
                        throw new IntermediateException("unknown operation '" + sign.getValue() + "'");
                }
                context.variablePool.freeIndex(num);
            }
        }
    }

    private void handleExpr7(NNode root, int res, Context context) {
        Node node = root.getChildren().get(0);
        if (node instanceof TNode) {
            TNode sign = (TNode) node;
            NNode expr = (NNode) root.getChildren().get(1);
            handleExpr8(expr, res, context);
            switch (sign.getValue()) {
                case "+":

                    break;
                case "-":
                    context.chunk.push(Command.Opposite, res, res);
                    break;
                case "!":
                    context.chunk.push(Command.Not, res, res);
                    break;
                default:
                    throw new IntermediateException("unknown operation '" + sign.getValue() + "'");
            }
        } else {
            handleExpr8((NNode) node, res, context);
        }
    }
    private void handleExpr8(NNode root, int res, Context context) {
        NNode node = (NNode) root.getChildren().get(0);
        if (node.getSymbol() == NonTerminalSymbol.Value) {
            handleValue(node, res, context);
        } else {
            handleExpr(node, res, context);
        }
    }
    private void handleValue(NNode root, int res, Context context) {
        TNode node = (TNode) root.getChildren().get(0);
        if (node.getSymbol() == TerminalSymbol.Identifier) {
            if (!context.recorder.contains(node.getValue())) {
                throw new IntermediateException("undefined '" + node.getValue() + "'");
            }
            if (root.getChildren().size() >= 2) {
                NNode index = (NNode) root.getChildren().get(1);
                int i = context.variablePool.createIndex();
                int num = context.recorder.getVarIndex(node.getValue());
                handleExpr(index, i, context);
                context.chunk.push(Command.Get, res, num, i);
                context.variablePool.freeIndex(i);
            } else {
                int num = context.recorder.getVarIndex(node.getValue());
                context.chunk.push(Command.Mov, res, num);
            }
        } else {
            ImmediateNumber number = new ImmediateNumber();
            switch (node.getSymbol()) {
                case Int:
                    number.integerValue = Integer.parseInt(node.getValue());
                    number.type = CodeChunk.ImmediateType.Integer;
                    break;
                case Double:
                    number.doubleValue = Double.parseDouble(node.getValue());
                    number.type = CodeChunk.ImmediateType.Double;
                    break;
                case Bool:
                    number.boolValue = Boolean.parseBoolean(node.getValue());
                    number.type = CodeChunk.ImmediateType.Bool;
                    break;
                case String:
                    number.stringValue = node.getValue();
                    number.type = CodeChunk.ImmediateType.String;
                    break;
            }
            context.chunk.push(Command.Mov, res, number);
        }

    }

    private ImmediateNumber initialize(PrimaryType type) {
        ImmediateNumber number = new ImmediateNumber();
        switch (type) {
            case Int:
                number.type = CodeChunk.ImmediateType.Integer;
                break;
            case Double:
                number.type = CodeChunk.ImmediateType.Double;
                break;
            case Bool:
                number.type = CodeChunk.ImmediateType.Bool;
                break;
            case String:
                number.type = CodeChunk.ImmediateType.String;
                break;
        }
        return number;
    }

}
