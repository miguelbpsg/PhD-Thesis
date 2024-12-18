package model.crossover;

import java.util.ArrayList;
import java.util.List;

import controller.ChromosomeFactory;
import model.chromosome.Chromosome;
import model.chromosome.FSMTest;

public class UniformCrossover extends Crossover {

	public UniformCrossover(double prob) {
		this.prob = prob;
		this.crossover = 1;
		this.text = "Co_";
	}

	public UniformCrossover() {
		this(0.6);
		this.text = "Co_";
	}

	
	@Override
	public Chromosome[] cross(Chromosome[] pob) {
		Chromosome ind1, ind2;
		List<FSMTest> t1, t2;
		
		for(int i = 0; i + 1 < pob.length; i = i+2) {		//i < pob.length && i+1 < pob.length --> i < i+1 --> RHS is enough
				ind1 = ChromosomeFactory.copyChromosome(pob[i]);
				ind2 = ChromosomeFactory.copyChromosome(pob[i+1]);
				t1 = new ArrayList<FSMTest>();
				t2 = new ArrayList<FSMTest>();
				
				for(int j = 0; j < ind1.getGenotype().size(); j++) {
					if (prob >= Rnd.nextDouble()) {
						t1.add(ind1.removeGene(j));
						j--;
					}
				}
				
				for(int j = 0; j < ind2.getGenotype().size(); j++) {
					if (prob >= Rnd.nextDouble()) {
						t2.add(ind2.removeGene(j));
						j--;
					}
				}

				ind1.setGenes(ind1.getGenotype().size(), t2);
				ind2.setGenes(ind2.getGenotype().size(), t1);
				
				pob[i] = ind1;
				pob[i+1] = ind2;
				pob[i].setModified(true);
				pob[i+1].setModified(true);
		}
		return pob;
	}
	
	@Override
	public String toString() {
		return "Uniform crossover";
	}
}
