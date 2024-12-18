package controller;

import model.crossover.UniformCrossover;
import model.crossover.Crossover;
import model.crossover.SinglePointCrossover;

public class CrossoverFactory {
	public static Crossover createCrossover(int type, double prob) { 
		switch(type) {
		case 1:
			return new UniformCrossover(prob);
		case 2:
			return new SinglePointCrossover(prob);
		default:
			System.err.println("Error at Crossover Factory");
			return null;
		}
	}
}
