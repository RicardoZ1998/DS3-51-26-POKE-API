package battle;

import model.Pokemon;

public class Battle {
    private final Pokemon pokemonDelPrimerJugador;
    private final Pokemon pokemonDelSegundoJugador;
    private static final double PROBABILIDAD_CRITICO = 0.0417; // 4.17 %
    private static final double MULTIPLICADOR_CRITICO = 1.5;

    private final BattleListener battleListener;
    private Pokemon pokemonAtacante;
    private Pokemon pokemonDefensor;
    private boolean combateTerminado;

    public Battle(Pokemon pokemonDelPrimerJugador, Pokemon pokemonDelSegundoJugador, BattleListener battleListener) {
        this.pokemonDelPrimerJugador = pokemonDelPrimerJugador;
        this.pokemonDelSegundoJugador = pokemonDelSegundoJugador;
        this.battleListener = battleListener;
    }

    // Elige quién empieza y deja el combate listo. No aplica ningún golpe todavía.
    // Devuelve el Pokémon que atacará primero.
    public Pokemon iniciarCombate() {
        combateTerminado = false;
        pokemonAtacante = elegirPokemonQueInicia();
        pokemonDefensor = pokemonAtacante == pokemonDelPrimerJugador
                ? pokemonDelSegundoJugador
                : pokemonDelPrimerJugador;
        return pokemonAtacante;
    }

    // Aplica un solo golpe (el del Pokémon al que le toca) y pasa el turno al otro.
    // Si el defensor llega a 0 de vida, notifica al ganador.
    public void siguienteGolpe() {
        if (combateTerminado) {
            return;
        }
        aplicarGolpe(pokemonAtacante, pokemonDefensor);
        if (pokemonDefensor.getCurrentHp() == 0) {
            combateTerminado = true;
            battleListener.onBattleEnded(nombreParaMostrar(pokemonAtacante));
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
        boolean esCritico = Math.random() < PROBABILIDAD_CRITICO;
        double modificador = esCritico ? MULTIPLICADOR_CRITICO : 1.0;
        int dañoDelGolpe = calcularDañoDelGolpe(pokemonAtacante, pokemonDefensor, modificador);

        pokemonDefensor.recibirDaño(dañoDelGolpe);
        battleListener.onTurn(nombreParaMostrar(pokemonAtacante), nombreParaMostrar(pokemonDefensor), dañoDelGolpe, esCritico, modificador);
        battleListener.onHpChanged(nombreParaMostrar(pokemonDefensor), pokemonDefensor.getCurrentHp());
    }

    // Nombre con la primera letra en mayúscula. Si los dos Pokémon se llaman igual,
    // se añade 1 o 2 según el jugador ("Pikachu 1", "Pikachu 2") para poder diferenciarlos en el log.
    public String nombreParaMostrar(Pokemon pokemon) {
        String nombre = pokemon.getName();
        nombre = nombre.substring(0, 1).toUpperCase() + nombre.substring(1);
        if (pokemonDelPrimerJugador.getName().equals(pokemonDelSegundoJugador.getName())) {
            nombre += pokemon == pokemonDelPrimerJugador ? " 1" : " 2";
        }
        return nombre;
    }

    // daño = ataque * modificador * aleatorio(0 a 1) - defensa * aleatorio(0 a 1)
    // El modificador es 1.5 si el golpe es crítico y 1.0 si no.
    // Si el resultado es menor que 1, el golpe hace 1. La vida no baja de 0: lo hace recibirDaño
    private int calcularDañoDelGolpe(Pokemon pokemonAtacante, Pokemon pokemonDefensor, double modificador) {
        double dañoBase = pokemonAtacante.getAttack() * modificador * Math.random()
                - pokemonDefensor.getDefense() * Math.random();
        int dañoDelGolpe = (int) Math.round(dañoBase);
        if (dañoDelGolpe < 1) {
            dañoDelGolpe = 1;
        }
        return dañoDelGolpe;
    }
}
