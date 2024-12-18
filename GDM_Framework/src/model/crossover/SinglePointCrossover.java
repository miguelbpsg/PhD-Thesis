package model.crossover;

import controller.ChromosomeFactory;
import model.chromosome.Chromosome;
import model.chromosome.Position;

public class SinglePointCrossover extends Crossover {

	public SinglePointCrossover(double prob) {
		this.prob = prob;
		this.crossover = 2;
	}

	public SinglePointCrossover() {
		this(0.6);
	}
	
	@Override
	public Chromosome[] cross(Chromosome[] pop) {
		Chromosome ind1, ind2;
		int pos1, pos2;
		Position exp1, exp2 = null;
		
		for(int i = 0; i + 1 < pop.length; i = i+2) {		//i < pob.length && i+1 < pob.length --> i < i+1 --> second part is enough
			ind1 = ChromosomeFactory.copyChromosome(pop[i]);
			ind2 = ChromosomeFactory.copyChromosome(pop[i+1]);

			if(prob >= Rnd.nextDouble()) { 
				pos1 = Rnd.nextInt(ind1.getNumExperts());	//crossover position 1
				do {
					pos2 = Rnd.nextInt(ind1.getNumExperts());	//crossover position 2
				} while(pos2 != pos1);
				if (pos1 > pos2) {
					int aux = pos1;
					pos1 = pos2;
					pos2 = aux;
				}
				
				
				for(int j = pos1; j <= pos2; j++) {
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
		return "Single point crossover";
	}
}