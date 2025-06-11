package style;

import java.awt.Color;
import java.awt.Cursor;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.text.JTextComponent;

import frame.MenuFrame;
/*
 * Pomocnicza klasa do zmiany stylu komponentow w formatce
 */
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
    public static JButton createGradientButton(String text, int fontSize, Color startColor, Color endColor, String iconPath) {
        JButton button = new JButton(text) {
            private boolean isHovered = false;
            private boolean isPressed = false;

            {
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseEntered(MouseEvent e) {
                        isHovered = true;
                        repaint();
                    }
                    @Override
                    public void mouseExited(MouseEvent e) {
                        isHovered = false;
                        isPressed = false;
                        repaint();
                    }
                    @Override
                    public void mousePressed(MouseEvent e) {
                        isPressed = true;
                        repaint();
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        isPressed = false;
                        repaint();
                    }
                });
            }
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0, 0, startColor, 0, getHeight(), endColor));
                g2.fillRect(0, 0, getWidth(), getHeight());

                if (isHovered) {
                    g2.setColor(new Color(255, 255, 255, 30));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }
                if (isPressed) {
                    g2.setColor(new Color(0, 0, 0, 60));
                    g2.fillRect(0, 0, getWidth(), getHeight());
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };

        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setForeground(new Color(255,255,255));
        button.setFont(new Font("Century Gothic", Font.PLAIN, fontSize));
        ImageIcon icon = new ImageIcon(MenuFrame.class.getResource(iconPath));
        Image scaledImage = icon.getImage().getScaledInstance(80, 80, Image.SCALE_SMOOTH);
        button.setIcon(new ImageIcon(scaledImage));
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setIconTextGap(7);
       
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
    
    //dla przycisku z ikona (w menu)
    public static JButton createIconButton(String resource, int x, int y, int width, int height) {
    	URL imgURL = GuiStyle.class.getResource(resource);
        ImageIcon icon = new ImageIcon(imgURL);
        JButton button = new JButton(icon);
        button.setBounds(x, y, width, height);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setContentAreaFilled(true);
                button.setOpaque(true);
                button.setBackground(new Color(37, 77, 112));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setContentAreaFilled(false);
            }
        });

        return button;
    }
    public static JButton createStyledButton(String text, int fontSize, int x, int y, int width, int height) {
        JButton button = new JButton(text);
        button = GuiStyle.applyStyleButton(button, fontSize);
        button.setBounds(x, y, width, height);
        return button;
    }

    
    
  
    
}
