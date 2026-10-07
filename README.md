# Pokémon Stadium Lite

## Ejecución

Abre el proyecto en IntelliJ IDEA. Hace falta internet, porque los Pokémon salen de PokeAPI.

En el paquete `ui`, clic derecho sobre `StadiumFrame` y elige **Run 'StadiumFrame.main()'**. No ejecutes `Main`: esa clase no abre la ventana.

En cada lado escribe un nombre y pulsa **Load**, o pulsa **Random**. Cuando los dos Pokémon estén cargados, **Fight** se habilita. El registro muestra cada golpe y, al final, el ganador.

## Diseño

El proyecto usa una arquitectura en capas. `model` guarda el `Pokemon`: nombre, tipo, imagen, estadísticas y la vida actual. `api` consulta PokeAPI con `PokeApiClient` y convierte el JSON en ese objeto. `battle` aplica el combate: empieza el de mayor Speed y, si empatan, el inicio es aleatorio. El daño usa ataque y defensa, y cada golpe resta al menos 1 de vida.

`Battle` no dibuja la pantalla. Avisa lo que ocurre por `BattleListener`: quién atacó, la vida restante y el ganador. La interfaz, en `ui.StadiumFrame`, se actualizará solo con esos avisos. Este combate no incluye golpe crítico ni efectividad de tipos.

