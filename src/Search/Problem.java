package Search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public abstract class Problem {
    private List<Operator> operators;
    private State initialState;
    private int depthLimit;
    private Strategy strategy;

    public List<Operator> getOperators() {
        return operators;
    }

    protected void setOperators(List<Operator> operators) {
        this.operators = operators;
    }

    public State getInitialState() {
        return initialState;
    }

    protected void setInitialState(State initialState) {
        this.initialState = initialState;
    }

    public int getDepthLimit() {
        return depthLimit;
    }

    public void setDepthLimit(int depthLimit) {
        this.depthLimit = depthLimit;
    }

    public Strategy getStrategy() {
        return strategy;
    }

    public void setStrategy(Strategy strategy) {
        this.strategy = strategy;
    }

    public abstract boolean isGoal(State state);

    public abstract List<Node> expand(Node node, List<Operator> operator);

    public abstract double calculatePathCost(Node node);

    public abstract double evaluateHeuristicOne(Node node);

    public abstract double evaluateHeuristicTwo(Node node);

    protected void setNodeEvalCriteria(Node node) {
        if (strategy == Strategy.UC) {
            node.setEval(node.getPathCost());
        } else if (strategy == Strategy.GR1) {
            node.setEval(evaluateHeuristicOne(node));
        } else if (strategy == Strategy.GR2) {
            node.setEval(evaluateHeuristicTwo(node));
        } else if (strategy == Strategy.AS1) {
            node.setEval(node.getPathCost() + evaluateHeuristicOne(node));
        } else if (strategy == Strategy.AS2) {
            node.setEval(node.getPathCost() + evaluateHeuristicTwo(node));
        }
    }

    protected static List<Node> getChosenExpandedNodes(Node node) {
        LinkedList<Node> nodes = new LinkedList<>();
        while (node != null) {
            nodes.addFirst(node);
            node = node.getParent();
        }
        return nodes;
    }

    private static boolean isRepeatedState(State testState, List<State> repeatedStates) {
        return repeatedStates.stream().anyMatch(state -> state.isSame(testState));
    }

    protected static Node generalSearch(Problem problem, Strategy strategy) {
        ArrayList<State> repeatedStates = new ArrayList<>();
        LinkedList<Node> nodes = new LinkedList<>();
        nodes.add(new Node(problem.initialState, null, 0, null));
        Node node;
        while (!nodes.isEmpty()) {
            node = nodes.removeFirst();
            if (problem.isGoal(node.getState())) {
                return node;
            }
            for (Node successorNode : problem.expand(node, problem.operators)) {
                if (!isRepeatedState(successorNode.getState(), repeatedStates)) {
                    switch (strategy) {
                        case BF:
                            nodes.addLast(successorNode);
                            break;
                        case DF:
                            nodes.addFirst(successorNode);
                            break;
                        case ID:
                            nodes.addFirst(successorNode);
                            break;
                        case UC:
                        case GR1:
                        case GR2:
                        case AS1:
                        case AS2:
                            nodes.add(successorNode);
                            Collections.sort(nodes);
                            break;
                    }
                    repeatedStates.add(successorNode.getState());
                }
            }
        }
        return null;
    }

    protected static Node iterativeDeepeningSearch(Problem problem) {
        problem.depthLimit = 0;
        Node goalNode = null;
        while (problem.depthLimit < Integer.MAX_VALUE) {
            goalNode = generalSearch(problem, Strategy.ID);
            if (goalNode != null) {
                break;
            }
            problem.depthLimit++;
        }
        return goalNode;
    }
}
