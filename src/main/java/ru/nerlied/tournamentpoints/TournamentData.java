package ru.nerlied.tournamentpoints;

import java.util.ArrayList;
import java.util.List;

public class TournamentData {
	public int tId = 0;
	//Номер раудна
	public int tRoundNumber = 0;
	public int tMatchNumber = 0;
	
	public List<String> tPlayers;
	
	public void clear() {
		tId = 0;
		tRoundNumber = 0;
		tMatchNumber = 0;
		tPlayers = new ArrayList<String>();
	}
}
