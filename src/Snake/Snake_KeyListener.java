import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;

public class Snake_KeyListener extends JPanel implements KeyListener, ActionListener {

    private static final int ANCHO_PANTALLA = 600;
    private static final int ALTO_PANTALLA = 600;
    private static final int TAM_CASILLA = 25;
    private static final int TOTAL_CASILLAS = (ANCHO_PANTALLA * ALTO_PANTALLA) / (TAM_CASILLA * TAM_CASILLA);
    private static final int RETRASO = 120;

    private final int[] x = new int[TOTAL_CASILLAS];
    private final int[] y = new int[TOTAL_CASILLAS];
    private final Random random = new Random();
    private final Timer timer;

    private int longitud;
    private int manzanaX;
    private int manzanaY;
    private int puntuacion;
    private char direccion = 'R';
    private boolean enJuego;

    public Snake_KeyListener() {
        setPreferredSize(new Dimension(ANCHO_PANTALLA, ALTO_PANTALLA));
        setBackground(new Color(18, 24, 32));
        setFocusable(true);
        addKeyListener(this);

        JFrame frame = new JFrame("Snake");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setVisible(true);

        timer = new Timer(RETRASO, this);
        iniciarJuego();
    }

    private void iniciarJuego() {
        longitud = 3;
        puntuacion = 0;
        direccion = 'R';
        enJuego = true;

        int inicioX = ANCHO_PANTALLA / 2;
        int inicioY = ALTO_PANTALLA / 2;

        for (int i = 0; i < longitud; i++) {
            x[i] = inicioX - (i * TAM_CASILLA);
            y[i] = inicioY;
        }

        generarManzana();
        timer.start();
        repaint();
    }

    private void generarManzana() {
        boolean posicionValida = false;

        while (!posicionValida) {
            manzanaX = random.nextInt(ANCHO_PANTALLA / TAM_CASILLA) * TAM_CASILLA;
            manzanaY = random.nextInt(ALTO_PANTALLA / TAM_CASILLA) * TAM_CASILLA;
            posicionValida = true;

            for (int i = 0; i < longitud; i++) {
                if (x[i] == manzanaX && y[i] == manzanaY) {
                    posicionValida = false;
                    break;
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        dibujar(g);
    }

    private void dibujar(Graphics g) {
        if (enJuego) {
            g.setColor(new Color(34, 44, 58));
            for (int i = 0; i <= ANCHO_PANTALLA / TAM_CASILLA; i++) {
                g.drawLine(i * TAM_CASILLA, 0, i * TAM_CASILLA, ALTO_PANTALLA);
                g.drawLine(0, i * TAM_CASILLA, ANCHO_PANTALLA, i * TAM_CASILLA);
            }

            g.setColor(new Color(220, 64, 64));
            g.fillOval(manzanaX, manzanaY, TAM_CASILLA, TAM_CASILLA);

            for (int i = 0; i < longitud; i++) {
                if (i == 0) {
                    g.setColor(new Color(66, 194, 92));
                } else {
                    g.setColor(new Color(38, 138, 61));
                }
                g.fillRoundRect(x[i], y[i], TAM_CASILLA, TAM_CASILLA, 8, 8);
            }

            g.setColor(Color.WHITE);
            g.setFont(new Font("SansSerif", Font.BOLD, 24));
            g.drawString("Puntos: " + puntuacion, 15, 30);
        } else {
            mostrarGameOver(g);
        }
    }

    private void mover() {
        for (int i = longitud; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direccion) {
            case 'U':
                y[0] -= TAM_CASILLA;
                break;
            case 'D':
                y[0] += TAM_CASILLA;
                break;
            case 'L':
                x[0] -= TAM_CASILLA;
                break;
            case 'R':
                x[0] += TAM_CASILLA;
                break;
            default:
                break;
        }
    }

    private void comprobarManzana() {
        if (x[0] == manzanaX && y[0] == manzanaY) {
            longitud++;
            puntuacion++;
            generarManzana();
        }
    }

    private void comprobarColisiones() {
        for (int i = longitud; i > 0; i--) {
            if (x[0] == x[i] && y[0] == y[i]) {
                enJuego = false;
            }
        }

        if (x[0] < 0 || x[0] >= ANCHO_PANTALLA || y[0] < 0 || y[0] >= ALTO_PANTALLA) {
            enJuego = false;
        }

        if (!enJuego) {
            timer.stop();
        }
    }

    private void mostrarGameOver(Graphics g) {
        String textoFinal = "Game Over";
        String textoPuntos = "Puntuacion final: " + puntuacion;
        String textoReinicio = "Pulsa ESPACIO para jugar otra vez";

        g.setColor(new Color(255, 244, 214));
        g.setFont(new Font("SansSerif", Font.BOLD, 50));
        FontMetrics titulo = getFontMetrics(g.getFont());
        g.drawString(textoFinal, (ANCHO_PANTALLA - titulo.stringWidth(textoFinal)) / 2, ALTO_PANTALLA / 2 - 30);

        g.setFont(new Font("SansSerif", Font.PLAIN, 26));
        FontMetrics cuerpo = getFontMetrics(g.getFont());
        g.drawString(textoPuntos, (ANCHO_PANTALLA - cuerpo.stringWidth(textoPuntos)) / 2, ALTO_PANTALLA / 2 + 20);
        g.drawString(textoReinicio, (ANCHO_PANTALLA - cuerpo.stringWidth(textoReinicio)) / 2, ALTO_PANTALLA / 2 + 60);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (enJuego) {
            mover();
            comprobarManzana();
            comprobarColisiones();
        }
        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
                if (direccion != 'R') {
                    direccion = 'L';
                }
                break;
            case KeyEvent.VK_RIGHT:
                if (direccion != 'L') {
                    direccion = 'R';
                }
                break;
            case KeyEvent.VK_UP:
                if (direccion != 'D') {
                    direccion = 'U';
                }
                break;
            case KeyEvent.VK_DOWN:
                if (direccion != 'U') {
                    direccion = 'D';
                }
                break;
            case KeyEvent.VK_SPACE:
                if (!enJuego) {
                    iniciarJuego();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    public static void main(String[] args) {
        new Snake_KeyListener();
    }
}
