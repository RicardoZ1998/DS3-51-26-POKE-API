package battle;

import model.Pokemon;

public class Battle {
    private final Pokemon pokemonDelPrimerJugador;
    private final Pokemon pokemonDelSegundoJugador;
    private static final double PROBABILIDAD_CRITICO = 0.10;
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
        boolean esCritico = Math.random() < PROBABILIDAD_CRITICO;
        double efectividad = calcularEfectividadDeTipos(pokemonAtacante.getType(), pokemonDefensor.getType());
        int dañoDelGolpe = calcularDañoDelGolpe(pokemonAtacante, pokemonDefensor, esCritico, efectividad);

        pokemonDefensor.recibirDaño(dañoDelGolpe);
        battleListener.onTurn(
                pokemonAtacante.getName(),
                pokemonDefensor.getName(),
                dañoDelGolpe,
                esCritico,
                efectividad
        );
        battleListener.onHpChanged(pokemonDefensor.getName(), pokemonDefensor.getCurrentHp());
    }

    // daño = ataque * aleatorio(0 a 1) - defensa * aleatorio(0 a 1).
    // Si es crítico (10 %), ese resultado se multiplica por 1.5.
    // Después se multiplica por la efectividad del primer tipo: 1.3, 0.7 o 1.0.
    // Si el resultado es menor que 1, el golpe hace 1. La vida no baja de 0: lo hace recibirDaño.
    private int calcularDañoDelGolpe(Pokemon pokemonAtacante, Pokemon pokemonDefensor, boolean esCritico, double efectividad) {
        double dañoBase = pokemonAtacante.getAttack() * Math.random()
                - pokemonDefensor.getDefense() * Math.random();
        if (esCritico) {
            dañoBase = dañoBase * MULTIPLICADOR_CRITICO;
        }
        dañoBase = dañoBase * efectividad;
        int dañoDelGolpe = (int) Math.round(dañoBase);
        if (dañoDelGolpe < 1) {
            dañoDelGolpe = 1;
        }
        return dañoDelGolpe;
    }

    // Solo el primer tipo. Agua>Fuego, Fuego>Planta, Planta>Agua = 1.3. Al revés = 0.7. El resto = 1.0.
    private double calcularEfectividadDeTipos(String tipoAtacante, String tipoDefensor) {
        if (tipoAtacante.equals("water") && tipoDefensor.equals("fire")) {
            return 1.3;
        }
        if (tipoAtacante.equals("fire") && tipoDefensor.equals("grass")) {
            return 1.3;
        }
        if (tipoAtacante.equals("grass") && tipoDefensor.equals("water")) {
            return 1.3;
        }
        if (tipoAtacante.equals("fire") && tipoDefensor.equals("water")) {
            return 0.7;
        }
        if (tipoAtacante.equals("grass") && tipoDefensor.equals("fire")) {
            return 0.7;
        }
        if (tipoAtacante.equals("water") && tipoDefensor.equals("grass")) {
            return 0.7;
        }
        return 1.0;
    }
}
