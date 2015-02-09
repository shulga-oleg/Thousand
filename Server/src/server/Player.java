/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import playermove.PlayerMove;
import tableinfo.TableInfo;




public class Player {
    private final String playerName;
    private final Socket playerSocket;
    private final TableInfo tableInfo;
    private final ObjectOutputStream out;
    private final ObjectInputStream in;
    private int score=0;
    private int totalScore=0;
    private Object obj;
    private PlayerMove playerMove;
    
    Player(Socket s, ObjectInputStream inStream, ObjectOutputStream outStream, String name, TableInfo tblInfo) throws IOException{
        this.playerName = name;
        this.playerSocket = s;
        this.tableInfo = tblInfo;
        this.in = inStream;
        this.out = outStream;
        playerMove = new PlayerMove();
        
        //setVariableSocket();
    }
    
    //получить имя игрока    
    public String getPlayerName(){
        return this.playerName;
    }
    
    /*public void setVariableSocket(){
        try {
            out = new ObjectOutputStream(playerSocket.getOutputStream());
            in = new ObjectInputStream(playerSocket.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
        }
    }*/
    
    public void resetScore(){
        this.totalScore+=score;
        this.score = 0;
    }
    
    public void resetTotalScore(){
        this.totalScore = 0;
    }
    
    public int getScore(){
        return score;
    }
    
    public int getTotalScore(){
        return totalScore;
    }
    
    public void setScore(int score){
        this.score+=score;
    }
    
    //получить  инфу о соединении с игроком
    public Socket getPlayerSocket(){
        //return this.playerSocket;
        return null;
    }
   //получить информацию о столе 
    public TableInfo getTableInfo(){
        return this.tableInfo;
    }

    public Object getMessage(){
        try {
            return  obj = in.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public void setMessage(Object ob){
        try {
            out.reset();
            out.writeObject(ob);
            out.flush();
        } catch (IOException ex) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void finalize (){
        try {
            out.close();
            in.close();
            //playerSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(Player.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    
    
    
 

}
