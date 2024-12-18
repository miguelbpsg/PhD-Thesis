package model.chromosome;

import java.util.Random;

public class Position extends Expert {

	final double marginError = 0.000000001;
	
	public Position(int id, int features) {
		super(id,features);
		
		Random rnd = new Random();
		
		for(int i = 0; i < features; i++) {
			for(int j = i+1; j < features; j++) {
				preferencesMatrix[i][j] = rnd.nextDouble();
			}
		}
		for(int i = 0; i < features; i++) {
			for(int j = 0; j < i; j++) {
				preferencesMatrix[i][j] = 1 - preferencesMatrix[j][i];
			}
		}
		
		for(int i = 0; i < features; i++) {
			preferencesMatrix[i][i] = 0.5;
		}
		
		meassureStability();
	}
	
	public Position(Position expert) {		//copy
	super(expert);
	}
	
	public Position(int id, double[][]preferencesMatrix, int features) { // reading from a file where the matrix is stored
		super(id, preferencesMatrix, features);
	}
	
	@Override
	public void meassureStability() {
		int i = 0;
		int f = getFeatures();
		while (i < f && stability) {
			int j = i;
			while (j < f && stability) {
				stability = 1 - marginError < preferencesMatrix[i][j] + preferencesMatrix[j][i]  && 
						preferencesMatrix[i][j] + preferencesMatrix[j][i] < 1 + marginError;
				j++;
			}
			i++;
		}
		if (!stability) {
			System.err.println("Position " + getId() + " is not consistent");
			System.out.println(toString());
		}
	}
	
	public void updatePosition(Speed s) {
		if (s.isStable() && s.getId() == getId()) {
			int f = getFeatures();
			for(int i = 0; i < f; i++) {
				for(int j = 0; j < f; j++) {
					preferencesMatrix[i][j] += s.getElem(i, j);
					if (preferencesMatrix[i][j] > 1)
						preferencesMatrix[i][j] = 1;
					if (preferencesMatrix[i][j] < 0)
						preferencesMatrix[i][j] = 0;
				}
			}
		meassureStability();
		}
		else
			System.out.println("ERROR UPDATING POSITION");

	}
	
	public void modify(int x, int y, double val){
		int f = getFeatures();
		if (x < f && y < f && 0 <= val && val <= 1 && x != y) {
			preferencesMatrix[x][y] = val;
			preferencesMatrix[y][x] = 1-val;
		}
	}
}
