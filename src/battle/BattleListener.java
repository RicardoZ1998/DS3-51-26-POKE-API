package battle;

public interface BattleListener {
    // critical: true si el golpe fue crítico
    // modifier: multiplicador aplicado al daño (efectividad/crítico); 1.0 = sin modificación
    void onTurn(String attacker, String defender, int damage, boolean critical, double modifier);

    void onHpChanged(String pokemon, int hpActual);

    void onBattleEnded(String winner);
}