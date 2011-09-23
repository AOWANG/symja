package org.matheclipse.core.visit;

import org.matheclipse.core.interfaces.IComplex;
import org.matheclipse.core.interfaces.IComplexNum;
import org.matheclipse.core.interfaces.IFraction;
import org.matheclipse.core.interfaces.IInteger;
import org.matheclipse.core.interfaces.INum;
import org.matheclipse.core.interfaces.IPattern;
import org.matheclipse.core.interfaces.IStringX;
import org.matheclipse.core.interfaces.ISymbol;

public abstract class AbstractVisitorInt  implements IVisitorInt {

	public AbstractVisitorInt() {
		super();
	}

	public int visit(IInteger element) {
		return 1;
	}

	public int visit(IFraction element) {
		return 1;
	}

	public int visit(IComplex element) {
		return 1;
	}

	public int visit(INum element) {
		return 1;
	}

	public int visit(IComplexNum element) {
		return 1;
	}

	public int visit(ISymbol element) {
		return 1;
	}

	public int visit(IPattern element) {
		return 1;
	}

	public int visit(IStringX element) {
		return 1;
	}

}