/*
 * $Id: PseudoReductionSeq.java 4290 2012-11-04 14:47:45Z kredel $
 */

package edu.jas.gbufd;


import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import edu.jas.gb.ReductionAbstract;
import edu.jas.poly.ExpVector;
import edu.jas.poly.GenPolynomial;
import edu.jas.structure.RingElem;


/**
 * Polynomial pseudo reduction sequential use algorithm. Coefficients of
 * polynomials must not be from a field, i.e. the fraction free reduction is
 * implemented. Implements normalform.
 * @param <C> coefficient type
 * @author Heinz Kredel
 */

public class PseudoReductionSeq<C extends RingElem<C>> extends ReductionAbstract<C> implements
                PseudoReduction<C> {


    private static final Logger logger = Logger.getLogger(PseudoReductionSeq.class);


    /**
     * Constructor.
     */
    public PseudoReductionSeq() {
    }


    /**
     * Normalform.
     * @param Ap polynomial.
     * @param Pp polynomial list.
     * @return nf(Ap) with respect to Pp.
     */
    @SuppressWarnings("unchecked")
    public GenPolynomial<C> normalform(List<GenPolynomial<C>> Pp, GenPolynomial<C> Ap) {
        if (Pp == null || Pp.isEmpty()) {
            return Ap;
        }
        if (Ap == null || Ap.isZERO()) {
            return Ap;
        }
        Map.Entry<ExpVector, C> m;
        GenPolynomial<C>[] P = new GenPolynomial[0];
        synchronized (Pp) {
            P = Pp.toArray(P);
        }
        int l = P.length;
        ExpVector[] htl = new ExpVector[l];
        C[] lbc = (C[]) new RingElem[l];
        GenPolynomial<C>[] p = new GenPolynomial[l];
        int i;
        int j = 0;
        for (i = 0; i < l; i++) {
            if (P[i] == null) {
                continue;
            }
            p[i] = P[i];
            m = p[i].leadingMonomial();
            if (m != null) {
                p[j] = p[i];
                htl[j] = m.getKey();
                lbc[j] = m.getValue();
                j++;
            }
        }
        l = j;
        ExpVector e;
        C a;
        boolean mt = false;
        GenPolynomial<C> R = Ap.ring.getZERO().copy();

        GenPolynomial<C> S = Ap.copy();
        while (S.length() > 0) {
            m = S.leadingMonomial();
            e = m.getKey();
            a = m.getValue();
            for (i = 0; i < l; i++) {
                mt = e.multipleOf(htl[i]);
                if (mt)
                    break;
            }
            if (!mt) {
                //logger.debug("irred");
                //R = R.sum(a, e);
                //S = S.subtract(a, e);
                R.doPutToMap(e, a);
                S.doRemoveFromMap(e, a);
                //System.out.println(" S = " + S);
            } else {
                e = e.subtract(htl[i]);
                //logger.info("red div = " + e);
                @SuppressWarnings("cast")
                C c = (C) lbc[i];
                if (a.remainder(c).isZERO()) { //c.isUnit() ) {
                    a = a.divide(c);
                    S = S.subtractMultiple(a, e, p[i]);
                } else {
                    R = R.multiply(c);
                    //S = S.multiply(c);
                    S = S.scaleSubtractMultiple(c, a, e, p[i]);
                }
                //Q = p[i].multiply(a, e);
                //S = S.subtract(Q);
            }
        }
        return R;
    }


    /**
     * Normalform.
     * @param Pp polynomial list.
     * @param Ap polynomial.
     * @return ( nf(Ap), mf ) with respect to Pp and mf as multiplication factor
     *         for Ap.
     */
    @SuppressWarnings("unchecked")
    public PseudoReductionEntry<C> normalformFactor(List<GenPolynomial<C>> Pp, GenPolynomial<C> Ap) {
        if (Ap == null) {
            return null;
        }
        C mfac = Ap.ring.getONECoefficient();
        PseudoReductionEntry<C> pf = new PseudoReductionEntry<C>(Ap, mfac);
        if (Pp == null || Pp.isEmpty()) {
            return pf;
        }
        if (Ap.isZERO()) {
            return pf;
        }
        Map.Entry<ExpVector, C> m;
        GenPolynomial<C>[] P = new GenPolynomial[0];
        synchronized (Pp) {
            P = Pp.toArray(P);
        }
        int l = P.length;
        ExpVector[] htl = new ExpVector[l];
        C[] lbc = (C[]) new RingElem[l]; // want C[] 
        GenPolynomial<C>[] p = new GenPolynomial[l];
        int i;
        int j = 0;
        for (i = 0; i < l; i++) {
            if (P[i] == null) {
                continue;
            }
            p[i] = P[i];
            m = p[i].leadingMonomial();
            if (m != null) {
                p[j] = p[i];
                htl[j] = m.getKey();
                lbc[j] = m.getValue();
                j++;
            }
        }
        l = j;
        ExpVector e;
        C a;
        boolean mt = false;
        GenPolynomial<C> R = Ap.ring.getZERO().copy();

        GenPolynomial<C> S = Ap.copy();
        while (S.length() > 0) {
            m = S.leadingMonomial();
            e = m.getKey();
            a = m.getValue();
            for (i = 0; i < l; i++) {
                mt = e.multipleOf(htl[i]);
                if (mt)
                    break;
            }
            if (!mt) {
                //logger.debug("irred");
                //R = R.sum(a, e);
                //S = S.subtract(a, e);
                R.doPutToMap(e, a);
                S.doRemoveFromMap(e, a);
                //System.out.println(" S = " + S);
            } else {
                e = e.subtract(htl[i]);
                //logger.info("red div = " + e);
                C c = lbc[i];
                if (a.remainder(c).isZERO()) { //c.isUnit() ) {
                    a = a.divide(c);
                    S = S.subtractMultiple(a, e, p[i]);
                } else {
                    mfac = mfac.multiply(c);
                    R = R.multiply(c);
                    //S = S.multiply(c);
                    S = S.scaleSubtractMultiple(c, a, e, p[i]);
                }
                //Q = p[i].multiply(a, e);
                //S = S.subtract(Q);
            }
        }
        if (logger.isInfoEnabled()) {
            logger.info("multiplicative factor = " + mfac);
        }
        pf = new PseudoReductionEntry<C>(R, mfac);
        return pf;
    }


    /**
     * Normalform with recording. <b>Note:</b> Only meaningful if all divisions
     * are exact. Compute first the multiplication factor <code>m</code> with
     * <code>normalform(Pp,Ap,m)</code>, then call this method with
     * <code>normalform(row,Pp,m*Ap)</code>.
     * @param row recording matrix, is modified.
     * @param Pp a polynomial list for reduction.
     * @param Ap a polynomial.
     * @return nf(Pp,Ap), the normal form of Ap wrt. Pp.
     */
    @SuppressWarnings("unchecked")
    public GenPolynomial<C> normalform(List<GenPolynomial<C>> row, List<GenPolynomial<C>> Pp,
                    GenPolynomial<C> Ap) {
        if (Pp == null || Pp.isEmpty()) {
            return Ap;
        }
        if (Ap == null || Ap.isZERO()) {
            return Ap;
        }
        GenPolynomial<C>[] P = new GenPolynomial[0];
        synchronized (Pp) {
            P = Pp.toArray(P);
        }
        int l = P.length;
        ExpVector[] htl = new ExpVector[l];
        Object[] lbc = new Object[l]; // want C 
        GenPolynomial<C>[] p = new GenPolynomial[l];
        Map.Entry<ExpVector, C> m;
        int j = 0;
        int i;
        for (i = 0; i < l; i++) {
            p[i] = P[i];
            m = p[i].leadingMonomial();
            if (m != null) {
                p[j] = p[i];
                htl[j] = m.getKey();
                lbc[j] = m.getValue();
                j++;
            }
        }
        l = j;
        ExpVector e;
        C a;
        boolean mt = false;
        GenPolynomial<C> zero = Ap.ring.getZERO();
        GenPolynomial<C> R = Ap.ring.getZERO().copy();
        GenPolynomial<C> fac = null;
        GenPolynomial<C> S = Ap.copy();
        while (S.length() > 0) {
            m = S.leadingMonomial();
            e = m.getKey();
            a = m.getValue();
            for (i = 0; i < l; i++) {
                mt = e.multipleOf(htl[i]);
                if (mt)
                    break;
            }
            if (!mt) {
                //logger.debug("irred");
                //R = R.sum(a, e);
                //S = S.subtract(a, e);
                R.doPutToMap(e, a);
                S.doRemoveFromMap(e, a);
                // System.out.println(" S = " + S);
                //throw new RuntimeException("Syzygy no GB");
            } else {
                e = e.subtract(htl[i]);
                //logger.info("red div = " + e);
                C c = (C) lbc[i];
                if (a.remainder(c).isZERO()) { //c.isUnit() ) {
                    a = a.divide(c);
                    S = S.subtractMultiple(a, e, p[i]);
                    //System.out.print("|");
                } else {
                    //System.out.print("*");
                    R = R.multiply(c);
                    //S = S.multiply(c);
                    S = S.scaleSubtractMultiple(c, a, e, p[i]);
                }
                //Q = p[i].multiply(a, e);
                //S = S.subtract(Q);
                fac = row.get(i);
                if (fac == null) {
                    fac = zero.sum(a, e);
                } else {
                    fac = fac.sum(a, e);
                }
                row.set(i, fac);
            }
        }
        return R;
    }

}
