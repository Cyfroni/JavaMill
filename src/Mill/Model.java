package Mill;

class Model
{

    //... Constants
    private boolean yourTurn, selected, capturing, game, server, connection;
    private int toNine;
    private int mouse[] = new int[3];
    private int status[] = new int[24];
    private int[][] tab = {
            { 31, 31},                       {349, 31},                       {669, 31},
            {137,137},            {349,137},            {563,137},
            {244,244}, {349,244}, {456,244},
            { 31,349}, {137,349}, {244,349},            {456,351}, {563,351}, {669,351},
            {244,456}, {351,456}, {456,456},
            {137,563},            {351,563},            {563,563},
            { 31,669},                       {351,669},                       {669,669}
    };


    //============================================================== constructor

    /**
     * Constructor
     */
    Model() {

        for(int i=0;i<24;i++)
        {
            tab[i][0] -= 13;
            tab[i][1] -= 11;
        }
        reset();
    }

    /**
     * Reset game
     */
    void reset() {
        game = true;
        yourTurn = server;
        selected = false;
        capturing = false;
        toNine = 0;
        for(int i=0;i<3;i++)
            mouse[i] = 0;
        for(int i=0;i<24;i++)
            status[i] = 0;
    }

    /**
     * connection getter
     * @return - Model.connection
     */
    boolean isConnection() {
        return connection;
    }

    /**
     * connection setter
     * @param connection - new value
     */
    void setConnection(boolean connection) {
        this.connection = connection;
    }

    /**
     * Game stage checker
     * @return - if player places less than 9 Pawns return true, else false
     */
    boolean isToNine() {
        return !(toNine >= 9);
    }

    /**
     * Pawn counter
     */
    void pawnPlaced() {
        toNine++;
    }

    /**
     * yourTurn getter
     * @return - Model.yourTurn
     */
    boolean isYourTurn() {
        return yourTurn;
    }

    /**
     * yourTurn setter
     * @param yourTurn - new value
     */
    void setYourTurn(boolean yourTurn) {
        this.yourTurn = yourTurn;
    }

    /**
     * selected getter
     * @return - Model.selected
     */
    boolean isSelected() {
        return selected;
    }

    /**
     * selected setter
     * @param selected - new value
     */
    void setSelected(boolean selected) {
        this.selected = selected;
    }

    /**
     * capturing getter
     * @return - Model.capturing
     */
    boolean isCapturing() {
        return capturing;
    }

    /**
     * capturing setter
     * @param capturing - new value
     */
    void setCapturing(boolean capturing) {
        this.capturing = capturing;
    }

    /**
     * game getter
     * @return - Model.game
     */
    boolean isGame() {
        return game;
    }

    /**
     * Set game to false
     */
    void endGame() {
        game = false;
    }

    /**
     * Calculate enemy pawns
     * @return - number of enemy pawns
     */
    int getEnemyPawns() {
        int sum = 0;
        for(int i=0;i<24;i++)
            if (status[i]<0) sum++;
        return sum;
    }

    /**
     * Calculate ally pawns
     * @return - number of ally pawns
     */
    int getMyPawns() {
        int sum = 0;
        for(int i=0;i<24;i++)
            if (status[i]>0) sum++;
        return sum;
    }

    /**
     * mouse getter
     * @return - Model.mouse
     */
    int[] getMouse() {
        return mouse;
    }

    /**
     * status getter
     * @return - Model.status
     */
    int[] getStatus() {
        return status;
    }

    public void setStatus(int[] status) {
        this.status = status;
    }

    /**
     * tab getter
     * @return - Model.tab
     */
    int[][] getTab() {
        return tab;
    }

    /**
     * Reduce status of all pawns to 1 (ally) or -1 (enemy)
     */
    void reduceStatus() {
        for(int i=0;i<24;i++)
        {
            if (status[i]<0) status[i]=-1;
            else if (status[i]>0) status[i] = 1;
        }
    }

    /**
     * Make client
     */
    void makeClient (){
        yourTurn = false;
        server = false;
    }

    /**
     * Make server
     */
    void makeServer (){
        yourTurn = true;
        server = true;
    }

    /**
     * Serialize status to string
     * @return - serialized status
     */
    String getStatusSerialized(){

        String message = new String();

        for(int i=0;i<24;i++)
        {
            message += Integer.toString((-1) * status[i]);
        }
        return message;
    }

    /**
     * Deserialize status and overwrite its own
     * @param message - status to deserialize
     */
    void deserializeStatus(String message){

        boolean repair = false;
        if (message.charAt(0) == '1') repair = true;
        String[] string = message.split("-");
        int x = 0;
        for(String str : string)
        {
            //System.out.println(str);
            for(int i=0;i<str.length();i++)
            {
                status[x] = Character.getNumericValue(str.charAt(i));
                if( i == 0 ) status[x] *= -1;
                x++;
            }

        }
        if(repair) status[0] *= -1;
    }

    /**
     * Check if pawn is in mill
     * @param n - pawn's position in status
     * @return - if the pawn is in mill - true, else false
     */
    boolean checkMill(int n) {
        int counter = 0;
        for(int i=0;i<24;i++)
            if(tab[i][0] == tab[n][0] && status[i] > 0) counter++;
        if (counter == 3)
        {
            for(int i=0;i<24;i++)
                if(tab[i][0] == tab[n][0]) status[i]=3;
            return true;
        }
        counter = 0;
        for(int i=0;i<24;i++)
            if(tab[i][1] == tab[n][1] && status[i] > 0) counter++;
        if (counter == 3)
        {
            for(int i=0;i<24;i++)
                if(tab[i][1] == tab[n][1]) status[i]=3;
            return true;
        }
        return false;
    }

    /**
     * Check if enemy can move
     * @return - if enemy can move return true, else false
     */
    boolean checkIfEnemyCanMove() {
        if (isToNine() || getEnemyPawns() == 3) return true;
        for(int i=0;i<24;i++)
        {
            if (status[i] < 0)
            {
                for(int j=0;j<24;j++)
                {
                    if (  (tab[i][0] == tab[j][0] && (tab[i][1]+tab[j][1]) != (tab[0][1]+tab[21][1]) ) ||
                            (tab[i][1] == tab[j][1] && (tab[i][0]+tab[j][0]) != (tab[0][0]+tab[2][0]))      )
                    {
                        if (status[j] == 0 && (Math.abs(i-j) != 2) && !( ( i==1 && j==7) || ( i==7 && j==1) || ( i==16 && j==22) || ( i==22 && j==16) ))
                            return true;
                    }
                }
            }
        }
        return false;
    }

}