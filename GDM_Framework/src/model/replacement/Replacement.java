package model.replacement;

import model.chromosome.Chromosome;

public abstract class Replacement {
	protected int replacement;
	protected Chromosome initialExperts;
	
    public abstract Chromosome[] replace(Chromosome[] pop, Chromosome[] new_pop);
    
    public int getReplacement() {
    	return replacement;
    }
}
