package model.initialization;

import Distances.Distance;
import controller.ChromosomeFactory;
import model.chromosome.Chromosome;

public class IncludingInitialization extends Initialization {
	private Distance expsDist;
	private Distance consDist;
	
	public IncludingInitialization(Distance expsDist, Distance consDist) {
		this.expsDist = expsDist;
		this.consDist = consDist;
		this.initialization = 1;
	}

	@Override
	public Chromosome[] initialize(int pop_size, int num_experts, int num_features, Chromosome initialExperts) {
		Chromosome[] pop = new Chromosome[pop_size];
		for(int i = 0; i < pop_size; i++) {
			pop[i] =	
					i % 10 == 0?
					ChromosomeFactory.copyChromosome(initialExperts)	:
					ChromosomeFactory.createChromosome(num_experts, num_features, expsDist, consDist);
			pop[i].evaluate(initialExperts);
			pop[i].updateLocalArchive();
		}
		
		return pop;
	}

	@Override
	public String toString() {
		return "Including initialization";
	}

}