package model.replacement;

import java.util.ArrayList;
import java.util.List;

import model.chromosome.Chromosome;
import utils.NSGA_II_Operators;

public class NSGAReplacement extends Replacement {

	public NSGAReplacement(int[][] testsVSmutants) {
		this.replacement = 3;
		this.testsVSmutants = testsVSmutants;
	}

	@Override
	public Chromosome[] replace(Chromosome[] pob, Chromosome[] new_pob) {
		List<Chromosome> R = new ArrayList<Chromosome>();
		for(Chromosome i : pob) {
			i.evaluateMutationScore(testsVSmutants);
			R.add(i);
		}
		for(Chromosome i : new_pob) {
			i.evaluateMutationScore(testsVSmutants);
			if(!R.contains(i))
				R.add(i);
		}
		
		List<List<Chromosome>> fronts = NSGA_II_Operators.fastNonDominatedSort(R, pob.length);
		
		Chromosome[] sol = new Chromosome[pob.length];
		int i = 0;
		int frontInd = 0;
		while(i  < pob.length && frontInd < fronts.size() && i + fronts.get(frontInd).size() < pob.length) {
			List<Chromosome> front = fronts.get(frontInd);
			NSGA_II_Operators.crowdingDistanceAssignment(front);
			for(Chromosome c : front) {
				sol[i] = c;
				i++;
			}
			frontInd++;
		}
		if(frontInd >= fronts.size()) {
			System.err.println("ERROR AT REMPLAZO, i= " + i);
		}
		
		if(i < pob.length) {
			List<Chromosome> front = NSGA_II_Operators.NSGASort(fronts.get(frontInd));
			for(int j = 0; i < pob.length; i++, j++)
				sol[i] = front.get(j);
		}
		return sol;
	}

	
   	@Override
	public String toString() {
		return "NSGA replacement";
	}
}
