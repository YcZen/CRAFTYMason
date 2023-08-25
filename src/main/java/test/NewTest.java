package test;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;

import com.manyangled.gibbous.optim.convex.*;

public class NewTest{
	
	public static void main(String[] args) {


		// create a convex objective function
		QuadraticFunction q = new QuadraticFunction(
		    new double[][] { { 1.0, 0.0 }, { 0.0, 1.0 } },
		    new double[] { 0.0, 0.0 },
		    0.0);

		// optimize function q with an inequality constraint and an equality constraint,
		// using the barrier method
		BarrierOptimizer barrier = new BarrierOptimizer();
		PointValuePair pvp = barrier.optimize(
		    new ObjectiveFunction(q),
		    new LinearInequalityConstraint(
		        new double[][] { { -1.0, 0.0 } }, // constraint x > 1,
		        new double[] { -1.0 }),
		    new LinearEqualityConstraint(
		        new double[][] { { 0.0, 1.0 } },  // constraint y = 1,
		        new double[] { 1.0 }),
		    new InitialGuess(new double[] { 10.0, 10.0 }));

		double[] xmin = pvp.getFirst();  // { 1.0, 1.0 }
		double vmin = pvp.getSecond();   // 1.0
	}
}