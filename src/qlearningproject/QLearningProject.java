
package qlearningproject;

import java.awt.Dimension;
import javax.swing.JFrame;


public class QLearningProject {

    public static void main(String[] args) {
        Window window =  new Window();
        window.setVisible(true);
        window.pack();
        window.setLocationRelativeTo(null);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setFocusable(true);
        
    }
    
}
