package net.lldv.pydow.playerexperience.components.data;

import cn.nukkit.utils.Identifier;

public class RewardData {

    private final RewardType rewardType;

    private String itemID;
    private int itemMeta;
    private int itemAmount;

    private int money;

    private String permission;
    private String permName;

    public RewardData(RewardType rewardType, String itemID, int itemMeta, int itemAmount) {
        this.rewardType = rewardType;
        this.itemID = itemID;
        this.itemMeta = itemMeta;
        this.itemAmount = itemAmount;
    }

    public RewardData(RewardType rewardType, int money) {
        this.rewardType = rewardType;
        this.money = money;
    }

    public RewardData(RewardType rewardType, String permission, String permName) {
        this.rewardType = rewardType;
        this.permission = permission;
        this.permName = permName;
    }

    public RewardType getRewardType() {
        return rewardType;
    }

    public String getItemID() {
        return itemID;
    }

    public int getItemMeta() {
        return itemMeta;
    }

    public int getItemAmount() {
        return itemAmount;
    }

    public int getMoney() {
        return money;
    }

    public String getPermission() {
        return permission;
    }

    public String getPermName() {
        return permName;
    }
}
