package test;


import net.sourceforge.jFuzzyLogic.FIS;
import net.sourceforge.jFuzzyLogic.FunctionBlock;
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart;
import net.sourceforge.jFuzzyLogic.rule.Rule;
import net.sourceforge.jFuzzyLogic.rule.Variable;

public class TestTipper2 {

	public static void main(String[] args) {
		// Load from 'FCL' file
		String fileName = "resources/fcl/fuzzyPolicy.fcl";
		FIS fis = FIS.load(fileName, true);

		// Error while loading?
		if (fis == null) {
			System.err.println("Can't load file: '" + fileName + "'");
			return;
		}

//------------------------------------------------------------------------------------------
//		// Get default function block
//		FunctionBlock functionBlock = fis.getFunctionBlock("tipper");
//
//		// Show
//		JFuzzyChart.get().chart(functionBlock);
//
//		// Set inputs
//		functionBlock.setVariable("service", 7);
//		functionBlock.setVariable("food", 7.5);
//
//		// Evaluate
//		functionBlock.evaluate();
//
//		// Show output variable's chart
//		Variable tip = functionBlock.getVariable("tip");
//		JFuzzyChart.get().chart(tip, tip.getDefuzzifier(), true);
//
//		// Print ruleSet
//		//System.out.println(fis);
//		System.out.println("Output value:" + functionBlock.getVariable("tip").getValue()); 
//		for( Rule r : functionBlock.getFuzzyRuleBlock("No1").getRules() ) {
//		      System.out.println(r);
//		  }
		
// -----------------------------------------------	
		// Get default function block
		FunctionBlock functionBlock = fis.getFunctionBlock("tax");

		// Show
		JFuzzyChart.get().chart(functionBlock);

		// Set inputs
		functionBlock.setVariable("gap", 10);

		// Evaluate
		functionBlock.evaluate();

		// Show output variable's chart
		Variable interven = functionBlock.getVariable("intervention");
		JFuzzyChart.get().chart(interven, interven.getDefuzzifier(), true);

		// Print ruleSet
//		System.out.println(functionBlock);
	    System.out.println("Output value:" + functionBlock.getVariable("intervention").getValue()); 
	 // Show each rule (and degree of support)
	    for( Rule r : functionBlock.getFuzzyRuleBlock("No1").getRules() ) {
	      System.out.println(r);
	  }
		
//-------------------------------------------------
	//	 Get default function block
//		FunctionBlock functionBlock = fis.getFunctionBlock("foodEmployment");
//
//		// Show
//		JFuzzyChart.get().chart(functionBlock);
//
//		// Set inputs
//		functionBlock.setVariable("employment", -1.0);
//		functionBlock.setVariable("foodProduction", 1.0);
//		// Evaluate
//		functionBlock.evaluate();
//
//		// Show output variable's chart
//		Variable interven = functionBlock.getVariable("subsidy");
//		JFuzzyChart.get().chart(interven, interven.getDefuzzifier(), true);
//
//		// Print ruleSet
////		System.out.println(functionBlock);
//	    System.out.println("Output value:" + functionBlock.getVariable("subsidy").getValue()); 
//	 // Show each rule (and degree of support)
//	    for( Rule r : functionBlock.getFuzzyRuleBlock("No1").getRules() ) {
//	      System.out.println(r);
//	  }
//	    
//	    List<Double> empList = new ArrayList<>();
//	    List<Double> foodList = new ArrayList<>();
//	    List<Double> subList = new ArrayList<>();
//	    
//	    for(int i = 0; i <= 10; i++ ) {
//	    	for(int j = 0; j <= 10; j++ ) {
//	    	double emp = 0.1 * i;
//	    	double fpro = 0.1 * j;
//	    	functionBlock.setVariable("employment", emp);
//	    	functionBlock.setVariable("foodProduction", fpro);
//	    	functionBlock.evaluate();
//	    	double sub = functionBlock.getVariable("subsidy").getValue();
//	    	empList.add(emp);
//	    	foodList.add(fpro);
//	    	subList.add(sub);
//		    }
//	    }
//	    System.out.println(empList);
//	    System.out.println(foodList);
//	    System.out.println(subList);
	    
	}
}
