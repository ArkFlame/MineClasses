package com.arkflame.classes.commandexecutors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import com.arkflame.classes.language.ClassesLanguage;
import com.arkflame.classes.language.LanguageManager;
import com.arkflame.classes.utils.Players;

public class ClassesCommandExecutor implements CommandExecutor {
  private final LanguageManager languageManager;
  
  private String version;
  
  public ClassesCommandExecutor(LanguageManager languageManager, String version) {
    this.languageManager = languageManager;
    this.version = version;
  }
  
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    ClassesLanguage language = this.languageManager.getLanguage(Players.getLocale(sender));
    if (args.length > 0 && !args[0].equals("help")) {
      sender.sendMessage(language.getEntry("unknown_command").replace("%version%", this.version));
    } else {
      sender.sendMessage(language.getEntry("help").replace("%version%", this.version));
    } 
    return true;
  }
}
