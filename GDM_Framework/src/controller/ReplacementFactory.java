package controller;

import model.replacement.Replacement;
import model.chromosome.Chromosome;
import model.replacement.DirectReplacement;
import model.replacement.ElitistReplacement;
import model.replacement.NSGAReplacement;

public class ReplacementFactory {

	public static Replacement createReplacement(int type, double elit, Chromosome initialExperts) {
		switch(type) {
		case 1:
			return new DirectReplacement(initialExperts);
		case 2:
			return new ElitistReplacement(elit, initialExperts);
		case 3:
			return new NSGAReplacement(initialExperts);
		default:
			System.err.println("Error at Replacement Factory");
			return null;
		}
	}
}
