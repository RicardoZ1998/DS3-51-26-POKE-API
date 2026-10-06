package api;

import model.Pokemon;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class PokeApiClient {
    private static final String URL = "https://pokeapi.co/api/v2/pokemon/";
    private final HttpClient client = HttpClient.newHttpClient();

    public Pokemon buscarPokemonPorNombre(String nombre) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL + nombre.trim().toLowerCase()))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 400){
            throw new IOException("Pokémon no encontrado");
        }

        if (response.statusCode() != 200) {
            throw new IOException("Error de red");
        }
        return crearPokemonDesdeJson(new JSONObject(response.body()));
    }

    public Pokemon buscarPokemonAleatorio() throws IOException, InterruptedException {
        int id = (int)(Math.random() * 151) + 1;
        return buscarPokemonPorNombre(String.valueOf(id));
    }

    public Pokemon crearPokemonDesdeJson (JSONObject json){
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

    public static void main(String[] args) throws Exception {
        PokeApiClient client = new PokeApiClient();

        Pokemon pikachu = client.buscarPokemonPorNombre("pikachu");
        System.out.println(pikachu.getName() + " | " + pikachu.getType() + " | HP " + pikachu.getCurrentHp());

        Pokemon random = client.buscarPokemonAleatorio();
        System.out.println(random.getName() + " | " + random.getType() + " | HP " + random.getCurrentHp());
    }
}
