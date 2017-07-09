package preparation;

import com.lorenzotorricelli.ex9sol.BlackScholesMonteCarlo;

import net.finmath.exception.CalculationException;
import net.finmath.montecarlo.assetderivativevaluation.products.EuropeanOption;
import net.finmath.time.TimeDiscretization;

public class test {

	public static void main(String[] args) throws CalculationException {
		
		int numberOfTimeSteps=100;
		int numberOfSimulations=100000;
		double initialPrice=100.0;
		double finalTime=1.0;
		double volatility=0.2;
		double strike=100.0;
		double barrier=70;

		double riskFreeRate=0.0;



		BlackScholesMonteCarlo bsModel= new	BlackScholesMonteCarlo(  
				new TimeDiscretization(0.0, numberOfTimeSteps, finalTime/numberOfTimeSteps),  //using the TimeDiscretization version of the BS model constructor
				numberOfSimulations,
				initialPrice,
				riskFreeRate,
				volatility);  //seeding has been set the same manually...try to improve this by defining a method extracting the seed in the displaced model and then feed it in the black Scholes model

		EuropeanOption europeanOption=new EuropeanOption(finalTime, strike);  //random variable

		System.out.println(europeanOption.getValue(1.0, bsModel).getAverage());
		
		BarrierOption barrierOption = new BarrierOption(finalTime, strike, barrier);
		
		System.out.println(barrierOption.getValue(1.0, bsModel).getAverage());
		
	}

}
