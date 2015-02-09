/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import javax.swing.JOptionPane;
import playermove.PlayerMove;
import tableinfo.TableInfo;


public class Table implements Runnable{
    public int playerCount=1;
    private boolean shutdown=false;//флаг выхода из потока с игрой

    public Log log;    
    private final TableInfo tableInfo;//инфо о столе(общее кол-во игроков,имя стола)
    public String tableName;//имя стола
    public int totalTablePlayers;//общее количество игроков 
    public int countTablePlayers = 1;//количество вступивших игроков
    private final ArrayList <Player> playerList;//
    private ArrayList<Player> playerListSort;//упорядоченный список для хода игры
    private List <String> cards = Arrays.asList("S0","C0","D0","H0","S10","C10","D10","H10","S2","C2","D2","H2",
                                            "S3","C3","D3","H3","S4","C4","D4","H4","S11","C11","D11","H11");
    private  List <String> cards_duplicate = new ArrayList<>();//копия колоды карт
    private ArrayList<Player> playerListTrade;//упорядоченный список игроков для торгов
    private final Random random = new Random();
    private ArrayList listScore = new ArrayList<>();//очки всех игроков за кон
    private int maxCard = 0;//старшая карта
    private int score = 0;//количество очков за взятку
    private PlayerMove playerMove;//ход игрока
    private PlayerMove playerMoveTemp;
    private Player playerWin;//игрок взявший взятку 
    private int playerNumber = 0;//указатель на игрока,с которого начинаются торги
    private int countPass=0;//количество спасовавших игроков
    private int maxTrade = 0;//сыгравшая ставка в торгах
    private Player playerWinTrade;//игрок выигравший торги
    private List<Player> playerTemp;
    private boolean shutdownTrade = false;//условие выхода из торгов
    private String st;
    private ArrayList listTemp;
    private String trumpSuit;//объявленный козырь
    private boolean trumpCard = false;
    
    
    Table(Player player) throws IOException{
        //конструктор,в который мы передаем
        this.listTemp = new ArrayList();
        this.playerTemp = new ArrayList<>();
        this.playerListTrade = new ArrayList<>();
        this.playerList = new ArrayList();
        this.playerListSort = new ArrayList();
        this.playerList.add(player);
        this.log = new Log();
        this.tableInfo = player.getTableInfo();
        this.tableName = tableInfo.tableName;
        this.totalTablePlayers = tableInfo.totalTablePlayers;
        this.playerMove = new PlayerMove();
    }

//добавить игрока к игре    
    public void addPlayer(Player player){
        playerList.add(player);
    }
//получаем кол-во игроков    
    public int getCountPlayers(){
        return playerList.size();
    }
 
//получаем карты для очередного игрока    
    private List<String> getCard(){
        List<String> cardsPlayer = new ArrayList<>();//создаем массив карт для игрока
        int card;//счетчик
        for (int i=0; i<7; i++){
            card = random.nextInt(cards_duplicate.size());//получаем случайное число
            cardsPlayer.add(cards_duplicate.get(card));//берем из колоды карту и добавляем к картам игрока
            cards_duplicate.remove(card);//удаляем карту из колоды
        }
        return cardsPlayer;//возвращаем карты игроку на раздачу
    }

//создаем колоду карт    
    private void copyArrays(){
        for (int i=0; i<24; i++){
            cards_duplicate.add(cards.get(i));
        }
    }
   

    //подсчет очков за взятку и вычисление игрока осуществившего взятку
    private void setScore(String card, Player player){
        int lengthCard = card.length();//определяем длину массива,в котором храним имя карты 
        int newCard = Integer.parseInt(card.substring(1,lengthCard));//берем значение карты(очки)
        String trump = card.substring(0,1);//берем масть карты которой походили
        if(player!=playerWin){  
            if(trumpSuit==null){//если нет козыря
                if (newCard>maxCard){//если карта последнего ходившего игрока старше
                    maxCard = newCard;//меняем макс.карту
                    playerWin = player;//меняем потенциального победителя
                }
            }else{//если козырь есть
                if (trumpSuit.equals(trump)){//если игрок ходит козырной картой
                    if(trumpCard){//если старшая карта козырь    
                        if (newCard>maxCard){//если казырная карта последнего ходившего игрока старше
                            maxCard = newCard;
                            playerWin = player;
                        }
                    }else{//если старшая карта не козырь 
                        maxCard = newCard;
                        playerWin = player;
                        trumpCard = true;
                    }    
                }else{//если игрок ходит не козырной картой
                    if(trumpCard){//если старшая карта козырь        
                        //ничего не происходит
                    }else{//если старшая карта не козырь
                        if (newCard>maxCard){//если карта последнего ходившего игрока старше
                            maxCard = newCard;
                            playerWin = player;
                        }
                    }    
                }
            }
        }
        score+=newCard;
        
    }
   //сбрасываем временные значения переменных
    private void resetValue(){
        score = 0;
        maxCard = 0;
        trumpCard = false;
        listScore.clear();
        countPass = 0;
        playerWin = null;
        shutdownTrade = false;
    }
    //определяем порядок ходов
    private ArrayList<Player> setListSort(ArrayList<Player> playerListSort, Player playerWin){
        ArrayList <Player> ListCopy = new ArrayList();
        int index  = playerListSort.indexOf(playerWin);
        for(int i=index;i<playerListSort.size();i++){
            ListCopy.add(playerListSort.get(i));
        }
        for(int i=0;i<index;i++){
            ListCopy.add(playerListSort.get(i));
        }
        return this.playerListSort = ListCopy;
    }
    
      //определяем порядок торгов
    private ArrayList<Player> sortPlayerListTrade (){
        ArrayList <Player> ListCopy = new ArrayList();
        for(int i=playerNumber;i<playerList.size();i++){
            ListCopy.add(playerList.get(i));
        }
        for(int i=0;i<playerNumber;i++){
            ListCopy.add(playerList.get(i));
        }
        return this.playerListTrade = ListCopy;
    }
    
   //определяем победителя игры 
    public boolean getWinPlayer(List<Player> playerList){
        Player pl1 = playerList.get(0);
        Player pl2 = playerList.get(1);
        Player pl3 = playerList.get(2);
        if(pl1.getTotalScore()>=1000){
            JOptionPane.showMessageDialog(null, "Победитель "+pl1.getPlayerName() +"!");
            return true;
        }else if(pl2.getTotalScore()>=1000){
            JOptionPane.showMessageDialog(null, "Победитель "+pl2.getPlayerName() +"!");
            return true;
        }else if(pl3.getTotalScore()>=1000){
                    JOptionPane.showMessageDialog(null, "Победитель "+pl3.getPlayerName() +"!");
            return true;
        }
        return false;
    }
    
    //получаем козырную масть
    public void setTrumpSuit(String s){
        this.trumpSuit = s.substring(0,1);
    }
    
    public int getBonus(){
        switch(trumpSuit){
            case "S": return 40;
            case "C": return 60;
            case "D": return 80;
            case "H": return 100;
        }
        return 0;
    }
        
    @Override
    public void run() {
        //JOptionPane.showMessageDialog(null, "Yahooo!!!");
        try {
            //ждем пока подключаться все игроки
            while (playerList.size()<totalTablePlayers){
                
            }
            ArrayList names = new ArrayList();//список имен игроков
           //рассылаем имена игроков для отображения в табл.
            for(Player player: playerList){
                for(Player player2: playerList){//перебираем всех игроков
                    if(player.getPlayerName().equals(player2.getPlayerName())){
                        names.add(player2.getPlayerName()+"(You)");
                    }else{
                        names.add(player2.getPlayerName());
                    }
                }
                player.setMessage(names); //отсылаем имена других игроков
                player.getMessage(); ////ждем ответа от игрока
                names.clear();//опустошаем спиок
            }
           
            //рассылаем имена игроков для отображения на столе
            for(Player player: playerList){
                for(Player player2: playerList){//перебираем всех игроков
                    if(!(player.getPlayerName().equals(player2.getPlayerName()))){
                        names.add(player2.getPlayerName());
                    }
                }    
                player.setMessage(names); //отсылаем имена других игроков
                player.getMessage(); ////ждем ответа от игрока
                names.clear();//опустошаем спиок
            }    
            //копируем массив для порядка хода игроков
            for(int i=0; i<playerList.size(); i++){
                playerListSort.add(playerList.get(i));
            }
            //копируем массив для поочередного перехода торгов
            for(int i=0; i<playerList.size(); i++){
                playerListTrade.add(playerList.get(i));
            }            
            
    //НАЧАЛО ПАРТИИ        
            while(!shutdown){ //пока       
                copyArrays(); //берем колоду карт
                for(Player player:playerList){
                    player.setMessage(getCard());//отбираем карты для игрока и отсылаем игроку
                    player.getMessage(); //ждем ответа от игрока
                }

                
////////////торги

                    Player playerT = playerListTrade.get(0);
                    playerMove.setMustTrade("");
                    playerT.setMessage(playerMove);

                while(!shutdownTrade){
                    
                    for(Player player: playerListTrade){//получаем от игроков сообщение со ставкой
                        if (!playerTemp.contains(player)){
                            if (!player.equals(playerT)){
                                playerMove.setCanMove();
                                player.setMessage(playerMove);
                                playerT=null;
                            }
                            st = (String)player.getMessage();
                            if(!st.equals("ПАС")){
                                if(Integer.parseInt(st) > maxTrade){//вычисляем макс.ставку
                                    maxTrade = Integer.parseInt(st);
                                    playerWinTrade = player;//записываем игрока выигравшего торги
                                }
                            }else{
                                    playerTemp.add(player);
                                    countPass++;
                                }
                        //отсылаем остальным игрокам ставку сделавшую игроком
                        for(Player player2: playerList){
                            if(player2!=player){
                                playerMove.setPlayer(player.getPlayerName());
                                player2.setMessage(playerMove);
                                if(playerTemp.contains(player)){
                                    player2.setMessage("ПАС");
                                }else{
                                    player2.setMessage(st);
                                }    
                            }    
                        }  
                        if (countPass == 2){
                            shutdownTrade = true;
                            break;
                        }
                        }                        
                    }
                }
                
                playerTemp.clear();
                
                //отсылаем прикуп            
                //отсылаем победителю инфу о прикупе
                playerMove.setTradeWin((ArrayList)cards_duplicate);
                playerWinTrade.setMessage(playerMove);
                //отсылаем игроками инфу о прикупе                            
                playerMove.setTrade((ArrayList)cards_duplicate);
                for(Player player: playerList){
                if(player!=playerWinTrade){
                    player.setMessage(playerMove);}

                }
                //playerListTrade.clear();
                cards_duplicate.clear();//колода закончена
////////////  

//////////// снос карт
            for(Player player: playerList){
                if(player!=playerWinTrade){
                    listTemp.add(playerWinTrade.getMessage().toString());//получаем скинутые карты
                    playerMove.setReset(listTemp);
                    player.setMessage(playerMove);
                    playerWinTrade.setMessage(null);
                    listTemp.clear();
                }
            }
////////////         
            
            //вывод максимальной ставки
            String trade ="Ставка: "+Integer.toString(maxTrade)+"("+playerWinTrade.getPlayerName()+")";
            for(Player player: playerList){
                playerMove.setTradeOut(trade);
                player.setMessage(playerMove);
            }
            //
                
                
                
                //определение порядка ходов 
                    setListSort(playerListSort,playerWinTrade);
////////////ход игры                
                for(int j=0; j<8; j++){//пока не закончатся карты
                    for(Player player:playerListSort){//берем очередного игрока
                        playerMove.setCanMove();
                        player.setMessage(playerMove);
                        playerMove = (PlayerMove) player.getMessage();
                        
                        for(Player player2:playerListSort){//отсылаем игрокам информацию о ходе
                            switch (playerMove.getNameMove()){
                                case "MOVE":{
                                    if(!(player.getPlayerName().equals(player2.getPlayerName()))){
                                        player2.setMessage(playerMove);//отсылаем инфу о ходе кроме ходившего игрока
                                    }else{
                                        //подсчитываем очки за взятку
                                        setScore(playerMove.getValueMove(),player);
                                    }
                                    break;
                                }
                                case "FOLD":{
                                    if(!(player.getPlayerName().equals(player2.getPlayerName()))){
                                        player2.setMessage(playerMove);//отсылаем инфу о ходе кроме ходившего игрока
                                    }else{
                                        //подсчитываем очки за взятку
                                        setScore(playerMove.getValueMove(),playerWin);
                                    }
                                    break;
                                }
                                case "MARRIAGE":{
                                    setTrumpSuit(playerMove.getValueMove());
                                    if(!(player.getPlayerName().equals(player2.getPlayerName()))){
                                        player2.setMessage(playerMove);//отсылаем инфу о ходе кроме ходившего игрока
                                    }else{
                                        //добавляем очки за марьяж
                                        player.setScore(getBonus());                                        
                                        setScore(playerMove.getValueMove(),player);
                                    }    
                                    break;
                                }
                            }
                            
                        }
                    }
                    Thread.sleep(1000);
                    //говорим очистить столы
                    for(Player playerClear:playerList){
                            playerMove.setClean();
                            playerClear.setMessage(playerMove);
                    }  
                    // отправляю игроку выигравшему взятку очки
                    playerWin.setScore(score);
                    //берем у всех игроков инфу об очках за взятку
                    for(Player playerGetScore:playerList){
                            listScore.add(playerGetScore.getScore());
                    }
                    
                    //рассылаем инфу об очках всем игрокам
                    playerMove.setScore(listScore);
                    for(Player playerSendScore:playerList){
                            playerSendScore.setMessage(playerMove);
                    }
                    //определение порядка ходов 
                    setListSort(playerListSort,playerWin);
                    //очищаем данные о счете за кон
                    resetValue(); 

                } 
////////////////    
                //проверяем сыграна ли ставка в торгах
                if (playerWinTrade.getScore()>=maxTrade){
                    playerWinTrade.setScore(0-playerWinTrade.getScore()+maxTrade);
                }else{
                    playerWinTrade.setScore(0-playerWinTrade.getScore()-maxTrade);
                }
                playerWinTrade = null;
                //определение порядка торгов
                playerNumber++;
                if (playerNumber>2){
                    playerNumber = 0;
                }
                sortPlayerListTrade();
                //
                
                maxTrade = 0;
                
                //онуляем набранные игроками очки за кон
                for(Player playerResetScore:playerList){
                    playerResetScore.resetScore();
                }
                //обнуляем значения временных переменных
                //resetValue(); 
                
                //берем у всех игроков инфу об очках за кон
                for(Player playerGetTotalScore:playerList){
                    listScore.add(playerGetTotalScore.getTotalScore());
                }
                //рассылаем инфу об очках всем игрокам
                playerMove.setTotalScore(listScore);
                for(Player playerSendTotalScore:playerList){
                    playerSendTotalScore.setMessage(playerMove);
                }  
                //вызываем метод проверки победителя и окончания партии
                shutdown = getWinPlayer(playerList);
            } 
            
        }catch (Exception ex) {
            log.writeLog("Error initialization clients streams:  "+ex.getMessage());
        }finally{//при закрытии сокета
            Thread.interrupted();
        }
    }
    
}


