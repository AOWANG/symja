package org.matheclipse.core.convert;

import java.math.BigInteger;
import java.util.List;

import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.ISymbol;

import edu.jas.arith.BigRational;
import edu.jas.arith.ModInteger;
import edu.jas.integrate.Integral;
import edu.jas.integrate.LogIntegral;
import edu.jas.poly.AlgebraicNumber;
import edu.jas.poly.AlgebraicNumberRing;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.poly.GenPolynomialRing;
import edu.jas.poly.Monomial;
import edu.jas.poly.PolyUtil;
import edu.jas.poly.TermOrder;
import edu.jas.structure.RingElem;
import edu.jas.structure.RingFactory;

public class JASConvert<C extends RingElem<C>> {
  private final RingFactory<C> fRingFactory;
  private final TermOrder fTermOrder;
  private final GenPolynomialRing<C> fPolyFactory;
  GenPolynomialRing<edu.jas.arith.BigInteger> fBigIntegerPolyFactory;
  private final List<? extends IExpr> fVariables;;

  public JASConvert(final List<? extends IExpr> variablesList) {
    this(variablesList, new BigRational(0));
  }

  public JASConvert(final List<? extends IExpr> variablesList,
      RingFactory ringFactory) {
    this(variablesList, ringFactory, new TermOrder(TermOrder.INVLEX));
  }

  public JASConvert(final List<? extends IExpr> variablesList,
      RingFactory ringFactory, TermOrder termOrder) {
    this.fRingFactory = ringFactory;
    this.fVariables = variablesList;
    String[] vars = new String[fVariables.size()];
    for (int i = 0; i < fVariables.size(); i++) {
      vars[i] = fVariables.get(i).toString();
    }
    this.fTermOrder = termOrder;
    this.fPolyFactory = new GenPolynomialRing<C>(fRingFactory, fVariables
        .size(), fTermOrder, vars);
    this.fBigIntegerPolyFactory = new GenPolynomialRing<edu.jas.arith.BigInteger>(
        edu.jas.arith.BigInteger.ZERO, fVariables.size(), fTermOrder, vars);
  }

  public IAST rationalPoly2Expr(final GenPolynomial<BigRational> poly)
      throws ArithmeticException, ClassCastException {
    if (poly.length() == 0) {
      return F.Plus(F.C0);
    }
    IAST result = F.Plus();
    for (Monomial<BigRational> monomial : poly) {
      BigRational coeff = (BigRational) monomial.coefficient();
      ExpVector exp = monomial.exponent();
      IFraction coeffValue = F.fraction(coeff.numerator(), coeff.denominator());
      IAST monomTimes = F.Times(coeffValue);
      long lExp;
      for (int i = 0; i < exp.length(); i++) {
        lExp = exp.getVal(i);
        if (lExp != 0) {
          monomTimes.add(F.Power(fVariables.get(i), F.integer(lExp)));
        }
      }
      result.add(monomTimes);
    }
    return result;
  }

  public IAST integerPoly2Expr(
      final GenPolynomial<edu.jas.arith.BigInteger> poly)
      throws ArithmeticException, ClassCastException {
    if (poly.length() == 0) {
      return F.Plus(F.C0);
    }
    IAST result = F.Plus();
    for (Monomial<edu.jas.arith.BigInteger> monomial : poly) {
      edu.jas.arith.BigInteger coeff = (edu.jas.arith.BigInteger) monomial
          .coefficient();
      ExpVector exp = monomial.exponent();
      IInteger coeffValue = F.integer(coeff.getVal());
      IAST monomTimes = F.Times(coeffValue);
      long lExp;
      for (int i = 0; i < exp.length(); i++) {
        lExp = exp.getVal(i);
        if (lExp != 0) {
          monomTimes.add(F.Power(fVariables.get(i), F.integer(lExp)));
        }
      }
      result.add(monomTimes);
    }
    return result;
  }

  public IAST poly2Expr(final GenPolynomial<BigRational> poly)
      throws ArithmeticException, ClassCastException {
    if (poly.length() == 0) {
      return F.Plus(F.C0);
    }
    IAST result = F.Plus();
    for (Monomial<BigRational> monomial : poly) {
      BigRational coeff = (BigRational) monomial.coefficient();
      ExpVector exp = monomial.exponent();
      IFraction coeffValue = F.fraction(coeff.numerator(), coeff.denominator());
      IAST monomTimes = F.Times(coeffValue);
      long lExp;
      for (int i = 0; i < exp.length(); i++) {
        lExp = exp.getVal(i);
        if (lExp != 0) {
          monomTimes.add(F.Power(fVariables.get(i), F.integer(lExp)));
        }
      }
      result.add(monomTimes);
    }
    return result;
  }

  public IAST modIntegerPoly2Expr(final GenPolynomial<ModInteger> poly)
      throws ArithmeticException, ClassCastException {
    if (poly.length() == 0) {
      return F.Plus(F.C0);
    }
    IAST result = F.Plus();
    for (Monomial<ModInteger> monomial : poly) {
      ModInteger coeff = (ModInteger) monomial.coefficient();
      ExpVector exp = monomial.exponent();
      IInteger coeffValue = F.integer(coeff.getVal());
      IAST monomTimes = F.Times(coeffValue);
      long lExp;
      for (int i = 0; i < exp.length(); i++) {
        lExp = exp.getVal(i);
        if (lExp != 0) {
          monomTimes.add(F.Power(fVariables.get(i), F.integer(lExp)));
        }
      }
      result.add(monomTimes);
    }
    return result;
  }

  public GenPolynomial<C> expr2Poly(final IExpr exprPoly)
      throws ArithmeticException, ClassCastException {
    if (exprPoly instanceof IAST) {
      final IAST ast = (IAST) exprPoly;
      GenPolynomial<C> result = fPolyFactory.getZERO();
      GenPolynomial<C> p = fPolyFactory.getZERO();
      if (ast.isASTSizeGE(F.Plus, 2)) {
        IExpr expr = ast.get(1);
        result = expr2Poly(expr);
        for (int i = 2; i < ast.size(); i++) {
          expr = ast.get(i);
          p = expr2Poly(expr);
          result = result.sum(p);
        }
        return result;
      } else if (ast.isASTSizeGE(F.Times, 2)) {
        IExpr expr = ast.get(1);
        result = expr2Poly(expr);
        for (int i = 2; i < ast.size(); i++) {
          expr = ast.get(i);
          p = expr2Poly(expr);
          result = result.multiply(p);
        }
        return result;
      } else if (ast.isAST(F.Power, 3)) {
        final IExpr expr = ast.get(1);
        for (int i = 0; i < fVariables.size(); i++) {
          if (fVariables.get(i).equals(expr)) {
            // may throw ClassCastExcepion or ArithmeticException
            ExpVector e = ExpVector.create(fVariables.size(), i,
                ((IInteger) ast.get(2)).toInt());
            return fPolyFactory.getONE().multiply(e);
          }
        }
        // return Constant.valueOf((IExpr) ast);
      }
    } else if (exprPoly instanceof ISymbol) {
      for (int i = 0; i < fVariables.size(); i++) {
        if (fVariables.get(i).equals(exprPoly)) {
          ExpVector e = ExpVector.create(fVariables.size(), i, 1L);
          return fPolyFactory.getONE().multiply(e);
        }
      }
    } else if (exprPoly instanceof IInteger) {
      // BigInteger bi = ((IInteger)
      // exprPoly).getBigNumerator().toJavaBigInteger();
      return fPolyFactory.fromInteger(((IInteger) exprPoly).toInt());
    } else if (exprPoly instanceof IFraction) {
      BigInteger n = ((IFraction) exprPoly).getBigNumerator()
          .toJavaBigInteger();
      BigInteger d = ((IFraction) exprPoly).getBigDenominator()
          .toJavaBigInteger();
      BigRational nr = new BigRational(n);
      BigRational dr = new BigRational(d);
      BigRational r = nr.divide(dr);
      return new GenPolynomial(fPolyFactory, r);// pfac.getONE().multiply(r);
    }
    throw new ClassCastException();

    // check if this expression contains any variable
    // for (int i = 0; i < variables.size(); i++) {
    // checkCanceled();
    // if (!exprPoly.isFree(exprPoly, variables.get(i))) {
    // throw new ClassCastException();
    // }
    // }
    // // TODO convert constant exprPoly to a?
    // return a;

  }

  /**
   * BigInteger from BigRational coefficients. Represent as polynomial with
   * BigInteger coefficients by multiplication with the lcm of the numerators of
   * the BigRational coefficients.
   * 
   * @param A
   *          polynomial with BigRational coefficients to be converted.
   * @return polynomial with BigInteger coefficients.
   */
  public GenPolynomial<edu.jas.arith.BigInteger> integerFromRationalCoefficients(
      GenPolynomial<BigRational> A) {
    return PolyUtil.integerFromRationalCoefficients(fBigIntegerPolyFactory, A);
  }

  public Object[] factorTerms(GenPolynomial<BigRational> A) {
    return PolyUtil.integerFromRationalCoefficientsFactor(
        fBigIntegerPolyFactory, A);
  }

  /**
   * Convert a jas <code>Integral</code> into a matheclipse expression
   * 
   * @param integral
   *          the JAS Integral
   * @return
   */
  public IAST integral2Expr(Integral<BigRational> integral) {
    IAST sum = F.Plus();
    GenPolynomial<BigRational> pol = integral.pol;
    List<GenPolynomial<BigRational>> rational = integral.rational;
    List<LogIntegral<BigRational>> logarithm = integral.logarithm;

    if (!pol.isZERO()) {
      sum.add(poly2Expr(pol));
    }
    if (rational.size() != 0) {
      int i = 0;
      while (i < rational.size()) {
        sum.add(F.Times(poly2Expr(rational.get(i++)), F.Power(
            poly2Expr(rational.get(i++)), F.CN1)));
      }
    }
    if (logarithm.size() != 0) {
      for (LogIntegral<BigRational> pf : logarithm) {
        sum.add(logIntegral2Expr(pf));
      }
    }
    return sum;
  }

  /**
   * Convert a jas <code>LogIntegral</code> into a matheclipse expression
   * 
   * @param logIntegral
   *          the JAS LogIntegral
   * @return
   */
  public IAST logIntegral2Expr(LogIntegral<BigRational> logIntegral) {
    IAST sum = F.Plus();

    List<BigRational> cfactors = logIntegral.cfactors;

    List<GenPolynomial<BigRational>> cdenom = logIntegral.cdenom;

    List<AlgebraicNumber<BigRational>> afactors = logIntegral.afactors;

    List<GenPolynomial<AlgebraicNumber<BigRational>>> adenom = logIntegral.adenom;

    for (int i = 0; i < cfactors.size(); i++) {
      BigRational cp = cfactors.get(i);
      GenPolynomial<BigRational> p = cdenom.get(i);
      sum.add(F.Times(F.fraction(cp.numerator(), cp.denominator()), F
          .Log(poly2Expr(p))));
    }
    // TODO implement this conversion for AlgebraicNumbers...
    for (int i = 0; i < afactors.size(); i++) {
      AlgebraicNumber<BigRational> ap = afactors.get(i);
      AlgebraicNumberRing<BigRational> ar = ap.factory();
      // sb.append(" ## over " + ap.factory() + "\n");
      GenPolynomial<AlgebraicNumber<BigRational>> p = adenom.get(i);
      if (p.degree(0) < ar.modul.degree(0) && ar.modul.degree(0) > 2) {
        // sb.append("sum_(" + ar.getGenerator() + " in ");
        // sb.append("rootOf(" + ar.modul + ") ) ");
      } else {
      }
      // sb.append("(" + ap.toString() + ")");
      // sb.append(" log( " + p.toString() + ")");
    }
    return sum;
  }
}