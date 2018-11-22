package SaveWesteros;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Grid {
    private int m;
    private int n;
    private Cell[][] cells;
    private Cell dragonStone;
    private Cell agentCell;
    private List<Cell> obstacles;
    private List<Cell> whiteWalkers;
    private int agentCapacity;
    public static final int MAX_DIMENSION = 60;
    public static final int MIN_DIMENSION = 4;
    public static final int MIN_WHITE_WALKERS = 3;
    public static final int MIN_OBSTACLES = 3;
    public static final int MIN_AGENT_CAPACITY = 1;

    public Grid() {
        Random rand = new Random();
        m = rand.nextInt((MAX_DIMENSION - MIN_DIMENSION) + 1) + MIN_DIMENSION;
        n = rand.nextInt((MAX_DIMENSION - MIN_DIMENSION) + 1) + MIN_DIMENSION;
        obstacles = new ArrayList<>();
        whiteWalkers = new ArrayList<>();
        cells = new Cell[m][n];
        initCells();
        generateMap();
    }

    public Grid(int n) throws Exception {
        this(n, n);
    }

    public Grid(int m, int n) throws Exception {
        if (!(m >= MIN_DIMENSION && m <= MAX_DIMENSION && n >= MIN_DIMENSION && n <= MAX_DIMENSION)) {
            throw new Exception("Map dimensions minimum is " + MIN_DIMENSION + " and maximum " + MAX_DIMENSION);
        }
        this.m = m;
        this.n = n;
        obstacles = new ArrayList<>();
        whiteWalkers = new ArrayList<>();
        cells = new Cell[m][n];
        initCells();
        generateMap();
    }

    public int getM() {
        return m;
    }

    public int getN() {
        return n;
    }

    public Cell[][] getCells() {
        return cells;
    }

    public Cell getDragonStone() {
        return dragonStone;
    }

    public Cell getAgentCell() {
        return agentCell;
    }

    public List<Cell> getObstacles() {
        return obstacles;
    }

    public List<Cell> getWhiteWalkers() {
        return whiteWalkers;
    }

    public int getAgentCapacity() {
        return agentCapacity;
    }

    private void initCells() {
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                cells[i][j] = new Cell(i, j);
            }
        }
    }

    private void generateMap() {
        Random rand = new Random();
        int whiteWalkersNumber = rand.nextInt((n - MIN_WHITE_WALKERS) + 1) + MIN_WHITE_WALKERS;
        int obstaclesNumber = rand.nextInt((n - MIN_OBSTACLES) + 1) + MIN_OBSTACLES;
        List<String> existingCoordinates = new ArrayList<>();
        int tmpRow = m - 1;
        int tmpCol = n - 1;

        // Place agent
        agentCell = cells[tmpRow][tmpCol];
        existingCoordinates.add(tmpRow + " " + tmpCol);

        // Place obstacles
        for (int i = 0; i < obstaclesNumber; i++) {
            do {
                tmpRow = rand.nextInt(m);
                tmpCol = rand.nextInt(n);
            } while (existingCoordinates.contains(tmpRow + " " + tmpCol));
            existingCoordinates.add(tmpRow + " " + tmpCol);
            obstacles.add(cells[tmpRow][tmpCol]);
        }

        // Place White Walkers
        for (int i = 0; i < whiteWalkersNumber; i++) {
            do {
                tmpRow = rand.nextInt(m);
                tmpCol = rand.nextInt(n);
            }
            while (existingCoordinates.contains(tmpRow + " " + tmpCol));
            existingCoordinates.add(tmpRow + " " + tmpCol);
            whiteWalkers.add(cells[tmpRow][tmpCol]);
        }

        // Place the dragonstone
        do {
            tmpRow = rand.nextInt(m);
            tmpCol = rand.nextInt(n);
        } while (existingCoordinates.contains(tmpRow + " " + tmpCol));
        dragonStone = cells[tmpRow][tmpCol];

        agentCapacity = rand.nextInt((whiteWalkersNumber - MIN_AGENT_CAPACITY) + 1) + MIN_AGENT_CAPACITY;
    }

    public void printGridInfo() {
        System.out.println("Generated Grid Size: " + m + " x " + n);
        System.out.println("No. of White Walkers: " + whiteWalkers.size());
        System.out.println("No. of Obstacles: " + obstacles.size());
        System.out.println("Agent Capacity: " + agentCapacity);
    }
}
