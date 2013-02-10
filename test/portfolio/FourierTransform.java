package portfolio;

import org.junit.Test;

import creditriskplus.Obligor;
import creditriskplus.Portfolio;

public class FourierTransform {
	Portfolio pt = new Portfolio();
	double[] exposures = {
			1.0,
			2.0
	};
	double[] defProb = {
			.05,
			.08
	};

	private void FillPortfolio()
	{
		for(int i = 0; i < exposures.length; i++)
		{
			Obligor o = new Obligor();
			o.setExposure(exposures[i]);
			o.setProbOfDefault(defProb[i]);
			pt.AddObligor(o);
		}
	}
	
	@Test
	public void test() 
	{
		FillPortfolio();
		pt.NormalizeExposures();
		pt.calculateLGDProbVec();
		pt.ApplyFFT();
		System.out.printf("|  O  |     f     |     g     |        f(fwd)         |        g(fwd)         |\n");
		System.out.printf("|     |           |           |   Real    | Imaginary |   Real    | Imaginary |\n");
		System.out.printf("|-----|-----------|-----------|-----------|-----------|-----------|-----------|\n");
		for(int i = 0; i < pt.getObligors().get(0).getDefProbVec().length; i++)
		{
			System.out.printf("|%4d |  %7.6f |  %7.6f |  %7.6f | %+7.6f |  %7.6f | %+7.6f |\n", 
							  i,
							  pt.getObligors().get(0).getDefProbVec()[i],
							  pt.getObligors().get(1).getDefProbVec()[i],
							  pt.getObligors().get(0).getDefProbVecFFT()[i].getReal(),
							  pt.getObligors().get(0).getDefProbVecFFT()[i].getImaginary(),
							  pt.getObligors().get(1).getDefProbVecFFT()[i].getReal(),
							  pt.getObligors().get(1).getDefProbVecFFT()[i].getImaginary());
		}
	}

}
