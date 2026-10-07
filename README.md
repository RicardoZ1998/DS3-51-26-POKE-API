# Pokémon Stadium Lite

## Ejecución

Pendiente. La ventana del combate todavía no está implementada

## Diseño

El proyecto usa una arquitectura en capas. `model` guarda el `Pokemon`: nombre, tipo, imagen, estadísticas y la vida actual. `api` consulta PokeAPI con `PokeApiClient` y convierte el JSON en ese objeto. `battle` aplica el combate: empieza el de mayor Speed y, si empatan, el inicio es aleatorio. El daño usa ataque y defensa, y cada golpe resta al menos 1 de vida.

`Battle` no dibuja la pantalla. Avisa lo que ocurre por `BattleListener`: quién atacó, la vida restante y el ganador. La interfaz, en `ui.StadiumFrame`, se actualizará solo con esos avisos. Este combate no incluye golpe crítico ni efectividad de tipos.

