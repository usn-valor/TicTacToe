import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameWindow extends JFrame {
    
    private static final int WIN_WIDTH = 507;
    private static final int WIN_HEIGHT = 555;
    private static final int WIN_POSX = 650;
    private static final int WIN_POSY = 250;
    
    private Map map;
    private SettingsWindow settings;
    
    GameWindow() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(WIN_WIDTH, WIN_HEIGHT);
        setLocation(WIN_POSX, WIN_POSY);
        setTitle("TicTacToe");
        setResizable(false);

        JButton btnStartGame = new JButton("Start new game");
        JButton btnExit = new JButton("Exit");
        map = new Map();
        settings = new SettingsWindow(this);
        
        btnStartGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                settings.setVisible(true);
            }
        });
        
        btnExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        
        JPanel panelBottom = new JPanel();
        panelBottom.setLayout(new GridLayout(1, 2));
        panelBottom.add(btnStartGame);
        panelBottom.add(btnExit);

        add(panelBottom, BorderLayout.SOUTH);
        add(map);

        setVisible(true);
    }
    
    void applySettings(int mode, int fieldSizeX, int fieldSizeY, int winLength) {
        map.startNewGame(mode, fieldSizeX, fieldSizeY, winLength);
    }
}