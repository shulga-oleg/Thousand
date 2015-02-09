/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Log {
        private FileWriter writeFile = null;
        private File logFile = new File("error.txt");
        
    public void writeLog(String str){
        try {
        //Если требуемого файла не существует.
        if(!logFile.exists()) {
            //Создаем его.
            logFile.createNewFile();
        }    
        writeFile = new FileWriter(logFile, true);
        writeFile.append(str+"\n");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(writeFile != null) {
            try {
                writeFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            }
        }
    }
}
