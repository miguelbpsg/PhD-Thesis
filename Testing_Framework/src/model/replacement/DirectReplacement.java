package model.replacement;

import model.chromosome.Chromosome;

public class DirectReplacement extends Replacement {

	public DirectReplacement(int[][] testsVSmutants) {
		this.replacement = 1;
		this.testsVSmutants = testsVSmutants;
	}

	@Override
	public Chromosome[] replace(Chromosome[] pob, Chromosome[] new_pob) {
		for (int i = 0; i < new_pob.length; i++) {
			new_pob[i].evaluateMutationScore(testsVSmutants);
		}
		return new_pob;
	}

	@Override
	public String toString() {
		return "Direct replacement";
	}
}
