package preparation;

import net.finmath.exception.CalculationException;
import net.finmath.montecarlo.RandomVariable;
import net.finmath.montecarlo.assetderivativevaluation.AssetModelMonteCarloSimulationInterface;
import net.finmath.stochastic.RandomVariableInterface;

public class BarrierOption {

	private final double maturity;
	private final double strike;
	private final Integer underlyingIndex;
	private final String nameOfUnderliyng;
	private final double barrier;

	/**
	 * Construct a product representing an European option on an asset S (where S the asset with index 0 from the model - single asset case).
	 * @param maturity The maturity T in the option payoff max(S(T)-K,0)
	 * @param strike The strike K in the option payoff max(S(T)-K,0).
	 * @param underlyingIndex The index of the underlying to be fetched from the model.
	 */
	public BarrierOption(double maturity, double strike, int underlyingIndex, double barrier) {
		super();
		this.maturity			= maturity;
		this.strike				= strike;
		this.underlyingIndex	= underlyingIndex;
		this.nameOfUnderliyng	= null;		// Use underlyingIndex
		this.barrier 			= barrier;
	}

	/**
	 * Construct a product representing an European option on an asset S (where S the asset with index 0 from the model - single asset case).
	 * @param maturity The maturity T in the option payoff max(S(T)-K,0)
	 * @param strike The strike K in the option payoff max(S(T)-K,0).
	 */
	public BarrierOption(double maturity, double strike, double barrier) {
		this(maturity, strike, 0, barrier);
	}

	/**
	 * This method returns the value random variable of the product within the specified model, evaluated at a given evalutationTime.
	 * Note: For a lattice this is often the value conditional to evalutationTime, for a Monte-Carlo simulation this is the (sum of) value discounted to evaluation time.
	 * Cashflows prior evaluationTime are not considered.
	 * 
	 * @param evaluationTime The time on which this products value should be observed.
	 * @param model The model used to price the product.
	 * @return The random variable representing the value of the product discounted to evaluation time
	 * @throws net.finmath.exception.CalculationException Thrown if the valuation fails, specific cause may be available via the <code>cause()</code> method.
	 */
	
	public RandomVariableInterface getValue(double evaluationTime, AssetModelMonteCarloSimulationInterface model) throws CalculationException {
		// Get underlying and numeraire

		// Get S(T)
		RandomVariableInterface[] underlying = new RandomVariableInterface[model.getTimeIndex(maturity)+1];
		RandomVariableInterface values = null;
		for(int i = 0; i<model.getTimeIndex(maturity); i++){
			underlying[i]	= model.getAssetValue(i , underlyingIndex);
			if(values == null){//First multiplication
				values = underlying[i].sub(barrier);
			} else{
				values = values.mult(underlying[i].sub(barrier).floor(0.0));
			}
		}
		underlying[model.getTimeIndex(maturity)] = model.getAssetValue(maturity, underlyingIndex);
		values = values.barrier(values.mult(-1), new RandomVariable(0.0), underlying[model.getTimeIndex(maturity)].sub(strike).floor(0.0));
		

		// Discounting...
		RandomVariableInterface numeraireAtMaturity		= model.getNumeraire(maturity);
		RandomVariableInterface monteCarloWeights		= model.getMonteCarloWeights(maturity);
		values = values.div(numeraireAtMaturity).mult(monteCarloWeights);

		// ...to evaluation time.
		RandomVariableInterface	numeraireAtEvalTime					= model.getNumeraire(evaluationTime);
		RandomVariableInterface	monteCarloProbabilitiesAtEvalTime	= model.getMonteCarloWeights(evaluationTime);
		values = values.mult(numeraireAtEvalTime).div(monteCarloProbabilitiesAtEvalTime);

		return values;
	}
	
	
	
}
