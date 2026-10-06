import org.json.JSONObject;

import javax.swing.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class PokeApi
{
    public void consultarPokemon()
    {
        try {
            //Solicitar el nombre del pokemón
            String nombrePokemon = JOptionPane.showInputDialog("Ingrese el nombre del Pokemon");
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

                System.out.println("ID Pokemon: " + jsonObject.getInt("id"));
                System.out.println("Nombre Pokemon: " + jsonObject.getString("name"));
                System.out.println("Peso Pokemon: " + jsonObject.getInt("weight"));
                System.out.println("Altura Pokemon: " + jsonObject.getInt("height"));

                //Vamos a obtener las habilidades pero estas están contenidas en un Array
                System.out.println("Habilidades");

                jsonObject.getJSONArray("abilities").forEach(habilidad ->
                {
                    //Accedemos al objeto
                    JSONObject abilityJson = (JSONObject) habilidad;

                    JSONObject abilityName = abilityJson.getJSONObject("ability");

                    System.out.println("Nombre habilidad: " + abilityName.getString("name"));

                });

                //Vamos a obtener las estadísticas

                System.out.println("Estadisticas o stats");

                jsonObject.getJSONArray("stats").forEach(stats -> {

                    //Accedemos al objeto
                    JSONObject statsJson = (JSONObject) stats;

                    JSONObject statName = statsJson.getJSONObject("stat");
                    System.out.println("Nombre stat: " + statName.getString("name") + " = " + statsJson.getInt("base_stat"));

                });

                //Vamos a obtener la imagen del pokemon
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
        catch (IOException |  InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        PokeApi pokeApi = new PokeApi();
        pokeApi.consultarPokemon();
    }
}
