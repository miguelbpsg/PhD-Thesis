package model.replacement;

import controller.ChromosomeFactory;
import model.chromosome.Chromosome;

public class ElitistReplacement extends Replacement {
	double elit;
	
	public ElitistReplacement(double elit, Chromosome initialExperts) {
		this.elit = elit;
		this.replacement = 2;
		this.initialExperts = initialExperts;
	}
	
	public ElitistReplacement() {
		this(0.02, null);
	}
	
	@Override
	public Chromosome[] replace(Chromosome[] pop, Chromosome[] new_pop) {
		for (int i = 0; i < new_pop.length; i++) {
				new_pop[i].evaluate(initialExperts);
}
		quicksort(pop, 0, pop.length - 1);
		quicksort(new_pop, 0, pop.length - 1);	//This substitutes the worst elit elements. Without this line, it substitutes random ones
		for(int i = 0; i < Math.floor(pop.length*elit); i++) {
			new_pop[pop.length-i-1] = ChromosomeFactory.copyChromosome(pop[i]);
		}

		return new_pop;
	}
	
	private void quicksort(Chromosome[] pop, int low, int high) {
		if (low < high) {
			int p = partition(pop, low, high);
			quicksort(pop, low, p - 1);
        	quicksort(pop, p + 1, high);
		}
	}
	
	private int partition(Chromosome[] pop, int low, int high) {
	    Chromosome pivot = pop[high];
	    Chromosome aux;
	    int i = low - 1;
	    for (int j = low; j < high; j++)
	        if (pop[j] == pop[j].better(pivot)) {
	            i++;
	            aux = ChromosomeFactory.copyChromosome(pop[i]);
	            pop[i] = ChromosomeFactory.copyChromosome(pop[j]);
	            pop[j] = aux;
	        }
        aux = ChromosomeFactory.copyChromosome(pop[i+1]);
        pop[i+1] = ChromosomeFactory.copyChromosome(pop[high]);
        pop[high] = aux;
        return i + 1;
	}
	
    	@Override
    	public String toString() {
    		return "Elitist replacement";
    	}
}