package model.selection;

import java.util.Random;

import controller.ChromosomeFactory;
import model.chromosome.Chromosome;

public class Ranking implements Selection{
	private Random Rnd = new Random();
	
	@Override
	public Chromosome[] select(Chromosome[] pop, int target_pop_size) {
		int sel_super[] = new int[target_pop_size];
		double prob;
		int pos_super;
		int totalRank = (target_pop_size + 1) * target_pop_size / 2;
		int parcialRank;
		for(int i = 0; i < target_pop_size; i++) {
			prob = Rnd.nextDouble();
			pos_super = 0;
			parcialRank = pop[pos_super].getRank();
			while(prob > parcialRank / totalRank){
				pos_super++;
				parcialRank += pop[pos_super].getRank();
			}
			sel_super[i] = pos_super;
		}
		Chromosome[] new_pob = new Chromosome[target_pop_size];
		for (int i = 0; i < target_pop_size; i++)
			new_pob[i] = ChromosomeFactory.copyChromosome(pop[sel_super[i]]);
		return new_pob;
	}

	@Override
	public String toString() {
		return "Ranking";
	}
	
	@Override
	public int getSelection() {
		return 4;
	}
}
