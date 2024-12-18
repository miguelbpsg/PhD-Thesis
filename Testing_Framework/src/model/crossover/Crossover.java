package model.crossover;

import java.util.Random;

import model.chromosome.Chromosome;

public abstract class Crossover {
	protected double prob;
	protected Random Rnd = new Random();
	protected int crossover;
	protected String text;
	
	public abstract Chromosome[] cross(Chromosome[] pob);
	public int getCrossover() {
		return crossover;
	}
	public String toFile() {
		return text;
	}
}
