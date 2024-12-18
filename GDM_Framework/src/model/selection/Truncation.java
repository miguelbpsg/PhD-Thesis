package model.selection;


import java.util.List;

import controller.ChromosomeFactory;
import model.chromosome.Chromosome;
import utils.NSGA_II_Operators;

public class Truncation implements Selection{
	private double trunc;

	public Truncation(double trunc) {
		this.trunc = trunc;
	}

	public Chromosome[] select(Chromosome[] pop, int target_pop_size) {
		Chromosome[] new_pop = new Chromosome[target_pop_size];
		int i = 0;
		List<Chromosome> aux_pop = NSGA_II_Operators.NSGASort(pop);
		for(int j = 0; i < target_pop_size && j <= Math.ceil(1/trunc); j++) {	//j is not needed, but helps to see the number of times each element is selected
			for(int k = 0; i < target_pop_size && k < Math.floor(pop.length*trunc); i++, k++)
				new_pop[i] = ChromosomeFactory.copyChromosome(aux_pop.get(k));
		}
		return new_pop;
	}

	@Override
	public String toString() {
		return "Truncation";
	}
	
	@Override
	public int getSelection() {
		return 2;
	}
}
