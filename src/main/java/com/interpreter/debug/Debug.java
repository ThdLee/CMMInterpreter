package com.interpreter.debug;

import com.interpreter.intermediatecode.CodeChunk;
import com.interpreter.intermediatecode.VariableRecorder;
import com.interpreter.virtualmachine.DataChunk;
import com.interpreter.virtualmachine.Interpreter;
import com.interpreter.virtualmachine.Value;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.*;


public class Debug {

    public static Debug instance = new Debug();

    private Debug() {}

    private final HashMap<Integer, VariableRecorder> recorderMap = new HashMap<>();
    private final HashMap<Integer, Integer> originToInterMap = new HashMap<>();
    private final HashMap<Integer, Integer> interToOriginMap = new HashMap<>();
    private final LinkedHashSet<Integer> breakSet = new LinkedHashSet<>();
    private final LinkedHashSet<Integer> breakInterSet = new LinkedHashSet<>();

    private static boolean activate = false;
    private int maxLine = 0;

    private InputStream in;
    private PrintStream out;

    private CodeChunk codeChunk;
    private DataChunk dataChunk;

    private Interpreter interpreter;

    private ArrayList<String> originCodes;

    public static void activation() {
        activate = true;
    }
    public static boolean isActivate() {
        return activate;
    }

    public void mapRecorder(int line, VariableRecorder recorder) {
        if (!activate) return;
        recorderMap.put(line, recorder);
        maxLine = line > maxLine ? line : maxLine;
    }

    public void mapCode(int line) {
        if (!activate) return;
        if (recorderMap.isEmpty()) {
            throw new RuntimeException("");
        }
        if (!originToInterMap.containsKey(maxLine)) {
            originToInterMap.put(maxLine, line);
            interToOriginMap.put(line, maxLine);
        }

    }

    public void setOriginCodes(ArrayList<String> originCodes) {
        if (!activate) return;
        this.originCodes = originCodes;
    }

    private int line;
    public void intoDebugMode(Interpreter interpreter, CodeChunk codeChunk, DataChunk dataChunk, InputStream in, PrintStream out) {
        Scanner scanner = new Scanner(in);
        this.in = in;
        this.out = out;
        this.codeChunk = codeChunk;
        this.dataChunk = dataChunk;
        this.interpreter = interpreter;

        line = 0;

        while (true) {
            out.print(">>> ");
            String command = scanner.nextLine();
            String[] commands = command.split(" +");

            switch (commands[0]) {
                case "l":
                case "list":
                    parseList(commands);
                    break;
                case "r":
                case "run":
                    parseRun(commands);
                    break;
                case "c":
                case "continue":
                    parseContinue(commands);
                    break;
                case "b":
                case "break":
                    parseBreak(commands);
                    break;
                case "n":
                case "next":
                    parseNext(commands);
                    break;
                case "p":
                case "print":
                    parsePrint(commands);
                    break;
                case "d":
                case "delete":
                    parseDelete(commands);
                case "q":
                case "quit":
                    System.exit(0);
                case "info":
                    parseInfo(commands);
                    break;
                default:
                    out.println("invalid command '" + command + "'");
            }
        }

    }

    private void run(int line) {
        CodeChunk.Code code = codeChunk.getCodeByLine(line);
        this.line = interpreter.run(line, code, dataChunk);
    }

    private void parseList(String[] commands) {
        switch (commands.length) {
            case 1:
                for (int i = 0; i < originCodes.size(); i++) {
                    out.println(String.format("%-5d", i+1) + originCodes.get(i));
                }
                return;
            case 2:
                if (commands[1].equals("inter")) {
                    for (int i = 0; i < codeChunk.getSize(); i++) {
                        out.println(String.format("%-5d", i+1) + codeChunk.getCodeByLine(i));
                    }
                }
                return;
            default:
                out.println("list: l/list [inter]");
        }
    }

    private void parseRun(String[] commands) {
        switch (commands.length) {
            case 1:
                if (line >= codeChunk.getSize()) line = 0;
                while (true) {
                    if (line >= codeChunk.getSize()) break;
                    if (breakSet.contains(line)) {
                        out.println("breakpoint at " + originCodes.get(covertToOriginCodeLine(line)));
                        return;
                    } else if (breakInterSet.contains(line)) {
                        out.println("breakpoint at " + codeChunk.getCodeByLine(line));
                        return;
                    }
                    run(line);
                }
                return;
            default:
                out.println("run: r/run");
        }
    }

    private void parseContinue(String[] commands) {
        switch (commands.length) {
            case 1:
                while (true) {
                    if (line >= codeChunk.getSize()) break;
                    if (breakSet.contains(line)) {
                        out.println("breakpoint at " + originCodes.get(covertToOriginCodeLine(line)));
                        return;
                    } else if (breakInterSet.contains(line)) {
                        out.println("breakpoint at " + codeChunk.getCodeByLine(line));
                        return;
                    }
                    run(line);
                }
                return;
            default:
                out.println("continue: c/continue");
        }
    }

    private void parseBreak(String[] commands) {
        int line = 0;
        switch (commands.length) {
            case 2:
                try {
                    line = Integer.parseInt(commands[1]);
                } catch (NumberFormatException e) {
                    out.println("'" + commands[1] + "' is not an integer");
                    return;
                }
                if (line > 0 && line <= originCodes.size()) {
                    breakSet.add(convertToInterCodeLine(line-1));
                    out.println("breakpoint at line " + line);
                } else {
                    out.println("'" + line + "' is out of bounds");
                }
                return;
            case 3:
                if (commands[1].equals("inter")) {
                    try {
                        line = Integer.parseInt(commands[3]);
                    } catch (NumberFormatException e) {
                        out.println("'" + commands[1] + "' is not an integer");
                        return;
                    }
                    if (line > 0 && line <= codeChunk.getSize()) {
                        breakInterSet.add(line-1);
                        out.println("breakpoint at intermediate code line " + line);
                    } else {
                        out.println("'" + line + "' is out of bounds");
                    }
                    return;
                }
            default:
                out.println("break: b/break [inter] <line>");
        }
    }

    private void parseNext(String[] commands) {
        switch (commands.length) {
            case 1:
                int originLine = covertToOriginCodeLine(line);
                while (originLine == covertToOriginCodeLine(line)) {
                    if (line >= codeChunk.getSize()) return;
                    run(line);
                }
                return;
            case 2:
                if (!commands[1].equals("inter")) {
                    out.println("next: n/next [inter]");
                }
                if (line >= codeChunk.getSize()) return;
                run(line);
                return;
            default:
                out.println("next: n/next [inter]");
        }
    }

    private void parsePrint(String[] commands) {
        switch (commands.length) {
            case 2:
                Integer re = null;
                try {
                    re = Integer.parseInt(commands[1]);
                } catch (NumberFormatException e) {
                    re = recorderMap.get(covertToOriginCodeLine(line)).getVarIndex(commands[1]);
                }
                if (re == null || re < 0 || re >= dataChunk.getSize() || dataChunk.getData(re) == null) {
                    out.println("invalid identifier or register '" + commands[1] + "'");
                } else {
                    Value value = dataChunk.getData(re);
                    out.println(commands[1] + "  " + value);
                }
                return;
            default:
                out.println("print: p/print <identifier/register>");
        }
    }

    private void parseDelete(String[] commands) {
        int line = 0;
        switch (commands.length) {
            case 2:
                try {
                    line = Integer.parseInt(commands[1]);
                } catch (NumberFormatException e) {
                    out.println("'" + commands[1] + "' is not an integer");
                    return;
                }
                line -= 1;
                if (line >= 0 && line < originCodes.size()) {
                    if (breakSet.contains(line)) {
                        breakSet.remove(line);
                        out.println("breakpoint at line:" + line + " has been deleted");
                    } else {
                        out.println("no breakpoint at line:" + line);
                    }
                } else {
                    out.println("'" + line + "' is out of bounds");
                }
                return;
            case 3:
                if (commands[1].equals("inter")) {
                    try {
                        line = Integer.parseInt(commands[3]);
                    } catch (NumberFormatException e) {
                        out.println("'" + commands[1] + "' is not an integer");
                        return;
                    }
                    line -= 1;
                    if (line >= 0 && line < codeChunk.getSize()) {
                        if (breakInterSet.contains(line)) {
                            breakInterSet.remove(line);
                            out.println("breakpoint at intermediate code line:" + line + " has been deleted");
                        } else {
                            out.println("no breakpoint at line:" + line);
                        }
                    } else {
                        out.println("'" + line + "' is out of bounds");
                    }
                    return;
                }
            default:
                out.println("delete: d/delete [inter] <line>");
        }
    }

    private void parseInfo(String[] commands) {
        switch (commands.length) {
            case 2:
                if (commands[1].equals("break")) {
                    for (int b : breakSet) {
                        out.println("breakpoint at line " + (covertToOriginCodeLine(b)+1));
                    }
                } else {
                    out.println("info [inter] break");
                }
                return;
            case 3 :
                if (commands[1].equals("inter") && commands[2].equals("break")) {
                    for (int b : breakInterSet) {
                        out.println("breakpoint at intermediate code line " + (b+1));
                    }
                } else {
                    out.println("info [inter] break");
                }
                return;
            default:
                out.println("info [inter] break");
        }
    }

    private int covertToOriginCodeLine(int line) {
        while (line >= 0 && line < codeChunk.getSize() && !interToOriginMap.containsKey(line)) {
            line -= 1;
        }
        return interToOriginMap.getOrDefault(line, 0);
    }

    private int convertToInterCodeLine(int line) {
        while (line >= 0 && line < originCodes.size() && !originToInterMap.containsKey(line)) {
            line += 1;
        }
        return originToInterMap.getOrDefault(line, Integer.MAX_VALUE);
    }

}
