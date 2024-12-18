package Distances;

import model.chromosome.Expert;

public class CanberraDistance implements Distance{

	@Override
	public double meassure(double[] x, double[] y) {
		if (x.length != y.length)
			System.err.println("Canberra distance error");

		double sum = 0;
		for(int i = 0; i < x.length; i++)
			if (!(Math.abs(x[i]) + Math.abs(y[i]) == 0))
				sum += Math.abs((x[i] - y[i])) / (Math.abs(x[i]) + Math.abs(y[i]));

		if(Double.isNaN(sum))
			System.err.println("Error en camberra");
		return sum / x.length; //x.length normalizes
	}

	@Override
	public double meassure(Expert x, Expert y) {
		if (x.getFeatures() != y.getFeatures())
			System.err.println("Canberra error2");
		
		double sum = 0;
		for(int i = 0; i < x.getFeatures(); i++) {
			for(int j = i+1; j < x.getFeatures(); j++) {
				if (!(Math.abs(x.getElem(i,j)) + Math.abs(y.getElem(i,j)) == 0))
				//Only half matrix
					sum += Math.abs((x.getElem(i,j) - y.getElem(i,j))) / (Math.abs(x.getElem(i,j)) + Math.abs(y.getElem(i,j)));
			}
		}
		
		if(Double.isNaN(sum))
			System.err.println("Error en camberra");
		return sum/(x.getFeatures()*(x.getFeatures() - 1) / 2);
	}
	
	@Override
	public String toString(){
		return "Can";
	}
}
