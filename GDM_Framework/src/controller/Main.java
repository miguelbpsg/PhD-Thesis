package controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;

import Distances.BrayCurtisDistance;
import Distances.CanberraDistance;
import Distances.Distance;
import Distances.EuclideanDistance;
import Distances.ManhattanDistance;
import model.chromosome.Chromosome;
import utils.NSGA_II_Operators;

public class Main {
	public static void main(String[] args) {		

		//CASE CREATION
			//16 groups of experts varying:
					//5 10 15 20 features
					//6 10 14 18 experts
		Chromosome c;
		for(int numExperts = 6; numExperts <= 18; numExperts += 4) {
			for(int numFeatures = 5; numFeatures <= 20; numFeatures += 5) {
				c = new Chromosome(numExperts, numFeatures, new EuclideanDistance(), new EuclideanDistance());
				
			//writing the file
				try {
					PrintWriter writer = new PrintWriter("files/input/Experts"+"_" + numExperts + "_Feat_" + numFeatures + ".txt", "UTF-8");
					writer.print(c.toFile());
					writer.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}

			//EXECUTION		
		long time = System.currentTimeMillis();
		System.out.println("PSOs:");

		int[] size_pop = {25,50};
		int[] max_iters = {25,50};
		int repetitions = 20;
		Distance[] distances = {new BrayCurtisDistance(), new CanberraDistance(),
				new EuclideanDistance(), new ManhattanDistance()};
		for(int numExperts = 6; numExperts <= 18; numExperts += 4) {
			for(int numFeatures = 5; numFeatures <= 20; numFeatures += 5) {
				for(Distance expsDist : distances) {
					for(Distance consDist: distances) {
						for(int size : size_pop) {
							for(int iters : max_iters) {
								Controller.PSO(size, iters, repetitions, "files/input/Experts"+"_" + numExperts + "_Feat_" + numFeatures + ".txt", expsDist, consDist);
							}
						}
						System.out.println(System.currentTimeMillis() - time);
					}
				}
			}
		}
   
		System.out.println("GAs:");
		
//		Distance[] distances = {new BrayCurtisDistance(), new CanberraDistance(),
//				new EuclideanDistance(), new ManhattanDistance()};
		for(int numExperts = 6; numExperts <= 18; numExperts += 4) {
			for(int numFeatures = 5; numFeatures <= 20; numFeatures += 5) {
				for(Distance expsDist : distances) {
					for(Distance consDist: distances) {
							Controller.GA("files/input/Experts"+"_" + numExperts + "_Feat_" + numFeatures + ".txt", expsDist, consDist);
							System.out.println(System.currentTimeMillis()- time);
					}
				}
			}
		}
		
		
		
		//QUALITY INDICATORS

		String inFile = "";
		try {
			BufferedReader br = new BufferedReader(new FileReader("files/Results/Reds.csv")); //only the points
			StringBuilder sb = new StringBuilder();
			String line = br.readLine();

			while (line != null) {
				sb.append(line);
				line = br.readLine();
				if (line != null)
					sb.append("\n");
			}
			inFile = sb.toString();
		    br.close();
		}
		catch(IOException e) {
			System.err.println(e.getMessage());
		}
		String[] lines = inFile.split("\n");
		String[] line;
		List<Double[][]> resultsPSO = new ArrayList<Double[][]>();
		Double[][] front;
		for(int i = 0; i < lines.length; i++) {
			line = lines[i].split(";");
			front = new Double[line.length/2][2];
			for(int j = 0; j < line.length; j++) {
				front[j/2][j%2] = Double.parseDouble(line[j]);
			}
			resultsPSO.add(front);
		}
		
		@SuppressWarnings("unused")
		int PSOConfigurations = 256;	//256 total PSO configurations, 16 cases of experts/features at each PSO, which is one GA file. That's why there's only 16 GA configurations per file
		int casesPerPSO = 320;	//320 in PSO, 3840 in GA
		int GAConfigurations = 16;	//256 in PSO, 16 in GA
		int casesPerGA = 3840;	//320 in PSO, 3840 in GA
		int iteration = 0;
		for(int numExperts = 6; numExperts <= 18; numExperts += 4) {
			for(int numFeatures = 5; numFeatures <= 20; numFeatures += 5) {
				inFile = "";
				try {
					BufferedReader br = new BufferedReader(new FileReader("files/Results/GAs/" + numExperts +  " " + numFeatures + "Red.csv"));
					StringBuilder sb = new StringBuilder();
					String lin = br.readLine();
		
					while (lin != null) {
						sb.append(lin);
						lin = br.readLine();
						if (lin != null)
							sb.append("\n");
					}
					inFile = sb.toString();
				    br.close();
				}
				catch(IOException e) {
					System.err.println(e.getMessage());
				}
				lines = inFile.split("\n");
				List<Double[][]> resultsGA = new ArrayList<Double[][]>();
				for(int i = 0; i < lines.length; i++) {
					line = lines[i].split(";");
					front = new Double[line.length/2][2];
					for(int j = 0; j < line.length; j++) {
						front[j/2][j%2] = Double.parseDouble(line[j]);
					}
					resultsGA.add(front);
				}				

				
	//HYPERVOLUME INDICATOR		
		        try {
		    		PrintWriter wHVGA = new PrintWriter(new BufferedWriter(new FileWriter("files/Results/GAs/" + numExperts + " " + numFeatures +"HV.csv", true)));
		    		PrintWriter wHVPSO = new PrintWriter(new BufferedWriter(new FileWriter("files/Results/HV.csv", true)));
				for (int problem = 0; problem < GAConfigurations; problem++) {
					for (int caseInFile = 0; caseInFile < casesPerGA; caseInFile++) {					//Obtaining the HV of all GAs in the file
						Double[][] d = resultsGA.get(problem*casesPerGA + caseInFile);					
						double[][] data = new double[d.length][2];
						for(int index = 0; index < d.length; index++) {
							data[index] = ArrayUtils.toPrimitive(d[index]);
						}
			        	wHVGA.print(NSGA_II_Operators.myHypervolumeGDM(data));
		    			wHVGA.println();
					}

					for (int caseInFile = 0; caseInFile < casesPerPSO; caseInFile++) {				//Obtaining the HV of the PSOs of such configuration of experts/featuresGA
						Double[][] d = resultsPSO.get((iteration*16 + problem)*casesPerPSO + caseInFile);	//16 is because PSOConfigurations/4/4
						double[][] data = new double[d.length][2];
						for(int index = 0; index < d.length; index++) {
							data[index] = ArrayUtils.toPrimitive(d[index]);
						}
			        	wHVPSO.print(NSGA_II_Operators.myHypervolumeGDM(data));
		    			wHVPSO.println();
					}
					
	
					
	//EPSILON-INDICATOR
					double minepsilon = Double.MAX_VALUE;
					double[][] best = null; //it should be assigned in the following loops. Needs initialization for the following loop
					for(int i = 0; i < casesPerGA; i++) {
						Double[][] d = resultsGA.get(problem*casesPerGA + i);
						double[][] a = new double[d.length][2];
						for(int index = 0; index < d.length; index++) {
							a[index] = ArrayUtils.toPrimitive(d[index]);
						}
		//					double[][] a = (double[][]) (ArrayUtils.toPrimitive(results.get(1280*problem + i)));
						for(int j = i + 1; j < casesPerGA; j++) {
							d = resultsGA.get(problem*casesPerGA + j);
							double[][] b = new double[d.length][2];
							for(int index = 0; index < d.length; index++) {
								b[index] = ArrayUtils.toPrimitive(d[index]);
							}
		//						double[][] b = (double[][]) (ArrayUtils.toPrimitive(results.get(1280*problem + j)));
							double[] epss = NSGA_II_Operators.I_epsilonAdd(a, b);
							if(epss[0] < minepsilon) {
								minepsilon = epss[0];
								best = a;
							}
							if(epss[1] < minepsilon) {
								minepsilon = epss[1];
								best = b;
							}
						}
					}		//epsilons among GAs
					
					for(int i = 0; i < casesPerPSO; i++) {
						Double[][] d = resultsPSO.get((iteration*16 + problem)*casesPerPSO + i);
						double[][] a = new double[d.length][2];
						for(int index = 0; index < d.length; index++) {
							a[index] = ArrayUtils.toPrimitive(d[index]);
						}
		//					double[][] a = (double[][]) (ArrayUtils.toPrimitive(results.get(1280*problema + i)));
						
						for(int j = i + 1; j < casesPerPSO; j++) {
							d = resultsPSO.get((iteration*16 + problem)*casesPerPSO + j);
							double[][] b = new double[d.length][2];
							for(int index = 0; index < d.length; index++) {
								b[index] = ArrayUtils.toPrimitive(d[index]);
							}
		//						double[][] b = (double[][]) (ArrayUtils.toPrimitive(results.get(1280*problema + j)));
							double[] epss = NSGA_II_Operators.I_epsilonAdd(a, b);
							if(epss[0] < minepsilon) {
								minepsilon = epss[0];
								best = a;
							}
							if(epss[1] < minepsilon) {
								minepsilon = epss[1];
								best = b;
							}
						}
						
						for(int j = 0; j < casesPerGA; j++) {
							d = resultsGA.get(problem*casesPerGA + j);
							double[][] b = new double[d.length][2];
							for(int index = 0; index < d.length; index++) {
								b[index] = ArrayUtils.toPrimitive(d[index]);
							}
		//						double[][] b = (double[][]) (ArrayUtils.toPrimitive(results.get(1280*problema + j)));
							double[] epss = NSGA_II_Operators.I_epsilonAdd(a, b);
							if(epss[0] < minepsilon) {
								minepsilon = epss[0];
								best = a;
							}
							if(epss[1] < minepsilon) {
								minepsilon = epss[1];
								best = b;
							}
						}
					}		//Epsilons among PSOs and between PSOs and GAs
		
			        try {
						PrintWriter wEpsGA = new PrintWriter(new BufferedWriter(new FileWriter("files/Results/GAs/" + numExperts + " " + numFeatures +"eps.csv", true)));
						PrintWriter wEpsPSO = new PrintWriter(new BufferedWriter(new FileWriter("files/Results/Epsilons.csv", true)));
			    		for(int i = 0; i < casesPerGA; i++) {
							Double[][] d = resultsGA.get(problem*casesPerGA + i);
							double[][] a = new double[d.length][2];
							for(int index = 0; index < d.length; index++) {
								a[index] = ArrayUtils.toPrimitive(d[index]);
							}
			    			wEpsGA.print(NSGA_II_Operators.I_epsilonAdd(a, best)[0]);
			    			wEpsGA.println();
			    		}
			    		
						for (int i = 0; i < casesPerPSO; i++) {
							Double[][] d = resultsPSO.get((iteration*16 + problem)*casesPerPSO + i);
							double[][] a = new double[d.length][2];
							for(int index = 0; index < d.length; index++) {
								a[index] = ArrayUtils.toPrimitive(d[index]);
							}
				        	wEpsPSO.print(NSGA_II_Operators.I_epsilonAdd(a, best)[0]);
			    			wEpsPSO.println();
						}
			    		
			    		wEpsGA.close();
			    		wEpsPSO.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
		
				}
				wHVGA.close();
				wHVPSO.close();
		        }
				catch (IOException e) {
					e.printStackTrace();
				}finally {}
		        iteration++;
		        System.out.println(iteration);
			}
		}
		
	}
}
