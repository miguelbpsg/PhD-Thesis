package controller;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Distances.Distance;
import Distances.EuclideanDistance;
import model.crossover.UniformCrossover;
import model.crossover.Crossover;
import model.crossover.SinglePointCrossover;
import model.chromosome.Chromosome;
import model.initialization.IncludingInitialization;
import model.initialization.Initialization;
import model.initialization.RandomInitialization;
import model.mutation.PreferenceLevelMutation;
import model.mutation.Mutation;
import model.mutation.ExpertLevelMutation;
import model.replacement.DirectReplacement;
import model.replacement.ElitistReplacement;
import model.replacement.NSGAReplacement;
import model.replacement.Replacement;
import model.selection.Selection;
import model.selection.Tournament;
import model.selection.Truncation;
import utils.NSGA_II_Operators;

public class Controller {
	
	private final static Distance comparator = new EuclideanDistance();
	
	public static void GA(String file, Distance expsDist, Distance consDist) {
		try {
			Chromosome initialExperts = ChromosomeFactory.readChromosome(file, expsDist, consDist);
			int numExperts = initialExperts.getNumExperts();
			int numFeatures = initialExperts.getFeatures();
			
			//Control:
			int batch_size = 20;
			int[] size_pop = {25,50};
			int max_iters = 50;
			
			Initialization[] metInit = {new RandomInitialization(expsDist, consDist), new IncludingInitialization(expsDist, consDist)};
			final Selection[] metSel = {new Tournament(3, 0.8), new Truncation(0.125)};
			final Crossover[] metCruce = {new UniformCrossover(0.6), new SinglePointCrossover(0.6)};
			final Mutation metMut[] = {new PreferenceLevelMutation(0.05), new ExpertLevelMutation(0.05)};
			final Replacement metRepl[] = {new DirectReplacement(initialExperts), new ElitistReplacement(0.02, initialExperts), new NSGAReplacement(initialExperts)};
			
			for(Initialization init : metInit) {
				for(Selection sel : metSel) {
					for(Crossover cros : metCruce) {
						for(Mutation mut : metMut) {
							for(Replacement rep : metRepl) {
								PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("files/Results/GAs/"+numExperts+" "+numFeatures+".csv", true)));
	
								for(int size : size_pop) {
									
									for(int repetition = 0; repetition < batch_size; repetition++) {
										long time = System.currentTimeMillis();
										Chromosome[] population = init.initialize(size, numExperts, numFeatures, initialExperts);
						        		
						        		for(int it = 1; it <=max_iters; it++) {
								        	population =
												rep.replace(
													population,
													mut.mutate(
														cros.cross(
															sel.select(population, size)
														)
													)
												);
								        	if (it == (max_iters / 2) || it == max_iters) {	//Showing 25 and 50 iterations
												writer.print(size);
												writer.print(";");
												writer.print(it);
												writer.print(";");
												writer.print(numExperts);
												writer.print(";");
												writer.print(numFeatures);
												writer.print(";");
												writer.print(expsDist.toString());
												writer.print(";");
												writer.print(consDist.toString());
												writer.print(";");
												writer.print("Including");
												writer.print(";");
												writer.print(sel.toString());
												writer.print(";");
												writer.print(cros.toString());
												writer.print(";");
												writer.print(mut.toString());
												writer.print(";");
												writer.print(rep.toString());
												writer.print(";");
		
												writer.print(repetition);
												writer.print(";");
												writer.print(System.currentTimeMillis() - time);
		
												
												List<Chromosome> l = new ArrayList<Chromosome>();
								        		for (int i = 0; i < population.length; i++)
								        				l.add(population[i]);
								        		l = NSGA_II_Operators.getFirstFront(l);
								        		for(Chromosome c : l) {
									        		writer.print(";");
								        			writer.print(c);
								        		}
								        		writer.println();
								        	}
								        		
										}
									}
								}
								writer.close();
							}
						}
					}
				}
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}

	
	public static List<List<Chromosome>> PSO(int size_pop, int max_iters, 
			int repetition_size, String file, Distance expsDist, Distance consDist) {

		Chromosome initialExperts = ChromosomeFactory.readChromosome(file, expsDist, consDist);
		int numExperts = initialExperts.getNumExperts();
		int numFeatures = initialExperts.getFeatures();

		List<List<Chromosome>> results = new ArrayList<>();	//All the archives, resulting dominant populations
		try {
			PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter("files/Results/PSOs.csv", true)));

			Initialization[] metInit = {new RandomInitialization(expsDist, consDist), new IncludingInitialization(expsDist, consDist)};

			// direction means whether generationalBest is minimization of maximum distance or average distance
			for(int direction = 0; direction < 2; direction++) {
				for (Initialization init : metInit) {
					for(int repetition = 0; repetition < repetition_size; repetition++) {
						writer.print(size_pop);
						writer.print(";");
						writer.print(max_iters);
						writer.print(";");
						writer.print(numExperts);
						writer.print(";");
						writer.print(numFeatures);
						writer.print(";");
						writer.print(expsDist.toString());
						writer.print(";");
						writer.print(consDist.toString());
						writer.print(";");
						writer.print(init.toString());
						writer.print(";");
						writer.print(direction == 0 ? "Mean" : "Max");
						writer.print(";");
		
						long time = System.currentTimeMillis();
	
						//We initialize here, inside the repetitions, as a common initial position with the same positions,
						//the speed and directions will be similar, and only have variance with r_social and r_cognition
	
						Chromosome[] population = init.initialize(size_pop, numExperts, numFeatures, initialExperts);
						//Initialization already evaluates it
						
						List<Chromosome> archive = new ArrayList<Chromosome>();
						for(Chromosome p : population)
							NSGA_II_Operators.updateArchive(archive, p);
			    		
			    		Random rnd = new Random();
			    		for(int it = 0; it < max_iters; it++) {
			    			double r_cognition = rnd.nextDouble();
			    			double r_social = rnd.nextDouble();
			
			    			Chromosome generationalBestFromArchive = population[0];	//Not null initialization
		
			    			//We move towards the point that minimizes the movement every particle has to 
			    			//do in average. We only add them, because the division is constant in all of the cases.
			    			if(direction == 0) {
				    			double min_total_dist = Double.MAX_VALUE;
				    			for (Chromosome frontPoint : archive) {
				    				double total_dist = 0;
				    				for (Chromosome p : population) {
				    					total_dist += comparator.meassure(frontPoint.getObjectives(),p.getObjectives());
				    				}
				    				
				    				if (total_dist < min_total_dist) {
				    					min_total_dist = total_dist;
				    					generationalBestFromArchive = frontPoint;
				    				}
				    			}
			    			}
			    			
			    			
			    			//We move towards the point that minimizes the movement of the farthest particle has to do. 
			    			//That means that the particle that has to move the most, such value is the smallest among all possible scenarios.
			    			else if (direction == 1) {
				  				double min_max_dist = Double.MAX_VALUE;
								
				    			for (Chromosome frontPoint : archive) {
				    				double max_dist = -Double.MAX_VALUE;;
				    				for (Chromosome p : population) {
				    					double dist =  comparator.meassure(frontPoint.getObjectives(),p.getObjectives());
				    					if (dist > max_dist) {
				    						max_dist = dist;
										}
				    				}
				    				
				    				if (max_dist < min_max_dist) {
				    					min_max_dist = max_dist;
				    					generationalBestFromArchive = frontPoint;
				    				}
				    			}
			    			}	    			
			    			
			    			for(Chromosome p : population) {
			    				p.updatePSO(it, max_iters, r_cognition, r_social, generationalBestFromArchive);
			    				p.evaluate(initialExperts);
			    				p.updateLocalArchive();
			    				
			    				NSGA_II_Operators.updateArchive(archive, p);
			    			}
							archive = NSGA_II_Operators.getElemsFromFirstFront(archive, size_pop);
			
			    				
				        	//end of iteration
						}
			    		results.add(archive);
		
						writer.print(repetition);
						writer.print(";");
						writer.print(System.currentTimeMillis() - time);
		
						
						for(Chromosome c : archive) {
							writer.print(";");
							writer.print(c);
						}
						writer.println();
					}
				}
			}
			writer.close();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		return results;
	}
	
}
