package SaveWesteros;

import Search.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class SaveWesteros extends Problem {
    private Grid grid;
    private List<Cell> whiteWalkers;
    private List<Cell> obstacles;
    private Cell dragonStone;
    private int agentCapacity;
    public static final double PICKUP_COST = 5;
    public static final double MOVE_COST = 4;
    public static final double KILL_COST = 4;

    public SaveWesteros() {
        initOperators();
        whiteWalkers = new ArrayList<>();
        obstacles = new ArrayList<>();
    }

    private void initOperators() {
        super.setOperators(Arrays.asList(new Operator("UP", MOVE_COST), new Operator("DOWN", MOVE_COST), new Operator("LEFT", MOVE_COST), new Operator("RIGHT", MOVE_COST), new Operator("PICKUP", PICKUP_COST), new Operator("KILL")));
    }

    private List<Cell> cloneCells(List<Cell> cells) {
        return new ArrayList<>(cells);
    }

    private void prepareSearch() {
        whiteWalkers = grid.getWhiteWalkers();
        obstacles = grid.getObstacles();
        dragonStone = grid.getDragonStone();
        agentCapacity = grid.getAgentCapacity();
        super.setInitialState(new SaveWesterosState(cloneCells(whiteWalkers), grid.getAgentCell(), 0));
        super.setDepthLimit(Integer.MAX_VALUE);
    }

    private String getSequenceOfMoves(List<Node> expandedNodes) {
        return String.join(" -> ", expandedNodes.stream().skip(1).map(node -> node.getOperator().getName()).collect(Collectors.toList()));
    }

    private void visualizeGrid(List<Node> expandedNodes) {
        Cell[][] cells = grid.getCells();
        List<Cell> stateWhiteWalkers;
        Cell stateAgentCell;
        Cell current;
        for (SaveWesterosState state : expandedNodes.stream().map(node -> (SaveWesterosState) node.getState()).collect(Collectors.toList())) {
            stateWhiteWalkers = state.getWhiteWalkers();
            stateAgentCell = state.getAgentCell();
            System.out.println();
            System.out.println();
            for (int i = 0; i < grid.getM(); i++) {
                for (int j = 0; j < grid.getN(); j++) {
                    current = cells[i][j];
                    System.out.print("[" + (obstacles.contains(current) ? "O" : stateWhiteWalkers.contains(current) ? "W" : current == dragonStone ? "D" : current == stateAgentCell ? "A" : "E") + "]");
                }
                System.out.println();
            }
            System.out.println();
            System.out.println();
        }
    }

    private boolean canVisitCell(Cell cell) {
        return !(whiteWalkers.contains(cell) || obstacles.contains(cell));
    }

    private SaveWesterosState killWhiteWalkers(List<Cell> whiteWalkers, Cell currentAgentCell, int dragonGlass) {
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
    public boolean isGoal(State state) {
        return ((SaveWesterosState) state).getWhiteWalkers().isEmpty();
    }

    @Override
    public List<Node> expand(Node node, List<Operator> operators) {
        List<Node> expansion = new ArrayList<>();

        // Return empty list in case of Iterative Deepening Search with exceeded limit for the generalSearch to stop
        if (node.getDepth() > super.getDepthLimit()) {
            return expansion;
        }

        SaveWesterosState state = (SaveWesterosState) node.getState();
        Cell stateAgentCell = state.getAgentCell();
        int row = stateAgentCell.getRow();
        int col = stateAgentCell.getCol();
        Cell[][] cells = grid.getCells();
        List<Cell> stateWhiteWalkers;
        Cell nextAgentCell;
        Node successorNode;

        for (Operator operator : operators) {
            switch (operator.getName()) {
                case "UP":
                    if (row > 0) {
                        nextAgentCell = cells[row - 1][col];
                        if (canVisitCell(nextAgentCell)) {
                            successorNode = new Node(new SaveWesterosState(cloneCells(state.getWhiteWalkers()), nextAgentCell, state.getDragonGlass()), node, node.getDepth() + 1, operator);
                            successorNode.setPathCost(calculatePathCost(successorNode));
                            super.setNodeEvalCriteria(successorNode);
                            expansion.add(successorNode);
                        }
                    }
                    break;
                case "DOWN":
                    if (row < grid.getM() - 1) {
                        nextAgentCell = cells[row + 1][col];
                        if (canVisitCell(nextAgentCell)) {
                            successorNode = new Node(new SaveWesterosState(cloneCells(state.getWhiteWalkers()), nextAgentCell, state.getDragonGlass()), node, node.getDepth() + 1, operator);
                            successorNode.setPathCost(calculatePathCost(successorNode));
                            setNodeEvalCriteria(successorNode);
                            expansion.add(successorNode);
                        }
                    }
                    break;
                case "LEFT":
                    if (col > 0) {
                        nextAgentCell = cells[row][col - 1];
                        if (canVisitCell(nextAgentCell)) {
                            successorNode = new Node(new SaveWesterosState(cloneCells(state.getWhiteWalkers()), nextAgentCell, state.getDragonGlass()), node, node.getDepth() + 1, operator);
                            successorNode.setPathCost(calculatePathCost(successorNode));
                            setNodeEvalCriteria(successorNode);
                            expansion.add(successorNode);
                        }
                    }
                    break;
                case "RIGHT":
                    if (col < grid.getN() - 1) {
                        nextAgentCell = cells[row][col + 1];
                        if (canVisitCell(nextAgentCell)) {
                            successorNode = new Node(new SaveWesterosState(cloneCells(state.getWhiteWalkers()), nextAgentCell, state.getDragonGlass()), node, node.getDepth() + 1, operator);
                            successorNode.setPathCost(calculatePathCost(successorNode));
                            setNodeEvalCriteria(successorNode);
                            expansion.add(successorNode);
                        }
                    }
                    break;
                case "PICKUP":
                    if (stateAgentCell == dragonStone) {
                        successorNode = new Node(new SaveWesterosState(cloneCells(state.getWhiteWalkers()), stateAgentCell, agentCapacity), node, node.getDepth() + 1, operator);
                        successorNode.setPathCost(calculatePathCost(successorNode));
                        setNodeEvalCriteria(successorNode);
                        expansion.add(successorNode);
                    }
                    break;
                case "KILL":
                    stateWhiteWalkers = state.getWhiteWalkers();
                    if (!stateWhiteWalkers.isEmpty() && state.getDragonGlass() > 0) {
                        SaveWesterosState newState = killWhiteWalkers(stateWhiteWalkers, stateAgentCell, state.getDragonGlass());
                        successorNode = new Node(newState, node, node.getDepth() + 1, new Operator("KILL", KILL_COST - (stateWhiteWalkers.size() - newState.getWhiteWalkers().size())));
                        successorNode.setPathCost(calculatePathCost(successorNode));
                        setNodeEvalCriteria(successorNode);
                        expansion.add(successorNode);
                    }
                    break;
            }
        }
        return expansion;
    }

    @Override
    public double calculatePathCost(Node node) {
        return node.isRootNode() ? 0 : node.getParent().getPathCost() + node.getOperator().getCost();
    }

    @Override
    public double evaluateHeuristicOne(Node node) {
        SaveWesterosState state = (SaveWesterosState) node.getState();
        if(isGoal(state)) {
            return 0;
        }
        Cell stateAgentCell = state.getAgentCell();
        int stateWhiteWalkersCount = state.getWhiteWalkers().size();
        int stateDragonGlass = state.getDragonGlass();
        int killCost = (stateWhiteWalkersCount / 4) * stateDragonGlass;
        return stateDragonGlass > 0 ? killCost : ((Math.abs(dragonStone.getRow() - stateAgentCell.getRow()) + Math.abs(dragonStone.getCol() - stateAgentCell.getCol())) * MOVE_COST) + PICKUP_COST + killCost;
    }

    @Override
    public double evaluateHeuristicTwo(Node node) {
        SaveWesterosState state = (SaveWesterosState) node.getState();
        if(isGoal(state)) {
            return 0;
        }
        Cell stateAgentCell = state.getAgentCell();
        int stateDragonGlass = state.getDragonGlass();
        List<Cell> stateWhiteWalkers = state.getWhiteWalkers();
        OptionalInt averageStepsOptional = stateWhiteWalkers.stream().mapToInt(cell -> (Math.abs(cell.getRow() - stateAgentCell.getRow()) + Math.abs(cell.getCol() - stateAgentCell.getCol()))).min();
        double killCost = (averageStepsOptional.isPresent() ? averageStepsOptional.getAsInt() : 0) * MOVE_COST * (stateWhiteWalkers.size() / 4) * stateDragonGlass;
        return stateDragonGlass > 0 ? killCost : ((Math.abs(dragonStone.getRow() - stateAgentCell.getRow()) + Math.abs(dragonStone.getCol() - stateAgentCell.getCol())) * MOVE_COST) + PICKUP_COST + killCost;
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

    public List search(Grid grid, Strategy strategy, boolean visualize) {
        this.grid = grid;
        super.setStrategy(strategy);
        prepareSearch();
        List result = new ArrayList();
        Node goalNode;
        if (strategy == Strategy.ID) {
            goalNode = iterativeDeepeningSearch(this);
        } else {
            goalNode = generalSearch(this, strategy);
        }
        if (goalNode != null) {
            List<Node> chosenExpandedNodes = getChosenExpandedNodes(goalNode);
            String sequenceOfMoves = getSequenceOfMoves(chosenExpandedNodes);
            double solutionCost = 0;
            if (!(strategy == Strategy.BF || strategy == Strategy.DF || strategy == Strategy.ID)) {
                solutionCost = goalNode.getPathCost();
            }
            int chosenExpandedNodesCount = chosenExpandedNodes.size() - 1;
            if (visualize) {
                visualizeGrid(chosenExpandedNodes);
            }
            result.add(sequenceOfMoves);
            result.add(solutionCost);
            result.add(chosenExpandedNodesCount);
        }
        return result;
    }

    public static void main(String[] args) throws Exception {
        SaveWesteros saveWesteros = new SaveWesteros();
        List result;

        System.out.println("\n\n-------------------Grid 1-----------------\n\n");


        Grid grid1 = saveWesteros.genGrid(4);
        grid1.printGridInfo();

        System.out.println("\n\n-------------------Breadth First-----------------\n\n");

        result = saveWesteros.search(grid1, Strategy.BF, false);
        if (!result.isEmpty()) {
            System.out.println("Sequence of Moves: " + result.get(0));
            System.out.println("No. of Expanded Nodes: " + result.get(2));
        } else {
            System.out.println("No Solution");
        }

        System.out.println("\n\n-------------------Breadth First-----------------\n\n");


        System.out.println("\n\n-------------------Depth First-----------------\n\n");

        result = saveWesteros.search(grid1, Strategy.DF, false);
        if (!result.isEmpty()) {
            System.out.println("Sequence of Moves: " + result.get(0));
            System.out.println("No. of Expanded Nodes: " + result.get(2));
        } else {
            System.out.println("No Solution");
        }

        System.out.println("\n\n-------------------Depth First-----------------\n\n");


        System.out.println("\n\n-------------------Uniform Cost-----------------\n\n");

        result = saveWesteros.search(grid1, Strategy.UC, false);
        if (!result.isEmpty()) {
            System.out.println("Sequence of Moves: " + result.get(0));
            System.out.println("Solution Cost: " + result.get(1));
            System.out.println("No. of Expanded Nodes: " + result.get(2));
        } else {
            System.out.println("No Solution");
        }

        System.out.println("\n\n-------------------Uniform Cost-----------------\n\n");


        System.out.println("\n\n-------------------Iterative Deepening-----------------\n\n");

        result = saveWesteros.search(grid1, Strategy.ID, false);
        if (!result.isEmpty()) {
            System.out.println("Depth Limit: " + saveWesteros.getDepthLimit());
            System.out.println("Sequence of Moves: " + result.get(0));
            System.out.println("No. of Expanded Nodes: " + result.get(2));
        } else {
            System.out.println("No Solution");
        }
        System.out.println("\n\n-------------------Iterative Deepening-----------------\n\n");


        System.out.println("\n\n-------------------Greedy 1-----------------\n\n");

        result = saveWesteros.search(grid1, Strategy.GR1, false);
        if (!result.isEmpty()) {
            System.out.println("Sequence of Moves: " + result.get(0));
            System.out.println("Solution Cost: " + result.get(1));
            System.out.println("No. of Expanded Nodes: " + result.get(2));
        } else {
            System.out.println("No Solution");
        }

        System.out.println("\n\n-------------------Greedy 1-----------------\n\n");


        System.out.println("\n\n-------------------Greedy 2-----------------\n\n");

        result = saveWesteros.search(grid1, Strategy.GR2, false);
        if (!result.isEmpty()) {
            System.out.println("Sequence of Moves: " + result.get(0));
            System.out.println("Solution Cost: " + result.get(1));
            System.out.println("No. of Expanded Nodes: " + result.get(2));
        } else {
            System.out.println("No Solution");
        }

        System.out.println("\n\n-------------------Greedy 2-----------------\n\n");


        System.out.println("\n\n-------------------A* 1-----------------\n\n");

        result = saveWesteros.search(grid1, Strategy.AS1, false);
        if (!result.isEmpty()) {
            System.out.println("Sequence of Moves: " + result.get(0));
            System.out.println("Solution Cost: " + result.get(1));
            System.out.println("No. of Expanded Nodes: " + result.get(2));
        } else {
            System.out.println("No Solution");
        }

        System.out.println("\n\n-------------------A* 1-----------------\n\n");


        System.out.println("\n\n-------------------A* 2-----------------\n\n");

        result = saveWesteros.search(grid1, Strategy.AS2, false);
        if (!result.isEmpty()) {
            System.out.println("Sequence of Moves: " + result.get(0));
            System.out.println("Solution Cost: " + result.get(1));
            System.out.println("No. of Expanded Nodes: " + result.get(2));
        } else {
            System.out.println("No Solution");
        }

        System.out.println("\n\n-------------------A* 2-----------------\n\n");


        System.out.println("\n\n-------------------Grid 1-----------------\n\n");


        System.out.println("\n\n-------------------Grid 2-----------------\n\n");


        Grid grid2 = saveWesteros.genGrid();
        grid2.printGridInfo();


        System.out.println("\n\n-------------------Breadth First-----------------\n\n");

        result = saveWesteros.search(grid2, Strategy.BF, false);
        if (!result.isEmpty()) {
            System.out.println("Sequence of Moves: " + result.get(0));
            System.out.println("No. of Expanded Nodes: " + result.get(2));
        } else {
            System.out.println("No Solution");
        }

        System.out.println("\n\n-------------------Breadth First-----------------\n\n");


        System.out.println("\n\n-------------------Depth First-----------------\n\n");

        result = saveWesteros.search(grid2, Strategy.DF, false);
        if (!result.isEmpty()) {
            System.out.println("Sequence of Moves: " + result.get(0));
            System.out.println("No. of Expanded Nodes: " + result.get(2));
        } else {
            System.out.println("No Solution");
        }

        System.out.println("\n\n-------------------Depth First-----------------\n\n");


        System.out.println("\n\n-------------------Uniform Cost-----------------\n\n");

        result = saveWesteros.search(grid2, Strategy.UC, false);
        if (!result.isEmpty()) {
            System.out.println("Sequence of Moves: " + result.get(0));
            System.out.println("Solution Cost: " + result.get(1));
            System.out.println("No. of Expanded Nodes: " + result.get(2));
        } else {
            System.out.println("No Solution");
        }

        System.out.println("\n\n-------------------Uniform Cost-----------------\n\n");


        System.out.println("\n\n-------------------Iterative Deepening-----------------\n\n");

        result = saveWesteros.search(grid2, Strategy.ID, false);
        if (!result.isEmpty()) {
            System.out.println("Depth Limit: " + saveWesteros.getDepthLimit());
            System.out.println("Sequence of Moves: " + result.get(0));
            System.out.println("No. of Expanded Nodes: " + result.get(2));
        } else {
            System.out.println("No Solution");
        }
        System.out.println("\n\n-------------------Iterative Deepening-----------------\n\n");


        System.out.println("\n\n-------------------Greedy 1-----------------\n\n");

        result = saveWesteros.search(grid2, Strategy.GR1, false);
        if (!result.isEmpty()) {
            System.out.println("Sequence of Moves: " + result.get(0));
            System.out.println("Solution Cost: " + result.get(1));
            System.out.println("No. of Expanded Nodes: " + result.get(2));
        } else {
            System.out.println("No Solution");
        }

        System.out.println("\n\n-------------------Greedy 1-----------------\n\n");


        System.out.println("\n\n-------------------Greedy 2-----------------\n\n");

        result = saveWesteros.search(grid2, Strategy.GR2, false);
        if (!result.isEmpty()) {
            System.out.println("Sequence of Moves: " + result.get(0));
            System.out.println("Solution Cost: " + result.get(1));
            System.out.println("No. of Expanded Nodes: " + result.get(2));
        } else {
            System.out.println("No Solution");
        }

        System.out.println("\n\n-------------------Greedy 2-----------------\n\n");


        System.out.println("\n\n-------------------A* 1-----------------\n\n");

        result = saveWesteros.search(grid2, Strategy.AS1, false);
        if (!result.isEmpty()) {
            System.out.println("Sequence of Moves: " + result.get(0));
            System.out.println("Solution Cost: " + result.get(1));
            System.out.println("No. of Expanded Nodes: " + result.get(2));
        } else {
            System.out.println("No Solution");
        }

        System.out.println("\n\n-------------------A* 1-----------------\n\n");


        System.out.println("\n\n-------------------A* 2-----------------\n\n");

        result = saveWesteros.search(grid2, Strategy.AS2, false);
        if (!result.isEmpty()) {
            System.out.println("Sequence of Moves: " + result.get(0));
            System.out.println("Solution Cost: " + result.get(1));
            System.out.println("No. of Expanded Nodes: " + result.get(2));
        } else {
            System.out.println("No Solution");
        }

        System.out.println("\n\n-------------------A* 2-----------------\n\n");


        System.out.println("\n\n-------------------Grid 2-----------------\n\n");
    }
}
