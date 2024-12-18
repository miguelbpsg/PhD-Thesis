package model.initialization;

import java.util.ArrayList;
import java.util.List;

import controller.ChromosomeFactory;
import model.chromosome.Chromosome;
import model.chromosome.FSMTest;

public class IncrementalInitialization extends Initialization {
	
	public IncrementalInitialization(int size) {
		this.initialization = 1;
	}
	
	public IncrementalInitialization() {
		this.initialization = 1;
	}
	
	
	@Override
	public Chromosome[] initialize(int size_pob, List<FSMTest> allTests, int[][] testsVSmutants) {
		Chromosome[] pop = new Chromosome[size_pob];
		int max_size = 0;
		for(FSMTest t : allTests)
			max_size += t.getSize();
		
		for(int i = 0; i < size_pob; i++) {
			List<FSMTest> tests = new ArrayList<FSMTest>();
			tests.add(allTests.get(rnd.nextInt(allTests.size())));
			int actualSize = tests.get(0).getSize();
			for(int j = 1; actualSize < max_size && j < allTests.size() && rnd.nextDouble() > actualSize/max_size; j++) {
				FSMTest test = allTests.get(rnd.nextInt(allTests.size()));
				if(actualSize + test.getSize() < max_size && !tests.contains(test)) {
					tests.add(test);
					actualSize += test.getSize();
				}
			}
			pop[i] = ChromosomeFactory.createChromosome(tests,i);
			pop[i].evaluateMutationScore(testsVSmutants);
		}
		
		return pop;
		
	}

	@Override
	public String toString() {
		return "Incremental initialization";
	}
}



