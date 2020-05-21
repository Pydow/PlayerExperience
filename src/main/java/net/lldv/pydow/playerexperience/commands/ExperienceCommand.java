package net.lldv.pydow.playerexperience.commands;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.PluginCommand;
import cn.nukkit.item.Item;
import cn.nukkit.player.Player;
import cn.nukkit.utils.Config;
import net.lldv.pydow.playerexperience.PlayerExperience;
import net.lldv.pydow.playerexperience.components.api.ExperienceAPI;
import net.lldv.pydow.playerexperience.components.data.ExperienceLevel;
import net.lldv.pydow.playerexperience.components.tools.Command;
import net.lldv.pydow.playerexperience.components.tools.Language;

import java.util.List;

public class ExperienceCommand extends PluginCommand<PlayerExperience> {

    private PlayerExperience plugin;

    public ExperienceCommand(PlayerExperience owner) {
        super(owner, Command.create("experience", "Experience command",
                new String[]{},
                new String[]{"xp", "level"}));
        this.plugin = owner;
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length == 0) {
                ExperienceLevel level = ExperienceAPI.cachedLevels.get(ExperienceAPI.cachedPlayerLevel.get(player.getName()));
                ExperienceLevel nextLevel = ExperienceAPI.cachedLevels.get(ExperienceAPI.cachedPlayerLevel.get(player.getName()) + 1);
                if (nextLevel == null) {
                    player.sendMessage(Language.getAndReplace("my-level", level.getLevel()));
                    return true;
                }
                player.sendMessage(Language.getAndReplace("next-level", ExperienceAPI.cachedExperience.get(player.getName()), nextLevel.getExperience()));
            } else if (args.length == 2 && player.isOp()) {
                Config config = plugin.getConfig();
                if (args[0].equals("setblockxp")) {
                    int experience = Integer.parseInt(args[1]);
                    Item item = player.getInventory().getItemInHand();
                    if (item.getId().getName().equals("air")) {
                        player.sendMessage(Language.getAndReplace("invalid-item"));
                        return true;
                    }
                    List<String> list = config.getList("BlockBreakXp");
                    list.add(item.getId().getName() + ":" + item.getMeta() + ":" + experience);
                    config.set("BlockBreakXp", list);
                    ExperienceAPI.breakBlockXp.put(item.getId().getName() + ":" + item.getMeta(), experience);
                    config.save();
                    config.reload();
                    player.sendMessage(Language.getAndReplace("blockxp-added"));
                }
            } else if (args.length == 4 && player.isOp()) {
                if (args[0].equals("addlevel")) {
                    try {
                        int level = Integer.parseInt(args[1]);
                        int experience = Integer.parseInt(args[2]);
                        if (!ExperienceAPI.levelExists(level)) {
                            ExperienceAPI.createLevelData(level, experience, args[3]);
                            player.sendMessage(Language.getAndReplace("level-created", level));
                        } else player.sendMessage(Language.getAndReplace("level-already-exists"));
                    } catch (NumberFormatException e) {
                        player.sendMessage(Language.getAndReplace("invalid-number"));
                    }
                }
            }
        }
        return true;
    }
}
