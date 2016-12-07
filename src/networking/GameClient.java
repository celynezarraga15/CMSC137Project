package networking;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

import tankerman.WorldMap;

public class GameClient implements Runnable, Constants{

	int x=10,y=10,xspeed=2,yspeed=2,prevX,prevY;
	Thread t=new Thread(this);
	String name="Joseph";
	String pname;
	public static String server;
	static boolean connected=false;
    public static DatagramSocket socket;
	String serverData;
	int startGame = 0;
	private String port;
	static int playerID;
	static boolean endGame = false;
	static String timeRemaining;
	
	private int[] duration = {200,200};
	
	private Image[] walkUp = {new Image("res/charBack2.png"),new Image("res/charBack2.png")};
	private Image[] walkDown = {new Image("res/charFront2.png"),new Image("res/charFront2.png")};
	private Image[] walkLeft = {new Image("res/charLeft2.png"),new Image("res/charLeft2.png")};
	private Image[] walkRight = {new Image("res/charRight2.png"),new Image("res/charRight2.png")};

	
	private Animation moveUp = new Animation(walkUp,duration,false);
	private Animation moveDown = new Animation(walkDown,duration,false);
	private Animation moveLeft = new Animation(walkLeft,duration,false);
	private Animation moveRight = new Animation(walkRight,duration,false);
	
	
	
	public GameClient(String serverIp, String port, String name) throws Exception{
		this.server=serverIp;
		this.name=name;
		this.port=port;
		socket = new DatagramSocket();
		socket.setSoTimeout(100);	
		t.start();		
	}
	
	public static void send(String msg){
		try{
			byte[] buf = msg.getBytes();
        	InetAddress address = InetAddress.getByName(server);
        	DatagramPacket packet = new DatagramPacket(buf, buf.length, address, PORT);
        	socket.send(packet);
        }catch(Exception e){}
		
	}

	public void run(){
		while(true){
			try{
				Thread.sleep(1);
			}catch(Exception ioe){}
						
			//Get the data from players
			byte[] buf = new byte[256];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			try{
     			socket.receive(packet);
			}catch(Exception ioe){/*lazy exception handling :)*/}
			
			serverData=new String(buf);
			serverData=serverData.trim();
			
			//Study the following kids. 
			if (!connected && serverData.startsWith("CONNECTED")){
				connected=true;
//				System.out.println(serverData);
				String[] tokens = serverData.split("-");
				playerID = Integer.parseInt(tokens[1]);
				System.out.println("ID : " + playerID);
			}else if (!connected){
				System.out.println("Connecting..");				
				send("CONNECT "+name);
			}else if (connected){
//				offscreen.getGraphics().clearRect(0, 0, 640, 480);
				if(startGame==0 && serverData.startsWith("START")){
					startGame=1;
					System.out.println(startGame);
				}
				if(serverData.startsWith("END")){
					endGame = true;
				}
				if(serverData.startsWith("TIME")){
					String[] timeInfo = serverData.split("-");
					timeRemaining = timeInfo[1];
					//System.out.println(timeRemaining);
				}
				if (serverData.startsWith("PLAYER")){
					String[] playersInfo = serverData.split(":");
					for (int i=0;i<playersInfo.length;i++){
						String[] playerInfo = playersInfo[i].split(" ");
						int id =Integer.parseInt(playerInfo[1]);
						int x = Integer.parseInt(playerInfo[2]);
						int y = Integer.parseInt(playerInfo[3]);
						String character = playerInfo[4];
						WorldMap.players[id].setXpos(x);
						WorldMap.players[id].setYpos(y);
						
						if(character.equals("up")){
							WorldMap.players[id].setChar(moveUp);
							WorldMap.characters[id] = WorldMap.players[id].getChar();
						}else if(character.equals("down")){
							WorldMap.players[id].setChar(moveDown);
							WorldMap.characters[id] = WorldMap.players[id].getChar();
						}else if(character.equals("left")){
							WorldMap.players[id].setChar(moveLeft);
							WorldMap.characters[id] = WorldMap.players[id].getChar();
						}else if(character.equals("right")){
							WorldMap.players[id].setChar(moveRight);
							WorldMap.characters[id] = WorldMap.players[id].getChar();
						}
					
				}
				
			}			
		}
	}
	}
	
	public int getStartGame(){
		return startGame;
	}
	
	public void paintComponent(Graphics g){
//		g.drawImage(offscreen, 0, 0, null);
	}
	
	public static int getPlayerID(){
		return playerID;
	}
	
	public static boolean getEndGame(){
		return endGame;
	}
	
	public static String getTime(){
		return timeRemaining;
	}
	
	public static boolean getConnectionStatus(){
		return connected;
	}
	
//	class MouseMotionHandler extends MouseMotionAdapter{
//		public void mouseMoved(MouseEvent me){
//			x=me.getX();y=me.getY();
//			if (prevX != x || prevY != y){
//				send("PLAYER "+name+" "+x+" "+y);
//			}				
//		}
//	}
//	
//	class KeyHandler extends KeyAdapter{
//		public void keyPressed(KeyEvent ke){
//			prevX=x;prevY=y;
//			switch (ke.getKeyCode()){
//			case KeyEvent.VK_DOWN:y+=yspeed;break;
//			case KeyEvent.VK_UP:y-=yspeed;break;
//			case KeyEvent.VK_LEFT:x-=xspeed;break;
//			case KeyEvent.VK_RIGHT:x+=xspeed;break;
//			}
//			if (prevX != x || prevY != y){
//				send("PLAYER "+name+" "+x+" "+y);
//			}	
//		}
//	}
}
