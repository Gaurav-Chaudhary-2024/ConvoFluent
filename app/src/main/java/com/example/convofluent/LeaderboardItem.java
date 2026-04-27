package com.example.convofluent;

public class LeaderboardItem {
    private String rank;
    private String avatarText;
    private String name;
    private String country;
    private String xp;
    private String level;
    private int progress;
    private String nextLevelXp;
    private int avatarColor;

    public LeaderboardItem(String rank, String avatarText, String name, String country, String xp, String level, int progress, String nextLevelXp, int avatarColor) {
        this.rank = rank;
        this.avatarText = avatarText;
        this.name = name;
        this.country = country;
        this.xp = xp;
        this.level = level;
        this.progress = progress;
        this.nextLevelXp = nextLevelXp;
        this.avatarColor = avatarColor;
    }

    public String getRank() { return rank; }
    public String getAvatarText() { return avatarText; }
    public String getName() { return name; }
    public String getCountry() { return country; }
    public String getXp() { return xp; }
    public String getLevel() { return level; }
    public int getProgress() { return progress; }
    public String getNextLevelXp() { return nextLevelXp; }
    public int getAvatarColor() { return avatarColor; }
}
