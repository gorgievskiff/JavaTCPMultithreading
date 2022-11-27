package Lab2TCP;

import java.io.Serializable;

public class ClientObject implements Serializable {
    String name;
    int port;

    public ClientObject(String name, int port){
        this.name = name;
        this.port = port;
    }
}
