import java.awt.*;
import java.util.HashSet;
import java.util.Set;

class Board {        //This objects stores state of the game
    char[][] mainBoard = new char[8][8];

    void init() {
        for (int x = 0; x < 8; x++) {
            for (int y = 0; y < 8; y++) {
                mainBoard[y][x] = ' ';

            }
        }
        mainBoard[3][3] = 'B';
        mainBoard[4][4] = 'B';
        mainBoard[3][4] = 'W';
        mainBoard[4][3] = 'W';

    }

    //Checks if a move is valid, returns true if the move can be made
    boolean checkMove(int x, int y, char moveColor) {

        char oppoColor;
        if (moveColor == 'B') {
            oppoColor = 'W';
        } else {
            oppoColor = 'B';
        }
        int xMove, yMove;

        //Looks at the surrounding 3X3 square of the chosen place
        for (int yVar = -1; yVar <= 1; yVar++) {
            for (int xVar = -1; xVar <= 1; xVar++) {
                xMove = x + xVar;
                yMove = y + yVar;
                if ((xMove >= 0 & xMove <= 7 & yMove >= 0 & yMove <= 7) && mainBoard[yMove][xMove] == oppoColor) {
                    //If it finds something, continue in that direction

                    while (true) {
                        xMove += xVar;
                        yMove += yVar;
                        if (xMove < 0 | xMove > 7 | yMove < 0 | yMove > 7) {
                            break;
                        } else if (mainBoard[yMove][xMove] == ' ') {
                            break;
                        } else if (mainBoard[yMove][xMove] == moveColor) {
                            return true;
                        }
                    }

                }

            }
        }
        return false;
    }


    void flip(int x, int y) {         //Simply flips the counter over, is called a lot
        if (mainBoard[y][x] == 'B') {
            mainBoard[y][x] = 'W';
        } else if (mainBoard[y][x] == 'W') {
            mainBoard[y][x] = 'B';
        }
    }

    int doMove(int x, int y, char moveColor) { //It knows them move can be made, so does so

        char oppoColor;
        int changedNum = 0;
        if (moveColor == 'B') {
            oppoColor = 'W';
        } else {
            oppoColor = 'B';
        }
        int xMove, yMove;

        Set<Point> changeList = new HashSet<>();

        //Similar technique to checkMove function, but remembers changes that are made
        for (int yVar = -1; yVar <= 1; yVar++) {
            for (int xVar = -1; xVar <= 1; xVar++) {

                xMove = x;
                yMove = y;

                while (true) {
                    xMove += xVar;
                    yMove += yVar;
                    if (xMove < 0 | xMove > 7 | yMove < 0 | yMove > 7) {
                        break;
                    } else if (mainBoard[yMove][xMove] == ' ') {
                        break;

                    } else if (mainBoard[yMove][xMove] == moveColor) {
                        for (Point current : changeList) {
                            flip(current.x, current.y);
                        }
                        changedNum += changeList.size();
                        break;

                    } else if (mainBoard[yMove][xMove] == oppoColor) {
                        changeList.add(new Point(xMove, yMove));

                    }
                }

                changeList = new HashSet<>();
            }
        }
        return changedNum;
    }


    boolean checkMovesLeft(boolean checkWhite) {
        //See if there are any moves that can still be made by the specified color
        char color = 'B';
        if (checkWhite) color = 'W';

        for (int y = 0; y <= 7; y++) {
            for (int x = 0; x <= 7; x++) {
                if (mainBoard[y][x] == ' ') {
                    if (checkMove(x, y, color)) {
                        return true;
                    }
                }

            }
        }
        return false;

    }


}
