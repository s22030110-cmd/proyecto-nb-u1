package proyecto1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class Ventana extends JFrame {

    private DefaultListModel modeloLista;
    private JList listaZapatos;
    private JTextField NZapato;
    private JRadioButton rbHombre;
    private JRadioButton rbMujer;

    // ── Cambia esta ruta por la carpeta raíz de tu proyecto ──
    private static final String RUTA_BASE = "C:\\Users\\joser\\OneDrive\\Escritorio\\Proyecto1";

    // Así quedan las carpetas de imágenes por marca y género:
    // Proyecto1\Nike\hombre\01.png  02.png  03.png ...
    // Proyecto1\Nike\mujer\01.png   02.png  03.png ...
    // Proyecto1\Adidas\hombre\01.png ...
    // Proyecto1\Adidas\mujer\01.png ...
    // Proyecto1\Puma\hombre\01.png ...
    // Proyecto1\Puma\mujer\01.png ...

    public Ventana() {
        super("Tienda de Zapatos");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel Norte (sin cambios)
        JPanel panelNorte = new JPanel(new FlowLayout());
        NZapato = new JTextField(20);
        JButton btnAgregar = new JButton("Agregar Zapato");
        JButton btnEliminar = new JButton("Eliminar");
        panelNorte.add(new JLabel("Nuevo Zapato:"));
        panelNorte.add(NZapato);
        panelNorte.add(btnAgregar);
        panelNorte.add(btnEliminar);
        add(panelNorte, BorderLayout.NORTH);

        // Lista (sin cambios)
        modeloLista = new DefaultListModel<>();
        modeloLista.addElement("Nike");
        modeloLista.addElement("Adidas");
        modeloLista.addElement("Puma");
        listaZapatos = new JList(modeloLista);
        add(new JScrollPane(listaZapatos), BorderLayout.CENTER);

        // Panel opciones (sin cambios)
        JPanel panelOpciones = new JPanel();
        panelOpciones.setLayout(new FlowLayout());
        rbHombre = new JRadioButton("Hombre");
        rbMujer  = new JRadioButton("Mujer");
        ButtonGroup grupo = new ButtonGroup();
        grupo.add(rbHombre);
        grupo.add(rbMujer);
        panelOpciones.add(rbHombre);
        panelOpciones.add(rbMujer);
        add(panelOpciones, BorderLayout.EAST);

        // Listeners originales (sin cambios)
        btnAgregar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agregarZapato();
            }
        });

        btnEliminar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int indice = listaZapatos.getSelectedIndex();
                if (indice != -1) {
                    modeloLista.remove(indice);
                }
            }
        });

        // Doble clic en Nike / Adidas / Puma abre su propio catálogo
        listaZapatos.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int indice = listaZapatos.getSelectedIndex();
                    if (indice != -1) {
                        if (!rbHombre.isSelected() && !rbMujer.isSelected()) {
                            JOptionPane.showMessageDialog(
                                Ventana.this,
                                "Por favor selecciona primero Hombre o Mujer.",
                                "Selección requerida",
                                JOptionPane.WARNING_MESSAGE
                            );
                            return;
                        }
                        String marca  = (String) modeloLista.getElementAt(indice);
                        String genero = rbHombre.isSelected() ? "hombre" : "mujer";
                        abrirCatalogo(marca, genero);
                    }
                }
            }
        });
    }

    private void abrirCatalogo(String marca, String genero) {
        JDialog catalogo = new JDialog(this,
            "Catálogo " + marca + " — " + capitalizar(genero), true);
        catalogo.setSize(620, 500);
        catalogo.setLocationRelativeTo(this);
        catalogo.setLayout(new BorderLayout());

        // Título
        JLabel titulo = new JLabel(
            "  " + marca + "  |  " + capitalizar(genero),
            SwingConstants.LEFT);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        titulo.setBorder(BorderFactory.createEmptyBorder(12, 12, 8, 12));
        catalogo.add(titulo, BorderLayout.NORTH);

        // Panel de imágenes 3 columnas
        JPanel panelImagenes = new JPanel(new GridLayout(0, 3, 12, 12));
        panelImagenes.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Ruta: Proyecto1\Nike\hombre\  o  Proyecto1\Adidas\mujer\  etc.
        File carpeta = new File(RUTA_BASE + File.separator + marca + File.separator + genero);
        int encontrados = 0;

        for (int i = 1; i <= 20; i++) {
            String nombreArchivo = String.format("%02d.png", i);
            File archivo = new File(carpeta, nombreArchivo);

            if (!archivo.exists()) {
                if (encontrados == 0 && i > 6) break;
                if (i <= 6) {
                    panelImagenes.add(crearTarjeta(
                        crearPlaceholder(nombreArchivo),
                        "Modelo " + String.format("%02d", i)
                    ));
                }
                continue;
            }

            panelImagenes.add(crearTarjeta(
                escalarImagen(archivo.getAbsolutePath()),
                "Modelo " + String.format("%02d", i)
            ));
            encontrados++;
        }

        if (encontrados == 0 && panelImagenes.getComponentCount() == 0) {
            JLabel aviso = new JLabel(
                "<html><center>No hay imágenes para " + marca + " — " + capitalizar(genero) + "<br>"
                + "Pon tus archivos .png en:<br><b>"
                + carpeta.getAbsolutePath() + "</b></center></html>",
                SwingConstants.CENTER);
            aviso.setFont(new Font("SansSerif", Font.PLAIN, 13));
            catalogo.add(aviso, BorderLayout.CENTER);
        } else {
            catalogo.add(new JScrollPane(panelImagenes), BorderLayout.CENTER);
        }

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(ev -> catalogo.dispose());
        JPanel sur = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        sur.add(btnCerrar);
        catalogo.add(sur, BorderLayout.SOUTH);

        catalogo.setVisible(true);
    }

    private JPanel crearTarjeta(ImageIcon icono, String etiqueta) {
        JPanel tarjeta = new JPanel(new BorderLayout());
        tarjeta.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        tarjeta.setBackground(Color.WHITE);
        JLabel imgLabel = new JLabel(icono, SwingConstants.CENTER);
        imgLabel.setBorder(BorderFactory.createEmptyBorder(6, 6, 4, 6));
        JLabel nombre = new JLabel(etiqueta, SwingConstants.CENTER);
        nombre.setFont(new Font("SansSerif", Font.PLAIN, 11));
        nombre.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        tarjeta.add(imgLabel, BorderLayout.CENTER);
        tarjeta.add(nombre,   BorderLayout.SOUTH);
        return tarjeta;
    }

    private ImageIcon escalarImagen(String ruta) {
        try {
            ImageIcon original = new ImageIcon(ruta);
            if (original.getIconWidth() > 0) {
                Image escalada = original.getImage()
                    .getScaledInstance(150, 130, Image.SCALE_SMOOTH);
                return new ImageIcon(escalada);
            }
        } catch (Exception ignored) {}
        return crearPlaceholder("error");
    }

    private ImageIcon crearPlaceholder(String texto) {
        int w = 150, h = 130;
        java.awt.image.BufferedImage img =
            new java.awt.image.BufferedImage(w, h,
                java.awt.image.BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(new Color(235, 235, 235));
        g.fillRect(0, 0, w, h);
        g.setColor(new Color(170, 170, 170));
        g.drawRect(0, 0, w - 1, h - 1);
        g.setFont(new Font("SansSerif", Font.BOLD, 11));
        FontMetrics fm = g.getFontMetrics();
        g.drawString(texto, (w - fm.stringWidth(texto)) / 2, h / 2 + 4);
        g.dispose();
        return new ImageIcon(img);
    }

    private String capitalizar(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    private void agregarZapato() {
        String nombre = NZapato.getText();
        if (!nombre.isEmpty()) {
            modeloLista.addElement(nombre);
            NZapato.setText("");
        }
    }
}
