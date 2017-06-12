package Mill;

import java.awt.event.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import static java.lang.Thread.sleep;

class Controller{
    //... The Controller needs to interact with both the Model and View.
    private Model m_model;
    private View  m_view;

    private PrintWriter out;
    private BufferedReader in;
    private String inputLine;

    /**
     * Make server
     */
    private void server() {
        try {
            ServerSocket serverSocket = new ServerSocket(m_view.getPort());
            Socket clientSocket = serverSocket.accept();
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        }catch (Exception e) {
            e.printStackTrace();
        }

    }
    /**
     * Connect to server
     */
    private void client(){
        try
        {
            Socket kkSocket = new Socket(m_view.getIp(), m_view.getPort());
            out = new PrintWriter(kkSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(kkSocket.getInputStream()));
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    //========================================================== constructor
    /**
     * Constructor
     * @param model - our Model
     * @param view - our View
     * @throws InterruptedException - from Thread.sleep
     */
    Controller(Model model, View view) throws InterruptedException {
        m_model = model;
        m_view  = view;

        //... Add listeners to the view.
        view.addSGBListener(new SGBListener());
        view.addEBListener(new EBListener());
        view.addCBListener(new CBListener());
        view.addSBListener(new SBListener());
        view.addPlayAreaListener(new PlayAreaListener());


        while(!m_model.isConnection())
            sleep(100);
    }

    /**
     *   Send message consisting serialized status and instruction what to do next (on the end)
     *   Message can end with:
     *   1. "no" - it's still enemy turn
     *   2. "yes" - it's your turn now
     *   3. "endG" - Game over, you won
     *   4. "reset" - reset game
     *
     * @param endTurn - this is the instruction
     */
    void sendMessage(String endTurn) {
        String message = m_model.getStatusSerialized();
        if (endTurn.equals("yes")) //end of my turn
        {
            m_model.setYourTurn(false);
        }
        else if (endTurn.equals("endG"))            //game over
        {
            m_model.endGame();
            m_view.endGame(true);
            out.println(message + endTurn);
            return;
        }


        m_view.update();
        out.println(message + endTurn);
    }

    /**
     *   Message consist serialized status and instruction what to do next (on the end)
     *   Message can end with:
     *   1. "no" - it's still enemy turn
     *   2. "yes" - it's your turn now
     *   3. "endG" - Game over, you lost
     *   4. "reset" - reset game
     */
    private void receiveMessage() throws Exception{
        if (inputLine.endsWith("no")) //still enemy turn
        {
            inputLine = inputLine.replace("no","");
        }
        else if (inputLine.endsWith("yes")) //your turn now
        {
            inputLine = inputLine.replace("yes","");
            m_model.setYourTurn(true);
        }
        else if (inputLine.endsWith("endG")) //game over
        {
            inputLine = inputLine.replace("endG","");
            m_model.deserializeStatus(inputLine);
            m_model.endGame();
            m_view.endGame(false);
            return;
        }
        else if (inputLine.endsWith("reset")) //reset game
        {
            m_model.reset();
            m_view.update();
            return;
        }
        else throw new Exception("Unknown message");
        //System.out.println(inputLine);
        m_model.deserializeStatus(inputLine);
        m_view.update();
    }

    ////////////////////////////////////////// inner class SGBListener
    /**
     *   1. Reset model.
     *   2. Reset View.
     */
    class SGBListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            m_model.reset();
            m_view.update();
            if (!m_model.isConnection()) m_view.ChangeBar(true);
            else sendMessage("reset");
        }
    }
    ////////////////////////////////////////// inner class EBLListener
    /**
     *  Exit game
     */
    class EBListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    }

    ////////////////////////////////////////// inner class SBListener
    /**
     *  Make server
     */
    class SBListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            m_model.makeServer();
            server();
            m_view.ChangeBar(false);

            m_view.update();
            m_model.setConnection(true);
        }
    }

    ////////////////////////////////////////// inner class CBListener
    /**
     *  Make client
     */
    class CBListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            m_model.makeClient();
            client();
            m_view.ChangeBar(false);
            m_view.update();
            m_model.setConnection(true);
        }
    }

    //////////////////////////////////////////// inner class PlayAreaListener
    /**
     *   1. Record mouse click
     *   2. Check if game is in progress and if its your turn
     *   3. Check if the click is near splot
     *   4. Decide what to do next
     */
    class PlayAreaListener implements MouseListener
    {
        @Override
        public void mouseClicked(MouseEvent event) {
            if (!m_model.isGame() || !m_model.isYourTurn()) return;
            int x = event.getX();
            int y = event.getY();
            int[] status = m_model.getStatus();
            int[][] tab = m_model.getTab();
            int[] mouse = m_model.getMouse();
            for(int i=0;i<24;i++)
            {
                if (Math.abs(tab[i][0]-x) <= 50 && Math.abs(tab[i][1]-y) <= 50)
                {
                    if(m_model.isCapturing())
                    {
                        if(!capture(i, status)) return;
                    }
                    else if(m_model.isToNine())
                    {
                        toNine(i,status);
                        return;
                    }
                    else if(m_model.isSelected())
                    {
                        if(!select(i, status, tab, mouse)) return;
                    }
                    else
                    {
                        unselect(i, status, tab, mouse);
                        return;
                    }
                    checkEnemy(i);
                    return;
                }
            }
        }

        @Override
        public void mousePressed(MouseEvent e) {

        }

        @Override
        public void mouseReleased(MouseEvent e) {

        }

        @Override
        public void mouseEntered(MouseEvent e) {

        }

        @Override
        public void mouseExited(MouseEvent e) {

        }
    }

    void startGame() throws Exception{
            while (true) {
                try {
                    inputLine = in.readLine();
                }catch (IOException e)
                {
                    m_view.resetConnection();
                    System.exit(0);
                }
                receiveMessage();
            }
    }

    void unselect(int i, int[] status, int[][] tab, int[] mouse) {
        if (status[i] != 1) return;
        status[i] = 2;
        mouse[0] = tab[i][0];
        mouse[1] = tab[i][1];
        mouse[2] = i;
        m_model.setSelected(true);
        m_view.update();
    }

    void toNine(int i, int[] status) {
        if (status[i] != 0) return;
        status[i] = 1;
        checkEnemy(i);
        m_model.pawnPlaced();
    }

    void checkEnemy(int i) {
        if(m_model.checkMill(i))
        {
            m_model.setCapturing(true);
            m_view.update();
            sendMessage("no");
        }
        else if(!m_model.checkIfEnemyCanMove())
        {
            m_view.update();
            sendMessage("endG");
        }
        else sendMessage("yes");
    }

    boolean select(int i, int[] status, int[][]tab, int[] mouse) {
        if(  status[mouse[2]] == 2 && status[i] == 0 &&
                (  m_model.getMyPawns() == 3 ||
                        (
                                (
                                        (tab[i][0] == mouse[0] && (tab[i][1]+mouse[1]) != (tab[0][1]+tab[21][1]) ) ||
                                                (tab[i][1] == mouse[1] && (tab[i][0]+mouse[0]) != (tab[0][0]+tab[2][0]))
                                ) &&
                                        Math.abs(i-mouse[2]) != 2 &&
                                        !( ( i==1 && mouse[2]==7) || ( i==7 && mouse[2]==1) || ( i==16 && mouse[2]==22) || ( i==22 && mouse[2]==16) )
                        )
                )
                )
        {
            status[i] = 1;
            status[mouse[2]] = 0;
            m_model.setSelected(false);
            return true;
        }
        else if (tab[i][0] == mouse[0] && tab[i][1] == mouse[1])
        {
            m_model.setSelected(false);
            m_model.reduceStatus();
            m_view.update();
        }
        return false;

    }

    boolean capture(int i, int[] status) {
        if (status[i] >= 0) return false;
        status[i] = 0;
        m_model.setCapturing(false);
        m_model.reduceStatus();
        if(!m_model.isToNine() && m_model.getEnemyPawns() < 3)
        {
            m_view.update();
            sendMessage("endG");
        }
        else if(!m_model.checkIfEnemyCanMove())
        {
            m_view.update();
            sendMessage("endG");
        }
        else sendMessage("yes");
        return true;
    }
}