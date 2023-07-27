package org.example;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket serverSocket;

    public Server(ServerSocket serverSocket){
        this.serverSocket = serverSocket;
    }

    public void startServer(){
        try{
            // server is running until it closed
            while(!serverSocket.isClosed()){
                // .accept() - is a blocking method (program will be waiting until client is connected)
                Socket socket = serverSocket.accept();
                System.out.println("Client is connected");
                ClientHandler clientHandler = new ClientHandler(socket);

                Thread thread = new Thread(clientHandler);
                thread.start();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public void closeServerSocket(){
        try{
            if(serverSocket != null){
                serverSocket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            ServerSocket ss = new ServerSocket(1234);
            Server server = new Server(ss);
            server.startServer();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
