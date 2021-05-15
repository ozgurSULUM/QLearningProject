
package qlearningproject;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JPanel;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Arrays;

public class Board {
    private JPanel panel;
    private int column_row_number;
    private int cornerLengthY;
    private int cornerLengthX;
    
    private int state_number;
     private int obstacle_rate;
    private int[] obstacleStates;
    
    private int[][] R_matrisi;
    private double[][] Q_matrisi;
    private double y;
    private int normal_reward;
    private int hole_reward;
    private int target_reward;
    
    private Koordinat start;
    private Koordinat target;
    private Koordinat playerPosition;
    
    private Color startColor;
    private Color targetColor;
    private Color playerColor;
    private Color obstacleColor;
    
    
    
    private FileWriter filewriter;
    private Random randomGenerator;
    private Graphics graphics;
    
    public Board(JPanel panel, int column_row_number, int obstacle_rate,Koordinat start,Koordinat target) {
       
        this.normal_reward = 0;
        this.hole_reward = -25;
        this.target_reward = 100;
        this.y =  0.9;
        this.startColor = new Color(0,0,255,50);
        this.targetColor = new Color(0,255,0,50);
        this.obstacleColor = new Color(240,14,14,50);
        this.playerColor = Color.BLUE;
        
        this.panel = panel;
        this.column_row_number = column_row_number;
        this.obstacle_rate = obstacle_rate;
        this.graphics = panel.getGraphics();
        this.state_number = column_row_number * column_row_number;
        this.start = start;
        this.target = target;
        System.out.println("üçlü değer");
        System.out.println(target.getState());
        System.out.println(target.getX());
        System.out.println(target.getY());
        this.playerPosition = start;
        this.randomGenerator = new Random();
        try {
            filewriter = new FileWriter(new File("engel.txt"),false);
        } catch (IOException ex) {
            Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        createBoard();
        createR();
        createObstacles();
        startQLearning();
        //print2D(R_matrisi);
    }
    
    private void createBoard(){
        int maxCornerLength1 = (panel.getWidth() / column_row_number);
        int maxCornerLength2 = (panel.getHeight() / column_row_number);
        int cornerLength=0;
        if(maxCornerLength1 < maxCornerLength2){
            cornerLength = maxCornerLength1;
        }
        else if(maxCornerLength2 < maxCornerLength1){
            cornerLength = maxCornerLength2;
        }
        else{
            cornerLength = maxCornerLength1;
        }
        
        this.cornerLengthX = cornerLength+10;
        this.cornerLengthY = cornerLength-1;
        
        graphics.setColor(Color.BLACK);
        int tmpx=0;
        int tmpy=0;
        for(int i = column_row_number;i>=0;i--){
            graphics.drawLine(tmpx, tmpy, tmpx+(cornerLengthX*column_row_number), tmpy);
            tmpy= tmpy+cornerLengthY;
        }
        
        tmpx=0;
        tmpy=0;
        for(int i = column_row_number;i>=0;i--){
            graphics.drawLine(tmpx, tmpy, tmpx, tmpy+(cornerLengthY*column_row_number));
            tmpx= tmpx+cornerLengthX;
        }
        
        //başlangıç ve hedef noktaları tablo üzerinde gösterilir. player tabloya konulur
        graphics.setColor(startColor);
        graphics.fillRect((start.getX()*cornerLengthX)+1,(start.getY()*cornerLengthY)+1,cornerLengthX-1,cornerLengthY-1);
        //graphics.clearRect((start.getX()*cornerLengthX)+1,(start.getY()*cornerLengthY)+1,cornerLengthX-1,cornerLengthY-1);
        
        graphics.setColor(targetColor);
        graphics.fillRect((target.getX()*cornerLengthX)+1,(target.getY()*cornerLengthY)+1,cornerLengthX-1,cornerLengthY-1);
        
    }
    
    
    private void createR(){
        //matrisi oluşturduk
        R_matrisi = new int[state_number][state_number];
        for(int i = 0; i<state_number; i++){
            for(int j = 0; j<state_number; j++){
                R_matrisi[i][j] = normal_reward;
            }
        }
        //bir kutudan diğer kutuya geçebilme durumlarını 
        for(int i = 0; i<state_number; i++){
            for(int j = 0; j<state_number; j++){
                //if'lerin ayrı yazılmasının sebebi solkenardan bir önceki durumun sağüst kenar olma olasılığı ve
                //sağkenardan bir sonraki durumun solaltkenar olma olasılığıdır ve bu durumlar birbirleriye komşu olamazlar.
                
                //solkenar
                if(i%column_row_number == 0){
                    //solkenardaki blokların komşuları(sağ,sol,yukarı,aşağı,çaprazlar) haricideki bloklar -1
                    if((i+1 != j)&&(i+column_row_number != j)&&(i+(column_row_number+1) != j)&&(i-(column_row_number-1) != j)&&(i-column_row_number != j))
                        R_matrisi[i][j] = -1;
                }//sağkenar
                else if((i+1)%column_row_number == 0){
                    //sağkenardaki blokların komşuları(sağ,sol,yukarı,aşağı,çaprazlar) haricindeki bloklar -1
                    if((i-1 != j)&&(i+column_row_number != j)&& (i+(column_row_number-1) != j)&&(i-(column_row_number+1) != j)&& (i-column_row_number != j))
                        R_matrisi[i][j] = -1;
                }//orta
                else{
                    //ortadaki blokların komşuları(sağ,sol,yukarı,aşağı,çaprazlar) haricindeki bloklar -1
                    if((i-1 != j)&&(i+1 != j)&&(i+column_row_number != j)&& (i+column_row_number-1 != j)&&(i+column_row_number+1 != j)&&(i-column_row_number+1 != j)&& (i-column_row_number != j)&&(i-column_row_number-1 != j))
                        R_matrisi[i][j] = -1;
                }
                
            }
        }
        //targeta 20 değeri verilir.
        for(int i = 0; i<state_number; i++){
            for(int j = 0; j<state_number; j++){
                if((R_matrisi[i][j] != -1)&&(j == target.getState())){
                    R_matrisi[i][j] = target_reward;
                }
            }
        } 
    }
    
    
    private void createObstacles(){
        int obstacle_number = state_number * obstacle_rate / 100;
        this.obstacleStates = new int[obstacle_number];
        System.out.println(obstacle_number);
        
        graphics.setColor(obstacleColor);
        int i = 0;
        while(i<obstacle_number){
            int random = randomGenerator.nextInt(state_number);
            if((random == start.getState())||(random == target.getState())){
                continue;
            }
            boolean isObstacleExists = false;
            for(int obstacle:obstacleStates){
                if(obstacle == random){
                    isObstacleExists = true;
                    break;
                }
            }
            if(isObstacleExists){
                continue;
            }
            
            //kazanç matrisinde eğer j state'inden gidilebilecek bir state ise değeri hole_reward yapılır.
            for(int j = 0;j<state_number;j++){
                if(R_matrisi[j][random] == normal_reward){
                    R_matrisi[j][random]= hole_reward; 
                }
            }
            obstacleStates[i]=random;
            //x = column , y = row;
            drawObstacle(new Koordinat(random%column_row_number,random/column_row_number));
            i++;
        }
        try {
            filewriter.close();
        } catch (IOException ex) {
            Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
    
    private void drawObstacle(Koordinat koordinat){
        graphics.fillRect((koordinat.getX()*cornerLengthX),(koordinat.getY()*cornerLengthY),cornerLengthX,cornerLengthY);
        try {
           filewriter.write(koordinat.toString());
            
        } catch (IOException ex) {
            Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    private void startQLearning(){
        //Q matrisi oluşturulur.
        this.Q_matrisi = new double[state_number][state_number];
        for(int i = 0; i<state_number;i++){
            for(int j = 0; j<state_number; j++){
                Q_matrisi[i][j] = normal_reward;
            }
        }
        
        
        int episode = 0;
        int episode_length = 1000;
        for(;episode <episode_length;episode++){
            System.out.println(episode);
            //her iterasyonda border olmayan rastgele bir state üzerinden iterasyona başlanır.
            int randomPosition = randomGenerator.nextInt(state_number);
            boolean isObstacle0 = false;
            for(int obstacle: obstacleStates){
                if(obstacle == randomPosition){
                    isObstacle0 = true;
                }
            }
            if(isObstacle0){
                continue;
            }
            changePlayerPosition(new Koordinat(randomPosition%column_row_number,randomPosition/column_row_number));
            
            /*
            if((isTarget == false) && (episode_length>99990)){
                episode_length +=50000;
            }
            */
            while(true){
                
                try {
                    Thread.sleep(50);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                //playerin geçebileceği stateleri bulunur.
                int[] playerR = R_matrisi[playerPosition.getState()];
                ArrayList<Integer> statesFromPlayer = new ArrayList<>();
                for(int i = 0; i<playerR.length;i++){
                    if(playerR[i] != -1){
                        statesFromPlayer.add(i);
                    }
                }
                
                int nextState = QmaksState(playerPosition.getState(),statesFromPlayer);
                
               /*
                //currentState üzerinden random geçilecek state seçilir.
                int randomStateNumber = randomGenerator.nextInt(statesFromPlayer.size());
                int nextState = statesFromPlayer.get(randomStateNumber);
                
                ArrayList<Integer> statesFromRandom = new ArrayList<>();
                int[] randomR = R_matrisi[nextState];
                for(int i = 0; i<randomR.length;i++){
                    if(randomR[i] != -1){
                        statesFromRandom.add(i);
                    }
                }
                
                
                //statesFromRandom arraylisti içerisindeki statelerden en yüksek Q matrisi değerine sahip olan geçiş seçilir.
                double Q_maks = Q_matrisi[nextState][statesFromRandom.get(0)];
                for(int state: statesFromRandom){
                    if(Q_maks<Q_matrisi[nextState][state]){
                        Q_maks = Q_matrisi[nextState][state]; 
                    }
                }
               */
                
                
                Q_matrisi[playerPosition.getState()][nextState] =  R_matrisi[playerPosition.getState()][nextState] + y*findMaksQ(nextState);
                changePlayerPosition(new Koordinat(nextState%column_row_number,nextState/column_row_number));

                //eğer target'a vardıysak
                if(playerPosition.isEqual(target)){
                    break;
                }

                //eğer bir obstacle'a çarparsa başa döner gidilen stateler listesi temizlenir.
                boolean isObstacle = false;
                for(int obstacle:obstacleStates){
                    if(obstacle == nextState){
                        isObstacle = true;
                    }
                }

                if(isObstacle){
                    break;
                }


            }
        }
        System.out.println("iterasyon bitti.");
        
        //Daha sonra başlangıç noktasından başlanarak. maks maliyetli yol çizdirilir.
        drawPath();
        
        
    }
    
    private void changePlayerPosition(Koordinat targetPosition){
        if(playerPosition.isEqual(targetPosition)){
           return; 
        }
        
        graphics.clearRect((playerPosition.getX()*cornerLengthX)+cornerLengthX/3,(playerPosition.getY()*cornerLengthY)+cornerLengthY/3,cornerLengthX/3,cornerLengthY/2);
        boolean isFilled = false;
        for(int obstacle: obstacleStates){
            if(playerPosition.getState() == obstacle){
                graphics.setColor(obstacleColor);
                graphics.fillRect((playerPosition.getX()*cornerLengthX)+cornerLengthX/3,(playerPosition.getY()*cornerLengthY)+cornerLengthY/3,cornerLengthX/3,cornerLengthY/2);
                isFilled = true;
                break;
                     
            }
        }
        if(isFilled == false){
            if(playerPosition.getState() == start.getState()){
                graphics.setColor(startColor);
                graphics.fillRect((playerPosition.getX()*cornerLengthX)+cornerLengthX/3,(playerPosition.getY()*cornerLengthY)+cornerLengthY/3,cornerLengthX/3,cornerLengthY/2);
            }
            else if(playerPosition.getState() == target.getState()){
                graphics.setColor(targetColor);
                graphics.fillRect((playerPosition.getX()*cornerLengthX)+cornerLengthX/3,(playerPosition.getY()*cornerLengthY)+cornerLengthY/3,cornerLengthX/3,cornerLengthY/2);
            }
        }
        
        graphics.setColor(playerColor);
        graphics.fillRect((targetPosition.getX()*cornerLengthX)+cornerLengthX/3,(targetPosition.getY()*cornerLengthY)+cornerLengthY/3,cornerLengthX/3,cornerLengthY/2);
        
        playerPosition = targetPosition;
    }
    
    private int QmaksState(int playerState,ArrayList<Integer> statesFromPlayer){
        double[] q_values = new double[statesFromPlayer.size()];
        for(int i = 0; i<statesFromPlayer.size();i++){
            q_values[i] = R_matrisi[playerState][statesFromPlayer.get(i)] + y*findMaksQ(statesFromPlayer.get(i));
        }
        
        ArrayList<Integer> maksStates = new ArrayList<>();
        maksStates.add(0);
        double maksQ = q_values[maksStates.get(0)];
        for(int i = 0;i<q_values.length;i++){
            if(maksQ < q_values[i]){
                maksStates.clear();
                maksStates.add(i);
                maksQ = q_values[i];
            }
            else if( maksQ == q_values[i]){
                maksStates.add(i);
            }
        }
        int k = 0;
        for(Integer s1:statesFromPlayer){
            for(int obstacle: obstacleStates){
                if(obstacle == s1){
                    maksStates.add(k);
                }
            }
            k++;
        }
        if(maksStates.size() > 1){
            int randomNumber = randomGenerator.nextInt(maksStates.size());
            return statesFromPlayer.get(maksStates.get(randomNumber));
        }else{
            return statesFromPlayer.get(maksStates.get(0));
        }
    }
    
    private double findMaksQ(int state){
        //state üzerinden geçilebilecek state'ler bulunur.
        int[] playerR = R_matrisi[state];
        ArrayList<Integer> statesFromState = new ArrayList<>();
        for(int i = 0; i<playerR.length;i++){
            if(playerR[i] != -1){
                statesFromState.add(i);
            }
        }
        
        ArrayList<Double> qMaks = new ArrayList<>();
        qMaks.add(Q_matrisi[state][statesFromState.get(0)]);
        for(Integer state0: statesFromState){
            if(qMaks.get(0)<Q_matrisi[state][state0]){
                qMaks.clear();
                qMaks.add(Q_matrisi[state][state0]); 
            }
            else if(qMaks.get(0) == Q_matrisi[state][state0]){
                qMaks.add(Q_matrisi[state][state0]);
            }
        }
        if(qMaks.size() > 1){
            int randomNumber = randomGenerator.nextInt(qMaks.size());
            return qMaks.get(randomNumber);
        }else{
            return qMaks.get(0);
        }
        
    }
    
    private void drawPath(){
        //oyunca başa alınır.
        changePlayerPosition(start);
        boolean isTarget = false;
        while(!isTarget){
            try {
                    Thread.sleep(50);
                } catch (InterruptedException ex) {
                    Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
                }
            System.out.println("içinde");
            //oyuncunun gidebileceği stateler bulunur.
            int[] playerR = R_matrisi[playerPosition.getState()];
            ArrayList<Integer> statesFromPlayer = new ArrayList<>();
            for(int i = 0; i<playerR.length;i++){
                if((playerR[i] != -1)&&(playerR[i] != hole_reward)){
                    statesFromPlayer.add(i);
                }
            }
            
            double[] playerQ = Q_matrisi[playerPosition.getState()];
            double maks_q = playerQ[statesFromPlayer.get(0)];
            int nextState = statesFromPlayer.get(0);

            for(Integer state : statesFromPlayer ){
                if( playerQ[state] > maks_q){
                    maks_q = playerQ[state];
                    nextState = state;
                }
            }

            changePlayerPosition(new Koordinat(nextState%column_row_number,nextState/column_row_number));

            if(playerPosition == target){
                isTarget = true;
                System.out.println("hedefe varıldı.");
            }
        }
    }
    
    public static void print2D(int mat[][])
    {
        // Loop through all rows
        int kontrol = 0;
        for (int[] row : mat){
            
            // converting each row as string
            // and then printing in a separate line
            if(kontrol > 20){
                break;
            }
            System.out.println("block_değeri:"+kontrol);
            System.out.println(Arrays.toString(row));
            kontrol++;
            
          
        }
        
              
    }
    
}
