package javaapplication4;

import java.util.Scanner;
import java.util.regex.Pattern;

public class Mts {

    public static enum Lside {

        LEFT(-1),
        RIGHT(1),
        BOTH(0);

        public final int val;

        private Lside(int val) {
            this.val = val;
        }

    }

    public static enum Type {
        SET,
        UNDEFINED,
        VARIABLE, // x
        CONSTANT_F, // Deprecated
        REAL_NUMBER, // a,b,c,d

        ZERO, // 0
        ONE, // 1
        INTEGER, // n
        COMPLEX_NUMBER, // a+bi

        LINEAR, // ax+b ; a != 0

        GENERIC_EXP,
        POLYNOMIAL_F, // 3 and higher degree
        MONOMIAL_F,
        GENERIC_FUNCTION,
        RECIPROCAL, // 1/x = x^-1
        RATIONAL_POWER, // x^a
        POWER, // cx^a
        MONOMIAL, // cx^a, a >= 1 & integer
        POWER_COMPOSITE, // x^f(x) ; f(x)
        ROOT, // x^(1/a)
        SUM_OF_POWERS, // ax^b + cx^d ...

        LINEAR_F,
        QUADRATIC_F,
        CUBIC_F,
        RATIONAL_F,
        NROOT_F,
        EXPONENTIAL,
        LOG,
        LN,
        LB_F,
        EXP,
        A_PLUS_X,
        A_PLUS_XB,
        A_PLUS_CXB,
        AX,
        SIN,
        SIN_AX,
        SIN_LINEAR,
        SIN_COMPOSITE,
        SECX,
        SEC_AX,
        SEC_LINEAR,
        SECXTANX,
        SEC_COMPOSITE,
        SEC2_X,
        TANX,
        TR_COS,
        TR_TAN,
        TR_COT,
        TR_ARCSIN,
        TR_ARCCOS,
        TR_ARCTAN,
        TR_ARCCOT,
        TR_OTHER,
        //
        HYBRID,
        ABS,
        INVERSE_F,
        CEIL_F,
        FLOOR_F,
        SIGN_F,
        HSF,
        DDF,
        //
        LIM,
        INDETERMINATE,
        POS_INF,
        NEG_INF,
        DERIVATIVE,
        IDIFF,
        SIGMA,
        PI,
        ANTIDERIVATIVE,
        DEFINT,
        // INTEGRALS

    }

    final static double E = 2.7182818284;
    final static double PI = 3.14;
    final static double LN10 = 2.30258509299;

    final static Numbers.R symE = new Numbers.R(
            2.71828182845904523536028747135266249775724709369995957496696762772407663035354759457138217852516642742746639193200305992181741359662904357290033429526059563073813232862794349076323382988075319525101901157383418793070215408914993488416750924476146066808226480016847741185374234544243710753907774499206955170276183860626133);

    final static Numbers.R ONE = new Numbers.R(1);
    final static Numbers.R MINUS_ONE = new Numbers.R(-1);
    final static Numbers.R ZERO = new Numbers.R(0);

    final static Numbers.R Neg_Infinity = new Numbers.R(Double.NEGATIVE_INFINITY);
    final static Numbers.R Pos_Infinity = new Numbers.R(Double.POSITIVE_INFINITY);
    final static Numbers.R NAN = new Numbers.R(Double.NaN);

    final static Numbers.R EPSILON = new Numbers.R(Math.pow(Double.MIN_NORMAL, 2)); // Positive

    static class Numbers {

        final static double E = 2.7182818284;
        final static double PI = 3.14;

        static interface Number {
        }

        static class R implements Symbolic.Expression, Number, Sets.Set {

            double value;
            int numerator;
            int denominator;
            String error;
            boolean undefined = false;

            R(double JD) {
                value = JD;
            }

            R(int JI) {
                value = (double) JI;
            }

            R(long JL) {
                value = (double) JL;
            }

            R(String JS) {
                value = 0;
                error = JS;
            }

            R(boolean JB) {
                value = JB ? 1 : 0;
            }

            public boolean isInt() {
                return (value % 1) == 0;
            }

            @Override
            public Numbers.R evaluate(Numbers.R n) {
                return this;
            }

            @Override
            public Symbolic.Expression derivative() {
                return new R(0);
            }

            @Override
            public Type getType() {
                if (this.value == 0) {
                    return Type.ZERO;
                }
                if (this.value == 1) {
                    return Type.ONE;
                }
                return Type.REAL_NUMBER;
            }

            @Override
            public int getPre() {
                return 20;
            }

            @Override
            public String getText() {
                if (this.isInt()) {
                    return Parser.split("\\.", this.value + "")[0];
                } else if (this.value == Double.POSITIVE_INFINITY) {
                    return "∞";
                } else if (this.value == Double.NEGATIVE_INFINITY) {
                    return "-∞";
                } else {
                    return value + ""; // might be adjusted better
                }
            }

            @Override
            public Numbers.R[] limit(Numbers.R a, Lside b) {
                return new Numbers.R[] { this, new Numbers.R(0) };
            }

            @Override
            public Symbolic.Expression[] getOperands() {
                throw new UnsupportedOperationException("getOperands not supported for R");
            }

            @Override
            public Symbolic.Expression getSimplified() {
                return this;
            }

            @Override
            public String getLatex() {

                if (this.value == Pos_Infinity.value) {
                    return "\\infty";
                }
                if (this.value == Neg_Infinity.value) {
                    return "-\\infty";
                }
                return this.getText();
            }

            @Override
            public Symbolic.Expression antiDerivative(Symbolic.Variable d) {
                return new Symbolic.Multiply(this, d);
            }

        }

    }

    public static class Symbolic {

        // Elementary
        static interface Expression {

            public Numbers.R evaluate(Numbers.R n);

            public Expression derivative();

            public Expression antiDerivative(Variable d);
            // Has to be continous

            public Type getType();

            public String getText();

            Type FTYPE = Type.GENERIC_EXP;

            public int PRECEDENCE = 10;

            public int getPre();

            public Numbers.R[] limit(Numbers.R a, Lside side);

            public Expression[] getOperands();

            public Expression getSimplified();

            public String getLatex();
        }

        static interface Operation {

            public Numbers.R thisOp(Numbers.R m, Numbers.R n);

        }
        // Might be moved to class Set

        public static class Equation {

            // All arguments including double will be removed later !!!
            Expression left;
            Expression right;

            Equation(Expression a, Expression b) {

                left = a;
                right = b;

            }

        }

        public static class Inequality {

            Expression left, right;
            String comparator;

            Inequality(Expression a, Expression b, String c) {
                left = a;
                right = b;
                comparator = c;
            }

        }

        static class Variable implements Expression {

            char character;
            double value;
            int index; // Should be map instead
            // Not stored as a real number, rather as double value

            public Numbers.R evaluate(Numbers.R n) {
                return n;
            }

            Variable(char character) {
                this.character = character;
                this.index = -1; // Not a numbers.R

            } // Without index

            Variable(char character, int index) {
                this.character = character;
                this.index = index;

            }

            public Expression derivative() {
                return new Numbers.R(1);
            } // dx/dx = 1;

            public Type getType() {
                return Type.VARIABLE;
            }

            public int getPre() {
                return 15;
            }

            @Override
            public String getText() {
                return "" + character;
            }

            @Override
            public Numbers.R[] limit(Numbers.R a, Lside b) {
                return new Numbers.R[] { this.evaluate(a), new Numbers.R(b.val) };
            }

            @Override
            public Expression antiDerivative(Variable d) {
                return new Divide(new Power(d, new Numbers.R(2)), new Numbers.R(2));
            }

            @Override
            public Expression[] getOperands() {
                return null;
            }

            @Override
            public Expression getSimplified() {
                return this;
            }

            @Override
            public String getLatex() {
                if (index == -1) {
                    return character + "";
                } else {
                    return character + "_{" + index + "}";
                }
            }

        }

        static class Function implements Expression {

            Expression argument, formula;
            Sets.Set domain;

            // Fix
            boolean injective, surjective, continuous, differentiable, inverse;
            String character;

            Function(Expression a, Expression b, String c) {
                argument = a;
                formula = b;
                character = c;
            }

            public Numbers.R evaluate(Numbers.R n) {

                return argument.evaluate(n); // OOPS!

            } // Doesn't exaclty work as intended since it has its own input

            @Override
            public Expression derivative() {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from
                                                                               // nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            } // Might be edited later

            @Override
            public Type getType() {

                if (formula.getType() == Type.REAL_NUMBER) {
                    return Type.CONSTANT_F;
                }

                return Type.GENERIC_FUNCTION;
            }

            @Override
            public String getText() {
                throw new UnsupportedOperationException("ƒ(x)"); // Generated from
                                                                 // nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }

            @Override
            public int getPre() {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from
                                                                               // nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }

            @Override
            public Numbers.R[] limit(Numbers.R a, Lside side) {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from
                                                                               // nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }

            @Override
            public Expression[] getOperands() {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from
                                                                               // nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }

            @Override
            public Expression getSimplified() {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from
                                                                               // nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }

            @Override
            public String getLatex() {
                return character + "f({" + argument.getLatex() + "})";
            }

            @Override
            public Expression antiDerivative(Variable d) {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from
                                                                               // nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }

        }

        // SYMBOLIC TRIGONOMETRY --------------------------------------------------
        static class SymTrigo {

            // Symbolic sin,cos,tan,cot,arcsin,arccos,arctan,arccot,sec,csc

            // static abstract class TrF extends UnaryFunction implements Expression{
            //
            // Numbers.R period; // Periodic function impl.
            //
            // public TrF(Expression a) {
            // super(a);
            // }
            //
            // }

            // SINE FUNCTION --------------------------------
            static class Sin extends UnaryFunction implements Expression {

                public Sin(Expression a) {
                    super(a);
                    this.name = "sin";
                }

                @Override
                public Numbers.R thisOp(Numbers.R n) {
                    return Mts.Trigonometric.sin(n);
                }

                @Override
                public Expression derivative() {
                    return new Mts.Symbolic.Multiply(new Mts.Symbolic.SymTrigo.Cos(argument), argument.derivative());
                }

                @Override
                public Type getType() {

                    return switch (this.argument.getType()) {

                        case REAL_NUMBER ->
                            Type.REAL_NUMBER;
                        case VARIABLE ->
                            Type.SIN;
                        case LINEAR ->
                            Type.SIN_LINEAR;
                        default ->
                            Type.SIN_COMPOSITE;

                    };

                }

                @Override
                public Numbers.R[] limit(Numbers.R a, Lside b) {

                    if (Double.isInfinite(argument.limit(a, b)[0].value)) {
                        return new Numbers.R[] { NAN, new Numbers.R(b.val) };
                    } // We might need squeeze theorem

                    Numbers.R newSideToBeSend = Arithmetic.sgn(
                            thisOp(
                                    Arithmetic.sum(argument.limit(a, b)[0], argument.limit(a, b)[1])));

                    return new Numbers.R[] { thisOp(argument.limit(a, b)[0]),
                            newSideToBeSend };
                }

                public Expression antiDerivative(Variable d) {

                    // ∫sinx dx = -cosx + C
                    if (this.getType() == Type.SIN) {

                        return new Multiply(new Numbers.R(-1), new Cos(d));

                    }

                    // To be completed
                    return null;

                }

                @Override
                public Expression getSimplified() {
                    return this;
                    // sin(-x) -> -sinx

                }
            }

            // ---------------------------------------- COSINE

            static class Cos extends UnaryFunction implements Expression {

                public Cos(Expression a) {
                    super(a);
                    this.name = "cos";
                }

                @Override
                public Numbers.R thisOp(Numbers.R n) {
                    return Mts.Trigonometric.cos(n);
                }

                @Override
                public Expression derivative() {
                    return new Multiply(MINUS_ONE,
                            new Mts.Symbolic.Multiply(new Mts.Symbolic.SymTrigo.Sin(argument), argument.derivative()));
                }

                @Override
                public Type getType() {

                    // Should be fixed
                    return switch (this.argument.getType()) {

                        case REAL_NUMBER ->
                            Type.REAL_NUMBER;
                        case VARIABLE ->
                            Type.SIN;
                        case LINEAR ->
                            Type.SIN_LINEAR;
                        default ->
                            Type.SIN_COMPOSITE;

                    };

                }

                @Override
                public Numbers.R[] limit(Numbers.R a, Lside b) {

                    if (Double.isInfinite(argument.limit(a, b)[0].value)) {
                        return new Numbers.R[] { NAN, new Numbers.R(b.val) };
                    } // We might need squeeze theorem

                    Numbers.R newSideToBeSend = Arithmetic.sgn(
                            thisOp(
                                    Arithmetic.sum(argument.limit(a, b)[0], argument.limit(a, b)[1])));

                    return new Numbers.R[] { thisOp(argument.limit(a, b)[0]),
                            newSideToBeSend };
                }

                public Expression antiDerivative(Variable d) {

                    // ∫cosx dx = sinx + C
                    if (this.getType() == Type.SIN) {

                        return new Sin(d);

                    }

                    // To be completed
                    return null;

                }

                @Override
                public Expression getSimplified() {
                    return this;

                }
            }

        }

        // OPERATIONS ----------------------------
        static class Power implements Expression, Operation {

            Expression base, exponent;

            Power(Expression a, Expression b) {
                base = a;
                exponent = b;
            }

            public Numbers.R evaluate(Numbers.R n) {
                return thisOp(base.evaluate(n), exponent.evaluate(n));
            }

            public Numbers.R thisOp(Numbers.R m, Numbers.R n) {
                return Arithmetic.power(m, n);
            }

            public Expression derivative() {
                // Not necessary...
                if (TypeClass.isReal_Number(this)) {
                    return ZERO;
                }

                if (TypeClass.isReal_Number(exponent)) {

                    Expression z = new Multiply(exponent, base.derivative());
                    Expression n = new Power(base, new Mts.Symbolic.Minus(exponent, new Mts.Numbers.R(1)));
                    return new Multiply(n, z);
                    // Correct order?

                } else if (TypeClass.isReal_Number(base)) {

                    Expression z = new Multiply(new Ln(base), exponent.derivative());
                    return new Multiply(this, z);
                    // might adjust a bit better
                }

                return null;

            }

            public Type getType() {

                // this seems like a bad idea at this point
                if (base.getType() == Type.VARIABLE && exponent.getType() == Type.REAL_NUMBER) {
                    return Type.RATIONAL_POWER; // x^a
                }
                if (base.getType() == Type.GENERIC_FUNCTION && exponent.getType() == Type.REAL_NUMBER) {
                    return Type.POWER_COMPOSITE; // f(x)^a
                }
                if (exponent.getType() == Type.VARIABLE && base.getType() == Type.REAL_NUMBER) {
                    return Type.EXPONENTIAL; // a^x

                } else if (base.getType() == Type.REAL_NUMBER && exponent.getType() == Type.REAL_NUMBER) {
                    return Type.REAL_NUMBER; // a^b
                }

                return Type.GENERIC_EXP;
                // return Type.POWER_F;

            }

            public String getText() {
                String stack = "";

                stack += base.getPre() < this.getPre() ? "(" + base.getText() + ")^" : base.getText() + "^";
                stack += exponent.getPre() < this.getPre() ? "(" + exponent.getText() + ")" : exponent.getText();

                return stack;
                // we're probably ignoring right asc.
            }

            @Override
            public String getLatex() {

                if (base.getLatex().length() > 2) {
                    // Doesn't work
                    return "(" + base.getLatex() + ")^{" + exponent.getLatex() + "}";
                } else {
                    return base.getLatex() + "^{" + exponent.getLatex() + "}";
                }
            }

            @Override
            public int getPre() {
                return 4;
            }

            public Expression getSimplified() {

                if (TypeClass.isZero(exponent)) {
                    return ONE;
                } // 0 ^ 0 = 1

                else if (TypeClass.isZero(base)) {
                    return ZERO;
                }

                if (TypeClass.isOne(exponent)) {
                    return base.getSimplified();
                } else {
                    return new Power(base.getSimplified(), exponent.getSimplified());
                }
            }

            @Override
            public Numbers.R[] limit(Numbers.R a, Lside b) {

                if (TypeClass.isZero(base.limit(a, b)[0])
                        && TypeClass.isZero(exponent.limit(a, b)[0]) & TypeClass.isOne(base.limit(a, b)[1])) {
                    System.out.println("0 ^ 0");
                    Expression lHospital = new Power(symE, new Divide(exponent, new Divide(ONE, new Ln(base))));
                    return lHospital.limit(a, b);

                }

                if (TypeClass.isOne(base.limit(a, b)[0]) && Double.isInfinite(exponent.limit(a, b)[0].value)) {
                    // 1 ^ inf
                    System.out.println("1 ^ ∞");
                    Expression lHospital = new Power(symE, new Divide(new Ln(base), new Divide(ONE, exponent)));
                    return lHospital.limit(a, b);

                }

                if (TypeClass.isZero(exponent.limit(a, b)[0]) && Double.isInfinite(base.limit(a, b)[0].value)) {
                    // inf ^ 0
                    System.out.println("∞ ^ 0");
                    Expression lHospital = new Power(symE, new Divide(exponent, new Divide(ONE, new Ln(base))));
                    return lHospital.limit(a, b);
                }

                // ONLY IF EXISTS

                Numbers.R newSideToBeSend = Arithmetic.sgn(
                        Arithmetic.power(
                                Arithmetic.sum(base.limit(a, b)[0], base.limit(a, b)[1]),
                                Arithmetic.sum(exponent.limit(a, b)[0], exponent.limit(a, b)[1])));

                return new Numbers.R[] { Arithmetic.power(base.limit(a, b)[0], exponent.limit(a, b)[0]),
                        newSideToBeSend };

                // we still take the limit of exponent anyway
                // Keep the possibility of complex values

            }

            public Expression antiDerivative(Variable d) {

                // ∫x^n dx = x^(n+1)/n+1 + C ; n ≠ -1
                // ∫1/x dx = ln|x| + C
                // ∫e^x dx = e^x + C
                // ∫a^x dx = a^x / ln(a) + C
                switch (this.getType()) {
                    case RATIONAL_POWER -> {
                        return new Divide(
                                new Power(d, new Add(this.base, new Numbers.R(1))),
                                new Add(this.base, new Numbers.R(1)));
                    }

                    case EXP -> {
                        return this;
                    }
                    case EXPONENTIAL -> {
                        return new Divide(new Power(this.base, d), new Ln(this.base));
                    }
                    default -> {
                    }
                }
                System.out.println("Some null here not to be forgotten");
                return null;

            }

            @Override
            public Expression[] getOperands() {
                return new Expression[] { base, exponent };
            }

        }

        static abstract class UnaryFunction implements Expression {

            Expression argument;
            String name = "";

            UnaryFunction(Expression a) {
                argument = a;
            }

            public abstract Numbers.R thisOp(Numbers.R n);

            public Numbers.R evaluate(Numbers.R n) {
                return thisOp(argument.evaluate(n));
            }

            public int getPre() {
                return 10;
            }

            @Override
            public String getText() {
                return name + "(" + argument.getText() + ")";
            }

            @Override
            public String getLatex() {
                return "\\" + name + "(" + argument.getLatex() + ")";
            }

            @Override
            public Expression[] getOperands() {
                return new Expression[] { argument };
            }

            @Override
            public Expression getSimplified() {
                return this;
            }

        }

        static class Absolute extends UnaryFunction implements Expression {

            public Absolute(Expression a) {

                super(a);
            }

            @Override
            public Numbers.R thisOp(Numbers.R n) {
                return Arithmetic.abs(n);
            }

            @Override
            public Expression derivative() {
                return new Multiply(new Sgn(argument), argument.derivative());
            }

            public Type getType() {
                return Type.ABS;
            }

            @Override
            public String getText() {
                return "|" + argument.getText() + "|";
            }

            @Override
            public String getLatex() {
                return "|" + argument.getLatex() + "|";
            }

            @Override
            public Numbers.R[] limit(Numbers.R a, Lside b) {

                Numbers.R newSideToBeSend = Arithmetic.sgn(
                        thisOp(
                                Arithmetic.sum(argument.limit(a, b)[0], argument.limit(a, b)[1])

                        ));

                return new Numbers.R[] { thisOp(argument.limit(a, b)[0]),
                        newSideToBeSend };

            }

            @Override
            public Expression getSimplified() {
                if (TypeClass.isReal_Number(argument)) {
                    return thisOp(argument.evaluate(NAN));
                }
                return this;
            }

            @Override
            public Expression antiDerivative(Variable d) {
                throw new UnsupportedOperationException("Not supported yet.");
            }

        }

        static class Sgn extends UnaryFunction implements Expression {

            public Sgn(Expression a) {
                super(a);
                name = "sgn";
            }

            @Override
            public Numbers.R thisOp(Numbers.R n) {
                return Arithmetic.sgn(n);
            }

            @Override
            public Expression derivative() {
                return new Numbers.R(0);
                // also includes 0
            }

            @Override
            public Type getType() {
                return Type.SIGN_F;
            }

            @Override
            public Numbers.R[] limit(Numbers.R a, Lside b) {
                // Sgn(x) is continous at everywhere except sgn 0;
                Numbers.R newSideToBeSend = Arithmetic.sgn(
                        thisOp(
                                Arithmetic.sum(argument.limit(a, b)[0], argument.limit(a, b)[1])));

                if (argument.limit(a, b)[1].value == 1) {
                    return new Numbers.R[] { ONE, ONE };
                }

                if (argument.limit(a, b)[1].value == -1) {
                    return new Numbers.R[] { MINUS_ONE, ONE };
                }

                return new Numbers.R[] { thisOp(argument.limit(a, b)[0]),
                        newSideToBeSend };

            }

            @Override
            public Expression antiDerivative(Variable d) {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from
                                                                               // nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }

        }

        static class Log implements Expression {

            Expression base, argument;

            Log(Expression a, Expression b) {
                base = a;
                argument = b;
            }

            public Numbers.R thisOp(Numbers.R m, Numbers.R n) {
                return Arithmetic.log(m, n);
            }

            @Override
            public Numbers.R evaluate(Numbers.R n) {
                return thisOp(base.evaluate(n), argument.evaluate(n));
            }

            @Override
            public Expression derivative() {
                return new Multiply(new Divide(argument.derivative(), argument), new Ln(base));
                // Probably wrong
            }

            @Override
            public Type getType() {

                if (argument.getType().equals(Type.REAL_NUMBER) && base.getType().equals(Type.POLYNOMIAL_F)) {
                    return Type.RATIONAL_POWER;
                }

                return Type.LOG;
            }

            @Override
            public String getText() {
                return "log_(" + base.getText() + ")(" + argument.getText() + ")";
            }

            @Override
            public int getPre() {
                return 10;
            }

            @Override
            public Expression getSimplified() {

                if (TypeClass.isReal_Number(argument)) {
                    return argument.evaluate(new Numbers.R(0));
                    // Actually incorrect
                }

                if (TypeClass.isZero(argument)) {
                    return new Numbers.R("log at 0");
                }

                if (TypeClass.isOne(argument)) {
                    return new Numbers.R(0);
                }

                return this;

            }

            @Override
            public Numbers.R[] limit(Numbers.R a, Lside b) {
                if (argument.limit(a, b)[0].value == 0 && b.val == 1) {
                    return new Numbers.R[] { Neg_Infinity, new Numbers.R(-1) };
                }
                // THIS IS COMPLETELY WRONG
                // Base has to be a whole number
                Numbers.R newSideToBeSend = Arithmetic.sgn(
                        thisOp(base.evaluate(NAN),
                                Arithmetic.sum(argument.limit(a, b)[0], argument.limit(a, b)[1])));

                return new Numbers.R[] { thisOp(base.limit(a, b)[0], argument.limit(a, b)[0]), newSideToBeSend };

            }

            @Override
            public Expression[] getOperands() {
                return new Expression[] { base, argument };
            }

            @Override
            public String getLatex() {
                return "\\log_{" + base.getLatex() + "}" + "(" + argument.getLatex() + ")";
            }

            @Override
            public Expression antiDerivative(Variable d) {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from
                                                                               // nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }

        }

        static class Ln extends Log implements Expression {

            public Ln(Expression a) {

                super(new Mts.Numbers.R(Mts.E), a);
                base = new Mts.Numbers.R(Mts.E);

            }

            public Expression derivative() {
                return new Divide(argument.derivative(), argument);
            }

            public Type getType() {
                return Type.LN;
            }

            public String getText() {
                return "ln(" + argument.getText() + ")";
            }

            @Override
            public int getPre() {
                return 10;
            }

            @Override
            public String getLatex() {
                return "\\ln (" + argument.getLatex() + ")";
            }

        }

        static class Add implements Expression, Operation {
            // might combine into one abstract class with minus

            Expression term_1, term_2;
            private Numbers.R Identity = ZERO;

            String symbol = "+";

            Add(Expression a, Expression b) {
                term_1 = a;
                term_2 = b;
            }

            @Override
            public Numbers.R evaluate(Numbers.R n) {
                return thisOp(term_1.evaluate(n), term_2.evaluate(n));
            }

            @Override
            public Numbers.R thisOp(Numbers.R n, Numbers.R m) {
                return Arithmetic.sum(n, m);
            }

            @Override
            public int getPre() {
                return 1;
            }

            @Override
            public Expression getSimplified() {

                // Checks by TypeClass
                if (TypeClass.isZero(this)) {
                    return ZERO;
                } else if (TypeClass.isZero(term_1)) {
                    return term_2.getSimplified();
                } else if (TypeClass.isZero(term_2)) {
                    return term_1.getSimplified();
                }

                if (TypeClass.isReal_Number(this)) {
                    return thisOp(term_1.evaluate(new Numbers.R(Double.NaN)),
                            term_2.evaluate(new Numbers.R(Double.NaN)));
                    // We use NAN to evaluate in case this is not an actually a real number.
                }

                if (TypeClass.isReal_Number(term_1) && term_2 instanceof Add) {
                    // associativity!
                } // COMBINE LIKE TERMS

                return new Add(term_1.getSimplified(), term_2.getSimplified());
            }

            @Override
            public Type getType() {
                // Very bad approach

                if (term_1.getType() == Type.SUM_OF_POWERS) {
                    // TERM 1 IS A SUM_OF_POWERS
                    return Type.SUM_OF_POWERS;
                }

                if (term_1.getType() == Type.REAL_NUMBER) {
                    // TERM 1 IS A REAL NUMBER

                    switch (term_2.getType()) {

                        case REAL_NUMBER -> {

                            if (this.evaluate(new Numbers.R(Double.NaN)).value == 0) {
                                return Type.ZERO;
                            }

                            if (this.evaluate(new Numbers.R(Double.NaN)).value == 1) {
                                return Type.ONE;
                            } else {
                                return Type.REAL_NUMBER;
                            }

                        }

                        case VARIABLE -> {
                            return Type.A_PLUS_X;
                        }
                        case RATIONAL_POWER -> {
                            return Type.A_PLUS_XB;
                        }
                        case POWER -> {
                            return Type.A_PLUS_CXB;
                        }

                    }

                }

                if (term_1.getType() == Type.VARIABLE) {
                    // TERM 1 IS A VARIABLE

                    switch (term_2.getType()) {

                        case REAL_NUMBER -> {
                            return Type.A_PLUS_X;
                        }
                        case VARIABLE -> {
                            return Type.AX;
                        }
                        case RATIONAL_POWER, POWER -> {
                            return Type.SUM_OF_POWERS;
                        }

                    }

                }

                if (term_1.getType() == Type.RATIONAL_POWER) {
                    // TERM 1 IS A VARIABLE

                    switch (term_2.getType()) {

                        case REAL_NUMBER -> {
                            return Type.A_PLUS_XB;
                        }
                        case VARIABLE -> {
                            return Type.SUM_OF_POWERS;
                        }
                        case RATIONAL_POWER, POWER -> {
                            // What if the exponents are same power?
                            return Type.SUM_OF_POWERS;
                        }

                    }

                }

                if (term_1.getType() == Type.POWER) {
                    // TERM 1 IS A VARIABLE

                    switch (term_2.getType()) {

                        case REAL_NUMBER -> {
                            return Type.A_PLUS_CXB;
                        }
                        case VARIABLE -> {
                            return Type.SUM_OF_POWERS;
                        }
                        case RATIONAL_POWER, POWER -> {
                            // What if the exponents are same power?
                            return Type.SUM_OF_POWERS;
                        }

                    }

                }

                return Type.GENERIC_FUNCTION;

                // should add more
            }

            @Override
            public String getText() {

                String stack = "";
                stack += term_1.getPre() < this.getPre() ? "(" + term_1.getText() + ")" + symbol
                        : term_1.getText() + symbol;
                stack += term_2.getPre() < this.getPre() ? "(" + term_2.getText() + ")" : term_2.getText();
                // Probably redundant but we're gonna stay this here for a moment
                return stack;
            }

            @Override
            public String getLatex() {
                return term_1.getLatex() + symbol + term_2.getLatex();
            }

            @Override
            public Numbers.R[] limit(Numbers.R a, Lside b) {

                // Check if ∞ - ∞ -------------------------------------------
                if (term_1.limit(a, b)[0].value == Double.POSITIVE_INFINITY
                        && term_2.limit(a, b)[0].value == Double.NEGATIVE_INFINITY) {
                    System.out.println("∞ - ∞");
                    Expression lHospital = new Divide(
                            new Minus(new Divide(new Numbers.R(1), term_1),
                                    new Divide(new Numbers.R(1), term_2)),
                            new Divide(new Numbers.R(1), new Multiply(term_1, term_2))
                    // CONVERT IT TO 0/0 FORM
                    );

                    return lHospital.limit(a, b);
                }

                // lim_x -> a [f(x)+g(x)] = lim_x -> a [f(x)] + lim_x -> a [g(x)] ---

                Numbers.R newSideToBeSend = Arithmetic.sgn(
                        thisOp(

                                Arithmetic.sum(term_1.limit(a, b)[0], term_1.limit(a, b)[1]),
                                Arithmetic.sum(term_2.limit(a, b)[0], term_2.limit(a, b)[1])));

                return new Numbers.R[] { thisOp(term_1.limit(a, b)[0], term_2.limit(a, b)[0]), newSideToBeSend };

            }

            @Override
            public Expression derivative() {
                return new Add(term_1.derivative(), term_2.derivative());
                // Both terms have to be continuous!!!!!!!!! Fix it!!!!!!!!!!
            }

            @Override
            public Expression antiDerivative(Variable d) {

                // ∫f(x)+g(x)dx = ∫f(x)dx + ∫g(x)dx + C
                return new Add(term_1.antiDerivative(d), term_2.antiDerivative(d));

            }

            @Override
            public Expression[] getOperands() {

                Expression[] op = new Expression[2];
                op[0] = term_1;
                op[1] = term_2;

                return op;
            }

        }

        static class Minus extends Add implements Expression, Operation {

            public Minus(Expression a, Expression b) {
                super(a, b);
                this.symbol = "-";
            }

            @Override
            public Numbers.R thisOp(Numbers.R m, Numbers.R n) {
                return Arithmetic.minus(m, n);
            }

            public Expression derivative() {
                return new Minus(term_1.derivative(), term_2.derivative());
            }

            public Type getType() {

                if (term_1.getType().equals(Type.REAL_NUMBER) && term_2.getType().equals(Type.REAL_NUMBER)) {

                    if (NumericInequality.equalto(term_1.evaluate(NAN), term_2.evaluate(NAN))) {
                        return Type.ZERO;
                    } else {
                        return Type.REAL_NUMBER;
                    }

                } else {
                    return Type.GENERIC_FUNCTION;
                }
                // should add more

            }

            @Override
            public Numbers.R[] limit(Numbers.R a, Lside b) {

                Numbers.R newSideToBeSend = Arithmetic.sgn(
                        Arithmetic.minus(
                                Arithmetic.sum(term_1.limit(a, b)[0], term_1.limit(a, b)[1]),
                                Arithmetic.sum(term_2.limit(a, b)[0], term_2.limit(a, b)[1])));

                return new Numbers.R[] { Arithmetic.minus(term_1.limit(a, b)[0], term_2.limit(a, b)[0]),
                        newSideToBeSend };

            }

            @Override
            public Expression getSimplified() {

                if (TypeClass.isZero(this)) {
                    return new Numbers.R(0);
                } else if (TypeClass.isZero(term_1)) {
                    return new Multiply(new Numbers.R(-1), term_2.getSimplified());
                } else if (TypeClass.isZero(term_2)) {
                    return term_1.getSimplified();
                }

                if (TypeClass.isReal_Number(this)) {
                    return Arithmetic.minus(term_1.evaluate(new Numbers.R(Double.NaN)),
                            term_2.evaluate(new Numbers.R(Double.NaN)));
                }

                return new Minus(term_1.getSimplified(), term_2.getSimplified());
            }

            @Override
            public Expression antiDerivative(Variable d) {

                // ∫f(x)-g(x)dx = ∫f(x)dx - ∫g(x)dx + C
                return new Minus(term_1.antiDerivative(d), term_2.antiDerivative(d));

            }

        }

        static class Multiply implements Expression, Operation {

            private Numbers.R Identity = ONE;
            Expression factor_1, factor_2;
            public int precedence = 3;

            Multiply(Expression a, Expression b) {
                factor_1 = a;
                factor_2 = b;

            }

            public Numbers.R thisOp(Numbers.R m, Numbers.R n) {
                return Arithmetic.product(m, n);

            }

            @Override
            public Numbers.R evaluate(Numbers.R n) {
                return thisOp(factor_1.evaluate(n), factor_2.evaluate(n));

            }

            @Override
            public Type getType() {
                // Very bad approach

                if (factor_1.getType() == Type.REAL_NUMBER) {
                    // TERM 1 IS A REAL NUMBER

                    switch (factor_2.getType()) {

                        case REAL_NUMBER -> {

                            if (this.evaluate(new Numbers.R(Double.NaN)).value == 0) {
                                return Type.ZERO;
                            }

                            if (this.evaluate(new Numbers.R(Double.NaN)).value == 1) {
                                return Type.ONE;
                            } else {
                                return Type.REAL_NUMBER;
                            }

                        }

                        case VARIABLE -> {
                            return Type.AX;
                        }
                        case RATIONAL_POWER, POWER -> {
                            return Type.POWER;
                        }

                    }

                }

                if (factor_1.getType() == Type.VARIABLE) {
                    // TERM 1 IS A VARIABLE

                    switch (factor_2.getType()) {

                        case REAL_NUMBER -> {
                            return Type.AX;
                        }
                        case VARIABLE -> {
                            return Type.RATIONAL_POWER;
                        }
                        case RATIONAL_POWER -> {
                            return Type.RATIONAL_POWER;
                            // Might check some cases
                        }
                        case POWER -> {
                            return Type.POWER;
                        }

                    }

                }

                if (factor_1.getType() == Type.RATIONAL_POWER) {
                    // TERM 1 IS A VARIABLE

                    switch (factor_2.getType()) {

                        case REAL_NUMBER -> {
                            return Type.A_PLUS_XB;
                        }
                        case VARIABLE -> {
                            return Type.RATIONAL_POWER;
                        }
                        case POWER -> {
                            return Type.POWER;
                        }
                        case RATIONAL_POWER -> {
                            return Type.RATIONAL_POWER;
                        }

                    }

                }

                if (factor_1.getType() == Type.POWER) {
                    // TERM 1 IS A VARIABLE

                    switch (factor_2.getType()) {

                        case REAL_NUMBER -> {
                            return Type.POWER;
                        }
                        case VARIABLE -> {
                            return Type.POWER;
                        }
                        case RATIONAL_POWER, POWER -> {
                            // What if the exponents are same power?
                            return Type.POWER;
                        }

                    }

                }

                return Type.GENERIC_FUNCTION;

                // should add more
            }

            @Override
            public Expression derivative() {
                return new Add(new Multiply(factor_1.derivative(), factor_2),
                        new Multiply(factor_2.derivative(), factor_1));
            }

            @Override
            public String getText() {

                String stack = "";
                stack += factor_1.getPre() < this.getPre() ? "(" + factor_1.getText() + ")⋅" : factor_1.getText() + "⋅";
                stack += factor_2.getPre() < this.getPre() ? "(" + factor_2.getText() + ")" : factor_2.getText();
                return stack;
            }

            @Override
            public String getLatex() {

                String stack = "";
                stack += factor_1.getPre() < this.getPre() ? "(" + factor_1.getLatex() + ")\\cdot "
                        : factor_1.getLatex() + "\\cdot ";
                stack += factor_2.getPre() < this.getPre() ? "(" + factor_2.getLatex() + ")" : factor_2.getLatex();
                return stack;
                // Implied multiplication is missing

            }

            public Expression getSimplified() {

                if (TypeClass.isZero(factor_1) || TypeClass.isZero(factor_1)) {
                    return new Numbers.R(0);
                }

                if (TypeClass.isOne(factor_1) && TypeClass.isOne(factor_2)) {
                    return ONE;
                }

                if (TypeClass.isOne(factor_1)) {
                    return factor_2;

                } else if (TypeClass.isOne(factor_2)) {
                    return factor_1;
                }

                return new Multiply(factor_1.getSimplified(), factor_2.getSimplified());

            }

            @Override
            public int getPre() {
                return 2;
            }

            @Override
            public Numbers.R[] limit(Numbers.R a, Lside b) {

                // Only rewrites the limit, doesn't evaluate them
                if (factor_1.limit(a, b)[0].value == 0 && Double.isInfinite(factor_2.limit(a, b)[0].value)
                        || factor_2.limit(a, b)[0].value == 0 && Double.isInfinite(factor_1.limit(a, b)[0].value)) {

                    Expression lHospital = new Divide(factor_1, new Divide(new Numbers.R(1), factor_2));
                    System.out.println("0 * ∞");
                    return lHospital.limit(a, b);
                    // (0 * ∞)
                    // (∞ * 0)
                }

                // ONLY IF EXISTS
                Numbers.R newSideToBeSend = Arithmetic.sgn(
                        thisOp(
                                Arithmetic.sum(factor_1.limit(a, b)[0], factor_1.limit(a, b)[1]),
                                Arithmetic.sum(factor_2.limit(a, b)[0], factor_2.limit(a, b)[1])));

                return new Numbers.R[] { thisOp(factor_1.limit(a, b)[0], factor_2.limit(a, b)[0]),
                        newSideToBeSend };

            }

            public Expression antiDerivative(Variable d) {

                if (TypeClass.isReal_Number(this)) {

                    // ∫kdx = kxdx + C
                    return new Multiply(new Multiply(factor_1, factor_2), d);

                } else if (TypeClass.isReal_Number(factor_1)) {

                    // ∫k*f(x)dx = k*∫f(x)dx + C
                    return new Multiply(factor_1, factor_2.antiDerivative(d));

                } else if (TypeClass.isReal_Number(factor_2)) {

                    // ∫f(x)dx * k = ∫f(x)*k dx + C
                    return new Multiply(factor_2, factor_1.antiDerivative(d));

                }

                if (TypeClass.isSec_xTan_x(this)) {

                    return null;

                }
                // Integration by parts
                return null;

            }

            @Override
            public Expression[] getOperands() {
                return new Expression[] { factor_1, factor_2 };
            }

        }

        static class Divide implements Expression, Operation {

            Numbers.R Identity = ONE;
            Expression numerator, denominator;
            int precedence = 3;

            Divide(Expression a, Expression b) {
                numerator = a;
                denominator = b;
            }

            public Numbers.R thisOp(Numbers.R m, Numbers.R n) {
                return Arithmetic.divide(m, n);
            }

            public Numbers.R evaluate(Numbers.R n) {
                return thisOp(numerator.evaluate(n), denominator.evaluate(n));
            }

            public Expression derivative() {
                // Force applied Quotient Rule
                Expression q1 = new Multiply(numerator.derivative(), denominator);
                Expression q2 = new Multiply(numerator, denominator.derivative());
                Expression q4 = new Minus(q1, q2);
                Expression q3 = new Power(denominator, new Mts.Numbers.R(2));
                return new Divide(q4, q3);

            }

            @Override
            public Type getType() {

                Type numType = this.numerator.getType();
                Type denType = this.denominator.getType();

                // a/b is element of R
                if (numType == Type.REAL_NUMBER && denType == Type.REAL_NUMBER) {
                    return Type.REAL_NUMBER;
                }

                if (numType == Type.POWER && denType == Type.POWER) // what if powers are equal?
                // what if constants are equal?
                {
                    return Type.POWER;
                }

                // a/x is reciprocal function
                if (numType == Type.REAL_NUMBER && denType == Type.VARIABLE) {
                    return Type.RECIPROCAL;
                }

                if (numType == Type.REAL_NUMBER && denType == Type.POWER) {
                    return Type.POWER;
                }

                if (numType == Type.POWER && denType == Type.POWER) {
                    return Type.POWER;
                }

                if (numType == Type.POWER && denType == Type.POWER) {
                    return Type.POWER;
                }

                return null;

            }

            @Override
            public String getText() {

                String stack = "";
                stack += numerator.getPre() < this.getPre() ? "(" + numerator.getText() + ")/"
                        : numerator.getText() + "/";
                stack += denominator.getPre() < this.getPre() ? "(" + denominator.getText() + ")"
                        : denominator.getText();
                return stack;

            }

            public String getLatex() {

                return "\\frac{" + numerator.getLatex() + "}{" + denominator.getLatex() + "}";

            }

            @Override
            public int getPre() {
                return 3;
            }

            @Override
            public Numbers.R[] limit(Numbers.R a, Lside b) {

                if ((NumericInequality.equalto(numerator.limit(a, b)[0], new Numbers.R(0))
                        && NumericInequality.equalto(denominator.limit(a, b)[0], new Numbers.R(0)))
                        || (Double.isInfinite(numerator.limit(a, b)[0].value)
                                && Double.isInfinite(numerator.limit(a, b)[0].value))) {
                    System.out.println("0 / 0, ∞ / ∞");
                    // 0 / 0
                    // inf / inf
                    Expression lHospital = new Divide(numerator.derivative(), denominator.derivative());

                    return lHospital.limit(a, b);
                    // Might go in forever loop

                }

                if (TypeClass.isReal_Number(numerator.limit(a, b)[0])
                        && NumericInequality.equalto(denominator.limit(a, b)[0], new Numbers.R(0))) {
                    // lim f(x) / 0 ; f(x) != 0

                    // double newSideToBeSend = ((numerator.limit(a, b)[0].value + b.val) /
                    // denominator.limit(a, b)[0].value - (numerator.limit(a, b)[0].value /
                    // denominator.limit(a, b)[0].value));
                    if (denominator.limit(a, b)[1].value == 1) {
                        return new Numbers.R[] { Pos_Infinity, ONE };
                    }

                    if (denominator.limit(a, b)[1].value == -1) {
                        return new Numbers.R[] { Neg_Infinity, MINUS_ONE };
                    } else {
                        return null; // Should be fixed;
                    }

                }

                // ONLY IF EXISTS
                Numbers.R newSideToBeSend = Arithmetic.sgn(
                        Arithmetic.divide(
                                Arithmetic.sum(numerator.limit(a, b)[0], numerator.limit(a, b)[1]),
                                Arithmetic.sum(denominator.limit(a, b)[0], denominator.limit(a, b)[1])));

                return new Numbers.R[] { Arithmetic.divide(numerator.limit(a, b)[0], denominator.limit(a, b)[0]),
                        newSideToBeSend };

            }

            public Expression antiDerivative(Variable d) {

                // ∫1/xdx = lnx + C
                if (TypeClass.isReal_Number(this)) {

                    return new Multiply(new Divide(numerator, denominator), d);

                }

                if (TypeClass.isPower(numerator) && TypeClass.isPower(denominator)) {

                    return new Multiply(this.evaluate(new Numbers.R(1)),
                            numerator.getSimplified().getOperands()[1].getOperands()[1]);
                    // Uhhhhhhhh
                }

                return null;

            }

            @Override
            public Expression[] getOperands() {
                return new Expression[] { numerator, denominator };
            }

            @Override
            public Expression getSimplified() {

                if (TypeClass.isOne(denominator)) {
                    return numerator;
                }

                if (TypeClass.isOne(this)) {
                    return ONE;
                }

                if (TypeClass.isZero(numerator)) {
                    return ZERO; // Might remove some roots
                }

                return this;

            }

        }

    }

    static class NumericInequality {

        // A mess...
        static double max(double a, double b) {
            if (Mts.NumericInequality.greaterThan(a, b)) {
                return a;
            } else {
                return b;
            }
        }

        static double min(double a, double b) {
            if (Mts.NumericInequality.lessThan(a, b)) {
                return a;
            } else {
                return b;
            }
        }

        static boolean greaterThan(double a, double b) {

            return a > b;

        }

        static boolean lessThan(double a, double b) {

            return a < b;

        }

        static boolean lessThan(Numbers.R a, Numbers.R b) {

            return a.value < b.value;

        }

        static boolean greaterThan(Numbers.R a, Numbers.R b) {

            return a.value < b.value;

        }

        static boolean equalto(Numbers.R a, Numbers.R b) {
            return a.value == b.value;
        }

        static boolean equalto(Numbers.R a, double b) {
            return a.value == b;
        }
    }

    class Trigonometric {

        static double principalAngle() {
            return 0;
        }

        static Numbers.R sin(Numbers.R a) {
            return new Mts.Numbers.R(sin(a.value));
        }

        static double sin(double a) {

            return Math.sin(a);

            // double returnval = 0;
            // if (a == Mts.PI) {
            // return 0;
            // }
            // if (a < Mts.Arithmetic.divide(Mts.PI, 2) && a > 0) {
            //
            // for (int j = 1; j < 15; j += 2) {
            //
            // returnval = returnval + Mts.Arithmetic.power(-1, (j + 1) / 2 + 1) *
            // Mts.Arithmetic.divide(Mts.Arithmetic.power(a, j),
            // Mts.Arithmetic.factorial(j));
            // }
            //
            // return returnval;
            // } else if (a > Mts.Arithmetic.divide(Mts.PI, 2)) {
            //
            // for (; a < Mts.Arithmetic.divide(Mts.PI, 2); a -= PI / 2.0) {
            // }
            //
            // return a;
            //
            // } else {
            //
            // for (; a > 0; a += PI / 2.0) {
            // }
            //
            // return a;
            //
            // }
        }

        static Numbers.R arcsin(Numbers.R a) {
            return new Mts.Numbers.R(arcsin(a.value));
        }

        static double arcsin(Double a) {

            if (Arithmetic.abs(a) <= 1) {

                double returnValue = 0;

                for (int i = 0; i < 7; i++) {

                    returnValue = returnValue + Arithmetic.divide(
                            Arithmetic.factorial(2 * i) * Arithmetic.power(a, 2 * i + 1),
                            Arithmetic.power(4, i) * Arithmetic.power(Arithmetic.factorial(i), 2) * (2 * i + 1));

                }

                return returnValue;

            } else {
                return Double.NaN;
            }

        }

        static double sec(double a) {
            return Arithmetic.divide(1, cos(a));
        }

        static double cosec(double a) {
            return Arithmetic.divide(1, sin(a));
        }

        static double cos(double a) {
            return sin(Arithmetic.divide(PI, 2) - a);
        }

        static Numbers.R cos(Numbers.R a) {
            return new Numbers.R(cos(a.value));
            // Should be fixed alltogether
        }

        static Numbers.R tan(Numbers.R a) {
            return new Numbers.R(tan(a.value));
            // Should be fixed alltogether
        }

        static double tan(double a) {
            return Arithmetic.divide(sin(a), cos(a));
        }

        static double cot(double a) {
            return Arithmetic.divide(1, tan(a));
        }

    }

}

class TypeClass {

    public static boolean isZero(Mts.Symbolic.Expression a) {

        return a.getType() == Mts.Type.ZERO;
    }

    public static boolean isOne(Mts.Symbolic.Expression a) {

        return a.getType() == Mts.Type.ONE;
    }

    public static boolean isReal_Number(Mts.Symbolic.Expression a) {

        return a.getType() == Mts.Type.REAL_NUMBER
                || a.getType() == Mts.Type.INTEGER
                || a.getType() == Mts.Type.ZERO
                || // a.getType() == Mts.Type.CONSTANT ||
                a.getType() == Mts.Type.ONE;
    }

    public static boolean isVariable(Mts.Symbolic.Expression a) {

        return a.getType() == Mts.Type.VARIABLE;
    }

    public static boolean isLinear(Mts.Symbolic.Expression a) {

        return a.getType() == Mts.Type.LINEAR;
    }

    public static boolean isRationalPower(Mts.Symbolic.Expression a) {

        return a.getType() == Mts.Type.VARIABLE
                || a.getType() == Mts.Type.RECIPROCAL
                || a.getType() == Mts.Type.RATIONAL_POWER;
    }

    public static boolean isExp(Mts.Symbolic.Expression a) {

        return a.getType() == Mts.Type.EXP;
    }

    public static boolean isExponential(Mts.Symbolic.Expression a) {

        return a.getType() == Mts.Type.EXP
                || a.getType() == Mts.Type.EXPONENTIAL;
    }

    public static boolean isLn(Mts.Symbolic.Expression a) {

        return a.getType() == Mts.Type.LN;
    }

    public static boolean isLogarithm(Mts.Symbolic.Expression a) {

        return a.getType() == Mts.Type.LOG
                || a.getType() == Mts.Type.LN;
    }

    public static boolean isPower(Mts.Symbolic.Expression a) {

        return a.getType() == Mts.Type.VARIABLE
                || a.getType() == Mts.Type.RATIONAL_POWER
                || a.getType() == Mts.Type.POWER;
    }

    public static boolean isReciprocal(Mts.Symbolic.Expression a) {

        return a.getType() == Mts.Type.RECIPROCAL;
    }

    public static boolean isSumofPower(Mts.Symbolic.Expression a) {

        return a.getType() == Mts.Type.VARIABLE
                || a.getType() == Mts.Type.RATIONAL_POWER
                || a.getType() == Mts.Type.POWER
                || a.getType() == Mts.Type.SUM_OF_POWERS;
    }

    public static boolean isSec_xTan_x(Mts.Symbolic.Multiply a) {

        return a.factor_1.getType() == Mts.Type.SECX && a.factor_1.getType() == Mts.Type.TANX
                || a.factor_1.getType() == Mts.Type.TANX && a.factor_1.getType() == Mts.Type.SECX;
    }

}

class Main {

    static Mts.Symbolic.Variable x = new Mts.Symbolic.Variable('x');

    public static void main(String[] args) throws Exception {

        x.value = 0;
        // System.out.println(x.evaluate(null).getText());

        Scanner input = new Scanner(System.in);

        while (true) {

            double valx = Double.NEGATIVE_INFINITY;

            String input2 = input.nextLine();

            Mts.Symbolic.Expression inputKlsk = Parser.parse(
                    input2);

            System.out.println("This > " + inputKlsk.getText());
            System.out.println("LATEX > " + inputKlsk.getLatex());
            System.out.println("Eval> " + inputKlsk.evaluate(new Mts.Numbers.R(valx)).getLatex());

            System.out.println("derivative> " + inputKlsk.derivative().getText());
            System.out.println("derivLATEX> " + inputKlsk.derivative().getLatex());

            System.out.println("ThisSimpLatex> " + inputKlsk.getSimplified().getLatex());
            System.out.println("DerivSimpLatex> " + inputKlsk.getSimplified().derivative().getSimplified().getLatex());

            System.out.println("Left Limit > " + inputKlsk.limit(new Mts.Numbers.R(valx), Mts.Lside.LEFT)[0].getText());
            System.out
                    .println("Right Limit > " + inputKlsk.limit(new Mts.Numbers.R(valx), Mts.Lside.RIGHT)[0].getText());

            System.out.println("derivativeEval> " + inputKlsk.derivative().evaluate(new Mts.Numbers.R(valx)).getText());

            System.out.println(inputKlsk.getType());
            System.out.println("----------------------");
        }

    }

}
