package server;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.Server;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import tableinfo.TableInfo;


public class Control implements Runnable{
    public static boolean shutdown=false;
    public static Control _control;
    private int numClient=0;
    public ArrayList clientList;
    public ArrayList tableList;
    public Log log;
    private FileReader fileReader;
    private Scanner sc;
    private int port;
    
    Control() throws FileNotFoundException{
        clientList = new ArrayList();
        tableList = new ArrayList();
        _control=this;
        log = new Log();
        fileReader = new FileReader("port.txt");
        sc = new Scanner(fileReader);
    }
    
    //устанавливаем условие выхода из потока
    public void setShutDown(boolean f){
        shutdown = f;
    }
    
    public int getPort (){
        try {
            return port = sc.nextInt();

        } finally {
            if(fileReader != null) {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    public void run(){
        try {
            
            ServerSocket ss=new ServerSocket(getPort());
            log.writeLog("Server started");
            log.writeLog("Connection wait...");
            while(!shutdown){
                Socket incoming=ss.accept();
                log.writeLog("Player"+numClient+" connected");
                ClientThread client=new ClientThread(incoming, numClient);
                clientList.add(client);
                Thread t=new Thread(client);
                numClient++;
                t.start();
            }
        } catch (IOException ex) {
            log.writeLog("Server internal error "+ex.getMessage());
        }
    }
    //убрать из списка подключенных клиентов
    public void ShutdownClient(ClientThread c){
        clientList.remove(c);
    }
    
    //удаляем информацию о столе
    public void removeTableInfo(String nameTable){
        int i = 0;
        while (i<ListTableInfo.tableInfo.size()){
            TableInfo tableInfo = (TableInfo) ListTableInfo.tableInfo.get(i);//берем инфу о столе
            if(tableInfo.tableName.equals(nameTable)){
                ListTableInfo.tableInfo.remove(i);//удаляем стол из доступных для выбора
                break;}
            i++;
        }
    }
    
    //изменяем инфу о количестве игроков подключенных к столу
    public void changeTableInfo(String nameTable){
        for (int i=0;i<ListTableInfo.tableInfo.size();i++){
             TableInfo tableInfo = (TableInfo) ListTableInfo.tableInfo.get(i);//берем инфу о столе
             if(tableInfo.tableName.equals(nameTable)){
                tableInfo.tablePlayers++; 
                 ListTableInfo.tableInfo.set(i, tableInfo);
                break;
             }
        }
    }
    
    
    //создание нового стола
    public void newTable(Player player){
        try {
        //получаем инфу от юзера:сокет, стол, имя
        Table table=new Table(player);
        tableList.add(table);//добавляем созданный игровой стол в список столов
        } catch (IOException ex) {
            Logger.getLogger(Control.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    
    
    //присоединение к игре
     public void addToTable(Player player) {//получаем инфу от юзера:сокет, стол, имя
        try{
            int i;
            TableInfo tableInfoPlayer = player.getTableInfo();//узнаем у игрока имя стола 
            for (i=0; i<tableList.size();i++){//перебираем все свободные столы в массиве
                Table tableFromList =  (Table) tableList.get(i);//берем очередной стол
                if (tableFromList.tableName.equals(tableInfoPlayer.tableName)){//если названия выбранного стола совпадает со столом в списке выходим из цикла
                    tableFromList.addPlayer(player);//добавляем игрока к игровому столу
                    changeTableInfo(tableInfoPlayer.tableName);
                    if (tableFromList.getCountPlayers() == tableFromList.totalTablePlayers){//если все места заняты
                        tableList.remove(i);//удаляем стол из списка столов
                        removeTableInfo(tableInfoPlayer.tableName);//удаляем инфу о столе из списка инфы о столах
                        Thread t2=new Thread(tableFromList);//помещаем игру на столе в отдельно созданный поток
                        t2.start();//запускаем игру
                    }
                    _control.SendAll(ListTableInfo.tableInfo);//просим Control разослать сообщение всем
                    break;//выход из цикла
                }    
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, ex.getMessage());
            log.writeLog("Server internal error "+ex.getMessage());
        }    
    }   
    
    //разослать всем информацию о столах
    public void SendAll(ArrayList tableInfo){
        for(int i=0;i<clientList.size();i++){
            getClient(i).Send(tableInfo);  
        }
    }
    
    //выбрать очередного игрока
    public ClientThread getClient(int index){
        return((ClientThread)clientList.get(index));
    }
 
}



