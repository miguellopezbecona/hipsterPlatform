package hipster;

/**
 *
 * @author Miguel López
 */
public class Message {
    private String type;
    private String content;

    public Message(){
    }

    public String getType(){
        return type;
    }

    public String getContent(){
        return content;
    }

    public void setType(String t){
        type = t;
    }

    public void setContent(String c){
        content = c;
    }
}
