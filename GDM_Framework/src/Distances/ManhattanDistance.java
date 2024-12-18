package Distances;

import model.chromosome.Expert;

public class ManhattanDistance implements Distance{

	@Override
	public double meassure(double[] x, double[] y) {
		if (x.length != y.length)
			System.err.println("Manhattan distance error");
		
		double sum = 0;
		for(int i = 0; i < x.length; i++)
			sum += Math.abs((x[i] - y[i]));

		return sum / x.length;
	}

	@Override
	public double meassure(Expert x, Expert y) {
		if (x.getFeatures() != y.getFeatures())
			System.err.println("Manhattan error2");
		
		double sum = 0;
		for(int i = 0; i < x.getFeatures(); i++) {
			for(int j = i+1; j < x.getFeatures(); j++) {
				//Only half matrix
				sum += Math.abs((x.getElem(i,j) - y.getElem(i,j)));
			}
		}

		return sum / (x.getFeatures()*(x.getFeatures() - 1) / 2);
	}

	@Override
	public String toString(){
		return "Man";
	}
}
