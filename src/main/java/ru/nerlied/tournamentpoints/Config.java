package ru.nerlied.tournamentpoints;

public class Config {	
	public static boolean enableLog = true; 

	//jdbc:protocol://[username[:password]@]host/database
	//jdbc:mysql://root@localhost/wrtx
	public static String dbUrl = ""; 

	public static String dbTblTournaments = "tournaments";
	public static String dbTblPlayers = "player_stats";
	public static String dbTblPlayerStatLog = "player_stat_log";

	//Количество очков за победу в матче
	public static int pointsAddWinTournament = 0;
	//Количество очков за победу в раунде
	public static int pointsAddWinMatch = 0;
	
	//Количество очков за поражение в раунде, обычно меньше нуля
	public static int pointsAddLoseMatch = 0;
	//Количество очков за поражение в раунде, обычно меньше нуля
	public static int pointsAddLoseTournament = 0;
	
	public static void load() {
		Utils.loadJson(Const.CONFIG_MAIN, Config.class);
		save();
	}

	public static void save() {
		Utils.save(Const.CONFIG_FOLDER, Const.CONFIG_MAIN, new Config());
	}
}
