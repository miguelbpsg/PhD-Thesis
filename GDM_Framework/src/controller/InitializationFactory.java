package controller;

import model.initialization.Initialization;
import model.initialization.RandomInitialization;
import Distances.Distance;
import model.initialization.IncludingInitialization;

public class InitializationFactory {

	public static Initialization createInitialization(Initialization i, Distance expsDist, Distance consDist) {
		switch(i.getInitialization()) {
		case 1:
			return new IncludingInitialization(expsDist, consDist);
		case 2:
			return new RandomInitialization(expsDist, consDist);
		default:
			System.err.println("Error at Initialization Factory");
			return null;
		}
	}
}
