package model.mutation;


import model.chromosome.Chromosome;

public class PreferenceLevelMutation extends Mutation{

	public PreferenceLevelMutation(double prob) {
		this.prob = prob;
		this.mutation = 1;
	}
	
	public PreferenceLevelMutation() {
		this.mutation = 1;
	}
	public Chromosome[] mutate(Chromosome[] pop) {
		for(int i = 0; i < pop.length; i++) {
			for(int j = 0; j < pop[i].getNumExperts(); j++) {
				for(int x = 0; x < pop[i].getFeatures(); x++) {
					for(int y = 0; y < x; y++) {
						if(rnd.nextDouble() <= prob) {
							pop[i].getExpert(j).modify(x, y, rnd.nextDouble());
						}
					}
				}
			}
		}
		return pop;
	}
	
	@Override
	public String toString() {
		return "Preference level mutation";
	}
}
