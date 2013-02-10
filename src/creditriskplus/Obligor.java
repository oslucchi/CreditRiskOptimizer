package creditriskplus;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

public class Obligor 
{
	private double exposure;
	private int exposureNorm;
	private double probOfDefault;
	private double[] defProbVec;
	private Complex[] defProbVecFFT;
	
	public Obligor()
	{
		return;
	}

	public void ApplyFFT()
	{
		FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
		defProbVecFFT = fft.transform(defProbVec, TransformType.FORWARD);
	}
	
	public double getExposure() {
		return exposure;
	}

	public void setExposure(double exposure) {
		this.exposure = exposure;
	}

	public int getExposureNorm() {
		return exposureNorm;
	}

	public void setExposureNorm(int exposureNorm) {
		this.exposureNorm = exposureNorm;
	}

	public double getProbOfDefault() {
		return probOfDefault;
	}

	public void setProbOfDefault(double probOfDefault) {
		this.probOfDefault = probOfDefault;
	}

	public double[] getDefProbVec() {
		return defProbVec;
	}

	public void setDefProbVec(double[] defProbArray) {
		this.defProbVec = defProbArray;
	}

	public Complex[] getDefProbVecFFT() 
	{
		return defProbVecFFT;
	}
}
