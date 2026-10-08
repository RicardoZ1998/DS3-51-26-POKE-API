# Pokémon Stadium Lite

## Ejecución

Abre el proyecto en IntelliJ IDEA. Hace falta internet, porque los Pokémon salen de PokeAPI.

En el paquete `ui`, clic derecho sobre `StadiumGUI` y elige **Run 'StadiumGUI.main()'**. No ejecutes `Main`: esa clase no abre la ventana.

En cada lado escribe un nombre y pulsa **Load**, o pulsa **Random**. Cuando los dos Pokémon estén cargados, **Fight!** se habilita. **Fight!** prepara el combate y muestra quién empieza. Cada golpe se aplica con **Siguiente**. El registro muestra el atacante, el daño, el crítico y la efectividad cuando aplican, y la vida que queda. Al llegar a 0, anuncia el ganador.

## Diseño

El proyecto usa una arquitectura en capas. `model` guarda el `Pokemon`: nombre, tipo, imagen, estadísticas y la vida actual. `api` consulta PokeAPI con `PokeApiClient` y convierte el JSON en ese objeto. `battle` aplica el combate: empieza el de mayor Speed y, si empatan, el inicio es aleatorio. El daño es `ataque * aleatorio(0 a 1) - defensa * aleatorio(0 a 1)`. Un 10 % de los golpes es crítico y multiplica ese resultado por 1.5. La efectividad usa solo el primer tipo: Agua vence a Fuego, Fuego a Planta y Planta a Agua (x1.3); al revés, x0.7; el resto, x1.0. Cada golpe resta al menos 1 de vida.

`Battle` no dibuja la pantalla. Avisa lo que ocurre por `BattleListener`: quién atacó, si el golpe fue crítico, la efectividad, la vida restante y el ganador. La interfaz, en `ui.StadiumGUI`, se actualiza solo con esos avisos.

## Capturas

Las imágenes van en `capturas/`, en la raíz del proyecto.
