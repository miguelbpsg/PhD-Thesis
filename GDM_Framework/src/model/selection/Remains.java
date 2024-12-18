package model.selection;

import controller.ChromosomeFactory;
import model.chromosome.Chromosome;

public class Remains implements Selection{
	private Selection remain = new Roulette();

	public Remains(Selection remain) {
		this.remain = remain;
	}
	
	@Override
	public Chromosome[] select(Chromosome[] pop, int target_pop_size) {
		Chromosome[] new_pop = new Chromosome[target_pop_size];
		int j = 0;
		for(int i = 0; i < target_pop_size; i++) {
			for(int k = 0; k < Math.floor(pop[i].getScore()*target_pop_size); j++, k++)
				new_pop[j] = ChromosomeFactory.copyChromosome(pop[i]);
		}
		Chromosome[] remains = remain.select(pop, target_pop_size - j);
		for(int k = 0; k < target_pop_size - j; k++) {
			new_pop[j+k] = remains[k];
		}
		return new_pop;
	}

	@Override
	public String toString() {
		return "Remains";
	}
	
	@Override
	public int getSelection() {
		return 5;
	}
}
