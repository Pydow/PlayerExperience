package net.lldv.pydow.playerexperience.components.api;

import cn.nukkit.item.Item;
import cn.nukkit.level.Sound;
import cn.nukkit.player.Player;
import cn.nukkit.utils.Identifier;
import com.nukkitx.math.vector.Vector3f;
import com.nukkitx.protocol.bedrock.packet.PlaySoundPacket;
import net.lldv.LlamaEconomy.LlamaEconomy;
import net.lldv.pydow.permissionsystem.components.api.PermissionAPI;
import net.lldv.pydow.playerexperience.components.api.database.MongoDB;
import net.lldv.pydow.playerexperience.components.data.ExperienceLevel;
import net.lldv.pydow.playerexperience.components.event.PlayerLevelUpEvent;
import net.lldv.pydow.playerexperience.components.tools.Language;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ExperienceAPI {

    public static HashMap<String, Integer> breakBlockXp = new HashMap<>();

    public static HashMap<Integer, ExperienceLevel> cachedLevels = new HashMap<>();
    public static HashMap<String, Integer> cachedExperience = new HashMap<>();
    public static HashMap<String, Integer> cachedPlayerLevel = new HashMap<>();

    public static void createUserData(String user) {
        Document document = new Document("user", user)
                .append("level", 0)
                .append("experience", 0);
        MongoDB.getUserCollection().insertOne(document);
    }

    public static void createLevelData(int level, int experience, String reward) {
        List<String> list = new ArrayList<>();
        list.add(reward);
        Document document = new Document("level", level)
                .append("experience", experience)
                .append("rewards", list);
        MongoDB.getLevelCollection().insertOne(document);
    }

    public static boolean userExists(String user) {
        Document document = MongoDB.getUserCollection().find(new Document("user", user)).first();
        return document != null;
    }

    public static boolean levelExists(int level) {
        Document document = MongoDB.getLevelCollection().find(new Document("level", level)).first();
        return document != null;
    }

    public static void addExperienceCache(Player player, int experience) {
        int currentExperience = cachedExperience.get(player.getName());
        cachedExperience.remove(player.getName());
        cachedExperience.put(player.getName(), currentExperience + experience);
        checkForLevelUp(player);
    }

    public static void setExperience(String user) {
        CompletableFuture.runAsync(() -> {
            Document document = new Document("user", user);
            Document found = MongoDB.getUserCollection().find(document).first();
            assert found != null;
            int experience = cachedExperience.get(user);
            Bson newEntry = new Document("experience", experience);
            Bson newEntrySet = new Document("$set", newEntry);
            MongoDB.getUserCollection().updateOne(new Document("user", user), newEntrySet);
        });
    }

    public static void checkForLevelUp(Player player) {
        ExperienceLevel currentLevel = cachedLevels.get(cachedPlayerLevel.get(player.getName()));
        ExperienceLevel nextLevel = cachedLevels.get(cachedPlayerLevel.get(player.getName()) + 1);
        if (nextLevel == null) return;
        int experience = cachedExperience.get(player.getName());
        if (experience >= nextLevel.getExperience()) {
            addPlayerLevel(player.getName());
            setExperience(player.getName());
            nextLevel.getRewards().forEach(rewardData -> {
                player.getServer().getPluginManager().callEvent(new PlayerLevelUpEvent(player, currentLevel, nextLevel));
                player.sendMessage(Language.getAndReplace("levelup-info", nextLevel.getLevel()));
                playSound(player, Sound.RANDOM_TOAST, 1.0F, 1.0F);
                switch (rewardData.getRewardType()) {
                    case ITEM: {
                        Item item = Item.get(Identifier.fromString(rewardData.getItemID()), rewardData.getItemMeta(), rewardData.getItemAmount());
                        player.getInventory().addItem(item);
                        player.sendMessage(Language.getAndReplace("levelup-item", item.getName(), item.getCount()));
                    }
                    break;
                    case MONEY: {
                        LlamaEconomy.getAPI().addMoney(player, rewardData.getMoney());
                        player.sendMessage(Language.getAndReplace("levelup-money", rewardData.getMoney()));
                    }
                    break;
                    case PERMISSION: {
                        PermissionAPI.addUserPermission(player.getName(), rewardData.getPermission());
                        player.sendMessage(Language.getAndReplace("levelup-permission", rewardData.getPermName()));
                    }
                    break;
                }
            });
        }
    }

    public static void addPlayerLevel(String user) {
        CompletableFuture.runAsync(() -> {
            Document document = new Document("user", user);
            Document found = MongoDB.getUserCollection().find(document).first();
            assert found != null;
            int level = cachedPlayerLevel.get(user) + 1;
            Bson newEntry = new Document("level", level);
            Bson newEntrySet = new Document("$set", newEntry);
            MongoDB.getUserCollection().updateOne(new Document("user", user), newEntrySet);
            cachedPlayerLevel.remove(user);
            cachedPlayerLevel.put(user, level);
        });
    }

    public static int getPlayerLevel(String user) {
        Document document = MongoDB.getUserCollection().find(new Document("user", user)).first();
        assert document != null;
        return document.getInteger("level");
    }

    public static int getPlayerExperience(String user) {
        Document document = MongoDB.getUserCollection().find(new Document("user", user)).first();
        assert document != null;
        return document.getInteger("experience");
    }

    public static void playSound(Player player, Sound sound, float volume, float pitch) {
        PlaySoundPacket packet = new PlaySoundPacket();
        packet.setSound(sound.getSound());
        packet.setPosition(Vector3f.from(new Double(player.getLocation().getX()).intValue(), new Double(player.getLocation().getY()).intValue(), new Double(player.getLocation().getZ()).intValue()));
        packet.setVolume(volume);
        packet.setPitch(pitch);
        player.sendPacket(packet);
    }
}
