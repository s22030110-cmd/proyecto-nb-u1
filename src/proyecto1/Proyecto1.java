package proyecto1;
// @author emial

import javax.swing.JFrame;


public class Proyecto1 {

    public static void main(String[] args) {
    Ventana ventana = new Ventana();
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setSize(600, 600);
        ventana.setVisible(true);
        ventana.setResizable(false);
    }
    
}
