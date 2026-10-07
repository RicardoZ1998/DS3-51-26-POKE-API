package ui;

import api.PokeApiClient;
import battle.Battle;
import battle.BattleListener;
import model.Pokemon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class StadiumGUI implements BattleListener {
    private Pokemon p1;
    private Pokemon p2;
    private Battle batalla;
    private JPanel mainPanel;
    private JTextField campoId;
    private JTextField campoNombre;
    private JTextField campoPeso;
    private JTextField campoAltura;
    private JTextField campoHp;
    private JTextField campoAtaque;
    private JTextField campoDefensa;
    private JTextField campoAtaqEspecial;
    private JTextField campoDefEspecial;
    private JTextField campoVelocidad;
    private JLabel textoImagen;
    private JLabel textoImagen2;
    private JTextField campoNombre2;
    private JTextField campoHp2;
    private JTextField campoAtaque2;
    private JTextField campoDefensa2;
    private JTextField campoVelocidad2;
    private JButton buscarButton1;
    private JButton buscarButton2;
    private JButton ranButton1;
    private JButton ranButton2;
    private JTextArea battleLog;
    private JButton figthButton;
    private JButton nextButton;
    private int cargasEnCurso = 0;

    public StadiumGUI()
    {
        figthButton.setEnabled(false);
        nextButton.setEnabled(false);

        figthButton.addActionListener(new ActionListener() {
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
                cargarPokemon(true, () -> new PokeApiClient().buscarPokemonPorNombre(nombrePokemon));
            }
        });
        ranButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                cargarPokemon(true, () -> new PokeApiClient().buscarPokemonAleatorio());
            }
        });


        buscarButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                String nombrePokemon = campoNombre2.getText();
                cargarPokemon(false, () -> new PokeApiClient().buscarPokemonPorNombre(nombrePokemon));
            }
        });
        ranButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                cargarPokemon(false, () -> new PokeApiClient().buscarPokemonAleatorio());
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
        final ImageIcon sprite; // null si no se pudo descargar la imagen

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

        botonBuscar.setEnabled(false);
        botonAleatorio.setEnabled(false);
        cargasEnCurso++;
        actualizarBotonLuchar();

        new SwingWorker<PokemonCargado, Void>() {
            @Override
            protected PokemonCargado doInBackground() throws Exception
            {
                Pokemon pokemon = busqueda.buscar();

                ImageIcon sprite = null;
                try
                {
                    ImageIcon icon = new ImageIcon(new java.net.URL(pokemon.getSpriteUrl()));
                    if (icon.getImageLoadStatus() == MediaTracker.COMPLETE)
                    {
                        sprite = icon;
                    }
                }
                catch (java.net.MalformedURLException ex)
                {
                    // sprite queda en null y se avisa en done()
                }
                return new PokemonCargado(pokemon, sprite);
            }

            @Override
            protected void done()
            {
                try
                {
                    PokemonCargado cargado = get();
                    if (esPrimero)
                    {
                        p1 = cargado.pokemon;
                        rellenarCampos(p1, campoNombre, campoHp, campoAtaque, campoDefensa, campoVelocidad, textoImagen, cargado.sprite);
                    }
                    else
                    {
                        p2 = cargado.pokemon;
                        rellenarCampos(p2, campoNombre2, campoHp2, campoAtaque2, campoDefensa2, campoVelocidad2, textoImagen2, cargado.sprite);
                    }
                }
                catch (ExecutionException ex)
                {
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
                    mostrarError("La carga del Pokémon fue interrumpida.");
                }
                finally
                {
                    botonBuscar.setEnabled(true);
                    botonAleatorio.setEnabled(true);
                    cargasEnCurso--;
                    actualizarBotonLuchar();
                }
            }
        }.execute();
    }

    // Prepara el combate: bloquea los botones de carga y habilita "Siguiente".
    private void iniciarBatalla()
    {
        batalla = new Battle(p1, p2, this);
        Pokemon primero = batalla.iniciarCombate();

        figthButton.setEnabled(false);
        buscarButton1.setEnabled(false);
        ranButton1.setEnabled(false);
        buscarButton2.setEnabled(false);
        ranButton2.setEnabled(false);
        nextButton.setEnabled(true);

        battleLog.append("--- " + p1.getName() + " vs " + p2.getName() + " ---\n");
        battleLog.append("Empieza " + primero.getName() + ". Pulsa \"Siguiente\" para atacar.\n");
    }

    @Override
    public void onTurn(String attacker, String defender, int damage, boolean critical, double modifier)
    {
        battleLog.append(attacker + " atacó a " + defender + " e hizo " + damage + " de daño. ");
    }

    // Actualiza el campo HP de ambos con la vida actual (así funciona aunque los dos tengan el mismo nombre).
    @Override
    public void onHpChanged(String pokemon, int hpActual)
    {
        campoHp.setText(String.valueOf(p1.getCurrentHp()));
        campoHp2.setText(String.valueOf(p2.getCurrentHp()));
        battleLog.append("A " + pokemon + " le quedan " + hpActual + " HP.\n");
    }

    @Override
    public void onBattleEnded(String winner)
    {
        battleLog.append("¡" + winner + " ganó el combate!\n");
        JOptionPane.showMessageDialog(mainPanel, "¡" + winner + " ganó el combate!", "Fin del combate", JOptionPane.INFORMATION_MESSAGE);

        // Restablece la vida de ambos y deja la interfaz lista para otra pelea
        p1.reiniciarHp();
        p2.reiniciarHp();
        campoHp.setText(String.valueOf(p1.getMaxHp()));
        campoHp2.setText(String.valueOf(p2.getMaxHp()));

        nextButton.setEnabled(false);
        buscarButton1.setEnabled(true);
        ranButton1.setEnabled(true);
        buscarButton2.setEnabled(true);
        ranButton2.setEnabled(true);
        actualizarBotonLuchar();
    }

    private void mostrarError(String mensaje)
    {
        JOptionPane.showMessageDialog(mainPanel, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }

    // "Luchar" solo se habilita cuando ambos Pokémon están cargados y no hay ninguna carga en curso.
    // p1/p2 solo se asignan si la carga terminó bien, así que null = no cargado.
    private void actualizarBotonLuchar()
    {
        figthButton.setEnabled(p1 != null && p2 != null && cargasEnCurso == 0);
    }

    public void rellenarCampos(Pokemon p,
                               JTextField campoNombre,
                               JTextField campoHp,
                               JTextField campoAtaque,
                               JTextField campoDefensa,
                               JTextField campoVelocidad,
                               JLabel textoImagen,
                               ImageIcon sprite)
    {
        campoNombre.setText(String.valueOf(p.getName()));
        campoHp.setText(String.valueOf(p.getMaxHp()));
        campoAtaque.setText(String.valueOf(p.getAttack()));
        campoDefensa.setText(String.valueOf(p.getDefense()));
        campoVelocidad.setText(String.valueOf(p.getSpeed()));

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
    }

    public static void main(String[] args) {
        StadiumGUI gui = new StadiumGUI();

        JFrame frame = new JFrame("PokeApi");

        JScrollPane scrollPane = new JScrollPane(gui.mainPanel);

        scrollPane.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED
        );

        scrollPane.setHorizontalScrollBarPolicy(
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED
        );

        frame.setContentPane(scrollPane);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setSize(600, 700);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        frame.setVisible(true);
    }
}
