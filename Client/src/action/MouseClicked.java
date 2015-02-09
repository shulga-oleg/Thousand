/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package action;

import client.ClientFrame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import playermove.PlayerMove;


public class MouseClicked extends MouseAdapter{
    private ClientFrame parent;
    private PlayerMove playerMove;
    
    public MouseClicked(ClientFrame parent){
       this.parent = parent;
       playerMove = new PlayerMove();
    } 
    
    @Override
    public  void mouseClicked(MouseEvent e){
        
        try {
            while(true){
            if(parent.canMove){
                parent.sendMessageMove("");
                JLabel lbl = new JLabel();
                lbl = (JLabel)e.getSource();
                String suitCard = lbl.getName();//карта ходившего игрока
                String suit = suitCard.substring(0,1);//масть карты с которой походил игрок
                //проверка захваливания марьяжа
                if (parent.suit.equals("")){
                    String valueCard = suitCard.substring(1,suitCard.length());
                    if (valueCard.equals("3")){
                        String king = suit+"4";
                        if(parent.cards.contains(king)){
                            parent.setTrumpSuit(suit);
                            parent.setCard(lbl.getName());
                            playerMove.setMarriage(suitCard);
                            parent.sendToServer(playerMove);//и отсылаем карту противникам
                            parent.cards.remove(suitCard);
                            parent.removeCard(lbl);              
                            parent.canMove = false;
                            parent.canMarriage = false;
                            break;
                        }
                    }
                    
                }
                
 
                if (parent.suit.equals("")){parent.suit = suit;}//если 
                if (parent.suit.equals(suit) || parent.checkSuit()){
                    parent.setCard(lbl.getName());
                    if(parent.checkSuit()&&!parent.getTrumpCard(suit)){
                        playerMove.setFold(suitCard);
                    }else{
                        playerMove.setMove(suitCard);
                    }
                    parent.sendToServer(playerMove);//и отсылаем карту противникам
                    parent.cards.remove(suitCard);
                    parent.removeCard(lbl);                    
                    parent.canMove=false;
                }
            }    
            
            if(parent.canMoveTrade){
                JLabel lbl = new JLabel();
                lbl = (JLabel)e.getSource();
                String card = lbl.getName();
                    parent.setCard(card);
                    parent.cards.remove(card);
                    parent.removeCard(lbl);
                    parent.sendToServer(card);//и отсылаем карту противникам
            }
            break;
            }    
        } catch (IOException ex) {
            Logger.getLogger(MouseClicked.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(MouseClicked.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
