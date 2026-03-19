package proyecto1;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class Ventana extends JFrame {

    private DefaultListModel<String> modeloLista;
    private JList<String> listaZapatos;
    private JTextField NZapato;
    private JRadioButton rbHombre;
    private JRadioButton rbMujer;

    
    private static final String RUTA_BASE = "C:\\Users\\joser\\OneDrive\\Escritorio\\Proyecto1";

    private static final double[] preciosNike   = {1200, 1350, 1500, 1600, 1750, 1800};
    private static final double[] preciosAdidas = {1100, 1250, 1400, 1550, 1700, 1850};
    private static final double[] preciosPuma   = {1000, 1150, 1300, 1450, 1600, 1750};

    public Ventana() {
        super("Tienda de Zapatos");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Panel Norte
        JPanel panelNorte = new JPanel(new FlowLayout());
        panelNorte.setBackground(new Color(245, 245, 255));

        NZapato = new JTextField(20);
        NZapato.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1, true),
            BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));

        JLabel lblNuevo = new JLabel("Nuevo Zapato:");
        lblNuevo.setFont(new Font("SansSerif", Font.BOLD, 14));
        lblNuevo.setForeground(new Color(50, 50, 120));

        JButton btnAgregar = new JButton("Agregar Zapato");
        btnAgregar.setBackground(new Color(144, 238, 144));
        btnAgregar.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnAgregar.setFocusPainted(false);

        JButton btnEliminar = new JButton("Eliminar");
        btnEliminar.setBackground(new Color(255, 182, 193));
        btnEliminar.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnEliminar.setFocusPainted(false);

        // ✅ Se eliminó el JLabel duplicado "Nuevo Zapato:"
        panelNorte.add(lblNuevo);
        panelNorte.add(NZapato);
        panelNorte.add(btnAgregar);
        panelNorte.add(btnEliminar);
        add(panelNorte, BorderLayout.NORTH);

        // Lista de marcas
        modeloLista = new DefaultListModel<>();
        modeloLista.addElement("Nike");
        modeloLista.addElement("Adidas");
        modeloLista.addElement("Puma");
        listaZapatos = new JList<>(modeloLista);
        listaZapatos.setFont(new Font("SansSerif", Font.PLAIN, 14));
        add(new JScrollPane(listaZapatos), BorderLayout.CENTER);

        // Panel opciones (género)
        JPanel panelOpciones = new JPanel(new FlowLayout());
        rbHombre = new JRadioButton("Hombre");
        rbMujer  = new JRadioButton("Mujer");
        ButtonGroup grupo = new ButtonGroup();
        grupo.add(rbHombre);
        grupo.add(rbMujer);
        panelOpciones.add(rbHombre);
        panelOpciones.add(rbMujer);
        add(panelOpciones, BorderLayout.EAST);

        // Listeners botones
        btnAgregar.addActionListener(e -> agregarZapato());
        btnEliminar.addActionListener(e -> {
            int indice = listaZapatos.getSelectedIndex();
            if (indice != -1) modeloLista.remove(indice);
        });

        // Doble clic abre catálogo
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
                        String marca  = modeloLista.getElementAt(indice);
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
        catalogo.setSize(800, 600);
        catalogo.setLocationRelativeTo(this);
        catalogo.setLayout(new BorderLayout());

        // Título
        JLabel titulo = new JLabel("  " + marca + "  |  " + capitalizar(genero), SwingConstants.LEFT);
        titulo.setFont(new Font("SansSerif", Font.BOLD, 18));
        titulo.setBorder(BorderFactory.createEmptyBorder(12, 12, 8, 12));
        catalogo.add(titulo, BorderLayout.NORTH);

        // Panel de imágenes
        JPanel panelImagenes = new JPanel(new GridLayout(0, 3, 12, 12));
        panelImagenes.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Carrito
        DefaultListModel<String> carrito = new DefaultListModel<>();
        JList<String> listaCarrito = new JList<>(carrito);
        JLabel lblTotal = new JLabel("Total: $0.00");

        JButton btnEliminarCarrito = new JButton("Eliminar");
        btnEliminarCarrito.addActionListener(e -> {
            int index = listaCarrito.getSelectedIndex();
            if (index != -1) {
                carrito.remove(index);
                actualizarTotal(carrito, lblTotal);
            }
        });

        JPanel panelCarrito = new JPanel(new BorderLayout());
        panelCarrito.setPreferredSize(new Dimension(200, 0));
        panelCarrito.add(new JLabel("Carrito de compras:"), BorderLayout.NORTH);
        panelCarrito.add(new JScrollPane(listaCarrito), BorderLayout.CENTER);

        JPanel panelSurCarrito = new JPanel(new BorderLayout());
        panelSurCarrito.add(lblTotal, BorderLayout.WEST);
        panelSurCarrito.add(btnEliminarCarrito, BorderLayout.EAST);
        panelCarrito.add(panelSurCarrito, BorderLayout.SOUTH);

        catalogo.add(panelCarrito, BorderLayout.EAST);

       
        File carpeta = new File(RUTA_BASE + File.separator + marca + File.separator + genero);

        // Cantidad de modelos según la marca
        int cantidadModelos;
        switch (marca.toLowerCase()) {
            case "nike":   cantidadModelos = preciosNike.length;   break;
            case "adidas": cantidadModelos = preciosAdidas.length; break;
            case "puma":   cantidadModelos = preciosPuma.length;   break;
            default:       cantidadModelos = 0;
        }

        for (int i = 1; i <= cantidadModelos; i++) {
            String nombreArchivo = String.format("%02d.png", i);
            File archivo = new File(carpeta, nombreArchivo);

            double precio;
            switch (marca.toLowerCase()) {
                case "nike":   precio = preciosNike[i - 1];   break;
                case "adidas": precio = preciosAdidas[i - 1]; break;
                case "puma":   precio = preciosPuma[i - 1];   break;
                default:       precio = 999.99 + i;
            }

            Zapato zapato = new Zapato(
                marca,
                "Modelo " + String.format("%02d", i),
                precio,
                colorPorMarca(marca)
            );

          
            ImageIcon icono = archivo.exists()
                ? escalarImagen(archivo.getAbsolutePath())
                : crearPlaceholder(nombreArchivo);

            panelImagenes.add(crearTarjeta(icono, zapato, carrito, lblTotal));
        }

        catalogo.add(new JScrollPane(panelImagenes), BorderLayout.CENTER);

        // Botón finalizar compra
        JButton btnFinalizar = new JButton("Finalizar compra");
        btnFinalizar.addActionListener(ev -> {
            if (lblTotal.getText().equals("Total: $0.00")) {
                JOptionPane.showMessageDialog(
                    catalogo,
                    "No puedes finalizar la compra con un total de $0.00",
                    "Compra inválida",
                    JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            StringBuilder resumen = new StringBuilder("Has comprado:\n");
            for (int i = 0; i < carrito.size(); i++) {
                resumen.append("- ").append(carrito.get(i)).append("\n");
            }
            resumen.append(lblTotal.getText());

            Object[] opciones = {"Cancelar", "Continuar"};
            int seleccion = JOptionPane.showOptionDialog(
                catalogo,
                resumen.toString(),
                "Resumen de compra",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                opciones,
                opciones[0]
            );

            if (seleccion == 0) {
                catalogo.dispose();
                Ventana.this.setVisible(true);
            } else if (seleccion == 1) {
                abrirSeccionPagos();
            }
        });

        JPanel sur = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        sur.add(btnFinalizar);
        catalogo.add(sur, BorderLayout.SOUTH);

        catalogo.setVisible(true);
    }

    private JPanel crearTarjeta(ImageIcon icono, Zapato zapato,
                                DefaultListModel<String> carrito, JLabel lblTotal) {
        JPanel tarjeta = new JPanel(new BorderLayout());
        tarjeta.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        tarjeta.setBackground(zapato.color);

        JLabel imgLabel = new JLabel(icono, SwingConstants.CENTER);
        imgLabel.setBorder(BorderFactory.createEmptyBorder(6, 6, 4, 6));

        JLabel nombre = new JLabel(zapato.modelo + " - $" + zapato.precio, SwingConstants.CENTER);
        nombre.setFont(new Font("SansSerif", Font.PLAIN, 11));
        nombre.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));

        JButton btnAgregar = new JButton("Agregar");
        btnAgregar.addActionListener(e -> {
            carrito.addElement(zapato.marca + " - " + zapato.modelo + " $" + zapato.precio);
            actualizarTotal(carrito, lblTotal);
        });

        tarjeta.add(btnAgregar, BorderLayout.NORTH);
        tarjeta.add(imgLabel,   BorderLayout.CENTER);
        tarjeta.add(nombre,     BorderLayout.SOUTH);

        return tarjeta;
    }

    private void actualizarTotal(DefaultListModel<String> carrito, JLabel lblTotal) {
        double total = 0;
        for (int i = 0; i < carrito.size(); i++) {
            String item = carrito.get(i);
            String precioStr = item.substring(item.lastIndexOf("$") + 1);
            total += Double.parseDouble(precioStr);
        }
        lblTotal.setText("Total: $" + String.format("%.2f", total));
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
            new java.awt.image.BufferedImage(w, h, java.awt.image.BufferedImage.TYPE_INT_RGB);
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
        String nombre = NZapato.getText().trim();
        if (!nombre.isEmpty()) {
            modeloLista.addElement(nombre);
            NZapato.setText("");
        }
    }

    class Zapato {
        String marca;
        String modelo;
        double precio;
        Color color;

        Zapato(String marca, String modelo, double precio, Color color) {
            this.marca  = marca;
            this.modelo = modelo;
            this.precio = precio;
            this.color  = color;
        }
    }

    private Color colorPorMarca(String marca) {
        switch (marca.toLowerCase()) {
            case "nike":   return new Color(173, 216, 230); // azul claro
            case "adidas": return new Color(144, 238, 144); // verde claro
            case "puma":   return new Color(255, 182, 193); // rosa claro
            default:       return Color.LIGHT_GRAY;
        }
    }

    private void abrirSeccionPagos() {
        JDialog pagos = new JDialog(this, "Sección de Pagos", true);
        pagos.setSize(400, 300);
        pagos.setLocationRelativeTo(this);
        pagos.setLayout(new BorderLayout());

        JLabel lblMensaje = new JLabel("Procesando pago...", SwingConstants.CENTER);
        lblMensaje.setFont(new Font("SansSerif", Font.BOLD, 16));
        pagos.add(lblMensaje, BorderLayout.CENTER);

        JButton btnCerrar = new JButton("Cerrar");
        btnCerrar.addActionListener(e -> pagos.dispose());
        JPanel sur = new JPanel();
        sur.add(btnCerrar);
        pagos.add(sur, BorderLayout.SOUTH);

        pagos.setVisible(true);
    }
}