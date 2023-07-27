package org.example;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler implements Runnable{
    public static List<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    // read messages from client
    private BufferedReader bufferedReader;
    // write message to client
    private BufferedWriter bufferedWriter;
    private String clientName;
    public ClientHandler(Socket socket){
        try {
            this.socket = socket;
            // OutputStreamWriter/InputStreamReader convert byte stream into char stream
            this.bufferedWriter = new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream())
            );
            this.bufferedReader = new BufferedReader(
                    new InputStreamReader(socket.getInputStream())
            );
            // after connecting to the server client should pass name
            // it waiting for Client.sendMessage() to pass client name
            this.clientName = bufferedReader.readLine();
            // adding client to list of clients
            clientHandlers.add(this);
            broadcastMessage("SERVER: "+clientName + " has entered the chat!");
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }
    @Override
    public void run() {
        String message;
        while(socket.isConnected()){
            try{
                message = bufferedReader.readLine();
                broadcastMessage(message);
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break;
            }
        }
    }

    public void broadcastMessage(String messageToSend){
        for(ClientHandler clientHandler: clientHandlers){
            if(!clientHandler.clientName.equals(clientName)){
                try {
                    clientHandler.bufferedWriter.write(messageToSend);
                    // point on end of line
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush();
                } catch (IOException e) {
                    closeEverything(socket, bufferedReader, bufferedWriter);
                }
            }
        }
    }

    public void removeClientHandler(){
        clientHandlers.remove(this);
        broadcastMessage("SERVER: "+ clientName + " has left the chat!");
    }

    public void closeEverything(Socket socket,BufferedReader bufferedReader,BufferedWriter bufferedWriter){
        removeClientHandler();
        try {
            if(bufferedReader != null){
                bufferedReader.close();
            }
            if(bufferedWriter != null){
                bufferedWriter.close();
            }
            if(socket != null){
                socket.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
