package Search;

public class Node implements Comparable<Node> {
    private State state;
    private Node parent;
    private Operator operator;
    private int depth;
    private double eval;
    private double pathCost;

    public Node(State state, Node parent, int depth, Operator operator) {
        this.state = state;
        this.parent = parent;
        this.depth = depth;
        this.operator = operator;
    }

    public State getState() {
        return state;
    }

    public Node getParent() {
        return parent;
    }

    public Operator getOperator() {
        return operator;
    }

    public int getDepth() {
        return depth;
    }

    public double getEval() {
        return eval;
    }

    public void setEval(double eval) {
        this.eval = eval;
    }

    public double getPathCost() {
        return pathCost;
    }

    public void setPathCost(double pathCost) {
        this.pathCost = pathCost;
    }

    public boolean isRootNode() {
        return parent == null;
    }

    @Override
    public int compareTo(Node node) {
        return Double.compare(node.eval, this.eval);
    }
}
