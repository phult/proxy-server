package com.luong.proxyserver;

import java.io.*;
import java.net.*;

/**
 *
 * @author Phult Apr 14, 2015 10:38:12 PM
 */
public class App {

    //--------------------------------------------------------------------------
    //  Members
    //--------------------------------------------------------------------------
    //  Initialization
    //--------------------------------------------------------------------------
    //  Getter N Setter
    //--------------------------------------------------------------------------
    //  Method binding
    //--------------------------------------------------------------------------
    //  Implement N Override
    public static void main(String[] args) throws IOException {
        try {
            String host = "28.199.135.140";
            int remoteport = 80;
            int localport = 8088;
            // Print a start-up message
            System.out.println("Starting proxy for " + host + ":" + remoteport
                    + " on port " + localport);
            // And start running the server
            runServer(host, remoteport, localport); // never returns
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    /**
     * runs a single-threaded proxy server on the specified local port
     */
    public static void runServer(String host, int remoteport, int localport)
            throws IOException {
        // Create a ServerSocket to listen for connections
        ServerSocket ss = new ServerSocket(localport);

        final byte[] request = new byte[1024];
        byte[] reply = new byte[4096];

        while (true) {
            Socket client = null, server = null;
            try {
                // Wait for a connection on the local port
                client = ss.accept();

                final InputStream streamFromClient = client.getInputStream();
                final OutputStream streamToClient = client.getOutputStream();

                try {
                    server = new Socket(host, remoteport);
                } catch (IOException e) {
                    PrintWriter out = new PrintWriter(streamToClient);
                    out.print("Proxy server cannot connect to " + host + ":"
                            + remoteport + ":\n" + e + "\n");
                    out.flush();
                    client.close();
                    continue;
                }

                // Get server streams.
                final InputStream streamFromServer = server.getInputStream();
                final OutputStream streamToServer = server.getOutputStream();

                // a thread to read the client's requests and pass them to the server
                Thread t = new Thread() {
                    public void run() {
                        int bytesRead;
                        try {                            
                            while ((bytesRead = streamFromClient.read(request)) != -1) {
                                System.out.println("streamToServer.write:" + new String(request));
                                streamToServer.write(request, 0, bytesRead);
                                streamToServer.flush();
                            }
                        } catch (IOException e) {
                        }
                        try {
                            streamToServer.close();
                        } catch (IOException e) {
                        }
                    }
                };

                // Start the client-to-server request thread running
                t.start();

                // Read the server's responses and pass them back to the client.
                int bytesRead;
                try {
                    while ((bytesRead = streamFromServer.read(reply)) != -1) {
                        System.out.println("streamFromServer.read");
                        streamToClient.write(reply, 0, bytesRead);
                        streamToClient.flush();
                    }
                } catch (IOException e) {
                }
                // The server closed its connection to us, so we close our
                // connection to our client.
                streamToClient.close();
            } catch (IOException e) {
                System.err.println(e);
            } finally {
                try {
                    if (server != null) {
                        server.close();
                    }
                    if (client != null) {
                        client.close();
                    }
                } catch (IOException e) {
                }
            }
        }
    }
}
