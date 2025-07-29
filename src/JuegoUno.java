package uno;

import java.util.ArrayList;

//este costructor recibe los jugadores, crea el mazo,
// reparte cada carta y define la carta inicial del juego
public class JuegoUno {
    private ArrayList<Jugador> jugadores;
    private Mazo mazo;
    private Cartas cartaActual;
    private int turnoActual;
    private boolean direccionNormal;

    public JuegoUno(ArrayList<Jugador> jugadores) {
        this.jugadores = jugadores;
        this.mazo = new Mazo();
        this.turnoActual = 0;
        this.direccionNormal = true;
        repartirCartasIniciales();
        cartaActual = mazo.robarCarta();

        // Evitar iniciar con comodín
        while (cartaActual.esComodin()) {
            mazo.barajar();
            cartaActual = mazo.robarCarta();
        }
    }
    //reparte 7 cartas a cada jugador al comenzar la partida
    private void repartirCartasIniciales() {
        for (Jugador jugador : jugadores) {
            for (int i = 0; i < 7; i++) {
                jugador.agregarCarta(mazo.robarCarta());
            }
        }
    }

    //si es el turno del jugador puede jugar. Si la carta es valida, juega
    public boolean jugarTurno(Cartas cartaJugada) {
        Jugador jugadorActual = jugadores.get(turnoActual);

        if (esCartaValida(cartaJugada)) {
            jugadorActual.removerCarta(cartaJugada);
            cartaActual = cartaJugada;

            aplicarEfectoCarta(cartaJugada);
            siguienteTurno();
            return true;
        } else {
            // Si la carta es invalida, no deja jugar
            return false;
        }
    }
    //verifica si la carta es valida
    private boolean esCartaValida(Cartas cartaJugada) {
        return cartaJugada.getColor().equals(cartaActual.getColor()) ||
                cartaJugada.getValor().equals(cartaActual.getValor()) ||
                cartaJugada.esComodin();
    }
    //aplica el efecto de las cartas especiales
    private void aplicarEfectoCarta(Cartas carta) {
        String valor = carta.getValor();

        switch (valor) {
            case "+2":
                getSiguienteJugador().robarVariasCartas(mazo, 2);
                siguienteTurno();
                break;
            case "+4":
                getSiguienteJugador().robarVariasCartas(mazo, 4);
                siguienteTurno();
                break;
            case "Salta":
                siguienteTurno();
                break;
            case "Reversa":
                direccionNormal = !direccionNormal;
                break;
            case "Comodín":
                break;
        }
    }
    // calcula quien será el proximo jugador
    private Jugador getSiguienteJugador() {
        int index = direccionNormal ? (turnoActual + 1) % jugadores.size()
                : (turnoActual - 1 + jugadores.size()) % jugadores.size();
        return jugadores.get(index);
    }

    private void siguienteTurno() {
        if (direccionNormal) {
            turnoActual = (turnoActual + 1) % jugadores.size();
        } else {
            turnoActual = (turnoActual - 1 + jugadores.size()) % jugadores.size();
        }
    }
    //retorna la carta que está en el centro de la mesa
    public Cartas getCartaActual() {
        return cartaActual;
    }
    //retorna el jugador que debe jugar el turno actual
    public Jugador getJugadorActual() {
        return jugadores.get(turnoActual);
    }
    //si no tiene más cartas, el jugador gana
    public boolean haGanado(Jugador jugador) {
        return jugador.getMano().isEmpty();
    }
    //ver el mazo atravez de la interfaz
    public Mazo getMazo() {
        return mazo;
    }
}
