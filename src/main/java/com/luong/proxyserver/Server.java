package com.luong.proxyserver;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.*;

/**
 *
 * @author Phult Apr 14, 2015 10:38:23 PM
 */
public class Server {

    //--------------------------------------------------------------------------
    //  Members
    ServerSocket server;

    //--------------------------------------------------------------------------
    //  Initialization
    //--------------------------------------------------------------------------
    //  Getter N Setter
    //--------------------------------------------------------------------------
    //  Method binding
    //--------------------------------------------------------------------------
    //  Implement N Override
    public void start(int port) throws IOException {
        server = new ServerSocket(port);
        System.out.println("server listening...");
        while (true) {
            Socket socket = server.accept();
            System.out.println("client connected!");
            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Socket forwardSocket = new Socket();
            forwardSocket.connect(new InetSocketAddress("112.213.91.194", 80));
            BufferedWriter forwardBufferWriter = new BufferedWriter(new OutputStreamWriter(forwardSocket.getOutputStream()));
            BufferedReader forwardBufferReader = new BufferedReader(new InputStreamReader(forwardSocket.getInputStream()));
            String data = "";
            while (true) {
                String itemData = bufferReader.readLine();
                data += itemData;

                if ("".equals(itemData)) {
                    System.out.println(data);
                    forwardBufferWriter.write(data);
                    forwardBufferWriter.flush();

                    System.out.println(forwardSocket.isConnected());
                    System.out.println("-------");
                    System.out.println("host reply: " + forwardBufferReader.readLine());
                } else {
                    data += "\n";
                }
//                forwardBufferWriter.write(bufferReader.readLine());                
            }
//            System.out.println("client disconnected!");
        }
    }
    //--------------------------------------------------------------------------
    //  Utils
    //--------------------------------------------------------------------------
    //  Inner class
}
