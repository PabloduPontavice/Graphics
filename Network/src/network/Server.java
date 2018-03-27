package network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class Server {

        /*public static void main(String[] args) {
                // TODO Auto-generated method stub

        }*/

        private int counter = 0;
        private int port;
        private ServerSocket ss;
        private Socket[] connectedClientSockets = new Socket [3];

        public Server(int port) throws IOException{

                this.port = port;

                ss = new ServerSocket( port );
        }


        public int connectToClient() throws IOException{
                //connectedClients[counter] = new Client()
                if(counter >= 2){
                        System.out.println("Already 3 Clients");
                        return -1;
                }

                Socket clientSoc = ss.accept();
                this.connectedClientSockets[counter] = clientSoc;

                counter++;

                return (counter - 1);
        }


        public void sendToClient(String methodName, String[] args, ServerSocket ss, int socketIndex) throws IOException{

                String msg = methodName;

                Socket clientSoc = connectedClientSockets[socketIndex];


                for(int i = 0; i < args.length; i++){
                        msg += "/";
                        msg += args[i];
                }

                //String nickName = str.substring(0, 12) + msg.substring(0, index) + ' ' + str.substring(12) + msg.substring(index + 1);

                System.out.println(msg);

                OutputStreamWriter os = new OutputStreamWriter(clientSoc.getOutputStream());
                PrintWriter out =  new PrintWriter(os);
                out.println(msg);
                out.flush();

                System.out.println("S : Data sent from Server to Client: " + msg);

                /*clientSoc.close();
                ss.close();     */

        }


        public String waitForClient(ServerSocket ss, int socketIndex) throws IOException{

                Socket clientSoc = connectedClientSockets[socketIndex];

                BufferedReader br = new BufferedReader( new InputStreamReader( clientSoc.getInputStream() ) );
                String str = br.readLine();

                System.out.println("S : Data received from Client to Server: " + str);

                return str;

        }


        public void dispose() throws IOException{
                ss.close();
        }

        public int getCounter() {
                return counter;
        }


        public void setCounter(int counter) {
                this.counter = counter;
        }

        public ServerSocket getServerSocket() {
                return this.ss;
        }


        public void setClientSoc(ServerSocket ss, int port) throws IOException{
                this.ss = new ServerSocket( port );
        }

        public int getPort() {
                return port;
        }

        public void setPort(int port) {
                this.port = port;
        }

}
