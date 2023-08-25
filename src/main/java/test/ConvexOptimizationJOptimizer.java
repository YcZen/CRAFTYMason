package test;

import com.joptimizer.exception.JOptimizerException;
import com.joptimizer.functions.ConvexMultivariateRealFunction;
import com.joptimizer.functions.LinearMultivariateRealFunction;
import com.joptimizer.functions.PDQuadraticMultivariateRealFunction;
import com.joptimizer.functions.QuadraticMultivariateRealFunction;
import com.joptimizer.optimizers.OptimizationRequest;
import com.joptimizer.optimizers.PrimalDualMethod;

import cern.colt.matrix.DoubleFactory1D;
import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;

public class ConvexOptimizationJOptimizer {
    public static void main(String[] args) {
    	// Objective function (quadratic): f(x, y) = x^2 + y^2

        DoubleMatrix1D q = DoubleFactory1D.dense.make(new double[] { 0, 0 });  // Linear term coefficients
        DoubleMatrix2D P = DoubleFactory2D.dense.make(new double[][] { 
            { 3, -1 }, 
            { -1, 4 } 
        });  // Quadratic matrix
        
        PDQuadraticMultivariateRealFunction objectiveFunction = new PDQuadraticMultivariateRealFunction(P, q, 0);

        
        // Inequality constraints
        // x + y >= 1   =>   -x - y <= -1
        // x >= 0      =>   -x <= 0
        // y >= 0      =>   -y <= 0
        ConvexMultivariateRealFunction[] inequalities = new ConvexMultivariateRealFunction[3];
        inequalities[0] = new LinearMultivariateRealFunction(new double[] { 1, 2 }, -3);
        inequalities[1] = new LinearMultivariateRealFunction(new double[] { -1, 0 }, 1);
        inequalities[2] = new LinearMultivariateRealFunction(new double[] { 0, -1 }, 0);
        
        // Optimization request setup
        OptimizationRequest or = new OptimizationRequest();
        or.setF0(objectiveFunction);
        or.setFi(inequalities);
        or.setToleranceFeas(1.E-9);
        or.setTolerance(1.E-9);
        
        // Optimization
        PrimalDualMethod opt = new PrimalDualMethod();
        opt.setOptimizationRequest(or);
        try {
			opt.optimize();
		} catch (JOptimizerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        if (opt.getOptimizationResponse()!=null) {
            double[] sol = opt.getOptimizationResponse().getSolution();
            System.out.println("Solution: x = " + sol[0] + ", y = " + sol[1]);
       //     System.out.println("Value: " + opt.getOptimizationResponse().getValue());
        } else {
            System.out.println("Optimization failed");
        }
    }
    
}
