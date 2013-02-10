package creditriskplus;

import java.util.ArrayList;
import java.util.Vector;

public class Portfolio 
{
	private double totExp;
	private int totExpNorm, highestBand;
	private double minExp = Double.MAX_VALUE, maxExp = 0, avgExp = 0, dvsExp;
	private ArrayList<Obligor> obligors;
	private Vector<Vector<Double>> ptLGD; // array of vectors of Loss Given Default
	
	public Portfolio()
	{
		obligors = new ArrayList<Obligor>();
		totExp = 0;
		return;
	}
	
	public void AddObligor(Obligor o)
	{
		totExp += o.getExposure();
		if (o.getExposure() < minExp)
			minExp = o.getExposure();
		if (o.getExposure() > maxExp)
			maxExp = o.getExposure();
		avgExp = (avgExp * obligors.size() + o.getExposure()) / (obligors.size() + 1);
		obligors.add(o);
		
		dvsExp = 0;
		for(int i = 0; i < obligors.size(); i++)
		{
			dvsExp += Math.pow((obligors.get(i).getExposure() - avgExp), 2.0);
		}
		dvsExp /= obligors.size();
		dvsExp = Math.sqrt(dvsExp);
		if (dvsExp < 1)
			dvsExp = 1;
	}
	
	public void NormalizeExposures()
	{
		// TODO: adopt a more refined mechanism to calculate fitting bands
		Obligor o;
		totExpNorm = 0;
		highestBand = 0;
		for(int i = 0; i < obligors.size(); i++)
		{
			o = obligors.get(i);
			o.setExposureNorm(new Double(Math.ceil(o.getExposure() / dvsExp)).intValue());
			totExpNorm += o.getExposureNorm();
			if (highestBand < o.getExposureNorm())
				highestBand = o.getExposureNorm();
		}
	}

	public void ApplyFFT()
	{
		for(int i = 0; i < obligors.size(); i++)
		{
			obligors.get(i).ApplyFFT();
		}
	}
	
	private int factorial(int n) throws NumberFormatException
	{
		if (n < 0)
			throw new NumberFormatException("n should be > 0");
		if ((n == 0) || (n == 1))
			return(1);
		return(n * factorial(n -1));
	}
	
	public void calculateLGDProbVec()
	{
		int maxVecLen = 0;
		ptLGD = new Vector<Vector<Double>>();
		for(int i = 0; i < obligors.size(); i++)
		{
			Vector<Double> oblProbVec = new Vector<Double>();
			double probability = 0;
			int counter = 0;
			do {
				probability = obligors.get(i).getProbOfDefault();
				probability = (Math.pow(probability, counter) / factorial(counter)) * Math.pow(Math.E, - probability);
				oblProbVec.add(probability);
				counter++;
			} while(probability > CreditRiskPlus.EPSILON);
			for(int y = 0; y < counter; y++)
			{
				for(int k = 1; k < obligors.get(i).getExposureNorm(); k++)
				{
					oblProbVec.add(y * obligors.get(i).getExposureNorm() + k, 0.0);
				}
			}
			ptLGD.add(oblProbVec);
			if (maxVecLen < oblProbVec.size())
				maxVecLen = oblProbVec.size();
		}
		
		int vecDimension = 3;
		for(; Math.pow(2, vecDimension) < maxVecLen; vecDimension++)
			;
		vecDimension = (int) Math.pow(2, vecDimension);
		for(int i = 0; i < obligors.size(); i++)
		{
			int y;
			double[] defProbArray = new double[vecDimension];
			for(y = 0; y < ptLGD.get(i).size(); y++)
			{
				defProbArray[y] = ptLGD.get(i).get(y);
			}
			while(y < vecDimension)
			{
				defProbArray[y++] = 0.0;
			}
			obligors.get(i).setDefProbVec(defProbArray);
		}
	}
	
	public ArrayList<Obligor> getObligors()
	{
		return obligors;
	}

	public double getTotExp() 
	{
		return totExp;
	}

	public double getMinExp() 
	{
		return minExp;
	}

	public double getMaxExp() 
	{
		return maxExp;
	}

	public double getAvgExp() 
	{
		return avgExp;
	}

	public double getDvsExp() 
	{
		return dvsExp;
	}

	public int getTotExpNorm() {
		return totExpNorm;
	}
}
