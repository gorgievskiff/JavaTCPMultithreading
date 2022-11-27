package Lab2TCP;

import javax.xml.crypto.Data;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server {
    static int port = 5565;
    static List<ClientObject> clients = new ArrayList<>();
    static List<ClientHandler> listFromHandlers = new ArrayList<>();

    public static void main(String[] args) {

        try {
            ServerSocket ss = new ServerSocket(port);
            Socket s;
            ObjectInputStream ois;
            ObjectOutputStream oos;
            String initial = "1.First login using command login username\n2.If you want to get all active clients use 'get clients' command\n" +
                    "3.If you want to sent message to someone using 'sent#message#clientname', otherwise won't work";

            while(true){
                System.out.println("cekam na povrzuvanja");
                s = ss.accept();
                ois = new ObjectInputStream(s.getInputStream());
                oos = new ObjectOutputStream(s.getOutputStream());
                oos.flush();

                    System.out.println("Adding new handler for teacher " + s);
                    ClientHandler th = new ClientHandler(s,ois,oos);
                    // stavi gi vo lista site povrzani klienti
                    listFromHandlers.add(th);
                    Thread  t = new Thread(th);
                    t.start();

            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class ClientHandler extends Thread{
    Socket s;
    ObjectInputStream ois;
    ObjectOutputStream oos;
    String name;

    public ClientHandler(Socket s, ObjectInputStream ois, ObjectOutputStream oos){
        this.s = s;
        this.ois = ois;
        this.oos = oos;
    }

    @Override
    public void run(){
        System.out.println("Added client");

        while(true){
            try {
                var obj = (Message)ois.readObject();
                System.out.println(obj.content);
                if(obj.content.startsWith("login")){
                    StringTokenizer st = new StringTokenizer(obj.content);
                    String trash = st.nextToken();
                    String clientName = st.nextToken();
                    this.name = clientName.toLowerCase();

                    ClientObject client = new ClientObject(clientName,this.s.getPort());
                    Server.clients.add(client);

                    System.out.println("Stream for client " + clientName + " successfully added");

                    Message msg = new Message("Uspesno se najavivte");
                    oos.writeObject(msg);
                    oos.flush();
                }else if(obj.content.startsWith("get clients")){
                    // to be sure that list is serializable
                    var toSent = new ArrayList(Server.clients);
                    this.oos.writeObject(toSent);
                    //this.oos.writeUTF("test");
                    oos.flush();;
                }else if(obj.content.startsWith("sent")){
                    StringTokenizer st = new StringTokenizer(obj.content,"#");
                    //sent#message#clientName
                    String trash = st.nextToken();
                    String msg = st.nextToken();
                    String clientName = st.nextToken();
                    msg = this.name + ": " + msg;

                   for(ClientHandler ch : Server.listFromHandlers){
                       if(ch.name.equals(clientName)){
                           Message m = new Message(msg);
                           ch.oos.writeObject(m);
                           ch.oos.flush();
                       }
                   }
                }else if(obj.content.equals("help")){
                    String initial = "1.First login using command login username\n2.If you want to get all active clients use 'get clients' command\n" +
                            "3.If you want to sent message to someone using 'sent#message#clientname', otherwise won't work";
                    Message msg = new Message(initial);
                    this.oos.writeObject(msg);
                    this.oos.flush();

                } else{
                    Message msg = new Message("unknown command, please type help for more info");
                    this.oos.writeObject(msg);
                    this.oos.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
