package Search;

public class Operator {
    private String name;
    private double cost;

    public Operator(String name) {
        this.name = name;
        this.cost = 0;
    }

    public Operator(String name, double cost) {
        this(name);
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public double getCost() {
        return cost;
    }
}
