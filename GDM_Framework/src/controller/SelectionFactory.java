package controller;

import model.selection.Roulette;
import model.selection.Ranking;
import model.selection.Remains;
import model.selection.SUS;
import model.selection.Selection;
import model.selection.Tournament;
import model.selection.Truncation;

public class SelectionFactory {
	
	public static Selection createSelection(int type, int participants, double vict, double elit) {
		switch(type) {
		case 1:
			return new Tournament(participants, vict);
		case 2:
			return new Truncation(elit);
		case 3:
			return new SUS();
		case 4:
			return new Ranking();
		case 5:
			return new Remains(new Roulette());
		case 6:
			return new Roulette();
		default:
			System.err.println("Error at Selection Factory");
			System.err.printf("Type %d, Players %d, Elitism %f, Victory %f", type, participants, elit, vict);
			System.err.println();
			return null;
		}
	}
	
	public static Selection createSelection(Selection s, int participants, double vict, double elit) {
		return createSelection(s.getSelection(), participants, vict, elit);
	}
	
}
