package model.selection;

import java.util.Random;

import controller.ChromosomeFactory;
import model.chromosome.Chromosome;


public class SUS implements Selection{
	private Random Rnd = new Random();

	
	public Chromosome[] select(Chromosome[] pop, int tam_pob) {
		Chromosome[] new_pop = new Chromosome[tam_pob];
		double prob = Rnd.nextDouble()/tam_pob;
		int pos_super = 0;
		double step = (double)1 / tam_pob;
		for(int i = 0; i < tam_pob; i++) {
			while(prob >= pop[pos_super].getAccScore())
				pos_super++;
			new_pop[i] = ChromosomeFactory.copyChromosome(pop[pos_super]);
			prob += step;
		}
		return new_pop;
	}

	@Override
	public String toString() {
		return "SUS";
	}
	
	@Override
	public int getSelection() {
		return 3;
	}
}
