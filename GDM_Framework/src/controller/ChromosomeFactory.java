package controller;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import Distances.Distance;
import model.chromosome.Chromosome;
import model.chromosome.Position;

public class ChromosomeFactory {
	
	//Copies
	public static Chromosome copyChromosome(Chromosome c) {
		return new Chromosome(c);
	}
	
	//New individuals, usually from file where the experts are
	public static Chromosome createChromosome(Position[] experts, int features, Distance expsDist, Distance consDist) {
		return new Chromosome(experts, features, expsDist, consDist);
	}

	
	public static Chromosome createChromosome(int num_experts, int features, Distance expsDist, Distance consDist) {
		return new Chromosome(num_experts, features, expsDist, consDist);
	}

	//From file, just the positions, no speeds
	public static Chromosome readChromosome(String file, Distance expsDist, Distance consDist) {
		String inFile = "";
		try {
			BufferedReader br = new BufferedReader(new FileReader(file));
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

		int numExperts = Integer.parseInt(lines[0]);
		int numFeatures = Integer.parseInt(lines[1]);
		
		Position[] experts = new Position[numExperts];
		
		int i = 2;
		for(int expert = 0; expert < numExperts; expert++) {
			double[][] matrix = new double[numFeatures][numFeatures];
			for(int row = 0; row < numFeatures; row++) {
				String[] line = lines[i].split("\t");
				for(int col = 0; col < numFeatures; col++) {
					matrix[row][col] = Double.parseDouble(line[col]);
				}
				i++;
			}
			i++;	//each expert has an extra blank line
			experts[expert] = new Position(expert, matrix, numFeatures);
		}

		return new Chromosome(experts, numFeatures, expsDist, consDist);
	}
	
}
