package model.initialization;

import java.util.List;
import java.util.Random;

import model.chromosome.Chromosome;
import model.chromosome.FSMTest;

public abstract class Initialization {
	protected int initialization;
	protected Random rnd = new Random();

	public abstract Chromosome[] initialize(int size_pob, List<FSMTest> allTests, int[][] testsVSmutants);
	public int getInitialization() {
		return initialization;
	}
	
}
