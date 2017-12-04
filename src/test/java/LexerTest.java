import com.interpreter.analysis.AST;
import com.interpreter.analysis.Lexer;
import com.interpreter.analysis.Parser;
import com.interpreter.intermediatecode.CodeChunk;
import com.interpreter.intermediatecode.IntermediateCodeCreator;
import com.interpreter.virtualmachine.Array;
import com.interpreter.virtualmachine.VirtualMachine;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;
import java.util.ArrayList;

public class LexerTest {

//    @Test
//    public void Test1() throws IOException {
//        File file = new File("/Users/thdlee/Downloads/TestCases/error1_ID.cmm");
//        Reader reader = new FileReader(file);
//        Lexer lexer = new Lexer(reader);
//        Parser parser = new Parser(lexer);
//        AST ast = parser.prog();
//        IntermediateCodeCreator codeCreator = new IntermediateCodeCreator();
//        CodeChunk codeChunk = codeCreator.create(ast);
//        System.out.println(codeChunk);
//        reader.close();
//    }
//
//    @Test
//    public void Test2() throws IOException {
//        File file = new File("/Users/thdlee/Downloads/TestCases/error2_array.cmm");
//        Reader reader = new FileReader(file);
//        Lexer lexer = new Lexer(reader);
//        Parser parser = new Parser(lexer);
//        AST ast = parser.prog();
//        IntermediateCodeCreator codeCreator = new IntermediateCodeCreator();
//        CodeChunk codeChunk = codeCreator.create(ast);
//        System.out.println(codeChunk);
//        reader.close();
//    }
//
//    @Test
//    public void Test3() throws IOException {
//        File file = new File("/Users/thdlee/Downloads/TestCases/error3_comment.cmm");
//        Reader reader = new FileReader(file);
//        Lexer lexer = new Lexer(reader);
//        Parser parser = new Parser(lexer);
//        AST ast = parser.prog();
//        IntermediateCodeCreator codeCreator = new IntermediateCodeCreator();
//        CodeChunk codeChunk = codeCreator.create(ast);
//        System.out.println(codeChunk);
//        reader.close();
//    }

    @Test
    public void Test4() throws IOException {
        File file = new File("/Users/thdlee/Downloads/TestCases/test1_变量声明.cmm");
        Reader reader = new FileReader(file);
        VirtualMachine.getInstance().run(reader);
        reader.close();
    }

    @Test
    public void Test5() throws IOException {
        File file = new File("/Users/thdlee/Downloads/TestCases/test2_一般变量赋值.cmm");
        Reader reader = new FileReader(file);
        VirtualMachine.getInstance().run(reader);
        reader.close();
    }

    @Test
    public void Test6() throws IOException {
        File file = new File("/Users/thdlee/Downloads/TestCases/test3_数组.cmm");
        Reader reader = new FileReader(file);
        VirtualMachine.getInstance().run(reader);
        reader.close();
    }

    @Test
    public void Test7() throws IOException {
        File file = new File("/Users/thdlee/Downloads/TestCases/test4_算术运算.cmm");
        Reader reader = new FileReader(file);
        VirtualMachine.getInstance().run(reader);
        reader.close();
    }

    @Test
    public void Test8() throws IOException {
        File file = new File("/Users/thdlee/Downloads/TestCases/test5_IF-ELSE.cmm");
        Reader reader = new FileReader(file);
        VirtualMachine.getInstance().run(reader);
        reader.close();
    }

    @Test
    public void Test9() throws IOException {
        File file = new File("/Users/thdlee/Downloads/TestCases/test6_WHILE.cmm");
        Reader reader = new FileReader(file);
        VirtualMachine.getInstance().run(reader);
        reader.close();
    }

    @Test
    public void Test10() throws IOException {
        File file = new File("/Users/thdlee/Downloads/TestCases/test7_IF-ELSE与WHILE.cmm");
        Reader reader = new FileReader(file);
        VirtualMachine.getInstance().run(reader);
        reader.close();
    }


    @Test
    public void Test11() throws IOException {
        File file = new File("/Users/thdlee/Downloads/TestCases/test8_阶乘.cmm");
        Reader reader = new FileReader(file);
        VirtualMachine.getInstance().run(reader);
        reader.close();
    }


    @Test
    public void Test12() throws IOException {
        File file = new File("/Users/thdlee/Downloads/TestCases/test9_数组排序.cmm");
        Reader reader = new FileReader(file);
        VirtualMachine.getInstance().run(reader);
        reader.close();
    }


}
