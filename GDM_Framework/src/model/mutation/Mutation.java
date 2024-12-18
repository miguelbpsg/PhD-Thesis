package model.mutation;

import java.util.Random;

import model.chromosome.Chromosome;

public abstract class Mutation {
	protected Random rnd = new Random();
	protected double prob;
	protected int mutation;
		
	public abstract Chromosome[] mutate(Chromosome[] pop);
	public int getMutation() {
		return mutation;
	}
}
