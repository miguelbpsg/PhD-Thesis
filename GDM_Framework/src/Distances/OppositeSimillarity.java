package Distances;

import model.chromosome.Expert;

public class OppositeSimillarity extends Simillarity {

	
	public OppositeSimillarity(Distance distance) {
		this.distance = distance;
	}
	
	@Override
	public double meassure(double[] x, double[] y) {
		return 1 - distance.meassure(x,y);
	}

	@Override
	public double meassure(Expert x, Expert y) {
		return 1 - distance.meassure(x,y);
	}
}
