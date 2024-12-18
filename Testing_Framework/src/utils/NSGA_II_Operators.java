package utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import model.chromosome.Chromosome;
import model.chromosome.FSMTest;

public interface NSGA_II_Operators {
	static double marginError = 0.000000001;
	
	//this calculates the hypervolume of a hypercube, not a mixed one. For that, check calculateHypervolume
	private static double Hypervolume(Chromosome c, double[] bottom, double[] top) {
		double[] point = project(c,bottom);
		double hypervolume = 1;
		for(int objective = 0; objective < top.length; objective++) {
			hypervolume *= (top[objective] - point[objective]);
		}
		return hypervolume;
	}
	
	private static double[] project(Chromosome c, double[] bottom) {
		double[] point = new double[bottom.length];
		for(int objective = 0; objective < bottom.length; objective++)
			point[objective] = Math.max(bottom[objective], c.getObjective(objective));
		return point;
	}
	
	private static double[] newTop(Chromosome c, int dimension, double[] top) {
		double[] point = top.clone();
		point[dimension] = c.getObjective(dimension);
		return point;
	}
	
	private static double[] newBottom(Chromosome c, int dimension, double[] bottom) {
		double[] point = bottom.clone();
		for(int i = 0; i < dimension; i++)
			point[i] = c.getObjective(i);
		return point;
	}
	
	public static double hypervolumeConfiguration4Testing(List<Chromosome> front, List<FSMTest> totalTests) {
		if(!front.isEmpty() && front.get(0).getNumObjectives()==2) {
			double totalInputs = 0;
			for(FSMTest t : totalTests)
				totalInputs += t.getSize();
			double[] bottom = {0.0, 0.0};
			double[] top = {totalInputs, 1.0};
			return calculateHypervolume(front, bottom, top);
		}
		return 0;
	}
	
	private static double calculateHypervolume(List<Chromosome> front, double[] bottom, double[] top) {

		if(front.size() < 1) 
			return 0;
		Chromosome pivot = front.get(0);
		double hv = Hypervolume(pivot,bottom,top);
		for(int dim = 0; dim < bottom.length; dim++) {
			List<Chromosome> dominatingFront = new ArrayList<Chromosome>();
			for(int i = 1; i < front.size(); i++) {
				Chromosome elem = front.get(i);
				if(elem.getObjective(dim) < pivot.getObjective(dim) - marginError) //we move towards the better elements in such dimension (minimisation)
					dominatingFront.add(elem);
			}
			if(!dominatingFront.isEmpty()) {
				double[] newBottom = newBottom(pivot, dim, bottom);
				double[] newTop = newTop(pivot, dim, top);
				hv += calculateHypervolume(dominatingFront, newBottom, newTop);
			}
		}
		return hv;
	}

	public static List<Chromosome> getFirstFront(List<Chromosome> elems) {
		List<Chromosome> front = new ArrayList<Chromosome>();
		for (Chromosome p : elems) { // for each element p in the population
			int n_p = 0; // counter of elements that dominate the current element
			for (Chromosome q : elems) { // for each element q in the population
				if (!q.equals(p)) { // if it is different from p
					if (XdominatesY(q, p)) { // if q dominates p
						n_p++; // increase the counter of solutions that dominate p
					}
				}
			}
			if (n_p == 0) { // if no element dominates p
				front.add(p); // it is part of the first front
			}
		}
		return front;
	}

	public static List<List<Chromosome>> fastNonDominatedSort(List<Chromosome> elems) {
		List<List<Chromosome>> fronts = new ArrayList<List<Chromosome>>(); // array that stores in each position the set of keys that form the each fron
		
		fronts.add(new ArrayList<Chromosome>()); // initialize the first front
		
		for (Chromosome p : elems) { // for each element p in the population
			List<Chromosome> S_p = new ArrayList<Chromosome>(); // create a set to store the elements that it dominates
			int n_p = 0; // counter of elements that dominate the current element
			for (Chromosome q : elems) { // for each element q in the population
				if (!q.equals(p)) { // if it is different from p
					if (XdominatesY(p, q)) { // if p dominates q
//						if(!S_p.contains(q))
							S_p.add(q); // we add q to the set of elements dominated by p
					}
					else if (XdominatesY(q, p)) { // if q dominates p
						n_p++; // increase the counter of solutions that dominate p
					}
				}
			}
			p.setDominatedSet(S_p); // add the set of elements dominated by p to the list
			if (n_p == 0) { // if no element dominates p
				fronts.get(0).add(p); // it is part of the first front
				p.setFront(0);
			}
			p.setDominationNumber(n_p); // include the counter to the set of counters
		}
		
		int f = 0;
		while (f < fronts.size() && !fronts.get(f).isEmpty()) { // for each front f
			List<Chromosome> next_front = new ArrayList<Chromosome>(); // initialize the next front
			for(Chromosome p : fronts.get(f)) { // for each element p in f
				List<Chromosome> dominated_set = p.getDominatedSet();
				for (Chromosome q : dominated_set) { // for each element q dominated by p
					q.decreaseDominationNumber();  // decrease the domination counter
					if (q.getDominationNumber() == 0) { // if the updated counter is 0
						next_front.add(q); // it means that is is in the next front
						q.setFront(f+1);
					}
				}
			}

			if(!next_front.isEmpty()) {
				fronts.add(next_front); // add the next front to the list of fronts
			}
			f++;
		}
			
		return fronts;
	}
	  

	public static List<List<Chromosome>> fastNonDominatedSort(List<Chromosome> elems, int numElems) {
		List<List<Chromosome>> fronts = new ArrayList<List<Chromosome>>(); // array that stores in each position the set of keys that form the each front
		
		fronts.add(new ArrayList<Chromosome>()); // initialize the first front

		for (Chromosome p : elems) { // for each element p in the population
			List<Chromosome> S_p = new ArrayList<Chromosome>(); // create a set to store the elements that it dominates
			int n_p = 0; // counter of elements that dominate the current element
			for (Chromosome q : elems) { // for each element q in the population
				if (!q.equals(p)) { // if it is different from p
					if (XdominatesY(p, q)) { // if p dominates q
//						if(!S_p.contains(q))
							S_p.add(q); // we add q to the set of elements dominated by p
					}
					else if (XdominatesY(q, p)) { // if q dominates p
						n_p++; // increase the counter of solutions that dominate p
					}
				}
			}
			p.setDominatedSet(S_p); // add the set of elements dominated by p to the list
			if (n_p == 0) { // if no element dominates p
				fronts.get(0).add(p); // it is part of the first front
				p.setFront(0);
			}
			p.setDominationNumber(n_p); // include the counter to the set of counters
		}
		
		int f = 0;
		int selected = 0;
		while (f < fronts.size() && !fronts.get(f).isEmpty() && selected < numElems) { // for each front f
			List<Chromosome> next_front = new ArrayList<Chromosome>(); // initialize the next front
			for(Chromosome p : fronts.get(f)) { // for each element p in f
				List<Chromosome> dominated_set = p.getDominatedSet();
				for (Chromosome q : dominated_set) { // for each element q dominated by p
					q.decreaseDominationNumber();  // decrease the domination counter
					if (q.getDominationNumber() == 0) { // if the updated counter is 0
						next_front.add(q); // it means that is is in the next front
						selected++;
						q.setFront(f+1);
					}
				}
			}

			if(!next_front.isEmpty()) {
				fronts.add(next_front); // add the next front to the list of fronts
			}
			f++;
		}
		
		return fronts;
	}

	
	
	public static boolean XdominatesY(Chromosome x, Chromosome y) { //minimizing all objectives
		boolean dominates = true;
		boolean strict = false;
		
		int numObjs = x.getNumObjectives();
		
		int i = 0;
		while (i < numObjs && dominates) {
			dominates = x.getObjective(i) <= y.getObjective(i);
			if (!strict) {
				strict = x.getObjective(i) + marginError < y.getObjective(i);
			}
			i++;
		}
		return dominates && strict;
	}
	  
	public static boolean XdominatesY(double[] x, double[] y) { //minimizing all objectives
		boolean dominates = true;
		boolean strict = false;
		
		
		int i = 0;
		while (i < x.length && dominates) {
			dominates = x[i] + marginError <= y[i];
			if (!strict) {
				strict = x[i] + marginError < y[i];
			}
			i++;
		}
		return dominates && strict;
	}

	
	public static void crowdingDistanceAssignment(List<Chromosome> front){
		int length = front.size();
		
		for (Chromosome item : front) {
			item.setDistance(0.0); // initialize the distances
		}
		
		int num_objectives = front.iterator().next().getNumObjectives();	// get the number of objectives to be evaluated
		
		for (int i = 0; i < num_objectives; i++) { // for every objective

			List<Chromosome> sol = normalSort(front,i); //sort the front for objective i
			
			double fmin = sol.get(0).getObjective(i);
			double fmax = sol.get(length - 1).getObjective(i);
			double norm = fmax-fmin;
			
			sol.get(0).setDistance(Double.MAX_VALUE);// set to infinity the first and last element
			sol.get(length - 1).setDistance(Double.MAX_VALUE);
			for (int j = 1; j < length - 1; j++) { // for every element in the front
				if (sol.get(j).getCrowdingDistance() != Double.MAX_VALUE) {
					sol.get(j).addDistance((sol.get(j+1).getObjective(i)-sol.get(j-1).getObjective(i)) / norm); // compute distance to its neighbours
				}
			}
		}
	}
	
	
	public static List<Chromosome> normalSort(List<Chromosome> front, int objective){
		List<Chromosome> sol = new ArrayList<Chromosome>(front);
		
		
		sol.sort(new Comparator<Chromosome>() {
			public int compare(Chromosome o1, Chromosome o2) { //min to max
				if (o1.getObjective(objective) < o2.getObjective(objective))
					return -1;
				if (o1.getObjective(objective) > o2.getObjective(objective))
					return 1;
//				if (o1.getObjective(objective) == o2.getObjective(objective))
				return 0;
			}
		});
		return sol;
	}
	
	public static List<Chromosome> NSGASort(List<Chromosome> front) {
		List<Chromosome> sol = new ArrayList<Chromosome>(front);

		sol.sort(new Comparator<Chromosome>() {
			public int compare(Chromosome o1, Chromosome o2) { //min to max
				if (o1.getFront() < o2.getFront())
					return -1;
				if (o1.getFront() > o2.getFront())
					return 1;
//				if (o1.getObjective(objective) == o2.getObjective(objective))
				if (o1.getFront() == o2.getFront()) {
					if(o1.getCrowdingDistance() > o2.getCrowdingDistance())
						return -1;
					if(o1.getCrowdingDistance() < o2.getCrowdingDistance())
						return 1;
					return 0;
				}
				return 0;
			}
		});
		return sol;
	}
	
	//uncalled?
	public static List<Chromosome> NSGAFullSort(List<List<Chromosome>> fronts) {
		List<Chromosome> sol = new ArrayList<Chromosome>();
		for(List<Chromosome> front : fronts)
			sol.addAll(front);
		
		sol.sort(new Comparator<Chromosome>() {
			public int compare(Chromosome o1, Chromosome o2) { //min to max
				if (o1.getFront() < o2.getFront())
					return -1;
				if (o1.getFront() > o2.getFront())
					return 1;
//				if (o1.getObjective(objective) == o2.getObjective(objective))
				if (o1.getFront() == o2.getFront()) {
					if(o1.getCrowdingDistance() > o2.getCrowdingDistance())
						return -1;
					if(o1.getCrowdingDistance() < o2.getCrowdingDistance())
						return 1;
					return 0;
				}
				return 0;
			}
		});
		return sol;
	}

	public static List<Chromosome> NSGASort(Chromosome[] fronts) {
		List<Chromosome> sol = new ArrayList<Chromosome>();
		
		for(Chromosome c : fronts)
			sol.add(c);
		
		sol.sort(new Comparator<Chromosome>() {
			public int compare(Chromosome o1, Chromosome o2) { //min to max
				if (o1.getFront() < o2.getFront())
					return -1;
				if (o1.getFront() > o2.getFront())
					return 1;
//				if (o1.getObjective(objective) == o2.getObjective(objective))
				if (o1.getFront() == o2.getFront()) {
					if(o1.getCrowdingDistance() > o2.getCrowdingDistance())
						return -1;
					if(o1.getCrowdingDistance() < o2.getCrowdingDistance())
						return 1;
					return 0;
				}
				return 0;
			}
		});
		return sol;
	}
	
	public static Chromosome better(Chromosome c1, Chromosome c2) { //in minimization problems
				//< in minimization, > in maximization
		if (c1.getFront() < c2.getFront())	//only change these two operators if maximization
			return c1;
				//> in minimization, < in maximization
		if (c1.getFront() > c2.getFront())	//only change these two operators if maximization
			return c2;
//		if (o1.getObjective(objective) == o2.getObjective(objective))
		if (c1.getFront() == c2.getFront()) {
			if(c1.getCrowdingDistance() > c2.getCrowdingDistance())	//independent on minimization/maximization
				return c1;
			if(c1.getCrowdingDistance() < c2.getCrowdingDistance())
				return c2;
		}
		return c1;
		
	}
}
