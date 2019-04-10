package ru.nerlied.tournamentpoints;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Utils {
	public static int getCurTime() {
		return (int) (System.currentTimeMillis() / 1000);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Object loadJson(String fileName, Class c) {
		TournamentPoints.LOG.info("Loading: " + fileName);
		Object obj = null;
		
		File file = new File(fileName);
		if (file.exists()) {
			try {
				FileReader fr = new FileReader(file);
				
				Gson gson = new GsonBuilder()
					.excludeFieldsWithModifiers(java.lang.reflect.Modifier.TRANSIENT)
					.create();
				
				obj = gson.fromJson(fr, c);
				fr.close();
			} catch (IOException var2) {
				var2.printStackTrace();
			}
		} else {
			TournamentPoints.LOG.info("File not found: " + fileName);
		}

		return obj;
	}
	
	public static void save(String folder, String fileName, Object obj) {
		TournamentPoints.LOG.info("Saving: " + folder + fileName);

		try {
			File configFolder = new File(folder);
			configFolder.mkdirs();
			
			File file = new File(fileName);
			if (!file.exists()) {
				file.createNewFile();
			}

			PrintWriter pw = new PrintWriter(file);
			
			Gson gson = new GsonBuilder()
				.excludeFieldsWithModifiers(java.lang.reflect.Modifier.TRANSIENT)
				.create();
			
			String json = gson.toJson(obj);
			pw.print(json);
			pw.flush();
			pw.close();
		} catch (IOException var4) {
			var4.printStackTrace();
		}
	}
}
