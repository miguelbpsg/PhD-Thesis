package Distances;

import model.chromosome.Expert;

public class BrayCurtisDistance implements Distance {

	@Override
	public double meassure(double[] x, double[] y) {
		if (x.length != y.length)
			System.err.println("Bray-Curtis distance error");
		
		double sum = 0;
		double frac = 0;
		for(int i = 0; i < x.length; i++) {
			sum += Math.abs((x[i] - y[i]));
			frac += Math.abs(x[i]) + Math.abs(y[i]);
		}

		if (frac == 0)
			return 0;
		return sum / frac;
	}

	@Override
	public double meassure(Expert x, Expert y) {
		if (x.getFeatures() != y.getFeatures())
			System.err.println("Bray-Curtis error2");
		
		double sum = 0;
		double frac = 0;
		for(int i = 0; i < x.getFeatures(); i++) {
			for(int j = i+1; j < x.getFeatures(); j++) {
				//Only half matrix
				sum += Math.abs((x.getElem(i,j) - y.getElem(i,j)));
				frac += Math.abs(x.getElem(i,j)) + Math.abs(y.getElem(i,j));
			}
		}
		if (frac == 0)
			return 0;
		return sum / frac;
	}
	
	@Override
	public String toString(){
		return "BC";
	}
}
