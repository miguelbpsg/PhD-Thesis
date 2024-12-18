package controller;


import model.mutation.Mutation;
import model.mutation.ExpertLevelMutation;
import model.mutation.PreferenceLevelMutation;

public class MutationFactory {

	public static Mutation createMutation(int type, double prob) {		
		switch(type) {
		case 1:
			return new PreferenceLevelMutation(prob);
		case 2:
			return new ExpertLevelMutation(prob);
		default:
			System.err.println("Error at Mutation Factory");
			return null;
		}
	}
}
