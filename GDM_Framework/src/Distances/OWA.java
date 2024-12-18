package Distances;

import java.util.Arrays;

import org.apache.commons.lang3.ArrayUtils;

import model.chromosome.Expert;

public class OWA {

	//Linguistic cuantifier
	private static double Q(double r, double a, double b) {
		//a has to be smaller than b by definition.
		if (a > b) {
			double aux = a;
			a = b;
			b = aux;
		}
		if (r < a)
			return 0;
		if(a <= r && r <= b)
			return (r-a)/(b-a);
		//if (r > b)
		return 1;
	}
	
	@SuppressWarnings("unused")
	private static double Q_AtLeastHalf(double r) {
		return Q(r, 0, 0.5);
	}

	@SuppressWarnings("unused")
	private static double Q_MostOf(double r) {
		return Q(r, 0.3, 0.8);
	}

	private static double Q_AsManyAsPossible(double r) {
		return Q(r, 0.5, 1);
	}
	
	//OWA weights
	private static double weight(int k, int n) {
		return Q_AsManyAsPossible(((double)k)/n) - Q_AsManyAsPossible(((double)(k-1))/n);
	}

	@SuppressWarnings("unused")
	private static double weight(int k, int n, double a, double b) {
		return Q(((double)k)/n,a,b) - Q(((double)(k-1))/n,a,b);
	}
	
	//OWA
	private static double phi(double[] p) {
		Arrays.sort(p);
		ArrayUtils.reverse(p);
		double sum = 0;
		for(int i = 0; i < p.length; i++)
			sum += weight(i+1,p.length)*p[i];
		return sum;
	}

	//Similarity Matrix
	private static double[][] SM(Expert[] experts, int index, Simillarity s) {
		double[][] sm = new double[experts[0].getFeatures()][experts[0].getFeatures()];
		
		double[] copies = new double[experts.length - 1];
		double[] others = new double[experts.length - 1];
		for(int i = 0; i < experts[0].getFeatures(); i++) {
			for(int j = 0; j < experts[0].getFeatures(); j++) {
				for(int k = 0; k < experts.length - 1; k++) {
					copies[k] = experts[index].getElem(i, j);
					others[k] = experts[k + (k < index ? 0 : 1)].getElem(i, j);
				}
				sm[i][j] = s.meassure(copies, others);
			}
		}
		
		return sm;
	}

	//Consensus Matrix
	private static double[][] CM(Expert[] experts, Simillarity s) {
		int numFeatures = experts[0].getFeatures();
		double[][] cm = new double[numFeatures][numFeatures];

		double[][][] sms = new double[experts.length][numFeatures][numFeatures];
		for(int i = 0; i < experts.length; i++) {
			sms[i] = SM(experts, i, s);
		}
			
		double[] copies = new double[experts.length];
		
		for(int i = 0; i < numFeatures; i++) {
			for(int j = 0; j < numFeatures; j++) {
				for(int k = 0; k < experts.length; k++) {
					copies[k] = sms[k][i][j];
				}
				cm[i][j] = phi(copies);
			}
		}
		
		return cm;
	}
	
	private static double[][] consensusMatrix(Expert[] experts, Simillarity s) {
		return CM(experts, s);
	}
	
	private static double[] consensusVector(Expert[] experts, Simillarity s) {
		double[][] cm = consensusMatrix(experts, s);

		int numFeatures = experts[0].getFeatures();

		double[] cv = new double[numFeatures];
		double[] copies = new double[2*numFeatures - 2];

		for(int i = 0; i < numFeatures; i++) {
			int k = 0;
			for(int j = 0; j < numFeatures; j++) {
				if(i != j) {
					copies[k] = cm[i][j];
					k++;
					copies[k] = cm[j][i];
					k++;
				}
			}
			cv[i] = phi(copies);
		}
		return cv;
	}

	
	public static double consensus(Expert[] experts, Simillarity s) {
		return phi(consensusVector(experts, s));
	}


}
