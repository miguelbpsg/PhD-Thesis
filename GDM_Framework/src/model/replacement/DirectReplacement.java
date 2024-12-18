package model.replacement;

import model.chromosome.Chromosome;

public class DirectReplacement extends Replacement {

	public DirectReplacement(Chromosome initialExperts) {
		this.replacement = 1;
		this.initialExperts = initialExperts;
	}

	@Override
	public Chromosome[] replace(Chromosome[] pop, Chromosome[] new_pop) {
		for (int i = 0; i < new_pop.length; i++) {
			new_pop[i].evaluate(initialExperts);
		}
		return new_pop;
	}

	@Override
	public String toString() {
		return "Direct replacement";
	}
}
