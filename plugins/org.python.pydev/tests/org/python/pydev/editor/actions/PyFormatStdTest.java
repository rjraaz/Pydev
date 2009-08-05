/*
 * Created on Feb 22, 2005
 *
 * @author Fabio Zadrozny
 */
package org.python.pydev.editor.actions;

import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.python.pydev.core.docutils.SyntaxErrorException;
import org.python.pydev.core.docutils.StringUtils;
import org.python.pydev.editor.actions.PyFormatStd.FormatStd;

/**
 * @author Fabio Zadrozny
 */
public class PyFormatStdTest extends TestCase {

    private FormatStd std;
    
    private static boolean DEBUG = false;

    public static void main(String[] args) {
        try {
            PyFormatStdTest n = new PyFormatStdTest();
            n.setUp();
            DEBUG = true;
            n.testKeepTab();
            n.tearDown();
            
            junit.textui.TestRunner.run(PyFormatStdTest.class);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        
    }

    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        std = new PyFormatStd.FormatStd();
        std.operatorsWithSpace = true;
    }
    
    public void testNoCloseList(){
        
        std.operatorsWithSpace = true;
        std.assignWithSpaceInsideParens = true;
        std.spaceAfterComma = true;
        std.parametersWithSpace = false;
        
        String s = ""+
        "constants = [\n"+
        "    (qt.Qt.Key_Escape, ''), \n"+
        "    (qt.Qt.Key_Tab, '\t'), \n"+
        "\n";
        
        String s1 = ""+
        "constants = [\n"+
        "    (qt.Qt.Key_Escape, ''),\n"+
        "    (qt.Qt.Key_Tab, '\t'),\n"+
        "\n";
        
        checkFormatResults(s, s1);
    }
    
    public void testDontDisturbWildImport(){
        std.operatorsWithSpace = true;
        std.assignWithSpaceInsideParens = true;
        std.spaceAfterComma = true;
        std.parametersWithSpace = false;
        
        checkFormatResults("from x import *\n\n");
    }
    
    public void testDontDisturbWildImport2(){
        std.operatorsWithSpace = true;
        std.assignWithSpaceInsideParens = true;
        std.spaceAfterComma = true;
        std.parametersWithSpace = false;
        
        checkFormatResults("import *");
    }
    
    
    public void testSpacesOnCall(){
        std.operatorsWithSpace = true;
        std.assignWithSpaceInsideParens = true;
        std.spaceAfterComma = true;
        std.parametersWithSpace = false;
        
        String original = "" +
        		"raise RuntimeError( \n" +
        		"    'text'\n" +
        		"    % format )\n" +
        		"";
        
        String expected = "" +
                "raise RuntimeError(\n" +
                "    'text'\n" +
                "    % format)\n" +
                "";
        checkFormatResults(original, expected);
    }
    
    public void testDontDisturbVarArgsAndKwArgs(){
        std.operatorsWithSpace = true;
        std.assignWithSpaceInsideParens = true;
        std.spaceAfterComma = true;
        std.parametersWithSpace = false;
        
        String s = ""+
        "def a(  a,b, *args, **kwargs  ):\n"+
        "    call( *args, **kwargs)\n";
        
        String s1 = ""+
        "def a(a, b, *args, **kwargs):\n"+
        "    call(*args, **kwargs)\n";
        
        checkFormatResults(s, s1);
    }
    
    public void testFormatComma(){
        std.spaceAfterComma = true;
        std.parametersWithSpace = false;

        String s = ""+
"def a(  a,b  ):\n"+
"    pass   \n";
        
        String s1 = ""+
"def a(a, b):\n"+
"    pass   \n";
        
        checkFormatResults(s, s1);
    
        std.spaceAfterComma = false;

        String s2 = ""+
"def a(a,b):\n"+
"    pass   \n";
        
        checkFormatResults(s, s2);
    
    }
    
    public void testNoFormatCommaOnNewLine(){
        std.spaceAfterComma = true;
        std.parametersWithSpace = false;
        
        String s = ""+
        "def a(a,\n" +
        "      b):\n"+
        "    pass\n";
        
        String s1 = ""+
        "def a(a,\n" +
        "      b):\n"+
        "    pass\n";
        
        checkFormatResults(s, s1);
    }

    public void testFormatEscapedQuotes(){
        std.spaceAfterComma = false;
        std.parametersWithSpace = false;
        
        String s = ""+
        "foo(bar(\"\\\"\"))";
        
        checkFormatResults(s);
        
        s = ""+
        "foo(bar('''\\''''))";
        checkFormatResults(s);
    }
    

    public void testFormatPar(){
        std.spaceAfterComma = true;
        std.parametersWithSpace = true;

        String s = ""+
"def a():\n"+
"    pass   \n";
        
        String s1 = ""+
"def a():\n"+
"    pass   \n";
        
        checkFormatResults(s, s1);
    }


    public void testFormatComma2(){
        std.spaceAfterComma = true;
        std.parametersWithSpace = false;

        String s = ""+
"def a( a,   b):\n"+
"    pass   \n";
        
        String s1 = ""+
"def a(a, b):\n"+
"    pass   \n";
        
        checkFormatResults(s, s1);
    
        std.spaceAfterComma = false;

        String s2 = ""+
"def a(a,b):\n"+
"    pass   \n";
        
        checkFormatResults(s, s2);
    }

    

    public void testFormatCommaParams(){
        std.spaceAfterComma = true;
        std.parametersWithSpace = true;

        String s = ""+
"def a(a,   b):\n"+
"    pass   \n";
        
        String s1 = ""+
"def a( a, b ):\n"+
"    pass   \n";
        
        checkFormatResults(s, s1);
    
        std.spaceAfterComma = false;

        String s2 = ""+
"def a( a,b ):\n"+
"    pass   \n";
        
        checkFormatResults(s, s2);
    }

    public void testFormatInnerParams(){
        std.spaceAfterComma = true;
        std.parametersWithSpace = false;
        std.operatorsWithSpace = true;

        String s = ""+
"def a(a,   b):\n"+
"    return ( (a+b) + ( a+b ) )   \n";
        
        String s1 = ""+
"def a(a, b):\n"+
"    return ((a + b) + (a + b))   \n";
        
        checkFormatResults(s, s1);
    

        std.parametersWithSpace = true;
        String s2 = ""+
"def a( a, b ):\n"+
"    return ( ( a + b ) + ( a + b ) )   \n";
        
        checkFormatResults(s, s2);
    }

    public void testFormatInnerParams2(){
        std.spaceAfterComma = true;
        std.parametersWithSpace = true;
        std.operatorsWithSpace = false;

        String s = ""+
"def a(a,   b):\n"+
"    return ( callA() + callB(b+b) )   \n";
        
        String s1 = ""+
"def a( a, b ):\n"+
"    return ( callA()+callB( b+b ) )   \n";
        
        checkFormatResults(s, s1);
    }

    
    public void testFormatNotInsideStrings(){
        std.spaceAfterComma = true;
        std.parametersWithSpace = true;

        String s = ""+
"a = ''' test()\n"+
"nothing changes() ((aa) )\n"+
"'''";
        
        checkFormatResults(s);

        s = ""+
"a = ''' test()\n"+
"nothing changes() ((aa) )\n"+
"";
        
        checkFormatResults(s);

        s = ""+
"a = ' test()'\n"+
"'nothing changes() ((aa) )'\n"+
"";
        
        checkFormatResults(s);

        s = ""+
"a = ' test()'\n"+
"'nothing changes() ((aa) )\n"+
"";
        
        checkFormatResults(s);
    }

    
    public void testFormatNotInsideComments(){
        std.spaceAfterComma = true;
        std.parametersWithSpace = true;

        String s = ""+
"#a = ''' test()\n"+
"#nothing changes() ((aa) )\n"+
"#'''";
        
        checkFormatResults(s);
    }

    public void testFormatNotInsideComments5(){
        std.spaceAfterComma = true;
        std.parametersWithSpace = true;
        std.operatorsWithSpace = true;

        String s = ""+
"''' test()\n"+
"nothing 'changes() ((aa) )\n"+
"'''\n" +
"thisChanges(a+b + (a+b))";
        
        String s2 = ""+
"''' test()\n"+
"nothing 'changes() ((aa) )\n"+
"'''\n" +
"thisChanges( a + b + ( a + b ) )";
        
        checkFormatResults(s, s2);
        
        //unfinished comment
        s = ""+
"''' test()\n"+
"nothing 'changes() ((aa) )\n"+
"''\n" +
"thisDoesNotChange()";
        
        checkFormatResults(s);

        //unfinished comment at end of string
        s = ""+
"''' test()\n"+
"nothing 'changes() ((aa) )\n"+
"''";
        
        checkFormatResults(s);
    }

    public void testFormatNotInsideComments2(){
        std.spaceAfterComma = true;
        std.parametersWithSpace = true;

        String s = "methodname.split( '(' )";
        
        checkFormatResults(s);
    }

    public void testFormatNotInsideComments3(){
        std.spaceAfterComma = true;
        std.parametersWithSpace = true;

        String s = "methodname.split( #'(' \n" +
                " )";
        
        checkFormatResults(s);
    }

    public void testFormatNotInsideStrings2(){
        std.spaceAfterComma = true;
        std.parametersWithSpace = true;

        String s = "r = re.compile( \"(?P<latitude>\\d*\\.\\d*)\" )";

        
        checkFormatResults(s);
    }

    public void testFormatNotLinesOnlyWithParentesis(){
        std.spaceAfterComma = true;
        std.parametersWithSpace = true;

        String s = "" +
        "methodCall( a,\n"+
        "            b \n"+
        "           ) ";

        
        checkFormatResults(s);
    }

    
    public void testCommaOnParens(){
        std.spaceAfterComma = true;
        std.parametersWithSpace = false;
        
        String s = "" +
        "methodCall(a,b,c))\n";
        
        checkFormatResults(s, "methodCall(a, b, c))\n");
    }
    
    public void testOperators(){
        std.spaceAfterComma = true;
        std.parametersWithSpace = false;
        std.operatorsWithSpace = true;
        
        String s = "" +
        "i=i+1\n" +
        "submitted +=1\n" +
        "x = x*2 - 1\n" +
        "hypot2 = x*x + y*y\n" +
        "c = (a+b) * (a-b)\n" +
        "";
        
        String s1 = "" +
        "i = i + 1\n" +
        "submitted += 1\n" +
        "x = x * 2 - 1\n" +
        "hypot2 = x * x + y * y\n" +
        "c = (a + b) * (a - b)\n" +
        "";
        
        checkFormatResults(s, s1);
        
    }
    
    
    public void testEqualsWithSpaceInFunctionCall(){
        std.spaceAfterComma = true;
        std.parametersWithSpace = false;
        std.assignWithSpaceInsideParens = true;
        std.operatorsWithSpace = true;
        
        

        String s = "" +
        "a(xxx=10)\n" +
        "call(yyy = 20)\n";
        
        String s1 = "" +
        "a(xxx = 10)\n" +
        "call(yyy = 20)\n";
        
        checkFormatResults(s, s1);
        
        s1 = "" +
        "a(xxx = 10)\n" +
        "call(yyy = 20)\n";
        checkFormatResults(StringUtils.replaceAll(s, "=", "!="), StringUtils.replaceAll(s1, "=", "!="));
        checkFormatResults(StringUtils.replaceAll(s, "=", "<="), StringUtils.replaceAll(s1, "=", "<="));

        std.assignWithSpaceInsideParens = false;
        s1 = "" +
        "a(xxx=10)\n" +
        "call(yyy=20)\n";
        
        checkFormatResults(s, s1);
        
        s1 = "" +
        "a(xxx = 10)\n" +
        "call(yyy = 20)\n";
        checkFormatResults(StringUtils.replaceAll(s, "=", "!="), StringUtils.replaceAll(s1, "=", "!="));
        checkFormatResults(StringUtils.replaceAll(s, "=", "<="), StringUtils.replaceAll(s1, "=", "<="));
    }
    
    public void testNotValidCode(){
        //should not crash in these tests
        std.spaceAfterComma = true;
        std.parametersWithSpace = false;
        std.operatorsWithSpace = false;
        
        checkFormatResults("=", "=");
        checkFormatResults("==", "==");
        checkFormatResults("!", "!");
        checkFormatResults("!=", "!=");
    }
    
    public void testCompare(){
        std.spaceAfterComma = true;
        std.parametersWithSpace = false;
        std.operatorsWithSpace = true;
        
        String s = "" +
        "a==10\n" +
        "b== 20\n" +
        "c    =  30\n" +
        "d ==+1\n" +
        "e !=+1\n" +
        "e //=+1\n" +
        "";
        
        String s1 = "" +
        "a == 10\n" +
        "b == 20\n" +
        "c = 30\n" +
        "d == +1\n" +
        "e != +1\n" +
        "e //= +1\n" +
        "";
        
        checkFormatResults(s, s1);
    }
    
    public void testSimpleOperator(){
        std.spaceAfterComma = true;
        std.parametersWithSpace = false;
        std.operatorsWithSpace = true;
        
        String s = "" +
        "86000+10\n" +
        "";
        
        String s1 = "" +
        "86000 + 10\n" +
        "";
        
        checkFormatResults(s, s1);
    }
    
    public void testSimpleOperator2(){
        std.spaceAfterComma = true;
        std.parametersWithSpace = false;
        std.operatorsWithSpace = true;
        
        String s = "" +
        "+1\n" + //don't change if it's not summing anything (it's a sign for the number: unary operator)
        "";
        
        String s1 = "" +
        "+1\n" +
        "";
        
        checkFormatResults(s, s1);
    }

    public void testSimpleOperator3(){
        std.spaceAfterComma = true;
        std.parametersWithSpace = false;
        std.operatorsWithSpace = true;
        
        String s = "" +
        "call(+1)\n" + //don't change if it's not summing anything (it's a sign for the number: unary operator)
        "";
        
        String s1 = "" +
        "call(+1)\n" +
        "";
        
        checkFormatResults(s, s1);
    }
    
    public void testSimpleOperator4(){
        std.spaceAfterComma = true;
        std.parametersWithSpace = false;
        std.operatorsWithSpace = true;
        
        String s = "" +
        "call(1+1+2+(-1+(-1+1)))\n" + //don't change if it's not summing anything (it's a sign for the number: unary operator)
        "";
        
        String s1 = "" +
        "call(1 + 1 + 2 + (-1 + (-1 + 1)))\n" +
        "";
        
        checkFormatResults(s, s1);
    }
    
    
    public void testSimpleOperator5(){
        std.spaceAfterComma = true;
        std.parametersWithSpace = false;
        std.operatorsWithSpace = true;
        
        String s = "" +
        "expected_id = int(key.split('(')[ - 1][: - 1])\n" + //don't change if it's not summing anything (it's a sign for the number: unary operator)
        "";
        
        String s1 = "" +
        "expected_id = int(key.split('(')[ -1][:-1])\n" +
        "";
        
        checkFormatResults(s, s1);
    }
    
    public void testSimpleOperator6(){
        std.operatorsWithSpace = true;
        
        String s = "" +
        "if a>-10:print a\n" + //don't change if it's not summing anything (it's a sign for the number: unary operator)
        "";
        
        String s1 = "" +
        "if a > -10:print a\n" +
        "";
        
        checkFormatResults(s, s1);
    }
    
    public void testSimpleOperator7(){
        std.operatorsWithSpace = true;
        
        String s = "" +
        "if a>--10:print a\n" + //don't change if it's not summing anything (it's a sign for the number: unary operator)
        "";
        
        String s1 = "" +
        "if a > - -10:print a\n" +
        "";
        
        checkFormatResults(s, s1);
    }
    
    
    public void testSimpleOperator8(){
        std.operatorsWithSpace = true;
        
        String s = "" +
        "if a-10:print a *-10\n" + //don't change if it's not summing anything (it's a sign for the number: unary operator)
        "";
        
        String s1 = "" +
        "if a - 10:print a * -10\n" +
        "";
        
        checkFormatResults(s, s1);
    }
    
    
    public void testCorrectExponentials(){
        std.spaceAfterComma = true;
        std.parametersWithSpace = false;
        std.operatorsWithSpace = true;
        
        
        String s = "" +
        "a = 1e-6\n" + //operators should not have space
        "b = 1e+6\n" +
        "c = 1e3 + 6\n" +
        "d = 1e-3 - 6\n" +
        "e = 1+3 - 6\n" +
        "";
        
        String s1 = "" +
        "a = 1e-6\n" + //operators should not have space
        "b = 1e+6\n" +
        "c = 1e3 + 6\n" +
        "d = 1e-3 - 6\n" +
        "e = 1 + 3 - 6\n" +
        "";
        
        checkFormatResults(s, s1);
    }
    
    public void testUnaryOpWithSpace(){
        std.spaceAfterComma = true;
        std.parametersWithSpace = false;
        std.operatorsWithSpace = true;
        
        String s, s1;
        
        s = "" +
        "def test_formatter_unary():\n" +
        "   c = +1\n" +
        "   d = - 1\n" +
        "   e = (a, +2)\n" +
        "   e = (a, + 2)\n" +
        "   e = 1e - 3\n" +
        "   pass\n" +
        "";
        
        s1 = "" +
        "def test_formatter_unary():\n" +
        "   c = +1\n" +
        "   d = -1\n" +
        "   e = (a, +2)\n" +
        "   e = (a, +2)\n" +
        "   e = 1e-3\n" +
        "   pass\n" +
        "";
        checkFormatResults(s, s1);
        
        s1 = "" +
        "def test_formatter_unary():\n" +
        "   e = 1e-3\n" +
        "";
        
        s = "" +
        "def test_formatter_unary():\n" +
        "   e = 1e - 3\n" +
        "";
        
        checkFormatResults(s, s1);
        
    }
    
    
    public void testTrimAndNewLineEOL(){
        std.spaceAfterComma = true;
        std.parametersWithSpace = false;
        std.operatorsWithSpace = true;
        std.addNewLineAtEndOfFile = true;
        std.trimLines = true;
        
        String s = "" +
        "a=10  \n" +
        "b= 20  \n" +
        "c    =  30  ";
        
        String s1 = "" +
        "a = 10\n" +
        "b = 20\n" +
        "c = 30\n";
        
        checkFormatResults(s, s1);
    }
    
    
    public void testTrimAndNewLineEOL2(){
        std.spaceAfterComma = true;
        std.parametersWithSpace = false;
        std.operatorsWithSpace = true;
        std.addNewLineAtEndOfFile = true;
        std.trimLines = true;
        
        String s = "" +
        "#a=10  \n" +
        "#b= 20  \n" +
        "'''c    =  30 \n" +
        "   \n" +
        "''' \n" +
        "c =   30   ";
        
        String s1 = "" +
        "#a=10  \n" +
        "#b= 20  \n" +
        "'''c    =  30 \n" +
        "   \n" +
        "'''\n" +
        "c = 30\n";
        
        
        checkFormatResults(s, s1);
    }
    
    public void testTrimAndNewLineEOL3(){
        std.spaceAfterComma = true;
        std.parametersWithSpace = false;
        std.operatorsWithSpace = true;
        std.addNewLineAtEndOfFile = true;
        std.trimLines = true;
        
        checkFormatResults("c =  30", "c = 30\n");
        checkFormatResults("c =  30\n", "c = 30\n");
        checkFormatResults("c =  30 ", "c = 30\n");
        checkFormatResults("c =  30  ", "c = 30\n");
        checkFormatResults("c =  30\n ", "c = 30\n");
        checkFormatResults("c =  30\n\n ", "c = 30\n\n");
        checkFormatResults("c =  30\n\n \t ", "c = 30\n\n");
        checkFormatResults("c =  30 \t\n\n \t ", "c = 30\n\n");
        
        checkFormatResults("c = 30\n", "c = 30\n");
        checkFormatResults("c = 30", "c = 30\n");
        checkFormatResults("", "\n");
        checkFormatResults("  \t  ", "\n");
    }
    
    
    public void testEqualsWithSpace(){
        std.spaceAfterComma = true;
        std.parametersWithSpace = false;
        std.operatorsWithSpace = true;
        
    
        String s = "" +
        "a=10\n" +
        "b= 20\n" +
        "c    =  30";
        
        String s1 = "" +
        "a = 10\n" +
        "b = 20\n" +
        "c = 30";
        
        checkFormatResults(s, s1);
        checkFormatResults(StringUtils.replaceAll(s, "=", "!="), StringUtils.replaceAll(s1, "=", "!="));
        checkFormatResults(StringUtils.replaceAll(s, "=", "<="), StringUtils.replaceAll(s1, "=", "<="));
        
        s = "" +
        "a=\\n10\n" +
        "b= 20\n" +
        "c    =  30";
        
        s1 = "" +
        "a = \\n10\n" +
        "b = 20\n" +
        "c = 30";
        
        checkFormatResults(s, s1);
        checkFormatResults(StringUtils.replaceAll(s, "=", "!="), StringUtils.replaceAll(s1, "=", "!="));
        checkFormatResults(StringUtils.replaceAll(s, "=", "<="), StringUtils.replaceAll(s1, "=", "<="));
        
        s = "" +
        "a=10\n" +
        "b= 20\n" +
        "c    =  30";
        
        s1 = "" +
        "a=10\n" +
        "b=20\n" +
        "c=30";
        
        std.operatorsWithSpace = false;
        checkFormatResults(s, s1);
        checkFormatResults(StringUtils.replaceAll(s, "=", ">="), StringUtils.replaceAll(s1, "=", ">="));
        checkFormatResults(StringUtils.replaceAll(s, "=", "+="), StringUtils.replaceAll(s1, "=", "+="));
        
        s = "" +
        "a=\\n" +
        "10\n" +
        "b= 20\n" +
        "c    =  30";
        
        s1 = "" +
        "a=\\n" +
        "10\n" +
        "b=20\n" +
        "c=30";
        
        checkFormatResults(s, s1);
    }
    

    /**
     * Checks the results with the default passed and then with '\r' and '\n' considering
     * that the result of formatting the input string will be the same as the input.
     * 
     * @param s the string to be checked (and also the expected output)
     */
    private void checkFormatResults(String s) {
        checkFormatResults(s, s);
    }
    
    
    /**
     * Checks the results with the default passed and then with '\r' and '\n'
     * @param s the string to be checked
     * @param expected the result of making the formatting in the string
     */
    private void checkFormatResults(String s, String expected) {
        //default check (defined with \n)
        try{
            final PyFormatStd pyFormatStd = new PyFormatStd();
            String formatStr = pyFormatStd.formatStr(s, std, "\n", false);
            
            if(DEBUG){
                System.out.println(">>"+s.replace(' ', '.')+"<<");
                System.out.println(">>"+formatStr.replace(' ', '.')+"<<");
            }
            assertEquals(expected, formatStr);
            
            //second check (defined with \r)
            s = s.replace('\n', '\r');
            expected = expected.replace('\n', '\r');
            
            formatStr = pyFormatStd.formatStr(s, std, "\r", false);
            assertEquals(expected, formatStr);
            
            //third check (defined with \r\n)
            s = StringUtils.replaceAll(s, "\r", "\r\n");
            expected = StringUtils.replaceAll(expected, "\r", "\r\n");
            
            formatStr = pyFormatStd.formatStr(s, std, "\r\n", false);
            assertEquals(expected, formatStr);
            
            
            
            //now, same thing with different API
            Document doc = new Document();
            doc.set(s);
            pyFormatStd.formatAll(doc, null, true, std, false);
            assertEquals(expected, doc.get());
        }catch(SyntaxErrorException e){
            throw new RuntimeException(e);
        }
    }

    
    public void testDontLooseComma(){
        std.spaceAfterComma = true;
        std.parametersWithSpace = false;
        std.operatorsWithSpace = true;
        
        String s = "" +
        "call(aa, (1, 2)\n" +
        "";
        
        String s1 = "" +
        "call(aa, (1, 2)\n" +
        "";
        
        checkFormatResults(s, s1);
    }
    
    public void testKeepWhitespaces(){
        std.spaceAfterComma = true;
        std.parametersWithSpace = false;
        std.operatorsWithSpace = true;
        
        String s = "" +
        "t1 = ( 1, )\n" +
        "t2 = ( -1, )\n" +
        "l1 = [ 1 ]\n" +
        "l2 = [ -1 ]\n" +
        "d1 = { 1:0 }\n" +
        "d2 = { -1:0 }\n" +
        "";
        
        String s1 = "" +
        "t1 = (1,)\n" +
        "t2 = (-1,)\n" +
        "l1 = [ 1 ]\n" +
        "l2 = [ -1 ]\n" +
        "d1 = { 1:0 }\n" +
        "d2 = { -1:0 }\n" +
        "";
        
        checkFormatResults(s, s1);
    }
    
    public void testKeepTab() throws Exception{
        String s = "" +
        		"tmp = (\n" +
        		"\t)";
        checkFormatResults(s, s);
    }
    

}
