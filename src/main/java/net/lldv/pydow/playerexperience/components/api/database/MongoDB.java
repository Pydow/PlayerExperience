package net.lldv.pydow.playerexperience.components.api.database;

import cn.nukkit.utils.Config;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import net.lldv.pydow.playerexperience.PlayerExperience;
import net.lldv.pydow.playerexperience.components.api.ExperienceAPI;
import net.lldv.pydow.playerexperience.components.data.ExperienceLevel;
import net.lldv.pydow.playerexperience.components.data.RewardData;
import net.lldv.pydow.playerexperience.components.data.RewardType;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class MongoDB {

    private static MongoClient mongoClient;
    private static MongoDatabase mongoDatabase;
    private static MongoCollection<Document> levelCollection, userCollection;

    public static void connect(PlayerExperience server) {
        CompletableFuture.runAsync(() -> {
            Config config = PlayerExperience.getInstance().getConfig();
            MongoClientURI uri = new MongoClientURI(config.getString("MongoDB.Uri"));
            mongoClient = new MongoClient(uri);
            mongoDatabase = mongoClient.getDatabase(config.getString("MongoDB.Database"));
            levelCollection = mongoDatabase.getCollection("levels");
            userCollection = mongoDatabase.getCollection("users");
            server.getLogger().info("[MongoClient] Connection opened.");
            for (Document document : levelCollection.find()) {
                int level = document.getInteger("level");
                int experience = document.getInteger("experience");
                List<RewardData> rewards = new ArrayList<>();
                for (String s : document.getList("reward", String.class)) {
                    String[] e = s.split(":");
                    switch (e[0]) {
                        case "item": {
                            String item = e[1];
                            int meta = Integer.parseInt(e[2]);
                            int amount = Integer.parseInt(e[3]);
                            RewardData rewardData = new RewardData(RewardType.ITEM, item, meta, amount);
                            rewards.add(rewardData);
                            break;
                        }
                        case "money": {
                            int money = Integer.parseInt(e[1]);
                            RewardData rewardData = new RewardData(RewardType.MONEY, money);
                            rewards.add(rewardData);
                            break;
                        }
                        case "permission": {
                            String permission = e[1];
                            String permName = e[2];
                            RewardData rewardData = new RewardData(RewardType.PERMISSION, permission, permName);
                            rewards.add(rewardData);
                            break;
                        }
                    }
                }
                ExperienceLevel experienceLevel = new ExperienceLevel(level, experience, rewards);
                ExperienceAPI.cachedLevels.put(level, experienceLevel);
            }
        });
    }

    public static MongoClient getMongoClient() {
        return mongoClient;
    }

    public static MongoCollection<Document> getLevelCollection() {
        return levelCollection;
    }

    public static MongoCollection<Document> getUserCollection() {
        return userCollection;
    }

    public static MongoDatabase getMongoDatabase() {
        return mongoDatabase;
    }

}
