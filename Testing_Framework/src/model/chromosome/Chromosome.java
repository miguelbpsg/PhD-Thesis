package model.chromosome;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Chromosome {
	private List<FSMTest> tests;
	
	private double mutationScore;
	private double numInputs;
	private int id;
	private int front;
	private double crowdingDistance;
	private boolean modified;
	
	private final int numObjectives = 2;
	private double[] objectives; //inputs, ms
	
	private int dominationNumber;
	private List<Chromosome> dominatedSet;

	public Chromosome() {
		this.tests = new ArrayList<FSMTest>();
		this.numInputs = 0;
		this.mutationScore = 1.1;
		this.objectives  = new double[numObjectives];
		this.objectives[0] = numInputs;
		this.objectives[1] = mutationScore;
	}
	
	public Chromosome(List<FSMTest> tests, int id) {	
		this.tests = tests;

		this.id = id;
		int size = 0;
		for(FSMTest t : tests)
			size += t.getSize();
		this.numInputs = size;
		objectives = new double[numObjectives];
		objectives[0] = numInputs;
		this.modified = false;
	}

	public Chromosome(Chromosome c) {		//copy
		this.tests = c.copyGenotype();
		this.mutationScore = c.getMutationScore();
		this.numInputs = c.getNumInputs();
		this.id = c.getId();
		this.front = c.getFront();
		this.crowdingDistance = c.getCrowdingDistance();
		this.modified = c.isModified();
		this.objectives = new double[numObjectives];
		for(int i = 0; i < numObjectives; i++)
			this.objectives[i] = c.getObjective(i);
	}
	
	public Chromosome better(Chromosome other) { //in minimization problems
				//< in minimization, > in maximization
		if (front < other.getFront())	//only change these two operators if maximization
			return this;
				//> in minimization, < in maximization
		if (front > other.getFront())	//only change these two operators if maximization
			return other;
		//if (o1.getObjective(objective) == o2.getObjective(objective))
		if (front == other.getFront()) {
			if(crowdingDistance > other.getCrowdingDistance())	//independent on minimization/maximization
				return this;
			if(crowdingDistance < other.getCrowdingDistance())
				return other;
		}
		return this;
	}
	
	public Chromosome worse(Chromosome other) { // in minimization problems
				//< in minimization, > in maximization
		if (front < other.getFront())	//only change these two operators if maximization
			return other;
				//> in minimization, < in maximization
		if (front > other.getFront())	//only change these two operators if maximization
			return this;
		//if (o1.getObjective(objective) == o2.getObjective(objective))
		if (front == other.getFront()) {
			if(crowdingDistance > other.getCrowdingDistance())	//independent on minimization/maximization
				return other;
			if(crowdingDistance < other.getCrowdingDistance())
				return this;
		}
		return this;
	}
	
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
	
	public void increaseDominationNumber() {
			dominationNumber++;
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
	
	public void setDistance(double d) {
		crowdingDistance = d;
	}
	
	public void addDistance(double d) {
		if (crowdingDistance != Double.MAX_VALUE && crowdingDistance + d > 0) {
			crowdingDistance += d;
		}
	}

	
	public double getMutationScore() {
		return this.mutationScore;
	}
	
	public double getNumInputs() {
		return this.numInputs;
	}
	
	public double getNumTests() {
		return this.tests.size();
	}
	
	public int getId() {
		return this.id;
	}
	
	
	public double getCrowdingDistance() {
		return this.crowdingDistance;
	}
	
	public void setCrowdingDistance(double crowdingDistance) {
		this.crowdingDistance = crowdingDistance;
	}
	
	public Chromosome crowdedComparisonOperator(Chromosome other) {
		if (other.getFront() < this.front) {
			return other;
		}
		else if (this.front == other.getFront()) {
			if (other.getCrowdingDistance() > this.crowdingDistance) {
				return other;
			}
		}
		return this;
	}

	
	public boolean isModified() {
		return modified;
	}
	
	public void setModified(boolean b) {
		modified = b;
	}
	
	public List<FSMTest> getGenotype() {
		return tests;
	}
	
	public List<FSMTest> copyGenotype() {
		List<FSMTest> copy = new ArrayList<FSMTest>();
		for(int i = 0; i < tests.size(); i++) {
			copy.add(new FSMTest(tests.get(i)));
		}
		return copy;
	}

	public FSMTest removeGene(int pos) {
		if (tests.size() > 1) {
			numInputs -= tests.get(pos).getSize();
			objectives[0] = numInputs;
			return tests.remove(pos);
		}
		
		return tests.get(pos);
	}
	
	public FSMTest getGene(int pos) {
		return tests.get(pos);
	}

	public List<FSMTest> getGenes(int init, int end){
		List<FSMTest> l = new ArrayList<FSMTest>();
		for (int i = init; i < end; i++) {
			FSMTest t = tests.get(i);
			l.add(t);
		}
		return l;
	}
	
	public List<FSMTest> getGenes(int init) {
		return getGenes(init, tests.size());
	}
	
	public void setGene(int pos, FSMTest t) {
		//if (!tests.contains(t)) {
		if (!testInList(t)){
			int size = 0;
			if (pos < tests.size())
				tests.remove(pos);
			for(FSMTest test : tests)
				size += test.getSize();
			tests.add(pos, t);
			size += t.getSize();
			numInputs = size;
			objectives[0] = numInputs;
		}
	}	

	public void setGenes(int pos, List<FSMTest> ts) {
		int size = 0;
		FSMTest aTest = null;
		boolean eliminated = false;
		for(int i = tests.size() - 1; i >= pos; i--) {
			aTest = tests.remove(i);
			eliminated = true;
		}
		for(FSMTest test : tests)
			size += test.getSize();
		for(int i = 0; i < ts.size(); i++) {
			//if (!tests.contains(ts.get(i))) {
			if (!testInList(ts.get(i))){
				tests.add(ts.get(i));
				size += ts.get(i).getSize();
			}
		}
		if(eliminated && tests.size() == 0) {
			tests.add(aTest);
			size += aTest.getSize();
		}
		
		numInputs = size;
		objectives[0] = numInputs;

	}
	
	public boolean testInList(FSMTest o) {
		for(FSMTest t : this.tests) {
			if (t.equals(o)) {
				return true;
			}
		}
		return false;
	}
	
	public int getSize() {
		int size = 0;
		for(FSMTest t : tests)
			size += t.getSize();

		objectives[0] = size;
		return size;
	}
	
	public int getSizeTest(int i) {
		return tests.get(i).getSize();
	}
	
	public int getSizeTests(int init, int end) {
		int size = 0;
		for(int i = init; i < end; i++)
			size += tests.get(i).getSize();
		return size;
	}
	
//FITNESS
	
	public void evaluateMutationScore(int[][] testsVSmutants) {
		getSize();
		this.mutationScore = 0;
		int mutantsSize = testsVSmutants[0].length;

		HashSet<Integer> killedMutants = new HashSet<Integer>();
		for (int posTest = 0; posTest < this.tests.size(); posTest++) {
			for(int posMutant = 0; posMutant < mutantsSize; posMutant++) {
				if(testsVSmutants[this.tests.get(posTest).getId()][posMutant] != Integer.MAX_VALUE)
					killedMutants.add(posMutant);
			}
		}
		int killed = killedMutants.size();
		
		this.mutationScore = 1 - ((double)killed) / ((double)mutantsSize);
		this.objectives[1] = this.mutationScore;
	}
	
	
	public String toString() {
		return tests.toString();
	}


	@Override
	public boolean equals (Object c) {
		if (c == this) 
			return true;
		if(! (c instanceof Chromosome))
			return false;
		
		if (this.mutationScore != ((Chromosome)c).getMutationScore()) return false;
		if (this.numInputs != ((Chromosome)c).getNumInputs()) return false;
		
		return this.tests.containsAll(((Chromosome)c).getGenotype());
	}
	
}
