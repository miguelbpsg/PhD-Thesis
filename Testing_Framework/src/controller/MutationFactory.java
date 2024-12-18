package controller;

import java.util.List;

import model.mutation.Mutation;
import model.mutation.ReplacingMutation;
import model.chromosome.FSMTest;
import model.mutation.AddingMutation;

public class MutationFactory {

	public static Mutation createMutation(Mutation m, double prob, List<FSMTest> tests) {
		switch(m.getMutation()) {
		case 1:
			return new AddingMutation(prob, tests);
		case 2:
			return new ReplacingMutation(prob, tests);
		default:
			System.err.println("ERROR CREATING MUTATION");
			return null;
		}
	}
}
