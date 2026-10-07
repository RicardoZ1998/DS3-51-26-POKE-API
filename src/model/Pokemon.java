package model;

public class Pokemon {
    private final String name;
    private final String type;
    private final String spriteUrl;
    private final int maxHp;
    private final int attack;
    private final int defense;
    private final int speed;
    private int currentHp;

    public Pokemon(String name, String type, String spriteUrl, int maxHp, int attack, int defense, int speed) {
        this.name = name;
        this.type = type;
        this.spriteUrl = spriteUrl;
        this.maxHp = maxHp;
        this.attack = attack;
        this.defense = defense;
        this.speed = speed;
        this.currentHp = maxHp;
    }

    public void recibirDaño(int damage){
        currentHp = currentHp - damage;

        if (currentHp < 0) {
            currentHp = 0;
        }
    }

    public void reiniciarHp() {
        currentHp = maxHp;
    }

    public String getName() {
        return name;
    }
    public String getType() {
        return type;
    }
    public String getSpriteUrl() {
        return spriteUrl;
    }
    public int getMaxHp() {
        return maxHp;
    }
    public int getAttack() {
        return attack;
    }
    public int getDefense() {
        return defense;
    }
    public int getSpeed() {
        return speed;
    }
    public int getCurrentHp() {
        return currentHp;
    }
}
