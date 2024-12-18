package model.crossover;

import java.util.Random;

import model.chromosome.Chromosome;

public abstract class Crossover {
	protected double prob;
	protected Random Rnd = new Random();
	protected int crossover;
	
	public abstract Chromosome[] cross(Chromosome[] pop);
	public int getCrossover() {
		return crossover;
	}
}
