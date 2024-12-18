package controller;

import model.selection.Selection;
import model.selection.Tournament;
import model.selection.Truncation;

public class SelectionFactory {
	
	public static Selection createSelection(int type, int participants, double win_ratio, double elit) {
		switch(type) {
		case 1:
			return new Tournament(participants, win_ratio);
		case 2:
			return new Truncation(elit);
		default:
			System.err.println("PROBLEM CREATING SELECTION");
			System.err.printf("Type %d, Participants %d, Elitism %f, Win ratio %f", type, participants, elit, win_ratio);
			System.err.println();
			return null;
		}
	}
	
	public static Selection createSelection(Selection s, int participants, double win_ratio, double elit) {
		return createSelection(s.getSelection(), participants, win_ratio, elit);
	}
	
}
