import javax.swing.*;
import javax.swing.border.CompoundBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.concurrent.TimeUnit;

class GUI extends JPanel implements MouseListener {         //This is the board part of the display

    private Board gameBoard;
    private GameVars var;
    private InfoPanel info;
    private JFrame frame;
    private int width, height, widthS, xStart, heightS, yStart;
    private int xIntervals, yIntervals;

    GUI(Board temp, GameVars temp1, InfoPanel temp2, JFrame f) {     //Gives access to other components in the program
        gameBoard = temp;
        var = temp1;
        info = temp2;
        frame = f;

        addMouseListener(this);
        info.update();
    }


    public void paintComponent(Graphics g) {
        // boardResize();

        g.setColor(Color.WHITE);            //Does a full white background
        g.fillRect(0, 0, width, height);

        g.setColor(Color.LIGHT_GRAY);           //Board background

        g.fillRect(xStart, yStart, widthS, heightS);


        g.setColor(Color.DARK_GRAY);
        //Vertical Lines
        for (int x = 0; x <= 7; x++) {
            g.drawLine(x * xIntervals + xStart, yStart, x * xIntervals + xStart, yStart + heightS);
        }
        g.drawLine(xStart + widthS, yStart, xStart + widthS, yStart + heightS);

        //Horizontal Lines
        for (int y = 0; y <= 7; y++) {
            g.drawLine(xStart, y * yIntervals + yStart, xStart + widthS, y * yIntervals + yStart);
        }
        g.drawLine(xStart, yStart + heightS - 1, widthS, yStart + heightS - 1);

        //Circles with padding of 8 from edges
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                if (gameBoard.mainBoard[y][x] == 'B') {
                    g.setColor(Color.BLACK);
                    g.fillOval(x * xIntervals + 8 + xStart, y * yIntervals + 8 + yStart, xIntervals - 16, yIntervals - 16);
                } else if (gameBoard.mainBoard[y][x] == 'W') {
                    g.setColor(Color.WHITE);
                    g.fillOval(x * xIntervals + 8 + xStart, y * yIntervals + 8 + yStart, xIntervals - 16, yIntervals - 16);
                }

            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent me) {
        if (var.stillPlaying) {
            int tempX = me.getX();          //Find which box was clicked
            int tempY = me.getY();

            int xClick = (tempX - xStart) / xIntervals;
            int yClick = (tempY - yStart) / yIntervals;


            if ((xClick >= 0 & xClick <= 7 & yClick >= 0 & yClick <= 7) && gameBoard.mainBoard[yClick][xClick] == ' ') {

                if (var.whiteTurn) {                 //Checks and then does move
                    if (gameBoard.checkMove(xClick, yClick, 'W')) {
                        gameBoard.mainBoard[yClick][xClick] = 'W';
                        var.whiteCount += 1;
                        int changeNum = gameBoard.doMove(xClick, yClick, 'W');
                        var.whiteCount += changeNum;
                        var.blackCount -= changeNum;
                        info.changeTurn();
                    } else {
                        info.generalInfo.setText("Nope, still White turn");
                    }
                } else {
                    if (gameBoard.checkMove(xClick, yClick, 'B')) {
                        gameBoard.mainBoard[yClick][xClick] = 'B';
                        var.blackCount += 1;
                        int changeNum = gameBoard.doMove(xClick, yClick, 'B');
                        var.whiteCount -= changeNum;
                        var.blackCount += changeNum;
                        info.changeTurn();
                    } else {
                        info.generalInfo.setText("Nope, still Black turn");
                    }

                }

                info.update();      //Updates the screen from the move
                repaint();
                frame.repaint();

                //Check different endings
                if ((var.whiteCount + var.blackCount) == 64) {     //Full board
                    info.endGame();

                } else if (var.whiteCount == 0 | var.blackCount == 0) {          //Run out of pieces
                    info.endGame();

                } else if (!gameBoard.checkMovesLeft(var.whiteTurn)) {       //Everything else

                    if (!gameBoard.checkMovesLeft(!var.whiteTurn)) { //If both players can't move, then game ends
                        info.endGame();
                    } else {     //Only the current player can't move, so other player gets another go
                        info.changeTurn();
                        if (var.whiteTurn) {
                            info.generalInfo.setText("Black could not move, white turn again");
                        } else {
                            info.generalInfo.setText("White could not move, black turn again");
                        }
                    }

                }

            }
        }
    }

    //Empty methods I have to override
    @Override
    public void mousePressed(MouseEvent me) {
    }

    @Override
    public void mouseReleased(MouseEvent me) {
    }

    @Override
    public void mouseEntered(MouseEvent me) {
    }

    @Override
    public void mouseExited(MouseEvent me) {
    }

    void boardResize() {          //Sort out board resize so that it is always square
        width = this.getSize().width - 10;      //-10 is for the border
        height = this.getSize().height - 10;

        if (width > height) {      //If component is wider than it is tall, height becomes limiting factor
            widthS = height;
            xStart = (width - widthS) / 2;
            heightS = height;
            yStart = 0;

        } else {         //If component is taller than it is wide, width becomes limiting factor
            widthS = width;
            xStart = 0;
            heightS = width;
            yStart = (height - heightS) / 2;
        }
        xIntervals = widthS / 8;      //Do intervals, for where thing lie
        yIntervals = heightS / 8;

        repaint();

    }
}


class GameVars {             //This just stores lots of variable for general use
    boolean whiteTurn = true;
    int whiteCount = 2;
    int blackCount = 2;
    boolean stillPlaying = true;

    int boardWidth = 600;
    int frameWidth = boardWidth;
    int boardHeight = 450;
    int frameHeight = 600;

    final Dimension minSize = new Dimension(300, 450);

    void variablesResize(int width, int height) {      //Does resize
        frameWidth = width;
        frameHeight = height;
        boardWidth = frameWidth;
        boardHeight = (int) (frameHeight * 0.75);

    }

}


class InfoPanel extends JPanel {         //Displays information for what is going on in game

    JLabel generalInfo = new JLabel("Welcome, White turn first");
    private JLabel blackInfo = new JLabel();
    private JLabel whiteInfo = new JLabel();
    private GameVars var;
    private int width, height;
    private Font mainInfoFont;
    private Font otherFont;

    InfoPanel(GameVars temp, int w, int h) {      //Gets use of other objects that have been made
        var = temp;
        width = w;
        height = h;
        mainInfoFont = new Font("Serif", Font.BOLD, height / 2);
        otherFont = new Font("Serif", Font.PLAIN, height / 3);

    }

    public void init() {     //Organises how the panel is displayed
        blackInfo.setText("Black Score: " + Integer.toString(var.blackCount));
        whiteInfo.setText("White Score: " + Integer.toString(var.whiteCount));

        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();


        c.weightx = 0.5;
        c.anchor = GridBagConstraints.PAGE_START;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        //generalInfo.setAlignmentX(Component.CENTER_ALIGNMENT);

        this.add(generalInfo, c);
        generalInfo.setFont(mainInfoFont);

        c.anchor = GridBagConstraints.PAGE_END;
        c.weightx = 0.5;
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        this.add(blackInfo, c);
        blackInfo.setFont(otherFont);

        c.gridx = 1;
        c.gridy = 1;
        c.gridwidth = 1;
        this.add(whiteInfo, c);
        whiteInfo.setFont(otherFont);

        repaint();


    }

    @Override
    public void paintComponent(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);

    }

    public void update() {
        blackInfo.setText("Black Score: " + Integer.toString(var.blackCount));
        whiteInfo.setText("White Score: " + Integer.toString(var.whiteCount));
        repaint();
    }

    public void changeTurn() {     //Sets the turn
        var.whiteTurn ^= true;
        if (var.whiteTurn) {
            generalInfo.setText("White Turn");
        } else {
            generalInfo.setText("Black Turn");
        }
    }

    public void infoPanelResize(int w, int h) {          //Sorts out the resize, the integers are just numbers which I think look best
        width = w;
        height = h;
        mainInfoFont = new Font("Serif", Font.BOLD, height / 2);
        otherFont = new Font("Serif", Font.PLAIN, height / 3);

        generalInfo.setFont(mainInfoFont);
        blackInfo.setFont(otherFont);
        whiteInfo.setFont(otherFont);
        repaint();
        update();
    }

    public void endGame() {          //Is called when we know game has ended, counters and counted up
        var.stillPlaying = false;
        if (var.blackCount > var.whiteCount) {
            generalInfo.setText("Black wins!");
        } else if (var.whiteCount > var.blackCount) {
            generalInfo.setText("White wins!");
        } else {
            generalInfo.setText("It's a draw!");
        }
    }

}

class ReversalGame {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws InterruptedException {


        GameVars var = new GameVars();          //Creates variables object which holds general info


        JFrame frame = new JFrame("Reversal");         //Makes frame and does normal stuff
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(var.frameWidth, var.frameHeight);
        frame.setResizable(true);
        frame.setVisible(true);
        frame.setMinimumSize(var.minSize);

        Color headingColour = new Color(0, 0, 255);
        Color borderColour = new Color(0, 0, 255);

        Board gameBoard = new Board();      //Creates object which stores state of game
        gameBoard.init();


        InfoPanel infoPanel = new InfoPanel(var, var.frameWidth, (var.frameHeight - var.boardHeight) / 2);
        infoPanel.init();   //Makes info Panel


        GUI mainGUI;        //Makes visible board (Bad naming but CBA to change
        mainGUI = new GUI(gameBoard, var, infoPanel, frame);
        mainGUI.setSize(var.boardWidth, var.boardHeight);


        JPanel headerPanel = new JPanel();          //Title of the game
        headerPanel.setBackground(Color.WHITE);
        JLabel headerLabel = new JLabel("Reversal");
        headerLabel.setForeground(headingColour);
        headerLabel.setFont(new Font("Broadway", Font.BOLD, (var.frameHeight - var.boardHeight) / 3));
        headerPanel.add(headerLabel);


        //Calls the various resizing methods
        frame.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent CE) {
                Dimension s = frame.getSize();
                var.variablesResize(s.width, s.height);

                infoPanel.infoPanelResize(var.frameWidth, (var.frameHeight - var.boardHeight) / 2);
                headerLabel.setFont(new Font("Broadway", Font.BOLD, (var.frameHeight - var.boardHeight) / 3));
                mainGUI.boardResize();

                frame.repaint();
            }
        });


        //Sorts the layouts of each component inside the frame
        frame.getContentPane().setLayout(new BorderLayout(0, 0));
        frame.getContentPane().add(mainGUI, BorderLayout.CENTER);
        frame.getContentPane().add(headerPanel, BorderLayout.NORTH);
        frame.getContentPane().add(infoPanel, BorderLayout.SOUTH);
        frame.getContentPane().setBackground(Color.WHITE);

        //Border is total 10 width - hard set
        //Has an outer white bored with navy inner border
        mainGUI.setBorder(new CompoundBorder(BorderFactory.createLineBorder(Color.WHITE, 7), BorderFactory.createLineBorder(borderColour, 3)));


        //Small adjustments
        frame.repaint();
        TimeUnit.MILLISECONDS.sleep(100);
        mainGUI.boardResize();

    }

}
