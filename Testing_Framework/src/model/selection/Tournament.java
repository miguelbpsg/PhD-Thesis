package model.selection;

import java.util.Random;

import controller.ChromosomeFactory;
import model.chromosome.Chromosome;

public class Tournament implements Selection {
	private int participants;
	private double prob;
	private Random Rnd = new Random();
	private String text = "To_";
	public Tournament(){
		participants = 2;
		prob = 1;
	}
	
	public Tournament(double prob) {
		this();
		this.prob = prob;
	}

	@Override
	public String toFile() {
		return text;
	}
	
	public Tournament(int participants) {
		this();
		this.participants = participants;
	}
	
	public Tournament(int participants, double prob) {
		this.participants = participants;
		this.prob = prob;
	}
	
	@Override
	public Chromosome[] select(Chromosome[] pob, int tam_pob) {
		Chromosome[] new_pob = new Chromosome[tam_pob];
		int[] index = new int[participants];
		int ganador;
		for(int i = 0; i < tam_pob; i++) {
			for(int j = 0; j < participants; j++)
				index[j] = Rnd.nextInt(pob.length);
			ganador = knockout(pob, participants, index);
			new_pob[i] = ChromosomeFactory.copyChromosome(pob[ganador]);
			new_pob[i].setModified(false);
		}
		return new_pob;
	}

	private int knockout(Chromosome[] pob, int particip, int[] index) {
		if (particip == 1)
			return index[0];
		int max = index[0];
		int index_max = 0;
		for(int i = 0; i < particip; i++) {
			//if(pob[index[i]].getFitness() > pob[max].getFitness()) {
			if(pob[index[i]] == pob[index[i]].crowdedComparisonOperator(pob[max]) ){
				max = index[i];
				index_max = i;
			}
		}
		
		if (Rnd.nextDouble() < prob)
			return max;
		particip--;
		int[] index2 = new int[particip];
		int i = 0;
		for (; i < index_max; i++)
			index2[i] = index[i];
		for (; i < particip; i++)
			index2[i] = index[i+1];
		return knockout(pob, particip, index2);
	}

	@Override
	public String toString() {
		return "Tournament";
	}
	
	@Override
	public int getSelection() {
		return 1;
	}

}
