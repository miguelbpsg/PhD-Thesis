package model.crossover;

import controller.ChromosomeFactory;
import model.chromosome.Chromosome;
import model.chromosome.Position;

public class UniformCrossover extends Crossover {

	public UniformCrossover(double prob) {
		this.prob = prob;
		this.crossover = 1;
	}

	public UniformCrossover() {
		this(0.6);
	}

	
	@Override
	public Chromosome[] cross(Chromosome[] pop) {
		Chromosome ind1, ind2;
		Position exp1, exp2 = null;
		
		for(int i = 0; i + 1 < pop.length; i = i+2) {		//i < pob.length && i+1 < pob.length --> i < i+1 --> second part is enough
				ind1 = ChromosomeFactory.copyChromosome(pop[i]);
				ind2 = ChromosomeFactory.copyChromosome(pop[i+1]);

				for(int j = 0; j < ind1.getNumExperts(); j++) {
					if (prob >= Rnd.nextDouble()) {
						exp1 = ind1.getExpert(j);
						exp2 = ind2.getExpert(j);
						ind1.setExpert(j, exp2);
						ind2.setExpert(j, exp1);
					}
				}
			pop[i] = ind1;
			pop[i+1] = ind2;
		}
		return pop;
	}
	
	@Override
	public String toString() {
		return "Uniform crossover";
	}
}
