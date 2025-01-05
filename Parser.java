
package javaapplication4;

import java.util.regex.Pattern;

public class Parser {

    private Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

    boolean isNumber(String strNum) {
        if (strNum == null) {
            return false;
        }
        return pattern.matcher(strNum).matches();
    }

    static String[] split(String s, String p) {

        String regex = s;
        String stringtobeparsed = p;
        Pattern pattern = Pattern.compile(regex);
        int limit = 2; // binary operations
        String[] array = pattern.split(stringtobeparsed, limit);

        // Everything is binary 
        return array;

    }

    //    static Object binaryop (String op,int pOffset,String p) {
    //        
    //        
    //        
    //        for (int l=0;l<p.length();l++)
    //                   {   
    //                       pOffset = cond (p.charAt(l), pOffset);
    // 
    //                       
    //                       if (pOffset == 0 && p.charAt(l) == '+'){  
    //                         String after = p.substring(l+1,p.length());
    //                         String before = p.substring(0, l);
    //                         return new Mts.Symbolic.Add(parse(before),parse(after));}}
    //                         
    //    
    //    }
    static int cond(char a, int pOffset) {
        switch (a) {
            case '(':
            case '[':
            case '<':
                return ++pOffset;
            case ')':
            case ']':
            case '>':
                return --pOffset;
            case '|':
            // Absolute value   
            default:
                return pOffset;
        }
    }

    static int condCaret(char a, int pOffset) {

        // ^ is right associative
        switch (a) {
            case '(':
            case '[':
            case '<':
            case '{':
                return --pOffset;
            case ')':
            case ']':
            case '>':
            case '}':
                return ++pOffset;
            case '|':
            // Absolute value
            default:
                return pOffset;
        }
    }

    static Mts.Symbolic.Expression parse(String p) {
        // check later
        p = p.trim();
        try {
            // checks if the expression is number
            double doubleValue = Double.parseDouble(p);
            Mts.Numbers.R mobileNum = new Mts.Numbers.R(Double.parseDouble(p.trim()));
            return mobileNum;
        } catch (NumberFormatException errP) {
            // checks if the expression is variable or constant

            switch (p) {
                case "x":
                    //Mts.Symbolic.Variable mobileVar = new Mts.Symbolic.Variable('x');
                    Mts.Symbolic.Variable mobileVar = Main.x;
                    // always creates new variables, kind of fixed it                !!!
                    return mobileVar;
                //
                case "pi": {
                    Mts.Numbers.R mobileConst = new Mts.Numbers.R(Mts.Numbers.PI);
                    return mobileConst;
                }
                case "e": {
                    Mts.Numbers.R mobileConst = new Mts.Numbers.R(Mts.Numbers.E);
                    return mobileConst;
                }
                case "inf": {
                    Mts.Numbers.R mobileConst = Mts.Pos_Infinity;
                    return mobileConst;
                }
                // should be for loop !!!
                default:

                    int pOffset = 0;

                    for (int l = 0; l < p.length(); l++) {
                        pOffset = cond(p.charAt(l), pOffset);

                        if (pOffset == 0 && p.charAt(l) == '+') {
                            String after = p.substring(l + 1, p.length());
                            String before = p.substring(0, l);
                            return new Mts.Symbolic.Add(parse(before), parse(after));
                        }
                    }

                    for (int l = 0; l < p.length(); l++) {
                        pOffset = cond(p.charAt(l), pOffset);

                        if (pOffset == 0 && p.charAt(l) == '-') {
                            String after = p.substring(l + 1, p.length());
                            String before = p.substring(0, l);
                            
                            if (before.isBlank()) return new Mts.Symbolic.Multiply(Mts.MINUS_ONE, parse(after)); 
                            return new Mts.Symbolic.Minus(parse(before), parse(after));
                            
                        }
                    }

                    // should we differentiate minus and sub?
                    for (int l = 0; l < p.length(); l++) {
                        pOffset = cond(p.charAt(l), pOffset);

                        if (pOffset == 0 && p.charAt(l) == '*') {
                            String after = p.substring(l + 1, p.length());
                            String before = p.substring(0, l);
                            return new Mts.Symbolic.Multiply(parse(before), parse(after));
                        }
                    }

                    for (int l = 0; l < p.length(); l++) {
                        pOffset = cond(p.charAt(l), pOffset);

                        if (pOffset == 0 && p.charAt(l) == '/') {
                            String after = p.substring(l + 1, p.length());
                            String before = p.substring(0, l);
                            return new Mts.Symbolic.Divide(parse(before), parse(after));
                        }
                    }

                    for (int l = p.length() - 1; l >= 0; l--) { //Fix it!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                        pOffset = condCaret(p.charAt(l), pOffset);

                        if (pOffset == 0 && p.charAt(l) == '^') {
                            String after = p.substring(l + 1, p.length());
                            String before = p.substring(0, l);
                            return new Mts.Symbolic.Power(parse(before), parse(after));
                        }
                    }

                    for (int l = 0; l < p.length(); l++) {
                        pOffset = cond(p.charAt(l), pOffset);

                        if (pOffset == 0 && p.charAt(l) == 'r') {
                            String after = p.substring(l + 1, p.length());
                            String before = p.substring(0, l);
                            
                            if (before.isBlank()) {
                                before = "2";
                            }
                            return new Mts.Symbolic.Power(parse(after), new Mts.Symbolic.Divide(new Mts.Numbers.R(1), parse(before)));
                        }
                    }
                    // bit messy

                    for (int l = 0; l < p.length(); l++) {
                        pOffset = cond(p.charAt(l), pOffset);
                        try {
                            if (pOffset == 0 && "log".equals(p.substring(l, l + 3))) {

                                String after = p.substring(l + 3, p.length());
                                String before = p.substring(0, l);

                                if (before.isBlank()) {
                                    before = "10";
                                } // default log base is 10
                                return new Mts.Symbolic.Log(parse(before), parse(after));
                            }
                        } catch (Exception r) {
                        }

                    }
                    
                    for (int l = 0; l < p.length(); l++) {
                        pOffset = cond(p.charAt(l), pOffset);
                        try {
                            if (pOffset == 0 && "ln".equals(p.substring(l, l + 2))) {

                                String after = p.substring(l + 2, p.length());

                                return new Mts.Symbolic.Ln(parse(after));
                            }
                        } catch (Exception r) {
                        }

                    }
                    
                    for (int l = 0; l < p.length(); l++) {
                        pOffset = cond(p.charAt(l), pOffset);
                        try {
                            if (pOffset == 0 && "lim".equals(p.substring(l, l + 3))) {

                                String after = p.substring(l + 3, p.length());
                                String before = p.substring(0, l);
                                
                                return parse(after).limit(parse(before).evaluate(new Mts.Numbers.R(100)), Mts.Lside.LEFT)[0];
                            }
                        } catch (Exception r) {
                        }

                    }

                    // Unary operations
                    // This can be extended to all trigonometric function
                    for (int l = 0; l < p.length(); l++) {
                        pOffset = cond(p.charAt(l), pOffset);
                        try {
                            if (pOffset == 0 && "sin".equals(p.substring(l, l + 3))) {

                                String after = p.substring(l + 3, p.length());
                                return new Mts.Symbolic.SymTrigo.Sin(parse(after));
                            }
                        } catch (Exception r) {
                        }

                    }
                    
                    for (int l = 0; l < p.length(); l++) {
                        pOffset = cond(p.charAt(l), pOffset);
                        try {
                            if (pOffset == 0 && "abs".equals(p.substring(l, l + 3))) {

                                String after = p.substring(l + 3, p.length());
                                return new Mts.Symbolic.Absolute(parse(after));
                            }
                        } catch (Exception r) {
                        }

                    }

                    for (int l = 0; l < p.length(); l++) {
                        pOffset = cond(p.charAt(l), pOffset);
                        try {
                            if (pOffset == 0 && "cos".equals(p.substring(l, l + 3))) {

                                String after = p.substring(l + 3, p.length());
                                return new Mts.Symbolic.SymTrigo.Cos(parse(after));
                            }
                        } catch (Exception r) {
                        }

                    }
                    
                    for (int l = 0; l < p.length(); l++) {
                        pOffset = cond(p.charAt(l), pOffset);
                        try {
                            if (pOffset == 0 && "tan".equals(p.substring(l, l + 3))) {

                                String after = p.substring(l + 3, p.length());
                                return new Mts.Symbolic.SymTrigo.Cos(parse(after));
                            }
                        } catch (Exception r) {
                        }

                    }

                    for (int l = 0; l < p.length(); l++) {
                        pOffset = cond(p.charAt(l), pOffset);
                        try {
                            if (pOffset == 0 && "abs".equals(p.substring(l, l + 3))) {

                                String after = p.substring(l + 3, p.length());
                                return new Mts.Symbolic.Absolute(parse(after));
                            }
                        } catch (Exception r) {
                        }

                    }

                    // Unary operations
                    // This can be extended to all trigonometric function
                    // FOR GENERIC FUNCTIONS  -------------------------------------------------------------
                for (int l = 0; l < p.length(); l++) {
                    pOffset = cond(p.charAt(l), pOffset);
                    try {
                        if (pOffset == 0 && "f[".equals(p.substring(l, l + 2))) {                         
                           int endOF = -1;
                           for (int m = l; m < p.length(); m++) {

                               pOffset = cond(p.charAt(m), pOffset);
                               if(pOffset == 0 && "]".equals(p.substring(m, m+1))) {
                               endOF  = m;
                               break;
                               }       
                           } 
                           return new Mts.Symbolic.Function(parse(p.substring(l+2,endOF)), null, "f");
                        }
                    } catch (Exception r) {}

                }
//                    for (int l = 0; l < p.length(); l++) {
//                        pOffset = cond(p.charAt(l), pOffset);
//                        try {
//                            if (pOffset == 0 && "f".equals(p.substring(l, l + 1))) {
//
//                                String after = p.substring(l + 1, p.length());
//                                return new Mts.Symbolic.Function(parse(after), null, "f");
//                            }
//                        } catch (Exception r) {
//                        }
//
//                    }

                    // FOR ABSOLUTE VALUE -------------------------------------------------------------
                    for (int l = 0; l < p.length(); l++) {
                        pOffset = cond(p.charAt(l), pOffset);
                        try {
                            if (pOffset == 0 && "<".equals(p.substring(l, l + 1))) {
                                int endOF = -1;
                                for (int m = l; m < p.length(); m++) {

                                    pOffset = cond(p.charAt(m), pOffset);
                                    if (pOffset == 0 && ">".equals(p.substring(m, m + 1))) {
                                        endOF = m;
                                        break;
                                    }
                                }
                                return new Mts.Symbolic.Absolute(parse(p.substring(l + 1, endOF)));
                            }
                        } catch (Exception r) {
                        }

                    }

                    // SGN(X); Sign function
                    for (int l = 0; l < p.length(); l++) {
                        pOffset = cond(p.charAt(l), pOffset);
                        try {
                            if (pOffset == 0 && "sgn".equals(p.substring(l, l + 3))) {

                                String after = p.substring(l + 3, p.length());
                                return new Mts.Symbolic.Sgn(parse(after));
                            }
                        } catch (Exception r) {
                        }

                    }

                    // Remove paranthesis
                    for (int l = 0; l < p.length(); l++) {
                        pOffset = cond(p.charAt(l), pOffset);

                        // if offset is zero and the character is ")" and it is the end of the string: we need to remove the parantheses
                        if (p.charAt(l) == ')' && pOffset == 0 && l == p.length() - 1) {
                            return parse(p.substring(1, p.length() - 1));
                        }
                    }

                    return null;

            }
        }

    }

}