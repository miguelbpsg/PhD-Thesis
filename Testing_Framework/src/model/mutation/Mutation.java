package model.mutation;

import java.util.List;
import java.util.Random;

import model.chromosome.Chromosome;
import model.chromosome.FSMTest;

public abstract class Mutation {
	protected Random rnd = new Random();
	protected double prob;
	protected int mutation;
	protected List<FSMTest> tests;
	protected String text;
		
	public abstract Chromosome[] mutate(Chromosome[] pob);
	public int getMutation() {
		return mutation;
	}
	
	public String toFile() {
		return text;
	}
}
