package portfolio;

import junit.framework.Assert;

import org.junit.Test;

import creditriskplus.CreditRiskPlus;
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
	double[] f = { 
			0.951229, 0.047561, 0.001189, 0.000020, 0.000000, 0.000000, 0.000000, 0.000000,
			0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000 
		};

	double[] g = { 
			0.923116, 0.000000, 0.073849, 0.000000, 0.002954, 0.000000, 0.000079, 0.000000,
			0.000002, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000 
		};

	double[] fFwdRe = { 
			1.000000, 0.996019, 0.984846, 0.968571, 0.950041, 0.932206, 0.917612, 0.908122,
			0.904837, 0.908122, 0.917612, 0.932206, 0.950041, 0.968571, 0.984846, 0.996019
		};

	double[] fFwdIm = {
			0.000000, -0.019060, -0.034834, -0.044774, -0.047542, -0.043093, -0.032456, -0.017378,
			0.000000, 0.017378, 0.032456, 0.043093, 0.047542, 0.044774, 0.034834, 0.019060
		};

	double[] gFwdRe = {
			1.000000, 0.975278, 0.920164, 0.870951, 0.852144, 0.870951, 0.920164, 0.975278,
			1.000000, 0.975278, 0.920164, 0.870951, 0.852144, 0.870951, 0.920164, 0.975278
		};

	double[] gFwdIm = {
			0.000000, -0.055229, -0.073771, -0.049321, 0.000000, 0.049321, 0.073771, 0.055229,
			0.000000, -0.055229, -0.073771, -0.049321, 0.000000, 0.049321, 0.073771, 0.055229
		};

	double[] hRe = {
			1.000000, 0.970343, 0.903650, 0.841370, 0.809571, 0.814031, 0.846748, 0.886631,
			 0.904837, 0.886631, 0.846748, 0.814031, 0.809571, 0.841370, 0.903650, 0.970343
		};

	double[] hIm = {
			0.000000, -0.073598, -0.104706, -0.086767, -0.040512, 0.008446, 0.037828, 0.033206,
			0.000000, -0.033206, -0.037828, -0.008446, 0.040512, 0.086767, 0.104706, 0.073598
		};

	double[] Gn = {
			0.878095, 0.043905, 0.071345, 0.003531, 0.002898, 0.000142, 0.000078, 0.000004,
			0.000002, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000, 0.000000
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

	private void printMatrix()
	{
		System.out.printf("|  O  |     f     |     g     |        f(fwd)         |        g(fwd)         |        h(fwd)         |    G(n)   |\n");
		System.out.printf("|     |           |           |   Real    | Imaginary |   Real    | Imaginary |   Real    | Imaginary |           |\n");
		System.out.printf("|-----|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|-----------|\n");
		for(int i = 0; i < pt.getObligors().get(0).getDefProbVec().length; i++)
		{
			System.out.printf("|%4d |  %7.6f |  %7.6f |  %7.6f | %+7.6f |  %7.6f | %+7.6f |  %7.6f | %+7.6f | %+7.6f |\n", 
							  i,
							  pt.getObligors().get(0).getDefProbVec()[i],
							  pt.getObligors().get(1).getDefProbVec()[i],
							  pt.getObligors().get(0).getDefProbVecFFT()[i].getReal(),
							  pt.getObligors().get(0).getDefProbVecFFT()[i].getImaginary(),
							  pt.getObligors().get(1).getDefProbVecFFT()[i].getReal(),
							  pt.getObligors().get(1).getDefProbVecFFT()[i].getImaginary(),
							  pt.getPtDefProbFFT()[i].getReal(), pt.getPtDefProbFFT()[i].getImaginary(),
							  pt.getPtDefProb()[i]
			);
		}

	}
	@Test
	public void test() 
	{
		FillPortfolio();
		pt.NormalizeExposures();
		pt.calculateLGDProbVec();
		pt.ApplyFFT();
		printMatrix();
		for(int i = 0; i < pt.getObligors().get(0).getDefProbVec().length; i++)
		{
			Assert.assertTrue("f[" + i + "]", Math.abs(pt.getObligors().get(0).getDefProbVec()[i] - f[i]) < CreditRiskPlus.EPSILON);
			Assert.assertTrue("g[" + i + "]", Math.abs(pt.getObligors().get(1).getDefProbVec()[i] - g[i]) < CreditRiskPlus.EPSILON);
			Assert.assertTrue("fFwd[" + i + "].Re", Math.abs(pt.getObligors().get(0).getDefProbVecFFT()[i].getReal() - fFwdRe[i]) < CreditRiskPlus.EPSILON);
			Assert.assertTrue("fFwd[" + i + "].Im", Math.abs(pt.getObligors().get(0).getDefProbVecFFT()[i].getImaginary() - fFwdIm[i]) < CreditRiskPlus.EPSILON);
			Assert.assertTrue("gFwd[" + i + "].Re", Math.abs(pt.getObligors().get(1).getDefProbVecFFT()[i].getReal() - gFwdRe[i]) < CreditRiskPlus.EPSILON);
			Assert.assertTrue("gFwd[" + i + "].Im", Math.abs(pt.getObligors().get(1).getDefProbVecFFT()[i].getImaginary() - gFwdIm[i]) < CreditRiskPlus.EPSILON);
			Assert.assertTrue("h[" + i + "].Re", Math.abs(pt.getPtDefProbFFT()[i].getReal() - hRe[i]) < CreditRiskPlus.EPSILON);
			Assert.assertTrue("h[" + i + "].Im", Math.abs(pt.getPtDefProbFFT()[i].getImaginary() - hIm[i]) < CreditRiskPlus.EPSILON);
			Assert.assertTrue("Gn[" + i + "].Im", Math.abs(pt.getPtDefProb()[i] - Gn[i]) < CreditRiskPlus.EPSILON);
		}
	}
}