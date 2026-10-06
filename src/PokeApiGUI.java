import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class PokeApiGUI {
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
    private JTextArea campoAreaHabilidades;
    private JLabel textoImagen;

    public PokeApiGUI() {
        campoNombre.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                consultarPokemon();
            }
        });
    }

    public void consultarPokemon()
    {
        try {
            //Solicitar el nombre del pokemón
            String nombrePokemon = campoNombre.getText();
            String URL = "https://pokeapi.co/api/v2/pokemon/";
            //Crea un cliente http el cual se encarga de hacer las peticiones
            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(URL+nombrePokemon))
                    .build();

            //Ejecutamos la solicitud
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {

                JSONObject jsonObject = new JSONObject(response.body());

                campoId.setText(String.valueOf(jsonObject.getInt("id")));
                campoPeso.setText(String.valueOf(jsonObject.getInt("weight")));
                campoAltura.setText(String.valueOf(jsonObject.getInt("height")));

                //Vamos a obtener las habilidades pero estas están contenidas en un Array
                jsonObject.getJSONArray("abilities").forEach(habilidad ->
                {
                    //Accedemos al objeto
                    JSONObject abilityJson = (JSONObject) habilidad;
                    JSONObject abilityName = abilityJson.getJSONObject("ability");

                    campoAreaHabilidades.append(abilityName.getString("name") + "\n");
                });

                //Vamos a obtener las estadísticas

                System.out.println("Estadisticas o stats");

                jsonObject.getJSONArray("stats").forEach(stats -> {

                    //Accedemos al objeto
                    JSONObject statsJson = (JSONObject) stats;
                    JSONObject statName = statsJson.getJSONObject("stat");

                    String nombre = statName.getString("name");
                    int valor = statsJson.getInt("base_stat");

                    if (nombre.equals("hp"))
                        campoHp.setText(String.valueOf(valor));

                    else if (nombre.equals("attack")) {
                            campoAtaque.setText(String.valueOf(valor));
                        }
                    else if (nombre.equals("defense")) {
                        campoDefensa.setText(String.valueOf(valor));
                    }
                    else if (nombre.equals("special-attack")) {
                        campoAtaqEspecial.setText(String.valueOf(valor));
                    }
                    else if (nombre.equals("special-defense")) {
                        campoDefEspecial.setText(String.valueOf(valor));
                    }
                    else if (nombre.equals("speed")) {
                        campoVelocidad.setText(String.valueOf(valor));
                    }


                });

                //Vamos a obtener la imagen del pokemon


                JSONObject imagen = jsonObject.getJSONObject("sprites");

                try
                {
                    java.net.URL urlImage = new java.net.URL(imagen.getString("front_default"));
                    ImageIcon icon = new ImageIcon(urlImage);
                    //Se puede guardar la imagen para darle sus propias propiedades
                    Image image = icon.getImage().getScaledInstance(200, 200, Image.SCALE_DEFAULT);
                    textoImagen.setText("");
                    textoImagen.setIcon(new ImageIcon(urlImage));
                }catch (Exception e){
                    e.printStackTrace();
                    textoImagen.setText("No se pudo cargar la imagen");
                }

                System.out.println("Imagen pokemon");
                JSONObject jsonSprite = new JSONObject(response.body());
                System.out.println("Imagen front_default nuevo obejto : " + jsonSprite.getJSONObject("sprites").getString("front_default"));



                System.out.println("Otra manera de llamar la imagen sin crear otro objeto");
                System.out.println("Imagen front_default directo : " + jsonObject.getJSONObject("sprites").getString("front_default"));

                //Vamos a obtener los sonidos
                System.out.println("Sonido de Pokemon: " + jsonObject.getJSONObject("cries").getString("latest"));


            } else {
                JOptionPane.showMessageDialog(null, "No se encontro el Pokemon");
            }
        }
        catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("PokeApi");
        frame.setContentPane(new PokeApiGUI().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        frame.setResizable(true);
    }
}
