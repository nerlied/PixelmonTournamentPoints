package ru.nerlied.tournamentpoints;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AwardGiver {
	public static List<TournamentAward> awardsToGive = Collections.synchronizedList(new ArrayList<TournamentAward>());
	
	public static void giveAwards() {
		if(!awardsToGive.isEmpty()) {
			TournamentAward cmd;
			
			for(int i = awardsToGive.size() - 1; i >= 0; i--) {
				cmd = awardsToGive.get(i);
				if(Config.enableLog) System.out.println("Executing {" + cmd + "}");
				
				cmd.execute();
				awardsToGive.remove(i);
			}
		}
	}
}