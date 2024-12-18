package utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import model.chromosome.Chromosome;

public interface NSGA_II_Operators {
	static double marginError = 0.000000001;

	//requires to be normalized, normalize if not done
	//returns {epsilon(A,B), epsilon(B,A)}
	public static double[] I_epsilonAdd(List<Chromosome> A, List<Chromosome> B) {
		
		int dim = 0;
		if(!A.isEmpty())
			dim = A.get(0).getNumObjectives();
		double maxepsilon = -Double.MAX_VALUE;
		double maxepsilon2 = -Double.MAX_VALUE;
		double[] epsilon2 = new double[A.size()];
		for(int i = 0; i < A.size(); i++)
			epsilon2[i] = Double.MAX_VALUE;
		for(Chromosome b : B) {
			double epsilon = Double.MAX_VALUE;
			for(int index = 0; index < A.size(); index++) {
				Chromosome a = A.get(index);
				double localepsilon = -Double.MAX_VALUE;
				double localepsilon2 = -Double.MAX_VALUE;
				for(int i = 0; i < dim; i++) {
					double value = a.getObjective(i) - b.getObjective(i);
					localepsilon = Double.max(localepsilon, value);
					localepsilon2 = Double.max(localepsilon2, -value);
				}
				epsilon = Double.min(epsilon, localepsilon);
				epsilon2[index] = Double.min(epsilon2[index],localepsilon2);
			}
			maxepsilon = Double.max(maxepsilon, epsilon);
		}
		for(int i = 0; i < A.size(); i++)
			maxepsilon2 = Double.max(maxepsilon2, epsilon2[i]);
		double[] d = {maxepsilon, maxepsilon2};
		return d;
	}
	
	//To just read from file, the values, instead of the objects that would produce memory overflow
	public static double[] I_epsilonAdd(double[][] A, double[][] B) {
		
		int dim = 0;
		if(A.length > 0)
			dim = A[0].length;
		double maxepsilon = -Double.MAX_VALUE;
		double maxepsilon2 = -Double.MAX_VALUE;
		double[] epsilon2 = new double[A.length];
		for(int i = 0; i < A.length; i++)
			epsilon2[i] = Double.MAX_VALUE;
		for(int j = 0; j < B.length; j++) {
			double epsilon = Double.MAX_VALUE;
			for(int k = 0; k < A.length; k++) {
				double localepsilon = -Double.MAX_VALUE;
				double localepsilon2 = -Double.MAX_VALUE;
				for(int i = 0; i < dim; i++) {
					double value = A[k][i] - B[j][i];
					localepsilon = Double.max(localepsilon, value);
					localepsilon2 = Double.max(localepsilon2, -value);
				}
				epsilon = Double.min(epsilon, localepsilon);
				epsilon2[k] = Double.min(epsilon2[k],localepsilon2);
			}

			maxepsilon = Double.max(maxepsilon, epsilon);
		}

		for(int i = 0; i < A.length; i++)
			maxepsilon2 = Double.max(maxepsilon2, epsilon2[i]);
		double[] d = {maxepsilon, maxepsilon2};
		return d;
	}
	
	//this calculates the hypervolume of a hypercube, not a mixed shape. For that, check calculateHypervolume
	private static double Hyperbox(Chromosome c, double[] bottom, double[] top) {
		double[] point = project(c,bottom);			
		double hypervolume = 1;
		for(int objective = 0; objective < top.length; objective++) {
			hypervolume *= (top[objective] - point[objective]);
		}
		return hypervolume <= 0 ? 0 : hypervolume;
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
	
	
	public static double myHypervolumeGDM(List<Chromosome> front) {
		if(!front.isEmpty() && front.get(0).getNumObjectives()==2) {
			double[] bottom = {-1.0, 0.0};
			double[] top = {0, 1.0};
			return calculateHypervolume(front, bottom, top);
		}
		return 0;
	}
	
	//From values/file, to avoid memory overflow
	public static double myHypervolumeGDM(double[][] front) {
		if(front.length > 0 && front[0].length==2) {
			double[] bottom = {-1.0, 0.0};
			double[] top = {0, 1.0};
			return calculateHypervolume(front, bottom, top);
		}
		return 0;
	}

	//From values/file, to avoid memory overflow
	public static double calculateHypervolume(double[][] front, double[] bottom, double[] top) {

		if(front.length <= 0) 
			return 0;
		double pivot[] = front[0];
		double hv = Hypervolume(pivot,bottom,top);
		for(int dim = 0; dim < bottom.length; dim++) {
			int k = 0;
			double[][] dominatingFront = new double[front.length][front[0].length];
			for(int i = 1; i < front.length; i++) {
				double[] elem = front[i];
				if(elem[dim] < pivot[dim] - marginError) {//we move towards the better elements in such dimension (minimisation)
					dominatingFront[k] = elem;
					k++;
				}
			}
			if(k != 0) {
				double[] newBottom = newBottom(pivot, dim, bottom);
				double[] newTop = newTop(pivot, dim, top);
				hv += calculateHypervolume(Arrays.copyOf(dominatingFront,k), newBottom, newTop);
			}
		}
		return hv;
	}
	
	//From values/file, to avoid memory overflow
	private static double Hypervolume(double[] c, double[] bottom, double[] top) {
		double[] point = project(c,bottom);
		double hypervolume = 1;
		for(int objective = 0; objective < top.length; objective++) {
			hypervolume *= (top[objective] - point[objective]);
		}
		return hypervolume <= 0 ? 0 : hypervolume;
	}
	
	//From values/file, to avoid memory overflow
	private static double[] project(double[] c, double[] bottom) {
		double[] point = new double[bottom.length];
		for(int objective = 0; objective < bottom.length; objective++)
			point[objective] = Math.max(bottom[objective], c[objective]);
		return point;
	}
	
	//From values/file, to avoid memory overflow
	private static double[] newTop(double[] c, int dimension, double[] top) {
		double[] point = top.clone();
		point[dimension] = c[dimension];
		return point;
	}
	
	//From values/file, to avoid memory overflow
	private static double[] newBottom(double[] c, int dimension, double[] bottom) {
		double[] point = bottom.clone();
		for(int i = 0; i < dimension; i++)
			point[i] = c[i];
		return point;
	}

	private static double calculateHypervolume(List<Chromosome> front, double[] bottom, double[] top) {

		if(front.size() < 1) 
			return 0;
		Chromosome pivot = front.get(0);
		double hv = Hyperbox(pivot,bottom,top);
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
				p.setFront(0);
				front.add(p); // it is part of the first front
			}
		}
		return front;
	}

	public static List<Chromosome> getElemsFromFirstFront(List<Chromosome> elems, int num_elems) {
		if (elems.size() > num_elems) {
			crowdingDistanceAssignment(elems);
			elems = NSGASort(elems);
			for (int i = elems.size() - 1; i >= num_elems; i--)
				elems.remove(i);
		}
		return elems;
	}

	public static void updateArchive(List<Chromosome> archive, Chromosome p) {
		int i = 0;
		List<Chromosome> deleted = new ArrayList<Chromosome>();
		while (i < archive.size() && !XdominatesY(archive.get(i),p)) {
			Chromosome item = archive.get(i);
			if (XdominatesY(p,item))
				deleted.add(item);
			i++;
		}
		if(i == archive.size())
			archive.add(new Chromosome(p));
		archive.removeAll(deleted);
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
			item.setCrowdingDistance(0.0); // initialize the distances
		}
		
		int num_objectives = front.iterator().next().getNumObjectives();	// get the number of objectives to be evaluated
		
		for (int i = 0; i < num_objectives; i++) { // for every objective

			List<Chromosome> sol = normalSort(front,i); //sort the front for objective i
			
			double fmin = sol.get(0).getObjective(i);
			double fmax = sol.get(length - 1).getObjective(i);
			double norm = fmax-fmin;
			
			sol.get(0).setCrowdingDistance(Double.MAX_VALUE);// set to infinity the first and last element
			sol.get(length - 1).setCrowdingDistance(Double.MAX_VALUE);
			for (int j = 1; j < length - 1; j++) { // for every element in the front
				if (sol.get(j).getNSGACrowdingDistance() != Double.MAX_VALUE) {
					sol.get(j).addCrowdingDistance((sol.get(j+1).getObjective(i)-sol.get(j-1).getObjective(i)) / norm); // compute distance to its neighbours
				}
			}
		}
	}
	
	
	public static List<Chromosome> normalSort(List<Chromosome> front, int objective){
		List<Chromosome> sol = new ArrayList<Chromosome>(front);
		
		
		sol.sort(new Comparator<Chromosome>() {
			public int compare(Chromosome o1, Chromosome o2) { //min to max
				return Double.compare(o1.getObjective(objective), o2.getObjective(objective));
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
					return Double.compare(o1.getNSGACrowdingDistance(), o2.getNSGACrowdingDistance());
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
					return Double.compare(o1.getNSGACrowdingDistance(), o2.getNSGACrowdingDistance());
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
					return Double.compare(o1.getNSGACrowdingDistance(), o2.getNSGACrowdingDistance());
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
			if(c1.getNSGACrowdingDistance() > c2.getNSGACrowdingDistance())	//independent on minimization/maximization
				return c1;
			if(c1.getNSGACrowdingDistance() < c2.getNSGACrowdingDistance())
				return c2;
		}
		return c1;
		
	}
}