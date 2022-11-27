package Lab2TCP;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;

public class Client{
    static class MessageSender implements Runnable{
        public final static int port = 5565;
        private Socket socket;
        ObjectOutputStream oos;

        MessageSender(Socket socket,ObjectOutputStream oos) {
            this.socket = socket;
            this.oos = oos;
        }

        @Override
        public void run() {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            while(true){
                try {
                    var input = in.readLine();
                    Message msg = new Message(input);
                    this.oos.writeObject(msg);
                    this.oos.flush();
//                    if(input.startsWith("login")){
//                        //System.out.println("loginnnn");
//
//                    }else if(input.startsWith("get clients")){
//                        Message msg = new Message(input);
//                        this.oos.writeObject(msg);
//                        this.oos.flush();
//                    }else if(input.startsWith("sent")){
//                        Message msg = new Message(input);
//                        this.oos.writeObject(msg);
//                        this.oos.flush();
//                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

    }

    static class MessageReceiver implements Runnable{
        private Socket socket;
        private ObjectInputStream ois;

        public MessageReceiver(Socket socket, ObjectInputStream ois){
            this.socket = socket;
            this.ois = ois;
        }
        @Override
        public void run() {
            while(true){
                try {
                    var object = ois.readObject();
                    if(object instanceof List<?>){
                        //System.out.println("da e");
                        var rec = (List<ClientObject>) object;
                        for(var i=0; i<rec.size();i++){
                            System.out.println(rec.get(i).name);
                        }
                    }else if(object instanceof Message){
                        //System.out.println("da be toa e");
                        var rec = (Message) object;
                        System.out.println(rec.content);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        InetAddress host = null;
        try {
            host = InetAddress.getLocalHost();
            Socket s = new Socket(host,MessageSender.port);

            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
            MessageSender messageSender = new MessageSender(s,oos);
            oos.flush();

            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
            MessageReceiver messageReceiver = new MessageReceiver(s,ois);

            Thread ms = new Thread(messageSender);
            Thread mr = new Thread(messageReceiver);

            ms.start(); mr.start();
            ms.join(); mr.join();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}