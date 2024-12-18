package Distances;

import model.chromosome.Expert;

public abstract class Simillarity {
	protected Distance distance;
	
	public abstract double meassure (double[] x, double[] y);
	public abstract double meassure (Expert x, Expert y);
}
