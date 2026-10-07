package api;

import model.Pokemon;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class PokeApiClient {
    private static final String URL = "https://pokeapi.co/api/v2/pokemon/";
    private final HttpClient client = HttpClient.newHttpClient();

    public Pokemon buscarPokemonPorNombre(String nombre) throws IOException, InterruptedException {
        String nombreLimpio = nombre == null ? "" : nombre.trim().toLowerCase();
        if (nombreLimpio.isEmpty()) {
            throw new IOException("Escribe el nombre de un Pokémon antes de buscar.");
        }

        HttpRequest request;
        try {
            request = HttpRequest.newBuilder()
                    .uri(URI.create(URL + nombreLimpio))
                    .build();
        } catch (IllegalArgumentException ex) {
            throw new IOException("El nombre \"" + nombreLimpio + "\" contiene caracteres no válidos.", ex);
        }

        HttpResponse<String> response;
        try {
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException ex) {
            throw new IOException("Error de red: no se pudo conectar con PokeAPI. Revisa tu conexión a internet.", ex);
        }

        if (response.statusCode() == 404) {
            throw new IOException("Pokémon no encontrado: \"" + nombreLimpio + "\"");
        }

        if (response.statusCode() != 200) {
            throw new IOException("Error de red: PokeAPI respondió con el código " + response.statusCode() + ".");
        }

        try {
            return crearPokemonDesdeJson(new JSONObject(response.body()));
        } catch (JSONException ex) {
            throw new IOException("La respuesta de PokeAPI no tiene el formato esperado.", ex);
        }
    }

    public Pokemon buscarPokemonAleatorio() throws IOException, InterruptedException {
        int id = (int)(Math.random() * 151) + 1;
        return buscarPokemonPorNombre(String.valueOf(id));
    }

    // Descarga el sprite del Pokémon. Devuelve null si no se pudo descargar (URL inválida, sin red, etc.).
    public BufferedImage descargarSprite(Pokemon pokemon) {
        try {
            return ImageIO.read(URI.create(pokemon.getSpriteUrl()).toURL());
        } catch (IOException | IllegalArgumentException ex) {
            return null;
        }
    }

    private Pokemon crearPokemonDesdeJson(JSONObject json) {
        String type = json.getJSONArray("types")
                .getJSONObject(0)
                .getJSONObject("type")
                .getString("name");

        String sprite = json.getJSONObject("sprites").getString("front_default");

        int hp = 0;
        int attack = 0;
        int defense = 0;
        int speed = 0;

        JSONArray stats = json.getJSONArray("stats");
        for (int i = 0; i < stats.length(); i++) {
            JSONObject stat = stats.getJSONObject(i);
            String statName = stat.getJSONObject("stat").getString("name");
            int value = stat.getInt("base_stat");
            if (statName.equals("hp")) {
                hp = value;
            } else if (statName.equals("attack")) {
                attack = value;
            } else if (statName.equals("defense")) {
                defense = value;
            } else if (statName.equals("speed")) {
                speed = value;
            }
        }
        return new Pokemon(
                json.getString("name"),
                type,
                sprite,
                hp,
                attack,
                defense,
                speed
        );
    }
}
