package net.lldv.pydow.playerexperience;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.registry.CommandRegistry;
import net.lldv.pydow.playerexperience.commands.ExperienceCommand;
import net.lldv.pydow.playerexperience.components.api.ExperienceAPI;
import net.lldv.pydow.playerexperience.components.api.database.MongoDB;
import net.lldv.pydow.playerexperience.components.tools.Language;
import net.lldv.pydow.playerexperience.listeners.EventListener;

public class PlayerExperience extends PluginBase {

    private static PlayerExperience instance;

    @Override
    public void onLoad() {
        instance = this;
        registerCommands();
        saveDefaultConfig();
        MongoDB.connect(this);
    }

    @Override
    public void onEnable() {
        Language.init();
        getServer().getPluginManager().registerEvents(new EventListener(), this);
        getConfig().getStringList("BlockBreakXp").forEach(e -> {
            String[] f = e.split(":");
            ExperienceAPI.breakBlockXp.put(f[0] + ":" + f[1], Integer.parseInt(f[2]));
        });
    }

    private void registerCommands() {
        CommandRegistry registry = getServer().getCommandRegistry();
        registry.unregister(this, "xp");
        registry.register(this, new ExperienceCommand(this));
    }

    @Override
    public void onDisable() {
        MongoDB.getMongoClient().close();
    }

    public static PlayerExperience getInstance() {
        return instance;
    }
}
