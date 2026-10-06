package battle;

import model.Pokemon;

public class Battle {
    private final Pokemon primero;
    private final Pokemon segundo;
    private final BattleListener listener;
    public Battle(Pokemon primero, Pokemon segundo, BattleListener listener) {
        this.primero = primero;
        this.segundo = segundo;
        this.listener = listener;
    }
}
