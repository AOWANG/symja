package org.matheclipse.core.reflection.system;

import static org.matheclipse.core.expression.F.List;

import org.apache.commons.math.linear.FieldLUDecompositionImpl;
import org.apache.commons.math.linear.FieldMatrix;
import org.matheclipse.core.basic.Config;
import org.matheclipse.core.convert.Convert;
import org.matheclipse.core.eval.interfaces.AbstractFunctionEvaluator;
import org.matheclipse.core.expression.ExprFieldElement;
import org.matheclipse.core.expression.F;
import org.matheclipse.core.interfaces.IAST;
import org.matheclipse.core.interfaces.IExpr;

public class LUDecomposition extends AbstractFunctionEvaluator {

	public LUDecomposition() {
		super();
	}

	@Override
	public IExpr evaluate(final IAST function) {
		FieldMatrix<ExprFieldElement> matrix;
		try {
			if (function.size() == 2) {
				final IAST list = (IAST) function.get(1);
				matrix = Convert.list2Matrix(list);
				final FieldLUDecompositionImpl<ExprFieldElement> lu = new FieldLUDecompositionImpl<ExprFieldElement>(matrix);
				final FieldMatrix<ExprFieldElement> lMatrix = lu.getL();
				final FieldMatrix<ExprFieldElement> uMatrix = lu.getU();
				final int[] iArr = lu.getPivot();
				// final int permutationCount = lu.getPermutationCount();
				final IAST iList = List();
				for (int i = 0; i < iArr.length; i++) {
					// +1 because in MathEclipse the offset is +1 compared to java arrays
					iList.add(F.integer(iArr[i] + 1));
				}
				final IAST result = List();
				final IAST lMatrixAST = Convert.matrix2List(lMatrix);
				final IAST uMatrixAST = Convert.matrix2List(uMatrix);
				result.add(lMatrixAST);
				result.add(uMatrixAST);
				result.add(iList);
				// result.add(F.integer(permutationCount));
				return result;
			}
		} catch (final ClassCastException e) {
			if (Config.SHOW_STACKTRACE) {
				e.printStackTrace();
			}
		} catch (final IndexOutOfBoundsException e) {
			if (Config.SHOW_STACKTRACE) {
				e.printStackTrace();
			}
		}

		return null;
	}

	@Override
	public IExpr numericEval(final IAST functionList) {
		return evaluate(functionList);
	}
	// public FieldMatrix<IExpr> matrixEval(FieldMatrix<IExpr> matrix) {
	// DecompositionLU<IExpr> lu = matrix.lu();
	// return matrix.inverse();
	// }
}