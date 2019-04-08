package ru.nerlied.tournamentpoints;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.hiroku.tournaments.util.GsonUtils;

public class TPConfig {
	public static final String CONFIG_FOLDER = "config/tournamentpoints/";
	public static final String MAIN_CONFIG_FILE = CONFIG_FOLDER + "main.json";
	public static TPConfig INSTANCE;
	
	public boolean enableLog = true; 

	//jdbc:protocol://[username[:password]@]host/database
	//jdbc:mysql://root@localhost/wrtx
	public String dbUrl = ""; 

	public String dbTblTournaments = "tournaments";
	public String dbTblPlayers = "player_stats";
	public String dbTblPlayerStatLog = "player_stat_log";

	//Количество очков за победу в матче
	public int pointsAddWinTournament = 0;
	//Количество очков за победу в раунде
	public int pointsAddWinMatch = 0;
	
	//Количество очков за поражение в раунде, обычно меньше нуля
	public int pointsAddLoseMatch = 0;
	//Количество очков за поражение в раунде, обычно меньше нуля
	public int pointsAddLoseTournament = 0;
	
	public static void load() {
		INSTANCE = new TPConfig();
		File file = new File(MAIN_CONFIG_FILE);
		if (!file.exists()) {
			INSTANCE.save();
		} else {
			try {
				FileReader fr = new FileReader(file);
				INSTANCE = (TPConfig) GsonUtils.prettyGson.fromJson(fr, TPConfig.class);
				INSTANCE.save();
				fr.close();
			} catch (IOException var2) {
				var2.printStackTrace();
			}
		}

	}

	public void save() {
		try {
			File configFolder = new File(CONFIG_FOLDER);
			configFolder.mkdirs();
			
			File file = new File(MAIN_CONFIG_FILE);
			if (!file.exists()) {
				file.createNewFile();
			}

			PrintWriter pw = new PrintWriter(file);
			String json = GsonUtils.prettyGson.toJson(this);
			pw.print(json);
			pw.flush();
			pw.close();
		} catch (IOException var4) {
			var4.printStackTrace();
		}
	}
}
