package com.arkflame.classes.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import com.arkflame.classes.language.ClassesLanguage;
import com.arkflame.classes.language.LanguageManager;
import com.arkflame.classes.utils.Players;

public class BardCommand implements CommandExecutor {
  private final LanguageManager languageManager;

  public BardCommand(LanguageManager languageManager) {
    this.languageManager = languageManager;
  }

  public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
    ClassesLanguage language = this.languageManager.getLanguage(Players.getLocale(sender));
    sender.sendMessage(language.getEntry("bard"));
    return true;
  }
}
