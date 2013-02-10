package portfolio;

import junit.framework.Assert;

import org.junit.Test;

import creditriskplus.Obligor;
import creditriskplus.Portfolio;

public class PtAvrgAndExpBands {
	Portfolio pt = new Portfolio();
	double[] exposures = {
			150.0,
			460.0,
			435.0,
			370.0,
			190.0,
			480.0
	};
	double[] defProb = {
			.05,
			.08,
			.02,
			.08,
			.11,
			.03
	};
	
	int[] exposureBand = {
			2,
			4,
			4,
			3,
			2,
			4
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
	
	private void Statistics()
	{
		Assert.assertEquals("Average ", 347.5, pt.getAvgExp());
		Assert.assertEquals("Stdev ", 131.0, Math.ceil(pt.getDvsExp()));
		Assert.assertEquals("Min ", 480.0, pt.getMaxExp());
		Assert.assertEquals("Max ", 150.0, pt.getMinExp());
	}
	
	private void CheckExposureBands()
	{
		for(int i = 0; i < pt.getObligors().size(); i++)
		{
			Assert.assertEquals("Band " + i, exposureBand[i], pt.getObligors().get(i).getExposureNorm());
		}
	}

	@Test
	public void test() 
	{
		FillPortfolio();
		Statistics();
		pt.NormalizeExposures();
		CheckExposureBands();
		System.out.println("Portfolio value: " + pt.getTotExp() + "\n" +
						   "Portfolio normalized value: " + pt.getTotExpNorm() + "\n" +
						   "Normalization factor: " + pt.getDvsExp());	
	}

}
