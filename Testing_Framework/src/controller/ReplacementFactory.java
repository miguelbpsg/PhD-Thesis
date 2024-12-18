package controller;

import model.replacement.Replacement;
import model.replacement.DirectReplacement;
import model.replacement.ElitistReplacement;
import model.replacement.NSGAReplacement;

public class ReplacementFactory {

	public static Replacement createReplacement(Replacement r, int[][] testsVSmutants) {
		switch(r.getReplacement()) {
		case 1:
			return new DirectReplacement(testsVSmutants);
		case 2:
			return new ElitistReplacement(0.02, testsVSmutants);
		case 3:
			return new NSGAReplacement(testsVSmutants);
		default:
			System.err.println("ERROR CREATING REPLACEMENT");
			return null;
		}
	}
}
