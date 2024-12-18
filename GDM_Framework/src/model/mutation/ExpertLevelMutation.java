package model.mutation;

import model.chromosome.Chromosome;
import model.chromosome.Position;

public class ExpertLevelMutation extends Mutation{

	public ExpertLevelMutation(double prob) {
		this.prob = prob;
		this.mutation = 2;
	}
	
	public ExpertLevelMutation() {
		this.mutation = 2;
	}
	
	public Chromosome[] mutate(Chromosome[] pop) {
			for(int i = 0; i < pop.length; i++) {
			for(int j = 0; j < pop[i].getNumExperts(); j++) {
				if(rnd.nextDouble() <= prob) {
					pop[i].setExpert(j, new Position(j, pop[i].getFeatures()));
				}
			}
		}
		return pop;
	}
	
	@Override
	public String toString() {
		return "Expert level mutation";
	}
}