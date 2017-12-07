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

    private static final HashSet<String> commandSet = new HashSet<String>() {{
        add("l"); add("list");
        add("r"); add("run");
        add("c"); add("continue");
        add("b"); add("break");
        add("n"); add("next");
        add("p"); add("print");
        add("q"); add("quit");
        add("info");
    }};


    public static Debug instance = new Debug();

    private Debug() {}

    final HashMap<Integer, VariableRecorder> recorderMap = new HashMap<>();
    final LinkedHashMap<Integer, Integer> codeMap = new LinkedHashMap<>();
    final LinkedHashSet<Integer> breakSet = new LinkedHashSet<>();
    final LinkedHashSet<Integer> breakInterSet = new LinkedHashSet<>();

    private static boolean activate = false;
    private int maxLine = 0;

    private boolean interCodeMode = false;
    private Scanner scanner;

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
        codeMap.putIfAbsent(maxLine, line);
    }

    public void setOriginCodes(ArrayList<String> originCodes) {
        if (!activate) return;
        this.originCodes = originCodes;
    }

    private int line;
    public void intoDebugMode(Interpreter interpreter, CodeChunk codeChunk, DataChunk dataChunk, InputStream in, PrintStream out) {
        this.scanner = new Scanner(in);
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
        line = interpreter.run(line, code, dataChunk);
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
                run(line);
                return;
            default:
                out.println("run: r/run");
        }
    }

    private void parseContinue(String[] commands) {
        switch (commands.length) {
            case 1:
                run(line);
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
                    breakSet.add(line);
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
                        breakInterSet.add(line);
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
                run(line);
                return;
            default:
                out.println("next: n/next");
        }
    }

    private void parsePrint(String[] commands) {
        switch (commands.length) {
            case 2:
                Integer re = null;
                try {
                    re = Integer.parseInt(commands[1]);
                } catch (NumberFormatException e) {
                    re = recorderMap.get(line).getVarIndex(commands[1]);
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

    private void parseInfo(String[] commands) {
        switch (commands.length) {
            case 2:
                if (commands[1].equals("break")) {
                    for (int b : breakSet) {
                        out.println("breakpoint at line " + b);
                    }
                } else {
                    out.println("info [inter] break");
                }
                return;
            case 3 :
                if (commands[1].equals("inter") && commands[2].equals("break")) {
                    for (int b : breakInterSet) {
                        out.println("breakpoint at intermediate code line " + b);
                    }
                } else {
                    out.println("info [inter] break");
                }
                return;
            default:
                out.println("info [inter] break");
        }
    }

}
