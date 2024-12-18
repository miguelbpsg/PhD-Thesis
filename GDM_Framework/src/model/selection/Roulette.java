package model.selection;

import java.util.Random;

import controller.ChromosomeFactory;
import model.chromosome.Chromosome;


public class Roulette implements Selection{
	private Random Rnd = new Random();
	
	@Override
	public Chromosome[] select(Chromosome[] pop, int target_pop_size) {
		int sel_super[] = new int[target_pop_size];
		double prob;
		int pos_super;
		for(int i = 0; i < target_pop_size; i++) {
			prob = Rnd.nextDouble();
			pos_super = 0;
			while(prob > pop[pos_super].getAccScore())
				pos_super++;
			sel_super[i] = pos_super;
		}
		Chromosome[] new_pop = new Chromosome[target_pop_size];
		for (int i = 0; i < target_pop_size; i++)
			new_pop[i] = ChromosomeFactory.copyChromosome(pop[sel_super[i]]);
		return new_pop;
	}

	@Override
	public String toString() {
		return "Roulette wheel";
	}
	
	@Override
	public int getSelection() {
		return 6;
	}

}