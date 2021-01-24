import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

public class Map extends JPanel {
    public static final int MODE_HVH = 0;
    public static final int MODE_HVA = 1;

    private static final int DOT_EMPTY = 0;
    private static final int DOT_HUMAN = 1;
    private static final int DOT_AI = 2;
    private static final int DOT_PADDING = 20;

    private int stateGameOver;
    private static final int STATE_DRAW = 0;
    private static final int STATE_WIN_HUMAN = 1;
    private static final int STATE_WIN_AI = 2;

    private static final String MSG_WIN_HUMAN = "Победил игрок!";
    private static final String MSG_WIN_AI = "Победил компьютер!";
    private static final String MSG_DRAW = "Ничья!";

    private static final Random RANDOM = new Random();

    private int[][] field;
    private int fieldSizeX;
    private int fieldSizeY;
    private int winLength;
    private int cellWidth;
    private int cellHeight;
    private boolean isGameOver;
    private boolean initialized;

    Map() {
        setBackground(Color.DARK_GRAY);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                update(e);
            }
        });
        initialized = false;
    }

    private void update(MouseEvent e) {
        if (isGameOver || !initialized)
            return;
        int cellX = e.getX() / cellWidth;
        int cellY = e.getY() / cellHeight;
        if (!isValidCell(cellY, cellX) || !isEmptyCell(cellY, cellX))
            return;
        field[cellY][cellX] = DOT_HUMAN;
        if (checkEndGame(DOT_HUMAN, STATE_WIN_HUMAN))
            return;
        aiTurn();
        repaint();
        if (checkEndGame(DOT_AI, STATE_WIN_AI))
            return;
    }

    private boolean checkEndGame(int dot, int stateGameOver) {
        if (checkWin(dot)) {
            this.stateGameOver = stateGameOver;
            isGameOver = true;
            repaint();
            return true;
        }
        if (checkDraw()) {
            this.stateGameOver = STATE_DRAW;
            isGameOver = true;
            repaint();
            return true;
        }
        return false;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        render(g);
    }

    private void render(Graphics g) {
        if (!initialized)
            return;

        g.setColor(Color.white);

        cellWidth = getWidth() / fieldSizeX;
        cellHeight = getHeight() / fieldSizeY;
        for (int i = 1; i < fieldSizeX; i++)
            g.drawLine(i * cellWidth, 0, i * cellWidth, getHeight());
        for (int i = 1; i < fieldSizeY; i++)
            g.drawLine(0, i * cellHeight, getWidth(), i * cellHeight);

        for (int y = 0; y < fieldSizeY; y++) {
            for (int x = 0; x < fieldSizeX; x++) {
                if (isEmptyCell(y, x))
                    continue;
                if (field[y][x] == DOT_AI) {
                    g.setColor(Color.RED);
                    g.drawOval(x * cellWidth + DOT_PADDING,
                            y * cellHeight + DOT_PADDING,
                            cellWidth - DOT_PADDING * 2,
                            cellHeight - DOT_PADDING * 2);
                } else if (field[y][x] == DOT_HUMAN) {
                    g.setColor(new Color(1, 1, 255));
                    g.drawLine(x * cellWidth + DOT_PADDING,
                            y * cellHeight + DOT_PADDING,
                            cellWidth * (x + 1) - DOT_PADDING,
                            cellHeight * (y + 1) - DOT_PADDING);
                    g.drawLine(cellWidth * (x + 1) - DOT_PADDING,
                            y * cellHeight + DOT_PADDING,
                            x * cellWidth + DOT_PADDING,
                            cellHeight * (y + 1) - DOT_PADDING);
                } else {
                    throw new RuntimeException(
                            String.format("Can't recognize cell field[%d][%d]: %d", y, x, field[y][x]));
                }
            }
        }
        if (isGameOver)
            showMessageGameOver(g);
    }

    private void showMessageGameOver(Graphics g) {
        g.setColor(Color.MAGENTA);
        g.fillRect(0, 200, getWidth(), 70);
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Times new roman", Font.BOLD, 48));
        switch (stateGameOver) {
            case STATE_DRAW:
                g.drawString(MSG_DRAW, 180, getHeight() / 2);
                break;
            case STATE_WIN_AI:
                g.drawString(MSG_WIN_AI, 20, getHeight() / 2);
                break;
            case STATE_WIN_HUMAN:
                g.drawString(MSG_WIN_HUMAN, 70, getHeight() / 2);
                break;
            default:
                throw new RuntimeException("Unexpected gameOver state: " + stateGameOver);
        }
    }

    void startNewGame(int mode, int fieldSizeX, int fieldSizeY, int winLength) {
        this.fieldSizeX = fieldSizeX;
        this.fieldSizeY = fieldSizeY;
        this.winLength = winLength;
        field = new int[fieldSizeY][fieldSizeX];
        isGameOver = false;
        initialized = true;
        repaint();
    }

    private void aiTurn() {
        for (int y = 0; y < fieldSizeY; y++) {
            for (int x = 0; x < fieldSizeY; x++) {
                if(turnCheckHuman(DOT_HUMAN, y, x)) {
                    field[y][x] = DOT_AI;
                    return;
                }
                else if (turnCheckAI(DOT_AI, y, x)) {
                    field[y][x] = DOT_AI;
                    return;
                }
            }
        }

        int y, x;
        do {
            x = RANDOM.nextInt(fieldSizeX);
            y = RANDOM.nextInt(fieldSizeY);
        } while (!isEmptyCell(y, x));
        field[y][x] = DOT_AI;
    }

    private boolean turnCheckHuman(int c, int y, int x) {
        boolean b = false;
        if (isEmptyCell(y, x)) {
            field[y][x] = c;
            if (checkWin(c))
                b = true;
            field[y][x] = DOT_EMPTY;
        }

        return b;
    }

    private boolean turnCheckAI(int c, int y, int x) {
        boolean b = false;
        if (isEmptyCell(y, x)) {
            field[y][x] = c;
            if(checkWin(c))
                b = true;
            field[y][x] = DOT_EMPTY;
        }

        return b;
    }

    private boolean checkWin(int c) {
        int counterHor = 0, counterVer = 0, counterDiaPos = 0, counterDiaNeg = 0;

        for (int y = 0; y < fieldSizeY; y++) {
            for (int x = 0; x < fieldSizeY; x++) {
                counterHor = field[y][x] == c ? counterHor + 1 : 0;
                counterVer = field[x][y] == c ? counterVer + 1 : 0;

                if (field[y][x] == c && fieldSizeY - y >= winLength) {
                    if (x >= winLength - 1) {
                        counterDiaNeg++;
                        for (int i = 1; i < winLength; i++) {
                            if (field[y + i][x - i] == c)
                                counterDiaNeg ++;
                            else {
                                counterDiaNeg = 0;
                                break;
                            }
                        }
                    }

                    if (fieldSizeX - x >= winLength) {
                        counterDiaPos++;
                        for (int i = 1; i < winLength; i++) {
                            if (field[y + i][x + i] == c)
                                counterDiaPos ++;
                            else {
                                counterDiaPos = 0;
                                break;
                            }
                        }
                    }
                }

                if (counterHor == winLength || counterVer == winLength || counterDiaPos == winLength || counterDiaNeg == winLength)
                    return true;
            }

            counterHor = 0;
            counterVer = 0;
            counterDiaPos = 0;
            counterDiaNeg = 0;
        }

        return false;
    }

    private boolean checkDraw() {
        for (int y = 0; y < fieldSizeY; y++) {
            for (int x = 0; x < fieldSizeX; x++) {
                if (isEmptyCell(x, y))
                    return false;
            }
        }
        return true;
    }

    private boolean isEmptyCell(int y, int x) {
        return field[y][x] == DOT_EMPTY;
    }

    private boolean isValidCell(int y, int x) {
        return x >= 0 && x < fieldSizeX && y >= 0 && y < fieldSizeY;
    }
}