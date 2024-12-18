package model.selection;

import java.util.Random;

import controller.ChromosomeFactory;
import model.chromosome.Chromosome;

public class Tournament implements Selection {
	private int participants;
	private double prob;
	private Random Rnd = new Random();
	
	public Tournament(){
		participants = 2;
		prob = 1;
	}
	
	public Tournament(double prob) {
		this();
		this.prob = prob;
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
	public Chromosome[] select(Chromosome[] pop, int target_pop_size) {
		Chromosome[] new_pop = new Chromosome[target_pop_size];
		int[] ids = new int[participants];
		int winner;
		for(int i = 0; i < target_pop_size; i++) {
			for(int j = 0; j < participants; j++)
				ids[j] = Rnd.nextInt(pop.length);
			winner = knockout(pop, participants, ids);
			new_pop[i] = ChromosomeFactory.copyChromosome(pop[winner]);
		}
		return new_pop;
	}

	private int knockout(Chromosome[] pop, int num_participants, int[] participants_id) {
		if (num_participants == 1)
			return participants_id[0];
		int max = participants_id[0];
		int id_max = 0;
		for(int i = 0; i < num_participants; i++) {
			if(!pop[participants_id[i]].better(pop[max]).equals(pop[max])) {
				max = participants_id[i];
				id_max = i;
			}
		}
		
		if (Rnd.nextDouble() < prob)
			return max;
		num_participants--;
		int[] new_ids = new int[num_participants];
		int i = 0;
		for (; i < id_max; i++)
			new_ids[i] = participants_id[i];
		for (; i < num_participants; i++)
			new_ids[i] = participants_id[i+1];
		return knockout(pop, num_participants, new_ids);
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
