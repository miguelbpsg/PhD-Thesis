package Distances;

import model.chromosome.Expert;

public class EuclideanDistance implements Distance {

	public double meassure(double[] x, double[] y) {
		if (x.length != y.length)
			System.err.println("Euclidean distance error");
		
		double sum = 0;
		for(int i = 0; i < x.length; i++)
			sum += (x[i] - y[i]) * (x[i] - y[i]);
		sum = Math.sqrt(sum); 

		return sum / Math.sqrt(x.length);
	}

	public double meassure(Expert x, Expert y) {
		if (x.getFeatures() != y.getFeatures())
			System.err.println("Euclidean distance chromosome error");
		
		double sum = 0;
		for(int i = 0; i < x.getFeatures(); i++) {
			for(int j = i+1; j < x.getFeatures(); j++)
				//Only half matrix
				sum += (x.getElem(i, j) - y.getElem(i, j)) * (x.getElem(i, j) - y.getElem(i, j));
			sum = Math.sqrt(sum); 

		}

		return sum / Math.sqrt(x.getFeatures()*(x.getFeatures() - 1) / 2);

	}

	@Override
	public String toString(){
		return "Eu";
	}
	
}