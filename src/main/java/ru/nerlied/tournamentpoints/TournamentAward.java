package ru.nerlied.tournamentpoints;

import org.spongepowered.api.Game;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.text.Text;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.google.gson.reflect.TypeToken;

import gloomyfolken.mods.core.misc.gson.ItemStackDeserializer;
import gloomyfolken.mods.core.misc.gson.NBTTagCompoundDeserializer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class TournamentAward {
	@Expose
	public String command;
	
	@Expose
	public ItemStack stack;
	
	public String player;
	
	public void execute() {
		if(this.player == null) return;
		
		if(this.command != null && !this.command.equals("")) {
			String cmd = this.getCommand(player);
			
			if(cmd.startsWith("/")) {
				cmd = cmd.substring(1);
				CommandResult result = Sponge.getCommandManager().process(Sponge.getServer().getConsole(), cmd);
				
				if(result != CommandResult.success()) {
					TournamentPoints.LOG.warning("Error while executing command " + this.command + " for " + player + " (" + result.toString() + ")");
				}
			} else {
				Sponge.getServer().getConsole().sendMessage(Text.of(cmd));
			}
		}
		
		if(this.stack != null) {
			Game game = Sponge.getGame();
			EntityPlayerMP playerEntity = (EntityPlayerMP)game.getServer().getPlayer(player).get();
			if(!playerEntity.addItemStackToInventory(this.stack)) {
				playerEntity.entityDropItem(this.stack, 0.0f);
				
				TextComponentString s = new TextComponentString("Предмет " + this.stack.getDisplayName() + " не поместился в инвентаре и выпал на землю!");
				s.getStyle().setColor(TextFormatting.RED);
				
				playerEntity.sendMessage(s);
				TournamentPoints.LOG.warning("Warning giving " + this.stack.getDisplayName() + " for " + player + " (no space in inv): dropping it");
			}
		}
	}
	
	public boolean isNull() {
		return this.player == null || ((this.command == null || this.command.equals("")) && this.stack == null);
	}
	
	public String getCommand(String player) {
    	return getCommand(this.command, player);
    }
    
    private static String getCommand(String command, String player) {
    	return command.replaceAll("<player>", player);
    }
    
    public static TournamentAward[] createFromJson(String json) {
    	if(json.equals("")) return null;
    	
    	Gson gson = new GsonBuilder()
     			.excludeFieldsWithoutExposeAnnotation()
     			.registerTypeAdapter(ItemStack.class, new ItemStackDeserializer())
     			.registerTypeAdapter(NBTTagCompound.class, new NBTTagCompoundDeserializer())
     			.create();
    	
    	try {
    		TypeToken<TournamentAward[]> type = new TypeToken<TournamentAward[]>(){};
    		TournamentAward[] awardList = gson.fromJson(json, type.getType());
    		return awardList;
		} catch (Exception e) {
			e.printStackTrace();
		}
    	
    	return null;
    }
}
