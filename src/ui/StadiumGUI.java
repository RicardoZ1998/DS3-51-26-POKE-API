package ui;

import api.PokeApiClient;
import model.Pokemon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class StadiumGUI {
    private Pokemon p1;
    private Pokemon p2;
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

    public StadiumGUI()
    {
        buscarButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                PokeApiClient ap = new PokeApiClient();
                String nombrePokemon = campoNombre.getText();
                try
                {
                    p1 = ap.buscarPokemonPorNombre(nombrePokemon);
                    rellenarCampos(p1, campoNombre, campoHp, campoAtaque, campoDefensa, campoVelocidad, textoImagen);
                }
                catch (IOException ex)
                {
                    throw new RuntimeException(ex);
                }
                catch (InterruptedException ex)
                {
                    throw new RuntimeException(ex);
                }
            }
        });
        ranButton1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                PokeApiClient ap = new PokeApiClient();
                try
                {
                    p1 = ap.buscarPokemonAleatorio();
                    rellenarCampos(p1, campoNombre, campoHp, campoAtaque, campoDefensa, campoVelocidad, textoImagen);
                }
                catch (IOException ex)
                {
                    throw new RuntimeException(ex);
                }
                catch (InterruptedException ex)
                {
                    throw new RuntimeException(ex);
                }
            }
        });


        buscarButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                PokeApiClient ap = new PokeApiClient();
                String nombrePokemon = campoNombre2.getText();
                try
                {
                    p2 = ap.buscarPokemonPorNombre(nombrePokemon);
                    rellenarCampos(p2, campoNombre2, campoHp2, campoAtaque2, campoDefensa2, campoVelocidad2, textoImagen2);
                }
                catch (IOException ex)
                {
                    throw new RuntimeException(ex);
                }
                catch (InterruptedException ex)
                {
                    throw new RuntimeException(ex);
                }
            }
        });
        ranButton2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                PokeApiClient ap = new PokeApiClient();
                try
                {
                    p2 = ap.buscarPokemonAleatorio();
                    rellenarCampos(p2, campoNombre2, campoHp2, campoAtaque2, campoDefensa2, campoVelocidad2, textoImagen2);
                }
                catch (IOException ex)
                {
                    throw new RuntimeException(ex);
                }
                catch (InterruptedException ex)
                {
                    throw new RuntimeException(ex);
                }
            }
        });
    }



    public void rellenarCampos(Pokemon p,
                               JTextField campoNombre,
                               JTextField campoHp,
                               JTextField campoAtaque,
                               JTextField campoDefensa,
                               JTextField campoVelocidad,
                               JLabel textoImagen)
    {
        campoNombre.setText(String.valueOf(p.getName()));
        campoHp.setText(String.valueOf(p.getMaxHp()));
        campoAtaque.setText(String.valueOf(p.getAttack()));
        campoDefensa.setText(String.valueOf(p.getDefense()));
        campoVelocidad.setText(String.valueOf(p.getSpeed()));

        try
        {
            java.net.URL urlImage = new java.net.URL(String.valueOf(p.getSpriteUrl()));
            ImageIcon icon = new ImageIcon(urlImage);

            //Se puede guardar la imagen para darle sus propias propiedades
            Image image = icon.getImage().getScaledInstance(200, 200, Image.SCALE_DEFAULT);
            textoImagen.setText("");
            textoImagen.setIcon(new ImageIcon(urlImage));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, String.format("Error al cargar la imagen de %s", p.getName()));
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
