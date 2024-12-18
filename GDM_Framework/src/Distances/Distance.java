package Distances;

import model.chromosome.Expert;

public interface Distance {
	
	public double meassure(double[] x, double[] y);
	public double meassure(Expert x, Expert y);
}
