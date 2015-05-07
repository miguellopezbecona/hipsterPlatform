package hipster;

/**
 *
 * @author Miguel
 */
public class Link {
    private int source;
    private int target;
    private double weight;

    public Link(int source, int target, double weight) {
        this.source = source;
        this.target = target;
        this.weight = weight;
    }

    public Link(Link l, boolean inverted) {
        if(inverted){
            source = l.target;
            target = l.source;
        } else {
            source = l.source;
            target = l.target;
        }
        weight = l.weight;
    }

    public Link() {
    }


    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }
    
}
