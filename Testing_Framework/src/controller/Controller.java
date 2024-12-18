package controller;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import model.chromosome.Chromosome;
import model.chromosome.FSM;
import model.chromosome.FSMTest;
import model.crossover.UniformCrossover;
import model.crossover.Crossover;
import model.crossover.SinglePointCrossover;
import model.initialization.IncrementalInitialization;
import model.initialization.Initialization;
import model.mutation.AddingMutation;
import model.mutation.Mutation;
import model.mutation.ReplacingMutation;
import model.replacement.NSGAReplacement;
import model.replacement.Replacement;
import model.selection.Selection;
import model.selection.Tournament;
import model.selection.Truncation;
import utils.NSGA_II_Operators;

public class Controller {

	private int[][] testsVSmutants;
	private List<List<Chromosome>> bestFronts;
	
	public void GA(int size_pop, int iters,
					int participants, double win_ratio,
					double prob_cross,
					double prob_mut,
					String testsFile, String mutantsFile) {
		
		List<FSMTest> totalTests = ChromosomeFactory.readTests(testsFile);
		List<FSM> mutants = ChromosomeFactory.readMutants(mutantsFile);
		
		Initialization metInit= new IncrementalInitialization();
		Selection[] metSelect = {new Tournament(participants, win_ratio), new Truncation(0.25)};
		Crossover[] metCross = {new UniformCrossover(prob_cross), new SinglePointCrossover(prob_cross)};
		Mutation[] metMut = {new AddingMutation(prob_mut, totalTests), new ReplacingMutation(prob_mut, totalTests)};
		

		testsVSmutants = new int[totalTests.size()][mutants.size()];
		for(int test = 0; test < totalTests.size(); test++) {
			for(int mutant = 0; mutant < mutants.size(); mutant++)
					testsVSmutants[test][mutant] = totalTests.get(test).killMutant(mutants.get(mutant));
		}
		

		Replacement metReempl = new NSGAReplacement(testsVSmutants);
		
		for(Selection sel : metSelect) {
			for(Crossover cros : metCross) {
				for(Mutation mut : metMut) {
		
					for(int repetition = 0; repetition < 20; repetition++) {
					bestFronts = new ArrayList<List<Chromosome>>();
				

					Chromosome[] poblacion = metInit.initialize(size_pop, totalTests, testsVSmutants);
					List<Chromosome> l = new ArrayList<Chromosome>();
					for(Chromosome c : poblacion) {
						l.add(c);
						c.evaluateMutationScore(testsVSmutants);
					}
					List<List<Chromosome>> fronts = NSGA_II_Operators.fastNonDominatedSort(l, poblacion.length);
					int f = 0;
					while (f < fronts.size() && !fronts.get(f).isEmpty()) { // for each front f that fits entirely
						NSGA_II_Operators.crowdingDistanceAssignment(fronts.get(f));
						f++;
					}
		
			        for(int i = 0; i < iters; i++) {
			        	poblacion =
						metReempl.replace(
							poblacion,
							mut.mutate(
								cros.cross(
									sel.select(poblacion, size_pop)
								)
							)
						);
			    		l = new ArrayList<Chromosome>();
			    		for(Chromosome c : poblacion)
			    			l.add(c);
			            fronts = NSGA_II_Operators.fastNonDominatedSort(l, poblacion.length);
			    		System.out.println("Generation " + i);
					}

			        bestFronts.add(fronts.get(0));
				}
					
					try {
						bestFronts.sort(new Comparator<List<Chromosome>>() {

							@Override
							public int compare(List<Chromosome> o1, List<Chromosome> o2) {
								return (int)(NSGA_II_Operators.hypervolumeConfiguration4Testing(o2, totalTests)
										- NSGA_II_Operators.hypervolumeConfiguration4Testing(o1, totalTests));
							}
							
						});
						List<Chromosome> median = bestFronts.get(bestFronts.size()/2);
						toFile(testsFile, median, sel, cros, mut);
					} catch (IOException e) {
						e.printStackTrace();
					}

			        
				}
			}
		}
	}
	
	
	public void toFile(String testFile, List<Chromosome> solution, Selection sel, Crossover cros, Mutation mut) throws IOException {
		PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("Experiments/GA/Experiment_"+testFile.charAt(testFile.length()-5)+".csv", true)));
		writer.print(sel.toFile());
		writer.print(";");
		writer.print(cros.toFile());
		writer.print(";");
		writer.print(mut.toFile());
		writer.print("\n");
		for(Chromosome c : solution) {
			writer.print(c.getObjective(0));
			writer.print(";");
			writer.print(c.getObjective(1));
			writer.print("\n");
		}
		writer.print("\n");
		writer.close();
	}
}