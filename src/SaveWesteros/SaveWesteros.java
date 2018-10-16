package SaveWesteros;

import Search.*;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SaveWesteros extends Problem {
    private Grid grid;
    private List<Cell> whiteWalkers;
    private List<Cell> obstacles;
    private Cell dragonStone;
    private int agentCapacity;
    private Strategy strategy;
    public static final double PICKUP_COST = 6;
    public static final double MOVE_COST = 5;
    public static final double KILL_COST = 4;

    public SaveWesteros() {
        initOperators();
        whiteWalkers = new ArrayList<>();
        obstacles = new ArrayList<>();
    }

    public Grid getGrid() {
        return grid;
    }

    public List<Cell> getWhiteWalkers() {
        return whiteWalkers;
    }

    public List<Cell> getObstacles() {
        return obstacles;
    }

    public Cell getDragonStone() {
        return dragonStone;
    }

    public int getAgentCapacity() {
        return agentCapacity;
    }

    public Strategy getStrategy() {
        return strategy;
    }

    private void initOperators() {
        List<Operator> operators = new ArrayList<>();
        operators.add(new Operator("UP", MOVE_COST));
        operators.add(new Operator("DOWN", MOVE_COST));
        operators.add(new Operator("LEFT", MOVE_COST));
        operators.add(new Operator("RIGHT", MOVE_COST));
        operators.add(new Operator("KILL"));
        operators.add(new Operator("PICKUP", PICKUP_COST));
        super.setOperators(operators);
    }

    private void getDataFromGrid() {
        whiteWalkers = grid.getWhiteWalkers();
        obstacles = grid.getObstacles();
        agentCapacity = whiteWalkers.size();
        dragonStone = grid.getDragonStone();
        super.setInitialState(new SaveWesterosState(cloneCells(whiteWalkers), grid.getAgentCell(), 0));
    }

    public Grid genGrid() {
        return new Grid();
    }

    public Grid genGrid(int n) throws Exception {
        return new Grid(n);
    }

    public Grid genGrid(int m, int n) throws Exception {
        return new Grid(m, n);
    }

    private List<Cell> cloneCells(List<Cell> cells) {
        return new ArrayList<>(cells);
    }

    private String getSequenceOfMoves(List<Node> expandedNodes) {
        return String.join(" -> ", expandedNodes.stream().skip(1).map(node -> node.getOperator().getName()).collect(Collectors.toList()));
    }

    private void visualizeGrid(State state) {
        SaveWesterosState westerosState = (SaveWesterosState) state;
        List<Cell> currentWhiteWalkers = westerosState.getWhiteWalkers();
        Cell[][] cells = grid.getCells();
        Cell currentAgentCell = westerosState.getAgentCell();
        Cell current;
        System.out.println();
        System.out.println();
        for (int i = 0; i < grid.getM(); i++) {
            for (int j = 0; j < grid.getN(); j++) {
                current = cells[i][j];
                System.out.print("[" + (obstacles.contains(current) ? "O" : currentWhiteWalkers.contains(current) ? "W" : current == dragonStone ? "D" : current == currentAgentCell ? "A" : "E") + "]");
            }
            System.out.println();
        }
        System.out.println();
        System.out.println();
    }

    @Override
    public boolean isGoal(State state) {
//        System.out.println("DG " + ((SaveWesterosState) state).getDragonGlass());
//        System.out.println("No of WW: " + ((SaveWesterosState) state).getWhiteWalkers().size());
        return ((SaveWesterosState) state).getWhiteWalkers().isEmpty();
    }

    public boolean canVisitCell(Cell cell) {
        return !(whiteWalkers.contains(cell) || obstacles.contains(cell));
    }

    public SaveWesterosState killWhiteWalkers(List<Cell> whiteWalkers, Cell currentAgentCell, int dragonGlass) {
        List<Cell> clonedWhiteWalkers = cloneCells(whiteWalkers);
        int row = currentAgentCell.getRow();
        int col = currentAgentCell.getCol();
        Predicate<Cell> predicate = cell -> {
            int adjacentRow = cell.getRow();
            int adjacentCol = cell.getCol();
            return (adjacentRow == row - 1 && adjacentCol == col) || (adjacentRow == row + 1 && adjacentCol == col) || (adjacentRow == row && adjacentCol == col - 1) || (adjacentRow == row && adjacentCol == col + 1);
        };
        if (clonedWhiteWalkers.removeIf(predicate)) {
            dragonGlass--;
        }
        return new SaveWesterosState(clonedWhiteWalkers, currentAgentCell, dragonGlass);
    }

    @Override
    public List<Node> expand(Node node, List<Operator> operators) {
        SaveWesterosState state = (SaveWesterosState) node.getState();
        Cell currentAgentCell = state.getAgentCell();
        int row = currentAgentCell.getRow();
        int col = currentAgentCell.getCol();
        Cell[][] cells = grid.getCells();
        List<Node> expansion = new ArrayList<>();
        List<Cell> stateWhiteWalkers;
        Cell nextAgentCell;

        for (Operator operator : operators) {
            switch (operator.getName()) {
                case "UP":
                    if (row > 0) {
                        nextAgentCell = cells[row - 1][col];
                        if (canVisitCell(nextAgentCell)) {
                            expansion.add(new Node(new SaveWesterosState(cloneCells(state.getWhiteWalkers()), nextAgentCell, state.getDragonGlass()), node, node.getDepth() + 1, operator));
                        }
                    }
                    break;
                case "DOWN":
                    if (row < grid.getM() - 1) {
                        nextAgentCell = cells[row + 1][col];
                        if (canVisitCell(nextAgentCell)) {
                            expansion.add(new Node(new SaveWesterosState(cloneCells(state.getWhiteWalkers()), nextAgentCell, state.getDragonGlass()), node, node.getDepth() + 1, operator));
                        }
                    }
                    break;
                case "LEFT":
                    if (col > 0) {
                        nextAgentCell = cells[row][col - 1];
                        if (canVisitCell(nextAgentCell)) {
                            expansion.add(new Node(new SaveWesterosState(cloneCells(state.getWhiteWalkers()), nextAgentCell, state.getDragonGlass()), node, node.getDepth() + 1, operator));
                        }
                    }
                    break;
                case "RIGHT":
                    if (col < grid.getN() - 1) {
                        nextAgentCell = cells[row][col + 1];
                        if (canVisitCell(nextAgentCell)) {
                            expansion.add(new Node(new SaveWesterosState(cloneCells(state.getWhiteWalkers()), nextAgentCell, state.getDragonGlass()), node, node.getDepth() + 1, operator));
                        }
                    }
                    break;
                case "PICKUP":
                    if (currentAgentCell == dragonStone) {
                        expansion.add(new Node(new SaveWesterosState(cloneCells(state.getWhiteWalkers()), currentAgentCell, agentCapacity), node, node.getDepth() + 1, operator));
                    }
                    break;
                case "KILL":
                    stateWhiteWalkers = state.getWhiteWalkers();
                    if (!stateWhiteWalkers.isEmpty() && state.getDragonGlass() > 0) {
                        SaveWesterosState newState = killWhiteWalkers(stateWhiteWalkers, currentAgentCell, state.getDragonGlass());
                        expansion.add(new Node(newState, node, node.getDepth() + 1, new Operator("KILL", KILL_COST - (stateWhiteWalkers.size() - newState.getWhiteWalkers().size()))));
                    }
                    break;
            }
        }

        return expansion;
    }

    public List search(Grid grid, Strategy strategy, boolean visualize) {
        this.grid = grid;
        this.strategy = strategy;
        getDataFromGrid();
        int limit = 0;
        Node goalNode = null;
        if (strategy == Strategy.ID) {
            while (goalNode == null) {
                goalNode = Problem.generalSearch(this, strategy, limit++);
            }
        } else {
            goalNode = Problem.generalSearch(this, strategy, 0);
        }
        List result = new ArrayList();
        if (goalNode != null) {
            List<Node> chosenExpandedNodes = getChosenExpandedNodes(goalNode);
            String sequenceOfMoves = getSequenceOfMoves(chosenExpandedNodes);
            double solutionCost = 0;
            if (!(strategy == Strategy.BF || strategy == Strategy.DF || strategy == Strategy.ID)) {
                solutionCost = chosenExpandedNodes.stream().skip(1).mapToDouble(node -> node.getOperator().getCost()).sum();
            }
            int chosenExpandedNodesCount = chosenExpandedNodes.size() - 1;
            if (visualize) {
                for (State state : chosenExpandedNodes.stream().map(node -> node.getState()).collect(Collectors.toList())) {
                    visualizeGrid(state);
                }
            }
            result.add(sequenceOfMoves);
            result.add(solutionCost);
            result.add(chosenExpandedNodesCount);
        }
        return result;
    }

    public static void main(String[] args) throws Exception {
        SaveWesteros saveWesteros = new SaveWesteros();
        Grid grid = saveWesteros.genGrid(4);
        List result = saveWesteros.search(grid, Strategy.BF, true);
        if (!result.isEmpty()) {
            System.out.println();
            System.out.println("Sequence of Moves: " + result.get(0));
            System.out.println("Solution Cost: " + result.get(1));
            System.out.println("No. of Expanded Nodes: " + result.get(2));
        }
    }
}
