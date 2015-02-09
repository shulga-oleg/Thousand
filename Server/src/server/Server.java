/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import static java.awt.SystemColor.control;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Server extends javax.swing.JFrame {
    /**
     * Creates new form Server
     */
    
    private static Thread t;
    public Server() {
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            boolean shutdown = false;
            Scanner sc = new Scanner(System.in);
            Control control = new Control();
            System.out.println("Напечатайте команду \"start\" для запуска сервера или \"stop\" для остановки сервера или \"exit\" для выхода из программы");
            while (!shutdown){
                String s = sc.nextLine();
                switch(s){
                    case "start":{
                        t = new Thread(control);
                        t.start();
                        System.out.println("Сервер запущен");
                        break;
                    }
                    case "stop":{
                        control.setShutDown(true);
                        System.out.println("Сервер остановлен");
                        break;
                    }
                    case "exit":{
                        System.out.println("Завершение работы сервера");
                        Thread.sleep(3000);
                        shutdown = true;
                        break;
                    }
                    default:  System.out.println("Команда введена неверно, введите еще раз. ");
                }
            }
        } catch (FileNotFoundException | InterruptedException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            Control.shutdown = true;
            System.exit(0);
        }
    }
}
