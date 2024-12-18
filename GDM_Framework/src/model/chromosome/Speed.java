package model.chromosome;

import java.util.Random;

public class Speed extends Expert {

	//theshold should be between 0.1 and 0.2 of the range (in this case, the range is [0,1])
	private final double threshold = 0.1;
	private final double marginError = 0.000000001;
		
	public Speed(int id, int features) {
		super(id,features);	
		
		
		Random rnd = new Random();
		
		for(int i = 0; i < features; i++) {
			for(int j = i+1; j < features; j++) {
				//the value is between -threshold and threshold
				preferencesMatrix[i][j] = (2 * threshold * rnd.nextDouble()) - threshold;
			}
		}
		for(int i = 0; i < features; i++) {
			for(int j = 0; j < i; j++) {
				preferencesMatrix[i][j] = -preferencesMatrix[j][i];
			}
		}
		
		for(int i = 0; i < features; i++) {
			preferencesMatrix[i][i] = 0;
		}
		
		meassureStability();
	}
	
	public Speed(Speed expert) {		//copy
	super(expert);
	}
	
	public Speed(int id, double[][]preferencesMatrix, int features) { // reading from a file where the matrix is stored
		super(id, preferencesMatrix, features);
	}
	
	@Override
	public void meassureStability() {
		int i = 0;
		int f = getFeatures();
		while (i < f && stability) {
			int j = i;
			while (j < f && stability) {
				stability = - marginError < preferencesMatrix[i][j] + preferencesMatrix[j][i]  && 
						preferencesMatrix[i][j] + preferencesMatrix[j][i] < marginError;
				j++;
			}
			i++;
		}
		if (!stability) {
			System.err.println("Speed " + getId() + " is not consistent");
			System.out.println(toString());
		}
	}
	
	public void updateSpeed(Position p, Position localBest, Position generationalBest,
			double cognition, double socialWeight, double r1, double r2, double inercia) {
		if (p.isStable() && p.getId() == getId()) {
			int f = getFeatures();
			for(int i = 0; i < f; i++) {
				for(int j = 0; j < f; j++) {
					preferencesMatrix[i][j] = inercia*preferencesMatrix[i][j] + 
						cognition * r1 * (localBest.getElem(i, j) - p.getElem(i, j)) + 
						socialWeight * r2 * (generationalBest.getElem(i,j) - p.getElem(i, j));
					if (preferencesMatrix[i][j] > threshold)
						preferencesMatrix[i][j] = threshold;
					if (preferencesMatrix[i][j] < -threshold)
						preferencesMatrix[i][j] = -threshold;
				}
			}
			meassureStability();
		}
		else
			System.out.println("ERROR UPDATING SPEED");
	}
	
	public void modify(int x, int y, double val){
		int f = getFeatures();
		if (x < f && y < f && x != y) {
			if(threshold < val)
				val = threshold;
			else if (val < -threshold)
				val = -threshold;
			preferencesMatrix[x][y] = val;
			preferencesMatrix[y][x] = 1-val;
		}
	}
}
