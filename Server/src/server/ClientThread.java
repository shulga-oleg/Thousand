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
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import static server.Control._control;
import tableinfo.TableInfo;



public class ClientThread implements Runnable {
    Socket s;//здесь будем хранить ссылку на наш сокет
    private boolean shutdown=false;
    ObjectOutputStream outStream;
    ObjectInputStream inStream;
    private String myName;
    public Log log;
    ClientThread(Socket s, int num) throws IOException{//конструктор,в который мы передаем
        log = new Log();
        this.s=s;//ссылку на сокет
        myName="Player00"+num;//порядковый  номер который добавляется к имени клиента
    }
 
    @Override
    public void run() {
        try {
            outStream = new ObjectOutputStream(s.getOutputStream());
            inStream = new ObjectInputStream(s.getInputStream());
            while(!shutdown){ //пока   
                TableInfo tableInfo = (TableInfo) inStream.readObject();                       
                if(!shutdown){//если мы вручную не останавливаем сокет
                    if(tableInfo!=null){
                        if (tableInfo.totalTablePlayers!=0){
                            ListTableInfo.tableInfo.add(tableInfo);//считываем его
                            Control._control.SendAll(ListTableInfo.tableInfo);//просим Control разослать сообщение всем
                            Player player = new Player(s, inStream, outStream, myName,tableInfo);
                            Control._control.newTable(player);
                            break; 
                        }else{
                            Player player = new Player(s, inStream, outStream, myName,tableInfo);
                            Control._control.addToTable(player);
                            break;
                        }
                    }
                    this.Send(ListTableInfo.tableInfo);                    
                }
            } 
        }catch (IOException ex) {
            log.writeLog("Error initialization clients streams:  "+ex.getMessage());
        } catch (ClassNotFoundException ex) {
                Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
        }finally{//при закрытии сокета
            try {
                outStream = null;
                inStream = null;
                s=null;
                Control._control.ShutdownClient(this);//просим объект Control остановить нас
                log.writeLog("Client disconnect");
            } catch (Exception ex) {
                log.writeLog("Client thread error:  "+ex.getMessage());
            }
        }
    }

    public void Send(ArrayList listTable){
        try {
        //функция отправки клиенту сообщения
        outStream.reset();
        outStream.writeObject(listTable);
        outStream.flush();
        } catch (IOException ex) {
            Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}