package model.initialization;

import java.util.Random;

import model.chromosome.Chromosome;

public abstract class Initialization {
	protected Random rnd = new Random();
	protected int initialization;
	
	public abstract Chromosome[] initialize(int pop_size, int num_expert, int num_features, Chromosome initialExperts);
	
	public int getInitialization() {
		return initialization;
	} 
}
