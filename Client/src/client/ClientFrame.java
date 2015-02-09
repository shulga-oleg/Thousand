/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import action.MouseClicked;
import dialog.AboutDialog;
import dialog.HelpDialog;
import static java.awt.EventQueue.invokeLater;
import java.awt.Image;
import java.util.List;
import java.net.*;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Scanner;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import playermove.PlayerMove;
import tableinfo.TableInfo;





public class ClientFrame extends javax.swing.JFrame  implements Runnable{
    Socket ss = null; //Ссылка на сокетное соединение
    TableInfo tableInfo, tableInfoOut;
    private ArrayList listTable;
    private ObjectInputStream in = null;
    private ObjectOutputStream out = null;
    private DefaultTableModel  model;
    private boolean shutdownTables=false;
    private boolean shutdownGame=false;
    public boolean shutdownSubGame=true;
    private List<JLabel> listLabel;
    public String listCardReset;
    public List<String> cards;
    private int port = 1000;
    private String ip = "127.0.0.1";
    private boolean flag = true;
    private Object move = new Object();
    private PlayerMove playerMove;
    private int countCard = 0;
    public boolean canMove = false;
    public boolean canMoveTrade = false;
    public String suit = "";//масть
    public int countSubGames=0;
    //private int minRate = 120;
    private int maxRate = 120;
    private boolean turnLabel;
    private boolean shutdownTrade = false;
    private boolean youWin = false;
    public boolean canMarriage = true;
    private String trumpSuit;
    private FileReader fileReader;
    private File connectFile = new File("connect.txt");
    private Scanner sc;
    

//обновление информации о столах в таблице
public void tableRefresh(ArrayList listTable) throws InterruptedException {
    invokeLater (new Runnable() {
        public void run() {
            for(int i=0;i<jTable1.getRowCount();i++){
                model = (DefaultTableModel) jTable1.getModel();
                model.removeRow(i);
            }
            
            //выводим данные о сеансах в таблицу
            for(int i=0; i<listTable.size();i++){
                tableInfoOut = (TableInfo) listTable.get(i);
                model = (DefaultTableModel) jTable1.getModel();
                model.addRow(new Object[]{tableInfoOut.tableName,tableInfoOut.tablePlayers+"/"+tableInfoOut.totalTablePlayers, tableInfoOut.tableDate});
            }
        }
    });
 } 



//выводим данные об именах игроков на столе
public  void setPlayersNameTable( ArrayList playersName) throws InterruptedException {
    invokeLater (new Runnable() {
        public void run() {
            //colNames.add("You");
            for(int i=0; i<playersName.size();i++){
                JLabel lbl = listLabel.get(i);
                lbl.setText(playersName.get(i).toString());
             }
        }
    });
} 

//выводим данные об именах игроков в табл.
public  void setPlayersNameGrid (ArrayList playersName) throws InterruptedException {
    invokeLater (new Runnable() {
        @Override
        public void run() {
            Vector<Object> colNames = new Vector<Object>();
            
            for(int i=0; i<playersName.size();i++){
                colNames.add(playersName.get(i).toString());
            }
            DefaultTableModel newModel2 = new DefaultTableModel(colNames, 0); 
            DefaultTableModel newModel3 = new DefaultTableModel(colNames, 1);
            jTable3.setModel(newModel2);
            jTable4.setModel(newModel3);
        }
    });
 } 

public void addCardOpponent(){
        try {
            JLabel FlipSide1 = new JLabel();
            JLabel FlipSide2 = new JLabel();
            FlipSide1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            FlipSide2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            Image imgFlipSide = ImageIO.read(new File("media/cards/flipside.png"));
            FlipSide1.setIcon(new ImageIcon(imgFlipSide));
            FlipSide2.setIcon(new ImageIcon(imgFlipSide));
            invokeLater (new Runnable() {
                public void run() {
                    jPanel4.add(FlipSide1);
                    jPanel5.add(FlipSide2);
                }
            });
        } catch (IOException ex) {
            Logger.getLogger(ClientFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    invokeLater (new Runnable() {
        public void run() {
            jPanel4.updateUI();
            jPanel5.updateUI();
        }
    });
}

//получаем карты, выигранные в торгах
public void addCards(List<String> cards){
    try {
        for (String card : cards) {    
            JLabel pic = new JLabel();
            pic.setName(card);
            pic.addMouseListener(new MouseClicked(this));
            pic.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            Image img = ImageIO.read(new File("media/cards/" + card + ".png"));
            pic.setIcon(new ImageIcon(img));
            invokeLater (new Runnable() {
                public void run() {
                    jPanel6.add(pic);
                }
            });
        }
        invokeLater (new Runnable() {
            public void run() {
                jPanel6.updateUI();
            }
        });
        
	for(int i=0; i<cards.size();i++){
            this.cards.add(cards.get(i));
        }
        
    }catch (IOException ex) {
        Logger.getLogger(ClientFrame.class.getName()).log(Level.SEVERE, null, ex);
    }
}

//сортировка карт
public void setCardsSort(){
    invokeLater (new Runnable() {
        public void run() {
        jPanel6.removeAll();
        jPanel6.setLayout(new java.awt.FlowLayout());
        }
    });
    cards.sort(null);
        for (String card : cards) {
        try {
            JLabel pic = new JLabel();
            pic.setName(card);
            pic.addMouseListener(new MouseClicked(this));
            pic.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            Image img = ImageIO.read(new File("media/cards/" + card + ".png"));
            Image imgFlipSide = ImageIO.read(new File("media/cards/flipside.png"));
            pic.setIcon(new ImageIcon(img));
            invokeLater (new Runnable() {
                public void run() {
                    jPanel6.add(pic);
                }
            });
        } catch (IOException ex) {
            Logger.getLogger(ClientFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        }
    invokeLater (new Runnable() {
        public void run() {
            jPanel6.updateUI();
        }
    });
}

//выкладка карт 
public void setCards(List<String> cards) throws IOException, InterruptedException{
    invokeLater (new Runnable() {
        public void run() {
        jPanel4.setLayout(new java.awt.FlowLayout());
        jPanel5.setLayout(new java.awt.FlowLayout());
        jPanel6.setLayout(new java.awt.FlowLayout());
        }
    });
    cards.sort(null);
        for (String card : cards) {
        try {
            JLabel pic = new JLabel();
            pic.setName(card);
            pic.addMouseListener(new MouseClicked(this));
            JLabel FlipSide1 = new JLabel();
            JLabel FlipSide2 = new JLabel();
            pic.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            FlipSide1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            FlipSide2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
            Image img = ImageIO.read(new File("media/cards/" + card + ".png"));
            Image imgFlipSide = ImageIO.read(new File("media/cards/flipside.png"));
            pic.setIcon(new ImageIcon(img));
            FlipSide1.setIcon(new ImageIcon(imgFlipSide));
            FlipSide2.setIcon(new ImageIcon(imgFlipSide));
            invokeLater (new Runnable() {
                public void run() {
                    jPanel4.add(FlipSide1);
                    jPanel5.add(FlipSide2);
                    jPanel6.add(pic);
                }
            });
        } catch (IOException ex) {
            Logger.getLogger(ClientFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
        }
    invokeLater (new Runnable() {
        public void run() {
            jPanel4.updateUI();
            jPanel5.updateUI();
            jPanel6.updateUI();
        }
    });
}

//удаляем карты по одной со стола
public void removeCard(JLabel lbl) throws InterruptedException{
    invokeLater (new Runnable() {
        public void run() {
            jPanel6.remove(lbl);
            jPanel6.updateUI();
        }
    });
    
}

//удаляем карты противника со стола
public void removeCardOpponents() throws InterruptedException{
    invokeLater (new Runnable() {
        public void run() {
            if(flag){
                jPanel4.remove(0);
                jPanel4.updateUI();
                flag=false;
            }else{
                jPanel5.remove(0);
                jPanel5.updateUI();
                flag=true;
            }
        }
    });
}

//настройка подключения к серверу
public void setIP(String ip){
    this.ip = ip;
}
public void setPort(int port){
    this.port = port;
}
        
        
//выкладка прикупа закрытым
public void setCardsTakeClose() throws IOException, InterruptedException{
    invokeLater (new Runnable() {
        public void run() {
            jPanel7.setLayout(new java.awt.FlowLayout());
            for (int i=1; i<4;i++) {
            try {
                JLabel pic = new JLabel();
                pic.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                pic.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                Image imgFlipSide = ImageIO.read(new File("media/cards/flipside.png"));
                pic.setIcon(new ImageIcon(imgFlipSide));
                jPanel7.add(pic);
            } catch (IOException ex) {
                Logger.getLogger(ClientFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
            }
            jPanel7.updateUI();
        }
    });
}

//выкладка прикупа открытым
public  void setCardsTakeOpen(List<String> cards) throws IOException, InterruptedException{
    invokeLater (new Runnable() {
        public void run() {
            jPanel7.setLayout(new java.awt.FlowLayout());
            for (String card : cards) {
            try {
                JLabel pic = new JLabel();
                pic.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                Image img = ImageIO.read(new File("media/cards/" + card + ".png"));
                pic.setIcon(new ImageIcon(img));
                jPanel7.add(pic);
            } catch (IOException ex) {
                Logger.getLogger(ClientFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
            }
            jPanel7.updateUI();
        }
    });
}

//выводим карту на стол
public void setCard(String card) throws IOException, InterruptedException{
    invokeLater (new Runnable() {
        public void run() {
            try {
                jPanel7.setLayout(new java.awt.FlowLayout());
                JLabel pic = new JLabel();
                pic.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
                Image img = ImageIO.read(new File("media/cards/" + card + ".png"));
                pic.setIcon(new ImageIcon(img));
                jPanel7.add(pic);
                jPanel7.updateUI();
            } catch (IOException ex) {
                Logger.getLogger(ClientFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    });
}

//отправка сообщения на сервер
public void sendToServer(Object ob){
    try {
        out.reset();
        out.writeObject(ob);
        out.flush();
    } catch (IOException ex) {
        Logger.getLogger(ClientFrame.class.getName()).log(Level.SEVERE, null, ex);
    }
}

//очищаем место для карт
public void clearPanel() throws InterruptedException{
    invokeLater (new Runnable() {
        public void run() {
            jPanel7.removeAll();
            jPanel7.updateUI();
            }
        });
            countCard = 0;
            suit = "";
}

//выводим в табл. счет за кон
public  void setTableScore(ArrayList list) throws InterruptedException{
    invokeLater (new Runnable() {
        public void run() {
            jTable4.setValueAt(list.get(0),0, 0);
            jTable4.setValueAt(list.get(1),0, 1);
            jTable4.setValueAt(list.get(2),0, 2);
        }
    });
}

//вывести счет за кон в общ. табл.
public void setTableTotalScore(ArrayList list){
    invokeLater (new Runnable() {
        public void run() {
        model = (DefaultTableModel) jTable3.getModel();
        model.addRow(new Object[]{list.get(0),list.get(1), list.get(2)});
        }
    });    
}
//установка масти с которой зашли(первая карта)
public void setSuit(String card){
    String suit = card.substring(0,1);
    this.suit = suit;
}

//извещение о ходе игрока
public void sendMessageMove(String mess){
    invokeLater (new Runnable() {
        public void run() {
        jLabel9.setText(mess);
        }
    }); 
}

//проверка на наличие карты необходимой масти
public boolean checkSuit(){
    for(String card:cards){
        String suit = card.substring(0,1);
        if(this.suit.equals(suit)){
            return false;
        }
    }
    return true;
}
//очищаем надписи и место для карт 
private void clearLabel(){
    invokeLater (new Runnable() {
        public void run() {
            jLabel7.setText("");
            jLabel8.setText("");
            jLabel5.setText("");
        }
    });
}

private void clearTableScore(){
    invokeLater (new Runnable() {
        public void run() {
            jTable4.setValueAt("",0, 0);
            jTable4.setValueAt("",0, 1);
            jTable4.setValueAt("",0, 2);
    }
    });
}

//вибираем labelв который выводить ставку игрока

public void setSelectLabel(String s){
   if (s.equals(jLabel2.getText())){
       turnLabel = true;
   }else{
       turnLabel = false;
   }
}
//устанавливаем надписи и место для карт 
private void setLabel(String s){
    invokeLater (new Runnable() {
        public void run() {
            if(turnLabel){
                jLabel7.setText(s);
            }else{
                jLabel8.setText(s);
            }
        }
    });
}
private void setVisibleTradePanel(boolean f){
    invokeLater (new Runnable() {
        public void run() {
            jPanel8.setVisible(f);
        }    
    });
}

private void clearLabelMove(){
    invokeLater (new Runnable() {
        public void run() {
            jLabel10.setText("");
        }    
    });
}

private void canTrade(boolean b){
    jButton3.setEnabled(b);
    jButton4.setEnabled(b);
    jButton5.setEnabled(b);
    jButton6.setEnabled(b);
}

private void setTradeOut(String s){
    invokeLater (new Runnable() {
        public void run() {
            jLabel10.setText(s);
        }    
    });
}

private void setLabelTrade(String s){
    invokeLater (new Runnable() {
        public void run() {
            jLabel9.setText(s);
        }    
    });
}

//определяем макс.ставку
private void setMaxRate(){
    if(cards.contains("S3")&&cards.contains("S4")){
        maxRate  = 160;
    }
    if(cards.contains("C3")&&cards.contains("C4")){
        maxRate  = 180;
    }
    if(cards.contains("D3")&&cards.contains("D4")){
        maxRate  = 200;
    }
    if(cards.contains("H3")&&cards.contains("H4")){
        maxRate  = 220;
    }
}

//проверка козырной карты
public boolean getTrumpCard(String s){
    if(s.equals(trumpSuit)){
        return true;
    }else{
        return false;
    }
}

//получаем козырную масть и объявляем игроку
public void setTrumpSuit(String s){
    trumpSuit = s.substring(0,1);
    invokeLater (new Runnable() {
        public void run() {
            switch(trumpSuit){
            case "S": jLabel11.setText("Козырная масть : пики");break;
            case "C": jLabel11.setText("Козырная масть : трефы");break;
            case "D": jLabel11.setText("Козырная масть : бубны");break;
            case "H": jLabel11.setText("Козырная масть : червы");break;
        }
        }    
    });
}

//убираем со стола козырь
public void clearTrumpSuit(){
    invokeLater (new Runnable() {
        public void run() {
            jLabel11.setText("");
        }    
    });
}

public void reloadConnect(){
    if(connectFile.exists()) {
        try {
            fileReader = new FileReader("connect.txt");
            sc = new Scanner(fileReader);
            ip = sc.nextLine();
            port = sc.nextInt();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ClientFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }    

}


public void run() {
    try{
        //В цикле читаем данные из потока
        //получаем инфу о столах
        while(!shutdownTables) {
            tableInfoOut = new TableInfo();
            listTable = (ArrayList)in.readObject();
            tableRefresh(listTable);
        }
        
        //список игроков для таблицы
        ArrayList playersNameGrid  = (ArrayList)in.readObject();//получаем список имен игроков
        setPlayersNameGrid(playersNameGrid);//выводим имена игроков
        sendToServer(null);
        
        //список игроков для стола
        ArrayList playersNameTable  = (ArrayList)in.readObject();//получаем список имен игроков
        setPlayersNameTable(playersNameTable);//выводим имена игроков
        sendToServer(null);
        
        //игра
        while(!shutdownGame){
            //получаем список карт
            cards = (List<String>)in.readObject();//получаем карты
            setCards(cards);
            sendToServer(null);
            jPanel8.setVisible(true);
            
            //прикуп
            setCardsTakeClose();//выкладываем прикуп 
            
            //
            setVisibleTradePanel(true);
            
            setMaxRate();
            
            //торги
            while(!shutdownTrade){
                move = in.readObject();
                if(move!=null){
                    if(move instanceof PlayerMove){
                        PlayerMove playerM = (PlayerMove)move;
                        switch(playerM.getNameMove()){
                            case "TRAIDE":{
                                clearPanel();
                                setCardsTakeOpen(playerM.getListMove());
                                Thread.sleep(1500);
                                clearLabel();
                                clearPanel();
                                shutdownTrade = true;
                                break;
                            }
                            case "TRAIDE_WIN":{
                                clearPanel();
                                setCardsTakeOpen(playerM.getListMove());
                                Thread.sleep(1500);
                                clearLabel();
                                shutdownTrade = true;
                                clearPanel();
                                addCards(playerM.getListMove());
                                setCardsSort();
                                youWin=true;
                                break;
                            }
                            case "CAN_MOVE":{
                                canTrade(true);
                                sendMessageMove(" ваша ставка!");
                            break;
                            }
                            case "SELECT_PLAYER":{
                                setSelectLabel(playerM.getValueMove());
                                break;
                            }
                            case "MUST_TRADE":{
                                sendToServer(jLabel1.getText());
                                canTrade(false);
                                break;
                            }
                        }
                    }else {
                        setLabel((String)move);
                    }
                }else{
                        setLabel((String)move);
                        break;
                    }
            }
            shutdownTrade = false;
            //скрыть панель торгов
            jPanel8.setVisible(false);
            
        //сбрасываем 2 карты противникам
        if (youWin) { 
            setLabelTrade("Выберите 2 карты для сноса");   
            canMoveTrade = true;
            for (int i=0; i<2; i++){
                move = in.readObject();
            }
            Thread.sleep(1500);
            clearPanel();
            canMoveTrade = false;//запрещаем сбросить карты
            addCardOpponent();//добавляем по 1 карте(картинке) соперникам
        }
        youWin=false;
            //
            shutdownSubGame=false;
            while (!shutdownSubGame){
                
                //получаем объект
                move = in.readObject();
                //если объект не пустой продолжаем
                if(move!=null){
                    playerMove = (PlayerMove) move;
                    //в соответсвии с типом хода
                    switch(playerMove.getNameMove()){
                        //ход
                        case "MOVE":{
                            setCard(playerMove.getValueMove());
                            removeCardOpponents();
                            countCard++;
                            if (countCard==1){setSuit(playerMove.getValueMove());}
                            break;
                        }
                        case "FOLD":{
                            setCard(playerMove.getValueMove());
                            removeCardOpponents();
                            countCard++;
                            break;
                        }
                        //очки за взятку
                        case "SCORE":{
                            setTableScore(playerMove.getListMove());
                            break;
                        }
                        //очки за кон
                        case "TOTAL_SCORE":{
                            setTableTotalScore(playerMove.getListMove());
                            shutdownSubGame = true;
                            break;
                        }
                        //очистка места для карт
                        case "CLEAN":{
                            clearPanel();
                            break;
                        }
                        case "RESTART":{
                            shutdownSubGame=true;
                            break;
                        }
                        case "CAN_MOVE":{
                            canMove = true;
                            countCard++;
                            sendMessageMove(" ваш ход!");
                            break;
                        }
                        case "RESET":{
                            addCards(playerMove.getListMove());
                            addCardOpponent();//добавляем по 1 карте(картинке) соперникам
                            setCardsSort();
                            break;
                        }
                        case "TRADE_OUT":{
                            setTradeOut(playerMove.getValueMove());
                            break;
                        }         
                        case "MARRIAGE":{
                            setTrumpSuit(playerMove.getValueMove());
                            setCard(playerMove.getValueMove());
                            removeCardOpponents();
                            countCard++;
                            if (countCard==1){setSuit(playerMove.getValueMove());}
                            break;
                        }
                        
                    }
                }
            }
            clearLabelMove();
            clearTableScore();
            clearTrumpSuit();
        }
    }catch(Exception e){
        Logger.getLogger(ClientFrame.class.getName()).log(Level.SEVERE, null, e);
    }
    finally{
        try {
            in.close();
        } catch (Exception ex) {
            Logger.getLogger(ClientFrame.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

//соединение с сервером
public void connect() {
    try {
        //shutdown = true;
        //Устанавливаем соединение с локальным хостом
        ss = new Socket(ip, port); 
        //Получаем потоки ввода_вывода
        in = new ObjectInputStream(ss.getInputStream());
        out = new ObjectOutputStream(ss.getOutputStream());
        sendToServer(tableInfo);
        //Запускаем новый подпроцесс
        Thread process1 = new Thread(this); //Новый подпроцесс выполнения
          process1.start();
        
        JOptionPane.showMessageDialog(null, "Подключение выполнено успешно!");
        initMenuItems(true);
    }
    catch(Exception e){
        JOptionPane.showMessageDialog(null, "Ошибка "+e);
    }
}
 
//боликровка выбора в меню
    public void initMenuItems(boolean f){
        this.jMenuItem1.setEnabled(f);
        this.jMenuItem10.setEnabled(!f);
        
    }

//переключение между игрой и столами
    public void initPanel(boolean f){
        jPanel1.setVisible(!f);
        jPanel2.setVisible(f);
    }
    

    /**
     * Creates new form Client
     */
    public ClientFrame() throws InterruptedException {
        initComponents();
        initMenuItems(false);
        initPanel(false);
        this.listLabel = new ArrayList<>();
        listLabel.add(jLabel2);
        listLabel.add(jLabel3);
        canTrade(false);
        reloadConnect();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jMenuItem6 = new javax.swing.JMenuItem();
        jMenuItem8 = new javax.swing.JMenuItem();
        jMenuItem9 = new javax.swing.JMenuItem();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        jTable4 = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        jMenuItem10 = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        jMenuItem7 = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItem3 = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();
        jMenuItem4 = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenuItem5 = new javax.swing.JMenuItem();

        jMenuItem6.setText("jMenuItem6");

        jMenuItem8.setText("jMenuItem8");

        jMenuItem9.setText("jMenuItem9");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });
        getContentPane().setLayout(new java.awt.CardLayout());

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("Список доступных столов"));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Название стола", "Количество игроков", "Дата"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jButton1.setText("Присоединиться");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1034, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 552, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jButton1)
                .addContainerGap())
        );

        getContentPane().add(jPanel1, "card3");

        jPanel2.setBackground(new java.awt.Color(63, 128, 0));

        jLabel2.setText("          ");

        jLabel3.setText("          ");

        jLabel4.setText("You");
        jLabel4.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabel4MouseClicked(evt);
            }
        });

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Player 1", "Player 2", "Player 3"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable3.setEnabled(false);
        jScrollPane3.setViewportView(jTable3);

        jPanel4.setBackground(new java.awt.Color(63, 128, 0));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 295, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jPanel5.setBackground(new java.awt.Color(63, 128, 0));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 295, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jPanel6.setBackground(new java.awt.Color(63, 128, 0));

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 576, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        jPanel7.setBackground(new java.awt.Color(63, 128, 0));
        jPanel7.setBorder(new javax.swing.border.SoftBevelBorder(0));
        jPanel7.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 9, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 10, Short.MAX_VALUE)
        );

        jPanel8.setBackground(new java.awt.Color(63, 128, 0));
        jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder("Торги"));

        jLabel1.setText("110");

        jButton3.setText("Игра");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("Пас");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setText("+5");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setText("-5");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap(22, Short.MAX_VALUE)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jButton6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jButton5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton6))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(29, 29, 29)
                        .addComponent(jLabel1)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton4)
                .addGap(7, 7, 7))
        );

        jLabel6.setText("                  ");

        jTable4.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null}
            },
            new String [] {
                "Player 1", "Player 2", "Player 3"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable4.setEnabled(false);
        jScrollPane5.setViewportView(jTable4);

        jLabel5.setForeground(new java.awt.Color(255, 0, 0));
        jLabel5.setText("          ");

        jLabel7.setForeground(new java.awt.Color(255, 0, 0));
        jLabel7.setText("          ");

        jLabel8.setForeground(new java.awt.Color(255, 0, 0));
        jLabel8.setText("          ");

        jLabel9.setText("             ");

        jLabel10.setText("              ");

        jLabel11.setText("             ");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel7)
                                .addGap(0, 727, Short.MAX_VALUE))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 175, Short.MAX_VALUE)
                                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 123, Short.MAX_VALUE)
                        .addComponent(jLabel8)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel3))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 281, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel11))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 281, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(20, 20, 20))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel9)
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(493, 493, 493)
                .addComponent(jLabel10)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel2)
                        .addComponent(jLabel7))
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel3)
                        .addComponent(jLabel8)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel11)
                        .addGap(21, 21, 21)
                        .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel9))
                .addGap(16, 16, 16))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel6)
                .addGap(51, 51, 51))
        );

        getContentPane().add(jPanel2, "card4");

        jMenu1.setText("Игра");

        jMenuItem1.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, 0));
        jMenuItem1.setText("Новая игра");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);
        jMenu1.add(jSeparator3);

        jMenuItem10.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, 0));
        jMenuItem10.setText("Подключиться к серверу");
        jMenuItem10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem10ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem10);
        jMenu1.add(jSeparator4);

        jMenuItem7.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, 0));
        jMenuItem7.setText("Настройки подключения");
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem7);
        jMenu1.add(jSeparator1);

        jMenuItem3.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_E, 0));
        jMenuItem3.setText("Выйти");
        jMenuItem3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Справка");

        jMenuItem4.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F1, 0));
        jMenuItem4.setText("Справка");
        jMenuItem4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem4);
        jMenu2.add(jSeparator2);

        jMenuItem5.setText("О программе");
        jMenuItem5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem5);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
        ConnectionFrame connectionFrame = new ConnectionFrame();
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
               connectionFrame.setVisible(true);
                
            }
        });
        connectionFrame.setSettings(this);
    }//GEN-LAST:event_jMenuItem7ActionPerformed

    //создаем стол    
    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        try {
            shutdownTables=true;
            tableInfo = new TableInfo();
            //Получаем строку от пользователя и пересылаем серверу
            Calendar cal = new GregorianCalendar();
            tableInfo.tableDate = new SimpleDateFormat("dd.MM.yyyy HH:mm").format(cal.getTime());
            tableInfo.tableName = JOptionPane.showInputDialog("Название стола");
            tableInfo.tablePlayers = 1;
            tableInfo.totalTablePlayers = 3;
            sendToServer(tableInfo);
            initPanel(true);
        }
    catch(Exception e){e.getMessage();}
        
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem10ActionPerformed
        connect();//вызов соединения с сервером
    }//GEN-LAST:event_jMenuItem10ActionPerformed

    //выбор стола    
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try {
            shutdownTables=true;
            tableInfo = new TableInfo();
            int selectLine;
            //Получаем строку от пользователя и пересылаем серверу
            selectLine = jTable1.getSelectedRow();
            tableInfo.tableName = jTable1.getValueAt(selectLine, 0).toString();
            tableInfo.totalTablePlayers = 0; 
            sendToServer(tableInfo);
            initPanel(true);
        }catch(Exception e){e.getMessage();}
    }//GEN-LAST:event_jButton1ActionPerformed

    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened
        
    }//GEN-LAST:event_formWindowOpened
    
    
    private void jLabel4MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabel4MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_jLabel4MouseClicked

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        //увеличиваю ставку на 5
        if(Integer.parseInt(jLabel1.getText())<maxRate)
        jLabel1.setText(Integer.toString(Integer.parseInt(jLabel1.getText())+5));
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
    //уменьшаю ставку на 5
        if(Integer.parseInt(jLabel1.getText())>100){
            jLabel1.setText(Integer.toString(Integer.parseInt(jLabel1.getText())-5));
        }
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        sendToServer(jLabel1.getText());
        jLabel5.setText(jLabel1.getText());
        sendMessageMove("");
        canTrade(false);
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        sendToServer("ПАС");
        jLabel5.setText("ПАС");
        sendMessageMove("");
        canTrade(false);
        setVisibleTradePanel(false);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jMenuItem4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new HelpDialog(null,true).setVisible(true);
            }
        });
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AboutDialog(null,true).setVisible(true);
            }
        });
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ClientFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ClientFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ClientFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ClientFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new ClientFrame().setVisible(true);
                } catch (InterruptedException ex) {
                    Logger.getLogger(ClientFrame.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem10;
    private javax.swing.JMenuItem jMenuItem3;
    private javax.swing.JMenuItem jMenuItem4;
    private javax.swing.JMenuItem jMenuItem5;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem8;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable3;
    private javax.swing.JTable jTable4;
    // End of variables declaration//GEN-END:variables
}
