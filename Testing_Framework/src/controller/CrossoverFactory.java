package controller;

import model.crossover.UniformCrossover;
import model.crossover.Crossover;
import model.crossover.SinglePointCrossover;

public class CrossoverFactory {
	public static Crossover createCrossover(Crossover c, double prob) { 
		switch(c.getCrossover()) {
		case 1:
			return new UniformCrossover(prob);
		case 2:
			return new SinglePointCrossover(prob);
		default:
			System.err.println("ERROR CREATING CROSSOVER");
			return null;
		}
	}
}
