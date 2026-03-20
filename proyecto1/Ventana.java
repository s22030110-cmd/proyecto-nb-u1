package proyecto1;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Ventana extends JFrame {
    private DefaultListModel modeloLista;
    private JList listaZapatos;
    private JTextField NZapato;
    private JRadioButton rbHombre;
    private JRadioButton rbMujer;

    public Ventana() {

        super("Tienda de Zapatos");
        setSize(500,400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panelNorte = new JPanel(new FlowLayout());
        NZapato = new JTextField(20);

        JButton btnAgregar = new JButton("Agregar Zapato");
        JButton btnEliminar = new JButton("Eliminar");

        panelNorte.add(new JLabel("Nuevo Zapato:"));
        panelNorte.add(NZapato);
        panelNorte.add(btnAgregar);
        panelNorte.add(btnEliminar);

        add(panelNorte, BorderLayout.NORTH);

        modeloLista = new DefaultListModel<>();
        modeloLista.addElement("Nike");
        modeloLista.addElement("Adidas");
        modeloLista.addElement("Puma");

        listaZapatos = new JList(modeloLista);

        add(new JScrollPane(listaZapatos), BorderLayout.CENTER);

        JPanel panelOpciones = new JPanel();
        panelOpciones.setLayout(new FlowLayout());

        rbHombre = new JRadioButton("Hombre");
        rbMujer = new JRadioButton("Mujer");

        ButtonGroup grupo = new ButtonGroup();
        grupo.add(rbHombre);
        grupo.add(rbMujer);

        panelOpciones.add(rbHombre);
        panelOpciones.add(rbMujer);

        add(panelOpciones, BorderLayout.EAST);

        btnAgregar.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                agregarZapato();
            }
        });
        btnEliminar.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                int indice = listaZapatos.getSelectedIndex();
                if(indice != -1){
                    modeloLista.remove(indice);
                }
            }
        });
    }

    private void agregarZapato(){
        String nombre = NZapato.getText();
        if(!nombre.isEmpty()){
            modeloLista.addElement(nombre);
            NZapato.setText("");
        }
    }

}

    


