package battle;

public interface BattleListener {
    // critical: true si el golpe fue crítico (10 %, x1.5 ya incluido en damage).
    // modifier: efectividad del primer tipo (1.3, 0.7 o 1.0). No incluye el crítico.
    void onTurn(String attacker, String defender, int damage, boolean critical, double modifier);

    void onHpChanged(String pokemon, int hpActual);

    void onBattleEnded(String winner);
}