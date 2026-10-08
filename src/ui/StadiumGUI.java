package ui;

import api.PokeApiClient;
import battle.Battle;
import battle.BattleListener;
import model.Pokemon;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.ExecutionException;

public class StadiumGUI implements BattleListener {
    private Pokemon p1;
    private Pokemon p2;
    private Battle batalla;
    private Pokemon pokemonAtacanteActual;
    private JPanel mainPanel;
    private JTextField campoNombre;
    private JTextField campoTipo;
    private JTextField campoHp;
    private JTextField campoAtaque;
    private JTextField campoDefensa;
    private JTextField campoVelocidad;
    private JProgressBar vida1;
    private JLabel textoImagen;
    private JLabel textoImagen2;
    private JTextField campoNombre2;
    private JTextField campoTipo2;
    private JTextField campoHp2;
    private JTextField campoAtaque2;
    private JTextField campoDefensa2;
    private JTextField campoVelocidad2;
    private JProgressBar vida2;
    private JButton buscarButton1;
    private JButton buscarButton2;
    private JButton ranButton1;
    private JButton ranButton2;
    private JTextArea battleLog;
    private JButton fightButton;
    private JButton nextButton;
    private static final Dimension CAJA_SPRITE = new Dimension(96, 80);
    private int cargasEnCurso = 0;
    private int cargaJugador1 = 0;
    private int cargaJugador2 = 0;
    private final PokeApiClient apiClient = new PokeApiClient();

    public StadiumGUI()
    {
        aplicarApariencia();
        fightButton.setEnabled(false);
        nextButton.setEnabled(false);

        fightButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                iniciarBatalla();
            }
        });
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                batalla.siguienteGolpe();
            }
        });

        buscarButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String nombrePokemon = campoNombre.getText();
                cargarPokemon(true, () -> apiClient.buscarPokemonPorNombre(nombrePokemon));
            }
        });
        ranButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                cargarPokemon(true, () -> apiClient.buscarPokemonAleatorio());
            }
        });


        buscarButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String nombrePokemon = campoNombre2.getText();
                cargarPokemon(false, () -> apiClient.buscarPokemonPorNombre(nombrePokemon));
            }
        });
        ranButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                cargarPokemon(false, () -> apiClient.buscarPokemonAleatorio());
            }
        });
    }

    private interface BusquedaPokemon
    {
        Pokemon buscar() throws IOException, InterruptedException;
    }

    private static class PokemonCargado
    {
        final Pokemon pokemon;
        final ImageIcon sprite;

        PokemonCargado(Pokemon pokemon, ImageIcon sprite)
        {
            this.pokemon = pokemon;
            this.sprite = sprite;
        }
    }

    // Carga un Pokémon (datos + sprite) en un hilo de fondo para no bloquear la UI.
    // esPrimero: true = jugador 1, false = jugador 2. Todo lo que toca Swing ocurre en el EDT.
    private void cargarPokemon(boolean esPrimero, BusquedaPokemon busqueda)
    {
        JButton botonBuscar = esPrimero ? buscarButton1 : buscarButton2;
        JButton botonAleatorio = esPrimero ? ranButton1 : ranButton2;
        final int numeroDeCarga = esPrimero ? ++cargaJugador1 : ++cargaJugador2;

        botonBuscar.setEnabled(false);
        botonAleatorio.setEnabled(false);
        cargasEnCurso++;
        actualizarBotonLuchar();

        new SwingWorker<PokemonCargado, Void>() {
            @Override
            protected PokemonCargado doInBackground() throws Exception
            {
                Pokemon pokemon = busqueda.buscar();
                return new PokemonCargado(pokemon, descargarSprite(pokemon));
            }

            @Override
            protected void done()
            {
                try
                {
                    if (numeroDeCarga != cargaActual(esPrimero))
                    {
                        return;
                    }
                    PokemonCargado cargado = get();
                    if (esPrimero)
                    {
                        p1 = cargado.pokemon;
                        rellenarCampos(p1, campoNombre, campoTipo, campoHp, campoAtaque, campoDefensa, campoVelocidad, vida1, textoImagen, cargado.sprite);
                    }
                    else
                    {
                        p2 = cargado.pokemon;
                        rellenarCampos(p2, campoNombre2, campoTipo2, campoHp2, campoAtaque2, campoDefensa2, campoVelocidad2, vida2, textoImagen2, cargado.sprite);
                    }
                }
                catch (ExecutionException ex)
                {
                    if (numeroDeCarga != cargaActual(esPrimero))
                    {
                        return;
                    }
                    limpiarPokemon(esPrimero);
                    Throwable causa = ex.getCause();
                    if (causa instanceof IOException)
                    {
                        mostrarError(causa.getMessage());
                    }
                    else if (causa instanceof InterruptedException)
                    {
                        mostrarError("La carga del Pokémon fue interrumpida.");
                    }
                    else
                    {
                        mostrarError("Error inesperado al cargar el Pokémon: " + causa);
                    }
                }
                catch (InterruptedException ex)
                {
                    if (numeroDeCarga != cargaActual(esPrimero))
                    {
                        return;
                    }
                    limpiarPokemon(esPrimero);
                    mostrarError("La carga del Pokémon fue interrumpida.");
                }
                finally
                {
                    cargasEnCurso--;
                    if (numeroDeCarga == cargaActual(esPrimero))
                    {
                        botonBuscar.setEnabled(true);
                        botonAleatorio.setEnabled(true);
                    }
                    actualizarBotonLuchar();
                }
            }
        }.execute();
    }

    private int cargaActual(boolean esPrimero)
    {
        return esPrimero ? cargaJugador1 : cargaJugador2;
    }

    private ImageIcon descargarSprite(Pokemon pokemon)
    {
        if (pokemon.getSpriteUrl() == null || pokemon.getSpriteUrl().isBlank())
        {
            return null;
        }
        try
        {
            BufferedImage imagen = ImageIO.read(URI.create(pokemon.getSpriteUrl()).toURL());
            return imagen == null ? null : escalarSprite(new ImageIcon(imagen));
        }
        catch (IOException | IllegalArgumentException ex)
        {
            return null;
        }
    }

    // Prepara el combate: bloquea los botones de carga y habilita "Siguiente".
    private void iniciarBatalla()
    {
        batalla = new Battle(p1, p2, this);
        pokemonAtacanteActual = batalla.iniciarCombate();

        fightButton.setEnabled(false);
        buscarButton1.setEnabled(false);
        ranButton1.setEnabled(false);
        buscarButton2.setEnabled(false);
        ranButton2.setEnabled(false);
        nextButton.setEnabled(true);

        battleLog.setText("");
        escribir("--- " + nombreParaMostrar(p1) + " vs " + nombreParaMostrar(p2) + " ---\n");
        escribir("Empieza " + nombreParaMostrar(pokemonAtacanteActual) + ". Pulsa \"Siguiente\" para atacar.\n");
    }

    @Override
    public void onTurn(String attacker, String defender, int damage, boolean critical, double modifier)
    {
        Pokemon defensor = pokemonAtacanteActual == p1 ? p2 : p1;
        if (critical)
        {
            escribir("¡Golpe crítico! ");
        }
        escribir(nombreParaMostrar(pokemonAtacanteActual) + " atacó a " + nombreParaMostrar(defensor)
                + " e hizo " + damage + " de daño. ");
        if (Math.abs(modifier - 1.0) > 0.01)
        {
            escribir("Efectividad x" + modifier + ". ");
        }
    }

    @Override
    public void onHpChanged(String pokemon, int hpActual)
    {
        Pokemon defensor = pokemonQueRecibe(pokemon);
        if (defensor == p1)
        {
            campoHp.setText(String.valueOf(hpActual));
            pintarBarra(vida1, hpActual, p1.getMaxHp());
        }
        else if (defensor == p2)
        {
            campoHp2.setText(String.valueOf(hpActual));
            pintarBarra(vida2, hpActual, p2.getMaxHp());
        }
        escribir("A " + nombreParaMostrar(defensor) + " le quedan " + hpActual + " HP.\n");
        if (defensor.getCurrentHp() > 0)
        {
            pokemonAtacanteActual = defensor;
        }
    }

    @Override
    public void onBattleEnded(String winner)
    {
        Pokemon ganador = p1.getCurrentHp() == 0 ? p2 : p1;
        String nombreGanador = nombreParaMostrar(ganador);
        escribir("¡" + nombreGanador + " ganó el combate!\n");
        JOptionPane.showMessageDialog(mainPanel, "¡" + nombreGanador + " ganó el combate!", "Fin del combate", JOptionPane.INFORMATION_MESSAGE);

        p1.reiniciarHp();
        p2.reiniciarHp();
        mostrarVida(p1, campoHp, vida1);
        mostrarVida(p2, campoHp2, vida2);

        nextButton.setEnabled(false);
        buscarButton1.setEnabled(true);
        ranButton1.setEnabled(true);
        buscarButton2.setEnabled(true);
        ranButton2.setEnabled(true);
        actualizarBotonLuchar();
    }

    // Si los dos se llaman igual, el nombre del evento no distingue el lado.
    // En ese caso el que recibe es el que no está atacando en este turno.
    private Pokemon pokemonQueRecibe(String nombre)
    {
        boolean coincidePrimero = p1 != null && p1.getName().equals(nombre);
        boolean coincideSegundo = p2 != null && p2.getName().equals(nombre);
        if (coincidePrimero && !coincideSegundo)
        {
            return p1;
        }
        if (coincideSegundo && !coincidePrimero)
        {
            return p2;
        }
        return pokemonAtacanteActual == p1 ? p2 : p1;
    }

    private String nombreParaMostrar(Pokemon pokemon)
    {
        String nombre = pokemon.getName();
        nombre = nombre.substring(0, 1).toUpperCase() + nombre.substring(1);
        if (p1 != null && p2 != null && p1.getName().equals(p2.getName()))
        {
            nombre += pokemon == p1 ? " 1" : " 2";
        }
        return nombre;
    }

    private void mostrarVida(Pokemon pokemon, JTextField campo, JProgressBar barra)
    {
        campo.setText(String.valueOf(pokemon.getCurrentHp()));
        pintarBarra(barra, pokemon.getCurrentHp(), pokemon.getMaxHp());
    }

    private void pintarBarra(JProgressBar barra, int actual, int maximo)
    {
        barra.setMaximum(Math.max(maximo, 1));
        barra.setValue(actual);
        barra.setString(actual + " / " + maximo);
    }

    private void escribir(String texto)
    {
        battleLog.append(texto);
        battleLog.setCaretPosition(battleLog.getDocument().getLength());
    }

    private void aplicarApariencia()
    {
        Color verde = new Color(39, 128, 84);
        Color fondoBarra = new Color(186, 204, 216);
        for (JProgressBar barra : new JProgressBar[]{vida1, vida2})
        {
            barra.setForeground(verde);
            barra.setBackground(fondoBarra);
            barra.setStringPainted(true);
            barra.setString("");
        }

        battleLog.setLineWrap(true);
        battleLog.setWrapStyleWord(true);
        battleLog.setOpaque(true);
        battleLog.setBackground(Color.WHITE);
        battleLog.setForeground(new Color(43, 43, 43));
        battleLog.setMargin(new Insets(8, 8, 8, 8));

        fijarCajaSprite(textoImagen);
        fijarCajaSprite(textoImagen2);
    }

    private void fijarCajaSprite(JLabel etiqueta)
    {
        etiqueta.setPreferredSize(CAJA_SPRITE);
        etiqueta.setMinimumSize(CAJA_SPRITE);
        etiqueta.setMaximumSize(CAJA_SPRITE);
        etiqueta.setHorizontalAlignment(SwingConstants.CENTER);
        etiqueta.setVerticalAlignment(SwingConstants.CENTER);
    }

    private ImageIcon escalarSprite(ImageIcon icono)
    {
        int ancho = icono.getIconWidth();
        int alto = icono.getIconHeight();
        if (ancho <= 0 || alto <= 0 || (ancho <= CAJA_SPRITE.width && alto <= CAJA_SPRITE.height))
        {
            return icono;
        }
        double escala = Math.min(CAJA_SPRITE.width / (double) ancho, CAJA_SPRITE.height / (double) alto);
        int nuevoAncho = Math.max(1, (int) Math.round(ancho * escala));
        int nuevoAlto = Math.max(1, (int) Math.round(alto * escala));
        Image imagen = icono.getImage().getScaledInstance(nuevoAncho, nuevoAlto, Image.SCALE_SMOOTH);
        return new ImageIcon(imagen);
    }

    private void limpiarPokemon(boolean esPrimero)
    {
        if (esPrimero)
        {
            p1 = null;
            campoNombre.setText("");
            campoTipo.setText("");
            campoHp.setText("");
            campoAtaque.setText("");
            campoDefensa.setText("");
            campoVelocidad.setText("");
            vida1.setValue(0);
            vida1.setString("");
            textoImagen.setIcon(null);
            textoImagen.setText("Pokemon1");
            fijarCajaSprite(textoImagen);
        }
        else
        {
            p2 = null;
            campoNombre2.setText("");
            campoTipo2.setText("");
            campoHp2.setText("");
            campoAtaque2.setText("");
            campoDefensa2.setText("");
            campoVelocidad2.setText("");
            vida2.setValue(0);
            vida2.setString("");
            textoImagen2.setIcon(null);
            textoImagen2.setText("Pokemon2");
            fijarCajaSprite(textoImagen2);
        }
    }

    private void mostrarError(String mensaje)
    {
        JOptionPane.showMessageDialog(mainPanel, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // "Fight!" solo se habilita cuando ambos Pokémon están cargados y no hay ninguna carga en curso.
    private void actualizarBotonLuchar()
    {
        fightButton.setEnabled(p1 != null && p2 != null && cargasEnCurso == 0);
    }

    private void rellenarCampos(Pokemon p,
                               JTextField campoNombre,
                               JTextField campoTipo,
                               JTextField campoHp,
                               JTextField campoAtaque,
                               JTextField campoDefensa,
                               JTextField campoVelocidad,
                               JProgressBar barraVida,
                               JLabel textoImagen,
                               ImageIcon sprite)
    {
        campoNombre.setText(p.getName());
        campoTipo.setText(p.getType());
        campoAtaque.setText(String.valueOf(p.getAttack()));
        campoDefensa.setText(String.valueOf(p.getDefense()));
        campoVelocidad.setText(String.valueOf(p.getSpeed()));
        mostrarVida(p, campoHp, barraVida);

        if (sprite != null)
        {
            textoImagen.setText("");
            textoImagen.setIcon(sprite);
        }
        else
        {
            textoImagen.setIcon(null);
            textoImagen.setText("Sin imagen");
            mostrarError(String.format("Error al cargar la imagen de %s", p.getName()));
        }
        fijarCajaSprite(textoImagen);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            StadiumGUI gui = new StadiumGUI();

            JFrame frame = new JFrame("Pokémon Stadium Lite");
            frame.setContentPane(gui.mainPanel);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();

            Dimension pantalla = Toolkit.getDefaultToolkit().getScreenSize();
            int ancho = Math.min(frame.getWidth(), pantalla.width - 48);
            int alto = Math.min(frame.getHeight(), pantalla.height - 80);
            frame.setSize(ancho, alto);
            frame.setMinimumSize(new Dimension(Math.min(960, ancho), Math.min(640, alto)));
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
