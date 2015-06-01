package hipster;

/**
 *
 * @author Miguel López
 */
public class MyNode {
    private int id;
    private String info;
    private double x;
    private double y;

    public MyNode(){
    }

    public int getId(){
        return id;
    }

    public String getInfo(){
        return info;
    }

    public double getX(){
        return x;
    }

    public double getY(){
        return y;
    }

    public void setId(int id){
        this.id = id;
    }

    public void setInfo(String info){
        this.info = info;
    }

    public void setX(double x){
        this.x = x;
    }

    public void setY(double y){
        this.y = y;
    }
}
