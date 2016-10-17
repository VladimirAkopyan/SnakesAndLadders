package snakesladders;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

class Network implements Runnable 
{
	
	private Socket socket;
        private static ServerSocket serverSocket;
	private DataOutputStream out;
        private static DataOutputStream[] outputs;
	private DataInputStream in;
	private volatile static String send_message;
	private String playername;
        private static String this_user_name;
	private static SnakesLaddersGUI UI;
        private static volatile int N_connections=0;
        private InetAddress IP;
        /*true if a message must be sent*/
        private volatile static boolean message_pending=false;
        /*true for send, false for recieve*/
        private volatile boolean Send_mode;
        private volatile static boolean Game_running=true;
        private static volatile boolean connection_established=false;
        private static volatile boolean server_mode=false;
        private static volatile boolean Game_starting=true;
        private static Network[] Listener_array = new Network[4];
        private static Network sender;
        private static String[] player_list=new String[5];;
        private int player_id;
        private static char chat=(int)1;
        
        /* Constructor to be used from inside to create a new listener*/
	private Network(DataInputStream in, String player_name, int player_id){
		this.in = in;
		playername = player_name;
                Send_mode= false;  
                this.player_id=player_id;
	}
        
        /* Constructor to be used from inside to create a new listener*/
	private Network(){
                Send_mode= true;           
	}
        
/* constructor, initialises Network class in client mode*/
        public Network(SnakesLaddersGUI User_interface,String player_name, InetAddress Server_IP) {	
           /*assign variables*/     
            if(UI==null)
                UI=User_interface;
            playername = player_name;
            this_user_name= player_name;
            IP = Server_IP;
            server_mode=false;
            outputs= new DataOutputStream[1];
                
/*connect and start game*/                
           SnakesLaddersGUI.ThreadPool.execute(this); 
           sender = new Network();
	}
        
        /* constructor, initialises Network class in server mode*/
        public Network(SnakesLaddersGUI User_interface,String player_name) {	
           /*assign variables*/     
            if(UI==null)
                UI=User_interface;
            server_mode=true;
            playername = player_name;
            this_user_name= player_name;
            outputs= new DataOutputStream[4];
                
/*connect and start game*/                
           SnakesLaddersGUI.ThreadPool.execute(this);  
           sender = new Network();
	}
        

   @Override
   public void run(){
       if(connection_established==false){
           if(server_mode==true){
                start_server();
           }
           else
               try {
               connect_to_server();
           } catch (IOException ex) {
                UI.print("Connection unuccessful"); //notifies user and resets ui//
                UI.gameReset();
           }      
       }
       else{
            if(Send_mode==false)
                read_network();
            else{
                sendto_network();
            }
      } 
	
    }
    
   private void start_server(){
        try {
            	UI.print("Starting server...");
                serverSocket = new ServerSocket(7777);		/*creates new server-socket*/
                UI.print("Server started...");	/*NOtifies user that a server is now running*/
                UI.enable_game(true);   
                connection_established=true;          
            } catch (IOException ex) {
                UI.print("Failed to open socket");
                UI.gameReset(); /*Reserts the whole server if a client failed to connect*/
            }
		
            while (Game_starting==true && N_connections<5 )	
            {
                try {
                    socket=serverSocket.accept();				/*waits for clients to connect*/
                } catch (IOException ex) {
                    UI.print("A client attempted to connect and failed");
                }
                 
                /*creates connections for up to 4 cleints*/
                				/*loops for 10 clients*/
                try{
                   out = new DataOutputStream(socket.getOutputStream());		/*creates object for output stream*/
                   in = new DataInputStream(socket.getInputStream());			/*creates object for input stream*/
                   
                 } 
                catch (IOException ex) {
                    UI.print("Input-output error");
                    UI.gameReset(); }  
                
                  try{
                      playername = in.readUTF();
                  }catch(IOException e1){ 
                      UI.print("Someone connected from [" + socket.getInetAddress() + "] and submitted invalid packet");}
                  outputs[N_connections]= out;
                  Listener_array[N_connections] = new Network(in,playername,N_connections+1);		/*and creates Network from constructor*/
                  SnakesLaddersGUI.ThreadPool.execute(Listener_array[N_connections]);
                  UI.print("Connection from: "  + Listener_array[N_connections].playername);
                  UI.print(" [" + socket.getInetAddress() + "]"); 
                   N_connections++;
            try {
                out.writeUTF(Integer.toString(N_connections));
            } catch (IOException ex) {
                UI.print("Couldn't send client it's ID");
            }       
               }
                   
        }    
      
   private void connect_to_server() throws IOException{
            socket = new Socket(IP, 7777);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            out.writeUTF(playername);
            outputs[0]=out;
            UI.print("Connection successful");
            UI.enable_game(true);    
            connection_established=true;
            N_connections=1;
            player_id=Integer.parseInt(in.readUTF());
            read_network();
   }
   
   private synchronized void sendto_network(){
       String message =new String(send_message);
       if(message_pending==true)
       {   
           for(int i=0;i<N_connections;i++)
                {try {
                   outputs[i].writeUTF(message);
                   message_pending=false;
                } catch (IOException ex) {
                    UI.print("failed to send message to player" + i);
                }
           }
       }
   }  
   
   private void read_network(){
       	while(Game_running || Game_starting)
	{
            try{	
                String recieve_message = in.readUTF();
                parse_input(recieve_message);
            }catch(IOException e1){
		this.out = null;
		this.in = null;
                UI.print("Player " + playername +" disconnected");
                break;}//terminates connextions and the thread in case a client dropped and notifies the user//
	}
   }
   
   private synchronized void send_message_internal(String message){
            send_message= message;
            message_pending = true;
           SnakesLaddersGUI.ThreadPool.execute(sender);
   }
   
   private synchronized void parse_input (String input){
       int command, argument;
       command= (int)input.charAt(0);
       argument=(int)input.charAt(1);
       switch(command){
           case 1:  UI.print(playername +": "+ input.substring(1));
                if(server_mode)
                    send_message_internal(input);
               break;
          case 2: 
          int peg_number= (int)input.charAt(2);
                  UI.print(player_list[peg_number]+ " " + BoardJpanel.move_player(peg_number, argument));
                  int nextRolling;
                  if (server_mode!=true)
                    {nextRolling=peg_number+1;
                    if(nextRolling==player_id)
                        UI.I_am_rolling();
                    }
                  else
                    {send_message_internal(input);
                    nextRolling=peg_number%N_connections;
                    if(nextRolling==0)
                               UI.I_am_rolling();}
                  break;
          case 3: 
              int NofPlayers=0,prevPlayer, nextPlayer;   
              nextPlayer=input.indexOf("%", 1);
              prevPlayer=1;
              while(nextPlayer!=-1){
                  player_list[NofPlayers]=input.substring(prevPlayer, nextPlayer);
                  prevPlayer=nextPlayer+1;
                  nextPlayer=input.indexOf("%", prevPlayer);
                  NofPlayers++;
               }

              UI.setPlayer_list(player_list, player_id, NofPlayers);
              break;
              
       }
   }
   
   public synchronized void send_playerList () {
            
                char comm=(char)(int)3;
                String message;
                String set_ui[]= new String[5];
                set_ui[0]=this_user_name;
                message=(comm +""+ this_user_name + "%" );
                for(int i=0;i<N_connections;i++){
                     message+=Listener_array[i].playername+"%";
                send_message_internal(message);
                set_ui[i+1]=Listener_array[i].playername;
              }
                UI.setPlayer_list(set_ui, 0, N_connections+1);

   }
   //private synchronized void send_player_list ()
   
   public synchronized void send_message(String message){
            send_message= message;
            message_pending = true;
            if(server_mode)
                UI.print(playername+ ": " + send_message);
           SnakesLaddersGUI.ThreadPool.execute(sender);
        }
        
   public synchronized void shutdown(String message){
            Game_running=false;
        }
   
   /*starts game, the server stops listening for connections and roll button unlocks*/
   public synchronized void start_game(){
        Game_starting=false;
        Game_running=true;
        send_message_internal("1Game Starting");
    }
}