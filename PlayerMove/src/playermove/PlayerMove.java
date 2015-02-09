/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package playermove;

import java.io.Serializable;
import java.util.ArrayList;


public class PlayerMove implements Serializable {
    private final String MOVE = "MOVE";
    private final String MARRIAGE = "MARRIAGE"; 
    private final String SCORE = "SCORE";
    private final String TOTAL_SCORE = "TOTAL_SCORE";
    private final String CLEAN = "CLEAN";
    private final String RESET = "RESET";
    private final String RESTART = "RESTART";
    private final String CAN_MOVE = "CAN_MOVE";
    private final String FOLD = "FOLD";
    private final String TRAIDE_WIN = "TRAIDE_WIN";
    private final String TRAIDE = "TRAIDE";
    private final String SELECT_PLAYER = "SELECT_PLAYER";
    private final String TRADE_OUT = "TRADE_OUT";
    private final String MUST_TRADE = "MUST_TRADE";   
    
    
   // private String playerName;
    private String nameMove;
    private String valueMove;
    private ArrayList list;
    private Object other;

    public void setFold(String valueMove){
        this.nameMove = FOLD;
        this.valueMove = valueMove;
    }
    
    public void setMove(String valueMove){
        this.nameMove = MOVE;
        this.valueMove = valueMove;
    }
    
    public void setCanMove(){
        this.nameMove = CAN_MOVE;
        this.valueMove = "";
    }
    
    public void setMarriage(String valueMove){
        this.nameMove = MARRIAGE;
        this.valueMove = valueMove;
    }
    
    public void setScore(ArrayList valueMove){
        this.nameMove = SCORE;
        this.list = valueMove;
    }
    
    public void setTotalScore(ArrayList valueMove){
        this.nameMove = TOTAL_SCORE;
        this.list = valueMove;
    }
    
    public void setClean(){
        this.nameMove = CLEAN;
        this.valueMove = "";
    }
    
    public void setRestart(String valueMove){
        this.nameMove = RESTART;
        this.valueMove = valueMove;
    }
    
    public void setReset(ArrayList valueMove){
        this.nameMove = RESET;
        this.list = valueMove;
    }
    
    public void setTradeWin(ArrayList valueMove){
        this.nameMove = TRAIDE_WIN;
        this.list = valueMove;
    }
    
    public void setTrade(ArrayList valueMove){
        this.nameMove = TRAIDE;
        this.list = valueMove;
    }
    
    public void setPlayer(String valueMove){
        this.nameMove = SELECT_PLAYER;
        this.valueMove = valueMove;
    }
    
    public void setTradeOut(String valueMove){
        this.nameMove = TRADE_OUT;
        this.valueMove = valueMove;
    }
    
    
    public void setMustTrade(String valueMove){
        this.nameMove = MUST_TRADE;
        this.valueMove = valueMove;
    }
    
    public String getNameMove(){
        return nameMove;
    }
    
    public String getValueMove(){
        return valueMove;
    }
    
    public ArrayList getListMove(){
        return list;
    }
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
    }
    
}
