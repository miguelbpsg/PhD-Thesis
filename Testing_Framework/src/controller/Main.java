package controller;

public class Main {
	public static void main(String[] args) {
		
		for(int i = 0; i < 9 ; i++) {
			Controller c = new Controller();
			c.GA(50, 50, //population size, iterations
				3, 0.8, //tournament players, tournament win ratio
				0.75, 0.15, //crossover ratio, mutation ratio
				"inputs/Tests_"+i+".txt", "inputs/Muts_"+i+".txt");
		}
	}
}
