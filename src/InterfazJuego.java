package uno;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;


public class InterfazJuego extends JFrame {
    private JuegoUno juego;
    private JPanel panelCartas;
    private JLabel lblCartaActual;
    private JLabel lblTurno;
    private JButton btnRobar;

    public InterfazJuego(ArrayList<Jugador> jugadores) {
        juego = new JuegoUno(jugadores);
        setTitle("Juego UNO");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        lblTurno = new JLabel("Turno de: " + juego.getJugadorActual().getNombre());
        lblTurno.setHorizontalAlignment(SwingConstants.CENTER);
        lblTurno.setFont(new Font("Arial", Font.BOLD, 20));
        lblTurno.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(lblTurno, BorderLayout.NORTH);

        JPanel panelCentral = new PanelConFondo("/uno/FondoUNO.jpg");
        panelCentral.setLayout(new FlowLayout());
        lblCartaActual = new JLabel("Carta en juego:");
        lblCartaActual.setFont(new Font("Arial", Font.BOLD, 16));
        lblCartaActual.setForeground(Color.WHITE);
        panelCentral.add(lblCartaActual);
        add(panelCentral, BorderLayout.CENTER);

        panelCartas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                GradientPaint gradiente = new GradientPaint(
                        0, 0, new Color(101, 67, 33),
                        0, getHeight(), new Color(139, 69, 19)
                );

                g2d.setPaint(gradiente);
                g2d.fillRect(0, 0, getWidth(), getHeight());

                g2d.setColor(new Color(160, 82, 45, 20));
                for (int i = 0; i < getWidth(); i += 8) {
                    g2d.drawLine(i, 0, i, getHeight());
                }

                g2d.setColor(new Color(101, 67, 33, 30));
                for (int i = 0; i < getHeight(); i += 12) {
                    g2d.drawLine(0, i, getWidth(), i);
                }

                g2d.setColor(new Color(160, 82, 45, 60));
                g2d.drawLine(0, 0, getWidth(), 0);
                g2d.drawLine(0, 1, getWidth(), 1);
            }
        };
        panelCartas.setLayout(null);
        panelCartas.setPreferredSize(new Dimension(1200, 200));
        add(panelCartas, BorderLayout.SOUTH);

        JPanel panelDerecho = new JPanel();
        panelDerecho.setLayout(new BoxLayout(panelDerecho, BoxLayout.Y_AXIS));
        panelDerecho.setBackground(new Color(139, 69, 19));
        panelDerecho.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        btnRobar = new JButton("<html><center>ROBAR<br>CARTA</center></html>");
        btnRobar.setPreferredSize(new Dimension(100, 80));
        btnRobar.setBackground(new Color(0, 0, 139));
        btnRobar.setForeground(Color.WHITE);
        btnRobar.setFont(new Font("Arial", Font.BOLD, 12));
        btnRobar.setFocusPainted(false);
        btnRobar.addActionListener(e -> {
            Jugador actual = juego.getJugadorActual();
            Cartas robada = juego.getMazo().robarCarta();
            if (robada != null) {
                actual.agregarCarta(robada);
                actualizarVista();
            }
        });

        JLabel lblMazo = new JLabel("<html><center>Cartas<br>restantes:<br>" +
                juego.getMazo().cartasRestantes() + "</center></html>");
        lblMazo.setForeground(Color.WHITE);
        lblMazo.setFont(new Font("Arial", Font.PLAIN, 12));
        lblMazo.setAlignmentX(Component.CENTER_ALIGNMENT);

        panelDerecho.add(btnRobar);
        panelDerecho.add(Box.createVerticalStrut(10));
        panelDerecho.add(lblMazo);

        add(panelDerecho, BorderLayout.EAST);

        actualizarVista();
    }

    private void actualizarVista() {
        lblTurno.setText("Turno de: " + juego.getJugadorActual().getNombre());

        JPanel panelCentral = (JPanel) getContentPane().getComponent(1);
        panelCentral.removeAll();

        JLabel lblTexto = new JLabel("Carta en juego:");
        lblTexto.setFont(new Font("Arial", Font.BOLD, 16));
        lblTexto.setForeground(Color.WHITE);
        panelCentral.add(lblTexto);

        JButton cartaActualBtn = crearBotonCarta(juego.getCartaActual(), null);
        cartaActualBtn.setPreferredSize(new Dimension(90, 130));
        cartaActualBtn.setEnabled(false);
        panelCentral.add(cartaActualBtn);

        panelCentral.revalidate();
        panelCentral.repaint();

        panelCartas.removeAll();

        Jugador actual = juego.getJugadorActual();

        if (actual instanceof JugadorIA) {
            JugadorIA ia = (JugadorIA) actual;
            Cartas jugable = ia.obtenerCartaJugable(juego.getCartaActual());

            if (jugable != null) {
                juego.jugarTurno(jugable);
            } else {
                ia.robarVariasCartas(juego.getMazo(), 1);
            }

            if (juego.haGanado(ia)) {
                JOptionPane.showMessageDialog(null, ia.getNombre() + " ha ganado el juego!", "¡GANADOR!", JOptionPane.INFORMATION_MESSAGE);
                System.exit(0);
            } else {
                Timer timer = new Timer(1500, e -> actualizarVista());
                timer.setRepeats(false);
                timer.start();
            }

        } else {
            int numCartas = actual.getMano().size();
            int cartaAncho = 100;
            int cartaAlto = 140;
            int separacion = 30;

            int espacioTotal = (numCartas - 1) * separacion + cartaAncho;
            int inicioX = Math.max((getWidth() - espacioTotal) / 2, 10);
            int y = 50;

            for (int i = 0; i < numCartas; i++) {
                Cartas carta = actual.getMano().get(i);
                CartaPanel cartaPanel = new CartaPanel(carta.getValor(), getColorCarta(carta.getColor()));

                int posX = inicioX + i * separacion;
                int posY = y;

                cartaPanel.actualizarPosicionOriginal(posX, posY);
                cartaPanel.setBounds(posX, posY, 80, 120);

                panelCartas.add(cartaPanel);

                panelCartas.setComponentZOrder(cartaPanel, i);

                cartaPanel.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        boolean jugadaValida = juego.jugarTurno(carta);
                        if (jugadaValida) {
                            actual.removerCarta(carta);
                            if (juego.haGanado(actual)) {
                                JOptionPane.showMessageDialog(null, actual.getNombre() + " ha ganado el juego!", "¡GANADOR!", JOptionPane.INFORMATION_MESSAGE);
                                System.exit(0);
                            } else {
                                actualizarVista();
                            }
                        } else {
                            animarMazoInvalido();
                            JOptionPane.showMessageDialog(null, "¡Carta inválida! Debe coincidir en color, número o ser comodín.", "Jugada inválida", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                });
            }
        }

        JPanel panelDerecho = (JPanel) getContentPane().getComponent(3);
        JLabel lblMazo = (JLabel) panelDerecho.getComponent(2);
        lblMazo.setText("<html><center>Cartas<br>restantes:<br>" +
                juego.getMazo().cartasRestantes() + "</center></html>");

        panelCartas.revalidate();
        panelCartas.repaint();

    }

    private Color getColorCarta(String color) {
        return switch (color.toLowerCase()) {
            case "rojo" -> new Color(220, 20, 20);
            case "verde" -> new Color(20, 180, 20);
            case "azul" -> new Color(20, 20, 220);
            case "amarillo" -> new Color(255, 215, 0);
            case "negro" -> new Color(50, 50, 50);
            default -> Color.GRAY;
        };
    }

    private Color getColorTexto(String color) {
        return color.equalsIgnoreCase("amarillo") ? Color.BLACK : Color.WHITE;
    }

    private JButton crearBotonCarta(Cartas carta, ActionListener listener) {
        String textoValor = switch (carta.getValor()) {
            case "Salta" -> "⊘";
            case "Reversa" -> "⟲";
            case "Comodín" -> "★";
            default -> carta.getValor();
        };

        JButton boton = new JButton("<html><center>" + textoValor + "</center></html>");
        boton.setPreferredSize(new Dimension(70, 100));
        boton.setBackground(getColorCarta(carta.getColor()));
        boton.setForeground(getColorTexto(carta.getColor()));
        boton.setFont(new Font("Arial", Font.BOLD, 16));
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createRaisedBevelBorder(),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        boton.setFocusPainted(false);
        if (listener != null) boton.addActionListener(listener);

        boton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                boton.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Color.YELLOW, 3),
                        BorderFactory.createEmptyBorder(2, 2, 2, 2)));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                boton.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createRaisedBevelBorder(),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)));
            }
        });

        return boton;
    }

    private void animarMazoInvalido() {
        Color original = btnRobar.getBackground();
        btnRobar.setBackground(Color.RED);

        Timer timer = new Timer(300, e -> btnRobar.setBackground(original));
        timer.setRepeats(false);
        timer.start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MenuInicio(
                    () -> {
                        ArrayList<Jugador> jugadores = new ArrayList<>();
                        jugadores.add(new Jugador("Jugador"));
                        jugadores.add(new JugadorIA("Nicolás Maduro"));
                        jugadores.add(new JugadorIA("Mulino"));
                        jugadores.add(new JugadorIA("Cerresiete"));

                        InterfazJuego gui = new InterfazJuego(jugadores);
                        gui.setVisible(true);
                    },
                    () -> {
                        JOptionPane.showMessageDialog(null, "Opciones aún no implementadas.");
                    },
                    () -> System.exit(0)
            );
        });
    }
}
