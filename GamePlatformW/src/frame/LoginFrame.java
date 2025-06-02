package frame;

import java.awt.CardLayout;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JPasswordField;
import javax.swing.SwingConstants;
import java.awt.Color;
import javax.swing.JSeparator;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import database.SessionManager;
import javax.swing.JOptionPane;

import style.GuiStyle;
import database.*;


public class LoginFrame extends JFrame {

    private static final long serialVersionUID = 1L;
    private JPanel mainPanel, leftPanel, rightPanel, cardPanel;
    private CardLayout cardLayout;
    private JTextField loginField, emailField, loginFieldReg;
    private JLabel loginLabel, mainLogLabel, passwordLabel, emailLabel;
    private JLabel registerLabel, forgetPassLabel, loginChangeLabel;
    private JButton loginButton, registerButton;
    private JPasswordField passwordField, passwordFieldReg;
    private JPanel loginPanel, registerPanel;
    private JLabel iconLabel;
    private JLabel appNameLabel;
    private MouseAdapter hoverEffect;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    LoginFrame frame = new LoginFrame();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public LoginFrame() {
    	//generuje rozmieszczenie layoutow
    	generateLayout();
        //generuje caly wyglad, ktory maja wspolny obie formatki
        generateCommonLayout("Logowanie", loginPanel);
        generateCommonLayout("Rejestracja", registerPanel);
        //Kod do wygladu logowania
        generateLoginLayout();
        //Kod do wygladu rejestracji, czyli dodatkowy textbox
        generateRegisterLayout();
    }
    private void generateLayout() {
    	setResizable(false);
    	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 800, 500);
        mainPanel = new JPanel();
        setContentPane(mainPanel);
        GridBagLayout gblMainPanel = new GridBagLayout();
        gblMainPanel.rowWeights = new double[]{1.0};
        mainPanel.setLayout(gblMainPanel);

        GridBagConstraints gbcLeft = new GridBagConstraints();
        gbcLeft.insets = new Insets(0, 0, 0, 0);
        gbcLeft.fill = GridBagConstraints.BOTH;
        gbcLeft.gridx = 0;
        gbcLeft.gridy = 0;
        gbcLeft.weightx = 0.6;
        gbcLeft.weighty = 1.0;

        GridBagConstraints gbcRight = new GridBagConstraints();
        gbcRight.insets = new Insets(0, 0, 0, 0);
        gbcRight.fill = GridBagConstraints.BOTH;
        gbcRight.gridx = 1;
        gbcRight.gridy = 0;
        gbcRight.weightx = 0.4;
        gbcRight.weighty = 1.0;

        leftPanel = new JPanel();
        leftPanel.setBackground(new Color(100, 210, 195));
        rightPanel = new JPanel();
        leftPanel.setLayout(null);

        mainPanel.add(leftPanel, gbcLeft);
        
        //ikonka ze skalowaniem
        ImageIcon originalIcon = new ImageIcon(LoginFrame.class.getResource("/resources/loginIcon.png"));
        Image scaledImage = originalIcon.getImage().getScaledInstance(350,250, Image.SCALE_SMOOTH);
        ImageIcon scaledIcon = new ImageIcon(scaledImage);
        iconLabel = new JLabel(scaledIcon);
        iconLabel.setBounds(55, 95, 360, 290);
        leftPanel.add(iconLabel);
        
        appNameLabel = new JLabel("Platforma do gier");
        appNameLabel.setVerticalAlignment(SwingConstants.TOP);
        appNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
      
        GuiStyle.applyStyleLabelBasic(appNameLabel,30, 57, 20, 350, 40);  
        leftPanel.add(appNameLabel);
        
        mainPanel.add(rightPanel, gbcRight);

        cardPanel = new JPanel();
        cardLayout = new CardLayout();
        cardPanel.setLayout(cardLayout);       
        loginPanel = new JPanel();
        loginPanel.setBackground(new Color(36, 47, 65));
        
        registerPanel = new JPanel();
        registerPanel.setBackground(new Color(36, 47, 65));
        cardPanel.add(registerPanel, "Register");
        cardPanel.add(loginPanel, "Login");
    }

    
    private void generateCommonLayout(String nameOfLayout, JPanel typeOfPanel)
    {     
        typeOfPanel.setLayout(null);
        mainLogLabel = new JLabel(nameOfLayout);
        mainLogLabel.setVerticalAlignment(SwingConstants.TOP);
        mainLogLabel.setHorizontalAlignment(SwingConstants.CENTER);
        GuiStyle.applyStyleLabelBasic(mainLogLabel, 30,26, 20, 250, 40);
        typeOfPanel.add(mainLogLabel);
        
        loginLabel = new JLabel("Login :");
        GuiStyle.applyStyleLabelBasic(loginLabel, 15,25,102, 150, 20);
        typeOfPanel.add(loginLabel);
        
        passwordLabel = new JLabel("Hasło :");
        GuiStyle.applyStyleLabelBasic(passwordLabel,15, 25, 197, 150, 20);      
        typeOfPanel.add(passwordLabel);
            
        JSeparator separatorOne = new JSeparator();
        separatorOne.setBounds(25, 165, 250, 20);
        typeOfPanel.add(separatorOne);     
        
        JSeparator separatorTwo = new JSeparator();
        separatorTwo.setBounds(25, 260, 250, 20);
        typeOfPanel.add(separatorTwo);
       
        rightPanel.setLayout(new CardLayout());
        rightPanel.add(cardPanel);        
    }
    
    private void generateLoginLayout()
    {
    	//Odpowiedzialna za zmiane widoku na rejestracje 
        registerLabel = new JLabel("Nie masz konta? Zarejestruj się!");
        registerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        registerLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        //Efekt najechania myszka dla obu labelek
         hoverEffect = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                JLabel label = (JLabel) e.getSource();
                label.setForeground(new Color(185,197,217));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                JLabel label = (JLabel) e.getSource();
                label.setForeground(new Color(255, 255, 255));
            }
        };
        registerLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                cardLayout.show(cardPanel, "Register");
            }
        });
        registerLabel.addMouseListener(hoverEffect);      
        GuiStyle.applyStyleLabelBasic(registerLabel, 13, 50, 348, 195, 25);
        loginPanel.add(registerLabel);
        
        //Textbox dla loginu
        loginField = new JTextField();
        loginField = GuiStyle.applyStyleTextField(loginField);
        loginField.setBounds(25, 130, 250, 35);
        loginField.setColumns(10);
        loginPanel.add(loginField);
            
      //TextBox dla hasla
        passwordField = new JPasswordField();
        passwordField = GuiStyle.applyStyleTextField(passwordField);
        passwordField.setBounds(25, 225, 250, 35);
        loginPanel.add(passwordField);
        
        forgetPassLabel = new JLabel("Zapomniałeś hasło?");    
        forgetPassLabel.setHorizontalTextPosition(SwingConstants.CENTER);
        forgetPassLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        GuiStyle.applyStyleLabelBasic(forgetPassLabel, 13, 143, 265, 130, 25);
        forgetPassLabel.addMouseListener(new MouseAdapter() {
        	@Override
        	public void mouseClicked(MouseEvent e) {
        		//TODO wyslanie na e-maila mozliwosci zmiany hasla???
        	}
        });
        forgetPassLabel.addMouseListener(hoverEffect);    
        loginPanel.add(forgetPassLabel);
        
        loginButton = new JButton("Zaloguj się");
        loginButton = GuiStyle.applyStyleButton(loginButton, 16);
        loginButton.setBounds(25, 305, 250, 40);
        loginPanel.add(loginButton);
        loginButton.addActionListener(e -> {
            String username = loginField.getText();
            String password = new String(passwordField.getPassword());
            try {
            	//TODO do zmiany jak juz bedzie menu, bedzie sie otwierac formatka z grami
                String token = LoginService.login(username, password);
                SessionManager.setAuthToken(token, username);
                JOptionPane.showMessageDialog(LoginFrame.this, "Zalogowano pomyslnie!\nToken:\n" + token);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(LoginFrame.this, "Blad logowania: " + ex.getMessage());
            }
        });       
        cardLayout.show(cardPanel, "Login");
    	
    }
    private void generateRegisterLayout()
    {
    	 emailField = new JTextField();
    	 emailField = GuiStyle.applyStyleTextField(emailField);
    	 emailField.setBounds(25, 315, 250, 35);
         registerPanel.add(emailField);
               
         loginFieldReg = new JTextField();
         loginFieldReg = GuiStyle.applyStyleTextField(loginFieldReg);
         loginFieldReg.setBounds(25, 130, 250, 35);
         loginFieldReg.setColumns(10);
         registerPanel.add(loginFieldReg);
         
         passwordFieldReg = new JPasswordField();
         passwordFieldReg = GuiStyle.applyStyleTextField(passwordFieldReg);
         passwordFieldReg.setBounds(25, 225, 250, 35);
         registerPanel.add(passwordFieldReg);
          
         registerButton = new JButton("Zarejestruj się");
         registerButton = GuiStyle.applyStyleButton(registerButton, 16);
         registerButton.setBounds(25, 385, 250, 40);
         registerPanel.add(registerButton);
         registerButton.addActionListener(e -> { 
        	 String username = loginFieldReg.getText();
        	 String email = emailField.getText();
             String password = new String(passwordFieldReg.getPassword());
             
             try {
            	 
             	//TODO do zmiany jak juz bedzie menu, bedzie sie otwierac formatka z grami, message musi dzialac
            	 String message = RegisterService.register(username, email, password);
            	
            	 JOptionPane.showMessageDialog(this, message, "Sukces", JOptionPane.INFORMATION_MESSAGE);
                 cardLayout.show(cardPanel, "Login");

             } catch (Exception ex) {
            	 JOptionPane.showMessageDialog(this, ex.getMessage(), "Błąd z rejestracją", JOptionPane.ERROR_MESSAGE);
             }
         });       
         
         emailLabel = new JLabel("Email : ");
         emailLabel = GuiStyle.applyStyleLabelBasic(emailLabel, 15, 25, 290, 150, 20);
         registerPanel.add(emailLabel);
         
         JSeparator separatorThree = new JSeparator();
         separatorThree.setBounds(25, 350, 250, 20);
         registerPanel.add(separatorThree);
         
         loginChangeLabel = new JLabel("Masz juz konto? Zaloguj się!");
         loginChangeLabel = GuiStyle.applyStyleLabelBasic(loginChangeLabel, 13, 52, 427, 190, 25);
         loginChangeLabel.setHorizontalAlignment(SwingConstants.CENTER);
         loginChangeLabel.setHorizontalTextPosition(SwingConstants.CENTER);
         registerPanel.add(loginChangeLabel);
         loginChangeLabel.addMouseListener(new MouseAdapter() {
             public void mouseClicked(MouseEvent e) {
                 cardLayout.show(cardPanel, "Login");
             }
         });
         loginChangeLabel.addMouseListener(hoverEffect);      
    }
}