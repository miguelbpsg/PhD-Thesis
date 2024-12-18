package model.replacement;

import java.util.ArrayList;
import java.util.List;

import model.chromosome.Chromosome;
import utils.NSGA_II_Operators;

public class NSGAReplacement extends Replacement {
	private final double tol = 0.000000001;
	
	public NSGAReplacement(Chromosome initialExperts) {
		this.replacement = 3;
		this.initialExperts = initialExperts;
	}

	@Override
	public Chromosome[] replace(Chromosome[] pop, Chromosome[] new_pop) {
		for (int i = 0; i < new_pop.length; i++) {
			new_pop[i].evaluate(initialExperts);
		}

		boolean included = false;
		List<Chromosome> R = new ArrayList<Chromosome>();
		for(Chromosome i : pop) {
			double v = i.getObjective(1);
			if(v > tol || v < -tol)
				R.add(i);
			else if (!included) {
				included = true;
				R.add(i);
			}
				
		}
		for(Chromosome i : new_pop) {
			double v = i.getObjective(1);
			if(v > tol || v < -tol)
				R.add(i);
			else if (!included) {
				included = true;
				R.add(i);
			}
		}
		
		int k = 0;
		while (R.size() < pop.length) {
			double v = pop[k % pop.length].getObjective(1);
			if(v > tol || v < -tol)
				R.add(pop[k]);
			else if (!included) {
				included = true;
				R.add(pop[k]);
			}
			v = new_pop[k % new_pop.length].getObjective(1);
			if(v > tol || v < -tol)
				R.add(new_pop[k]);
			else if (!included) {
				included = true;
				R.add(new_pop[k]);
			}
			k++;
		}
		
		List<List<Chromosome>> fronts = NSGA_II_Operators.fastNonDominatedSort(R, pop.length);
		
		Chromosome[] sol = new Chromosome[pop.length];
		int i = 0;
		int frontInd = 0;
		while(i + fronts.get(frontInd).size() < pop.length) {
			List<Chromosome> front = fronts.get(frontInd);
			NSGA_II_Operators.crowdingDistanceAssignment(front);
			for(Chromosome c : front) {
				sol[i] = c;
				i++;
			}
			frontInd++;
		}
		
		if(i < pop.length) {
			List<Chromosome> front = NSGA_II_Operators.NSGASort(fronts.get(frontInd));
			for(int j = 0; i < pop.length; i++, j++)
				sol[i] = front.get(j);
		}
		return sol;
	}

	
   	@Override
	public String toString() {
		return "NSGA replacement";
	}
}
