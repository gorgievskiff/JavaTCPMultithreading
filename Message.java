package Lab2TCP;

import java.io.Serializable;

public class Message implements Serializable {
    String content;

    public Message(String content){
        this.content = content;
    }
}
