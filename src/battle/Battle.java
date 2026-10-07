package battle;

import model.Pokemon;

public class Battle {
    private final Pokemon pokemonDelPrimerJugador;
    private final Pokemon pokemonDelSegundoJugador;
    private final BattleListener battleListener;
    private Pokemon pokemonAtacante;
    private Pokemon pokemonDefensor;

    public Battle(Pokemon pokemonDelPrimerJugador, Pokemon pokemonDelSegundoJugador, BattleListener battleListener) {
        this.pokemonDelPrimerJugador = pokemonDelPrimerJugador;
        this.pokemonDelSegundoJugador = pokemonDelSegundoJugador;
        this.battleListener = battleListener;
    }

    // Elige quién empieza y deja el combate listo. No aplica ningún golpe todavía.
    // Devuelve el Pokémon que atacará primero.
    public Pokemon iniciarCombate() {
        pokemonAtacante = elegirPokemonQueInicia();
        pokemonDefensor = pokemonAtacante == pokemonDelPrimerJugador
                ? pokemonDelSegundoJugador
                : pokemonDelPrimerJugador;
        return pokemonAtacante;
    }

    // Aplica un solo golpe (el del Pokémon al que le toca) y pasa el turno al otro.
    // Si el defensor llega a 0 de vida, notifica al ganador.
    public void siguienteGolpe() {
        aplicarGolpe(pokemonAtacante, pokemonDefensor);
        if (pokemonDefensor.getCurrentHp() == 0) {
            battleListener.onBattleEnded(pokemonAtacante.getName());
            return;
        }

        Pokemon pokemonAtacanteAnterior = pokemonAtacante;
        pokemonAtacante = pokemonDefensor;
        pokemonDefensor = pokemonAtacanteAnterior;
    }

    private Pokemon elegirPokemonQueInicia() {
        if (pokemonDelPrimerJugador.getSpeed() > pokemonDelSegundoJugador.getSpeed()) {
            return pokemonDelPrimerJugador;
        }
        if (pokemonDelSegundoJugador.getSpeed() > pokemonDelPrimerJugador.getSpeed()) {
            return pokemonDelSegundoJugador;
        }
        return Math.random() < 0.5 ? pokemonDelPrimerJugador : pokemonDelSegundoJugador;
    }

    private void aplicarGolpe(Pokemon pokemonAtacante, Pokemon pokemonDefensor) {
        int dañoDelGolpe = calcularDañoDelGolpe(pokemonAtacante, pokemonDefensor);

        pokemonDefensor.recibirDaño(dañoDelGolpe);
        battleListener.onTurn(pokemonAtacante.getName(), pokemonDefensor.getName(), dañoDelGolpe, false, 1.0);
        battleListener.onHpChanged(pokemonDefensor.getName(), pokemonDefensor.getCurrentHp());
    }

    // daño = ataque * aleatorio(0 a 1) - defensa * aleatorio(0 a 1)
    // Si el resultado es menor que 1, el golpe hace 1. La vida no baja de 0: lo hace recibirDaño
    private int calcularDañoDelGolpe(Pokemon pokemonAtacante, Pokemon pokemonDefensor) {
        double dañoBase = pokemonAtacante.getAttack() * Math.random()
                - pokemonDefensor.getDefense() * Math.random();
        int dañoDelGolpe = (int) Math.round(dañoBase);
        if (dañoDelGolpe < 1) {
            dañoDelGolpe = 1;
        }
        return dañoDelGolpe;
    }
}
