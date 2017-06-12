package Mill;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.io.File;
import java.io.IOException;

class View extends JFrame {


    //... Constants
    private final int WIDTH = 1000, HEIGHT = 730, PLAY_HEIGHT = 700, PLAY_WIDTH = 700;

    //... Components
    private JPanel content = new JPanel();

    //MenuBar
    private JPanel MenuBar = new JPanel();
    private JButton m_StartGameButton = new JButton("NEW GAME");
    private JButton m_ExitButton = new JButton("EXIT");

    //StatisticBar
    private JPanel StatisticBar = new JPanel();
    private JLabel m_gPawnLabel = new JLabel();
    private JLabel m_rPawnLabel = new JLabel();
    private JLabel m_TurnLabel = new JLabel();
    private JLabel m_GameStage = new JLabel();

    //OptionBar
    private JPanel OptionBar = new JPanel();
    private JButton m_ClientButton = new JButton("CLIENT");
    private JButton m_ServerButton = new JButton("SERVER");
    private TextField m_IpField = new TextField("localhost");
    private TextField m_PortField = new TextField("1277");

    //PlayArea
    private JComponent m_PlayArea = new JComponent() {
        @Override
        public Dimension getPreferredSize()
        {
            return new Dimension(PLAY_WIDTH, PLAY_HEIGHT);
        }
        @Override
        public void paintComponent(Graphics g)
        {
            int[] status = m_model.getStatus();
            int[][] tab = m_model.getTab();
            for(int i=0;i<24;i++) {
                if (status[i] == 0) continue;

                Graphics2D g2d = (Graphics2D) g;
                Ellipse2D.Float circle = new Ellipse2D.Float(tab[i][0]-20, tab[i][1]-20, 40, 40);
                if (status[i] == 1) g2d.setColor(Color.GREEN);                              //green Pawn
                else if (status[i] == -1) g2d.setColor(Color.RED);                          //red Pawn
                else if (status[i] == 2) g2d.setColor(Color.WHITE);                         //selected Pawn
                else if (status[i] == 3 || status[i] == -3) g2d.setColor(Color.CYAN);       //mill
                g2d.fill(circle);
                g2d.draw(circle);
            }
        }
    };


    private Model m_model;

    //======================================================= constructor
    /**
     * Constructor
     * @param model our model
     * @throws IOException cant find image
     */
    View(Model model) throws IOException{
        //... Set up the logic
        m_model = model;

        //... Initialize components
        update();

        //... Layout the components.
        content = new JPanel();
        content.setLayout(new FlowLayout());

        m_PlayArea.setVisible(true);

        MenuBar = new JPanel();
        MenuBar.setLayout(new GridLayout(4, 1));
        MenuBar.add( new JLabel("MENU", SwingConstants.CENTER));
        MenuBar.add(m_StartGameButton);
        MenuBar.add(m_ExitButton);

        OptionBar = new JPanel();
        OptionBar.setLayout(new GridLayout(5, 1));
        OptionBar.add(new JLabel("Make server first", SwingConstants.CENTER));
        OptionBar.add(m_ServerButton);
        OptionBar.add(m_ClientButton);
        OptionBar.add(m_IpField);
        OptionBar.add(m_PortField);

        StatisticBar = new JPanel();
        StatisticBar.setLayout(new GridLayout(5, 2));
        StatisticBar.add(new JLabel("Zielone pionki", SwingConstants.CENTER));
        StatisticBar.add(m_gPawnLabel);
        m_gPawnLabel.setHorizontalAlignment(SwingConstants.CENTER);
        StatisticBar.add(new JLabel("Czerwone pionki", SwingConstants.CENTER));
        StatisticBar.add(m_rPawnLabel);
        m_rPawnLabel.setHorizontalAlignment(SwingConstants.CENTER);
        StatisticBar.add(new JLabel("Tura", SwingConstants.CENTER));
        StatisticBar.add(m_TurnLabel);
        m_TurnLabel.setHorizontalAlignment(SwingConstants.CENTER);
        StatisticBar.add(new JLabel("Faza", SwingConstants.CENTER));
        StatisticBar.add(m_GameStage);
        m_GameStage.setHorizontalAlignment(SwingConstants.CENTER);

        content.add(m_PlayArea);
        content.add(MenuBar);
        content.add(StatisticBar);
        content.setOpaque(false);

        //... finalize layout
        Image image = ImageIO.read(new File("resources/board.png"));
        this.setContentPane(new JComponent() {
            @Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(image, 0, 0, this);
            }
        });
        this.setSize(WIDTH,HEIGHT);
        this.setLayout(new FlowLayout());
        this.setResizable(false);
        this.add(content);

        this.setTitle("Nine Men's Morris");
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    /**
     * update view
     */
    void update()
    {
        m_gPawnLabel.setText(Integer.toString(m_model.getMyPawns()));
        m_rPawnLabel.setText(Integer.toString(m_model.getEnemyPawns()));
        m_TurnLabel.setText(m_model.isYourTurn() ? "Twoja" : "Przeciwnika");
        m_GameStage.setText(m_model.isGame() ? (m_model.isToNine() ? "Do 9" : "Przesuwanie") : "Koniec gry");

        m_PlayArea.removeAll();
        m_PlayArea.repaint();
        m_PlayArea.revalidate();
    }

    /**
     * change bar from menu to option or vice versa
     * @param toOption change to option if true, change to menu if false
     */
    void ChangeBar(boolean toOption)
    {
        if (toOption)
        {
            content.removeAll();
            content.add(m_PlayArea);
            content.add(OptionBar);
            content.add(StatisticBar);
        }
        else
        {
            m_StartGameButton.setText("RESET");
            content.removeAll();
            content.add(m_PlayArea);
            content.add(MenuBar);
            content.add(StatisticBar);
        }
        revalidate();
    }

    void addSGBListener(ActionListener sgbl) {
        m_StartGameButton.addActionListener(sgbl);
    }
    void addEBListener(ActionListener ebl) {
        m_ExitButton.addActionListener(ebl);
    }
    void addSBListener(ActionListener sbl) {
        m_ServerButton.addActionListener(sbl);
    }
    void addCBListener(ActionListener cbl) {
        m_ClientButton.addActionListener(cbl);
    }
    void addPlayAreaListener(MouseListener ml) {
        m_PlayArea.addMouseListener(ml);
    }

    /**
     * Get ip from textfield
     * @return user input
     */
    String getIp()
    {
        return m_IpField.getText();
    }

    /**
     * Get port from textbox
     * @return user input
     */
    int getPort()
    {
        return Integer.parseInt(m_PortField.getText());
    }

    /**
     * Update view and show ending message
     * @param win true if won, false if lost
     */
    void endGame(boolean win) {
        update();
        JOptionPane.showMessageDialog(this, win ? "Wygrałeś" : "Przegrałeś");
    }

    /**
     * Update view and show ending message
     */
    void resetConnection() {
        JOptionPane.showMessageDialog(this, "Przeciwnik się rozłączył");
    }
}

