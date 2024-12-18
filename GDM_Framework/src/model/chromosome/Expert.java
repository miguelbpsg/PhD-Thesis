package model.chromosome;


public abstract class Expert {
	private int id;
	protected double[][] preferencesMatrix;
	protected boolean stability;
	private int features;
	
	//Matrix initialization for the children to extend accordingly
	public Expert(int id, int features) {
		this.id = id;
		this.features = features;
		this.stability = true;
		
		preferencesMatrix = new double[features][features];

	}
	
	//reading from a file where the matrix is stored
	public Expert(int id, double[][]preferencesMatrix, int features) {
		this.id = id;
		this.preferencesMatrix = preferencesMatrix;
		this.features = features;

		stability = preferencesMatrix.length == features && preferencesMatrix[0].length == features;
		if (!stability)
			System.err.println("Matrix " + id + " does not match with features");

		meassureStability();
	}

	protected abstract void meassureStability();
	
	public Expert(Expert expert) {		//copy
		this.id = expert.getId();
		this.preferencesMatrix = expert.copyPreferencesMatrix();
		this.features = expert.getFeatures();
		this.stability = expert.isStable();
	}
		
	public int getId() {
		return id;
	}
	
	public double[][] getPreferencesMatrix() {
		return preferencesMatrix;
	}

	public double getElem(int x, int y) {
		return preferencesMatrix[x][y];
	}
	
	public double[][] copyPreferencesMatrix() {
		double[][] copy = new double[features][features];
		for(int i = 0; i < features; i++)
			for(int j = 0; j < features; j++)
				copy[i][j] = preferencesMatrix[i][j];
		return copy;
	}
	
	public int getFeatures() {
		return features;
	}
	
	public boolean isStable() {
		return stability;
	}

	public String toString() {
		String file = "Expert: " +id + "\n";
		for(int i = 0; i < features; i++) {
			for(int j = 0; j < features; j++) {
				file += preferencesMatrix[i][j];
				file += j == features - 1 ? "\n" : "\t ";
			}
			file += i == features - 1 ? "\n" : "";
		}
		return file;
	}
	
	public String toFile() {
		String file = "";
		for(int i = 0; i < features; i++) {
			for(int j = 0; j < features; j++) {
				file += preferencesMatrix[i][j];
				file += j == features - 1 ? "\n" : "\t ";
			}
			file += i == features - 1 ? "\n" : "";
		}
		return file;
	}
	
	
	public abstract void modify(int x, int y, double val);
	
	
}
