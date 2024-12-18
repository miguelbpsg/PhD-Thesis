package model.chromosome;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import Distances.BrayCurtisDistance;
import Distances.CanberraDistance;
import Distances.Distance;
import Distances.EuclideanDistance;
import Distances.OWA;
import Distances.OppositeSimillarity;
import Distances.Simillarity;
import utils.NSGA_II_Operators;

public class Chromosome {
	
	private int numExperts;
	private int numFeatures;
	private Position[] experts;
	
	//private double consensus; objectives[0]
	//private double distance2original; objectives[1]
	private int numObjectives;
	private double[] objectives;
	

	//next two attributes are unrelated, and initialized to avoid warnings, although they are always instantiated to the right configuration
	private Simillarity simillarity = new OppositeSimillarity(new BrayCurtisDistance());	//to obtain consensus
	
	private Distance expertsDistance = new CanberraDistance();	//to measure distance between experts

	//For fitness-based selection methods
	//obsolete since multiobjective
	private double score;
	private double accScore;
	private int rank = 0;	//higher rank = more likely to get chosen
	public double getScore() {return score;}
	public double getAccScore() {return accScore;}
	public int getRank() {return rank;}

	//NSGA-II information
	private int dominationNumber;
	private List<Chromosome> dominatedSet;
	private int front;

	private double NSGA_CrowdingDistance;
	
	private Speed[] speed;

	private final int localArchiveSize = 5;
	private List<Position[]> localArchive = new ArrayList<Position[]>(localArchiveSize);
	private List<double[]> localArchiveObjs = new ArrayList<double[]>(localArchiveSize);

	//This compares the objectives of different solutions for the archives
	private Distance positionsComparator = new EuclideanDistance();
	
	
	public Chromosome(int numExperts, int features, Distance expsDist, Distance consDist) {	//a random expert
		this.numExperts = numExperts;
		this.numFeatures = features;
		experts = new Position[numExperts];
		speed = new Speed[numExperts];
		for(int i = 0; i < numExperts; i++) {
			experts[i] = new Position(i, features);
			speed[i] = new Speed(i,features);
		}

		
		
		dominationNumber = 0;
		dominatedSet = new ArrayList<Chromosome>();
		front = 0;

		
		expertsDistance = expsDist;	//to meassure distance between experts
		simillarity = new OppositeSimillarity(consDist);	//to obtain consensus

		
		numObjectives = 2;
		objectives = new double[numObjectives];
		NSGA_CrowdingDistance = 0;
		objectives[0] = -OWA.consensus(experts, simillarity);
		objectives[1] = 0;	//to assign a value, as it has no initial experts to be compared to
		
		localArchive.add(experts);
		localArchiveObjs.add(objectives);

	}
	
	//This is only to read from a file, and at this moment it is only used to read the inital experts.
	//Thus, the speed is not required, and a chromosome without Speed values is created.
	public Chromosome(Position[] experts, int features, Distance expsDist, Distance consDist) {	// the selected expert
		this.experts = experts;
		this.numExperts = experts.length;
		this.numFeatures = features;

		speed = new Speed[numExperts];
		for(int i = 0; i < numExperts; i++) {
			speed[i] = new Speed(i,features);
		}

		
		dominationNumber = 0;
		dominatedSet = new ArrayList<Chromosome>();
		front = 0;
		
		expertsDistance = expsDist;	//to measure distance between experts
		simillarity = new OppositeSimillarity(consDist);	//to obtain consensus
		
		numObjectives = 2;
		objectives = new double[numObjectives];
		NSGA_CrowdingDistance = 0;
		objectives[0] = -OWA.consensus(experts, simillarity);
		objectives[1] = 0;

		localArchive.add(experts);
		localArchiveObjs.add(objectives);
	}
	
	public Chromosome(Chromosome c) {		//copy
		this.experts = c.copyExperts();
		this.numFeatures = c.getFeatures();
		this.numExperts = c.getNumExperts();

		//PSO added info
		this.speed = c.copySpeed();
		this.localArchive = new ArrayList<Position[]>(c.getArchive());
		this.localArchiveObjs = new ArrayList<double[]>(c.getArchiveObjs());

		this.dominatedSet = new ArrayList<Chromosome>(c.getDominatedSet());
		this.front = c.getFront();
		this.dominationNumber = c.getDominationNumber();
		this.numObjectives = c.getNumObjectives();
		this.objectives = new double[numObjectives];

		for(int i = 0; i < numObjectives; i++) {
			objectives[i] = c.getObjective(i);
		}
		this.expertsDistance = c.getExpertsDistance();	//to measure distance between experts
		this.simillarity = c.getSimillarity();	//to obtain consensus
		
		this.NSGA_CrowdingDistance = c.getNSGACrowdingDistance();
	}
	
	
	//Only called by NSGA-II (GA)
	public Chromosome better(Chromosome other) { //in minimization problems
				//< in minimization, > in maximization
		if (front < other.getFront())	//only change these two operators if maximization
			return this;
				//> in minimization, < in maximization
		if (front > other.getFront())	//only change these two operators if maximization
			return other;
		//if (o1.getObjective(objective) == o2.getObjective(objective))
		if (front == other.getFront()) {
			if(NSGA_CrowdingDistance > other.getNSGACrowdingDistance())	//independent on minimization/maximization
				return this;
			if(NSGA_CrowdingDistance < other.getNSGACrowdingDistance())
				return other;
		}
		return this;
	}

	//Only called by NSGA-II (GA)
	public Chromosome worse(Chromosome other) { // in minimization problems
				//< in minimization, > in maximization
		if (front < other.getFront())	//only change these two operators if maximization
			return other;
				//> in minimization, < in maximization
		if (front > other.getFront())	//only change these two operators if maximization
			return this;
		//if (o1.getObjective(objective) == o2.getObjective(objective))
		if (front == other.getFront()) {
			if(NSGA_CrowdingDistance > other.getNSGACrowdingDistance())	//independent on minimization/maximization
				return other;
			if(NSGA_CrowdingDistance < other.getNSGACrowdingDistance())
				return this;
		}
		return this;
	}
	
	public Position[] getExperts() {
		return experts;
	}
	
	public Position[] copyExperts() {
		Position[] copy = new Position[numExperts];
		for(int i = 0; i < numExperts; i++) {
			copy[i] = new Position(experts[i]);
		}
		return copy;
	}
	
	public Speed[] getSpeed() {
		return speed;
	}
	
	public Speed[] copySpeed() {
		Speed[] copy = new Speed[numExperts];
		for(int i = 0; i < numExperts; i++) {
			copy[i] = new Speed(speed[i]);
		}
		return copy;
	}

	
	public List<Position[]> getArchive() {
		return localArchive;
	}
	
	public List<double[]> getArchiveObjs() {
		return localArchiveObjs;
	}
	
	public Position getExpert(int pos) {
		return experts[pos];
	}
	
	public Speed getSpeed(int pos) {
		return speed[pos];
	}

	public void setExpert(int pos, Position e) {
		experts[pos] = e;
	}
	
	public void setSpeed(int pos, Speed s) {
		speed[pos] = s;
	}

	public int getNumExperts() {
		return numExperts;
	}
	
	public int getFeatures() {
		return numFeatures;
	}
		

	//NSGA
	public void setDominatedSet(List<Chromosome> S_p) {
		dominatedSet = S_p;
	}
	
	public List<Chromosome> getDominatedSet() {
		return dominatedSet;
	}
	
	public void setDominationNumber(int n_p) {
		dominationNumber = n_p;
	}
	
	public void decreaseDominationNumber() {
		if (dominationNumber > 0)
			dominationNumber--;
	}
	
	public int getDominationNumber() {
		return dominationNumber;
	}

	public void setFront(int f) {
		front = f;
	}
	
	public int getFront() {
		return front;
	}
	

	
	public int getNumObjectives() {
		return numObjectives;
	}
	
	public void setObjective(int pos, double d) {
		if (pos < numObjectives)
			objectives[pos] = d;
	}
	
	public double getObjective(int pos) {
		if (pos < numObjectives)
			return objectives[pos];
		return 0;
	}
	
	public double[] getObjectives() {
		return objectives;
	}
	
	public void setCrowdingDistance(double d) {
		NSGA_CrowdingDistance = d;
	}
	
	public void addCrowdingDistance(double d) {
		if (NSGA_CrowdingDistance != Double.MAX_VALUE && NSGA_CrowdingDistance + d > 0) {
			NSGA_CrowdingDistance += d;
		}
	}
	
	public double getNSGACrowdingDistance() {
		return NSGA_CrowdingDistance;
	}
	
	public Distance getExpertsDistance() {
		return expertsDistance;	//to measure distance between experts
	}
	
	public Simillarity getSimillarity() {
		return simillarity;	//to obtain consensus
	}
	
	//PSO
	public void updatePSO(double iteration, double max_iters, 
			double randomCognition, double randomSocial, Chromosome generationalBest) {

		double inertia = 0.9 - (0.5* iteration / max_iters);
		double cognition = 2.5 - (2* iteration / max_iters);	//computational intelligence 2007 andries engelbrecht
		double socialWeight = 0.5 + (2* iteration / max_iters);	
		double distance = Double.MAX_VALUE;
		int index = 0;
		for (int i = 0; i < localArchiveObjs.size(); i++) {
			double[] p = localArchiveObjs.get(i);
			double nd = positionsComparator.meassure(p, objectives);
			if (nd < distance) {
				distance = nd;
				index = i;
			}
		}
		Position[] localBest = localArchive.get(index);
		
		for(int i = 0; i < numExperts; i++) {
			experts[i].updatePosition(speed[i]);
			speed[i].updateSpeed(experts[i], localBest[i], generationalBest.getExpert(i), 
					cognition, socialWeight, randomCognition, randomSocial, inertia);
		}
		//The chromosome is evaluated in controller right after this,
		//with the local and global archives
	}

	
	//The objectives have already been updated
	public void updateLocalArchive() {
		boolean dominated = false;
		for(double[] objs : localArchiveObjs) {
			if (!dominated)
				dominated = NSGA_II_Operators.XdominatesY(objs, objectives);
		}
			
		if (!dominated) {
			removeDominatedArchive();
			if (localArchive.size() == localArchiveSize) {
				double distance = -Double.MAX_VALUE;
				int index = 0;
				for (int i = 0; i < localArchiveObjs.size(); i++) {
					double[] objectives_archive = localArchiveObjs.get(i);
					double new_distance = positionsComparator.meassure(objectives_archive, objectives);
					if (new_distance > distance) {
						distance = new_distance;
						index = i;
					}
				}
				localArchive.set(index, experts.clone());
				localArchiveObjs.set(index, objectives.clone());
				
			}
			
			else if (localArchive.size() < localArchiveSize) {
				//in order not to later modify the same pointer and have inconsistent data
				localArchive.add(experts.clone());
				localArchiveObjs.add(objectives.clone());
					
			}
			
		}
	}
	
	
	private void removeDominatedArchive() {
		int i = 0;
		boolean completed = false;
		while (!completed  && i < localArchive.size()) {
			if (NSGA_II_Operators.XdominatesY(objectives, localArchiveObjs.get(i))) {
				completed = removeAllFromEnd(i);
			}
			i++;
		}
	}

	private boolean removeAllFromEnd(int lowerBound) {
		int j = localArchive.size() - 1;
		boolean stop = false;
		double[] objs = null;
		Position[] arch = null;

		while(j >= lowerBound && !stop) {
			objs = localArchiveObjs.get(j);
			arch = localArchive.get(j);
			localArchive.remove(j);
			localArchiveObjs.remove(j);
			if (!NSGA_II_Operators.XdominatesY(objectives, objs)) {
				stop = true;
			}
			j--;
		}
		if (j >= lowerBound) {
			localArchive.set(lowerBound, arch);
			localArchiveObjs.set(lowerBound, objs);
			return false;
		}
		return true;
	}
	
	
//FITNESS (outdated since multiobjective)
	public void evaluate(Chromosome initialExperts) {
		objectives[0] = -OWA.consensus(experts, simillarity);
		
		double dist = 0;
		for(int i = 0; i < experts.length; i++) {
			dist += expertsDistance.meassure(experts[i], initialExperts.getExpert(i));
		}
		dist = dist / numExperts;
		
		objectives[1] = dist;

	}
	

	
	@Override
	public String toString() {
		
		/*
		String s = "";
		for(int i = 0; i < numExperts; i++)
			s += experts[i].toString();
		return s;*/
		return  String.format(Locale.US, "{\"consensus\":%.10f,\"distance\":%.10f}", objectives[0],objectives[1]); 
	}
	
	public String toFile() {
		String s = "" + numExperts + "\n" + numFeatures + "\n";
		for(int i = 0; i < numExperts; i++)
			s += experts[i].toFile();
		return s;
		
	}

}
