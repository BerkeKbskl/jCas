
package javaapplication4;

public class Arithmetic {

    // Numeric Computation
    static double floor(double a) {
        return Math.floor(a);
    }

    static Mts.Numbers.R floor(Mts.Numbers.R a) {
        return new Mts.Numbers.R(Math.floor(a.value));
    }

    static double ceil(double a) {
        return Math.ceil(a);
    }

    static Mts.Numbers.R ceil(Mts.Numbers.R a) {
        return new Mts.Numbers.R(Math.ceil(a.value));
    }

    static Mts.Numbers.R sgn(Mts.Numbers.R a) {
        if (a.value == 0) {
            return new Mts.Numbers.R(0);
        } else if (a.value > 0) {
            return new Mts.Numbers.R(1);
        } else {
            return new Mts.Numbers.R(-1);
        }
    }

    static double factorial(double a) {
        if (a < 0) {
            return 0;
        } else if (a == 0) {
            return 1;
        } else {
            for (double i = a - 1; i > 0; --i) {
                a = Arithmetic.product(a, i);
            }
        }
        return a;
    }

    static Mts.Numbers.R factorial(Mts.Numbers.R a) {
        return new Mts.Numbers.R(factorial(a.value));
    }

    static double sum(double a, double b) {
        return a + b;
    }

    static Mts.Numbers.R sum(Mts.Numbers.R a, Mts.Numbers.R b) {
        return new Mts.Numbers.R(a.value + b.value);
    }

    static double abs(double a) {
        if (a <= 0) {
            return -a;
        } else {
            return a;
        }
    }

    static Mts.Numbers.R abs(Mts.Numbers.R a) {
        if (a.value <= 0) {
            return new Mts.Numbers.R(-a.value);
        } else {
            return new Mts.Numbers.R(a.value);
        }
    }

    static double minus(double a, double b) {
        return a - b;
    }

    static Mts.Numbers.R minus(Mts.Numbers.R a, Mts.Numbers.R b) {
        return new Mts.Numbers.R(a.value - b.value);
    }

    static Mts.Numbers.R product(Mts.Numbers.R a, Mts.Numbers.R b) {
        return new Mts.Numbers.R(a.value * b.value);
    }

    static double product(double a, double b) {
        return a * b;
    }

    // }
    static double divide(double a, double b) {
        if (b == 0) {
            return Double.NaN;
        } else {
            return a / b;
        }
    }

    static Mts.Numbers.R divide(Mts.Numbers.R a, Mts.Numbers.R b) {
        if (b.value == 0) {
            return Mts.NAN;
        } else {
            return new Mts.Numbers.R(a.value / b.value);
        }
    }

    static Mts.Numbers.R power(Mts.Numbers.R a, Mts.Numbers.R b) {
        if (b.isInt()) {
            if (b.value == 0) {
                return new Mts.Numbers.R(1);
            }
            return new Mts.Numbers.R(power(a.value, (int) b.value));
        } else {
            return new Mts.Numbers.R(power(a.value, b.value));
        }
    }

    static double power(double a, double b) {
        return exp(b * ln(a));
    }

    static double power(double a, int b) {
        // var value = a;
        // for (int i = 1; i < b; i++) {
        // value = value * a;
        // }
        // might be fixed...
        return Math.pow(a, b);
    }

    static double root(double a, double b) {
        return power(a, 1.0 / b);
    }

    static Mts.Numbers.R root(Mts.Numbers.R a, Mts.Numbers.R b) {
        return new Mts.Numbers.R(power(a.value, 1.0 / b.value));
    }

    static double log(double a, double b) {
        if (b < 0) {
            return Double.NaN;
        } else if (a == 1 || a <= 0) {
            return Double.NaN;
        } else {
            return divide(ln(b), ln(a));
        }
    }

    static Mts.Numbers.R log(Mts.Numbers.R a, Mts.Numbers.R b) {
        // if (a.value <= 0) {
        // // OR EQUAL TO !
        // return new Mts.Numbers.R("Argument of log cannot be less than or equal to
        // 0\n");
        // } else if (a.value == 1 || a.value <= 0) {
        // // OR EQUAL TO IN SECOND COND. !
        // return new Mts.Numbers.R("Base of log cannot be less than or equal to 0 and
        // not equal to 1\n");
        // } else {
        // return new Mts.Numbers.R(divide(ln(b.value), ln(a.value)));
        // }
        // Very redundant. Should only compare values instead of calling Nineq

        return new Mts.Numbers.R(Math.log(b.value) / Math.log(a.value));
    }

    private static double lnSmall(double a) {
        double returnValue = 0;
        for (int j = 0; j < 2000; j++) {
            returnValue = returnValue + Math.pow(-1, j) * Arithmetic.divide(Arithmetic.power(a - 1, j + 1), j + 1);
        }
        return returnValue;
    }

    static double ln(double a) {
        String dot = (double) a + "";
        int point = dot.indexOf(".");
        double move = a / Arithmetic.power(10, point);
        return lnSmall(move) + (point) * Mts.LN10;
    }

    static Mts.Numbers.R ln(Mts.Numbers.R a) {
        String dot = (double) a.value + "";
        int point = dot.indexOf(".");
        double move = a.value / Arithmetic.power(10, point);
        return new Mts.Numbers.R(lnSmall(move) + (point) * Mts.LN10);
    }

    static double exp(double a) {
        double value = 1 + a / 20000000.0;
        for (int i = 1; i < 20000000; i++) {
            value *= (1 + divide(a, 20000000.0));
        }
        return Math.exp(a);
    }

    static Mts.Numbers.R exp(Mts.Numbers.R a) {
        double value = 1 + a.value / 20000000.0;
        for (int i = 1; i < 20000000; i++) {
            value *= (1 + divide(a.value, 20000000.0));
        }
        return new Mts.Numbers.R(value);
    }
    // Should reduce all double-arguments

}
