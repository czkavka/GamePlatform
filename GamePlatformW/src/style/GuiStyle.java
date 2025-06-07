package style;

import java.awt.Color;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.text.JTextComponent;

//klasa, ktora ustawia style, widac je dopiero po odpaleniu aplikacji, pomaga redundancji kodu 
public class GuiStyle {

    public static JLabel applyStyleLabelBasic(JLabel label, int fontSize, int x, int y, int width, int heigth) {
    	label.setFont(new Font("Century Gothic", Font.PLAIN, fontSize));
    	label.setBounds(x, y, width, heigth);
    	label.setForeground(new Color(255, 255, 255));
        return label;
    }
    //jtextcomponent --> jpassword, jtextfield
    public static <T extends JTextComponent> T applyStyleTextField(T textComponent) {
        textComponent.setBorder(null);
        textComponent.setFont(new Font("Century Gothic", Font.PLAIN, 16));
        textComponent.setForeground(new Color(255, 255, 255));
        textComponent.setBackground(new Color(37, 47, 65));
        return textComponent;
    }
    
    public static JButton applyStyleButton(JButton button, int fontSize) {
    	 button.setForeground(new Color(255, 255, 255));
         button.setBackground(new Color(100, 210, 195));
         button.setFont(new Font("Century Gothic", Font.PLAIN, fontSize));  	
    	return button;
    }
    
    public static  GridBagConstraints createGbc(int x, int y, double weightx, double weighty) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.weightx = weightx;
        gbc.weighty = weighty;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(0, 0, 0, 0);
        return gbc;
    }
    
    
  
    
}
