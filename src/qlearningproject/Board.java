
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
    private int row_number;
    private int column_number;
    private int obstacle_rate;
    private int cornerLengthY;
    private int cornerLengthX;
    private int state_number;
    private int[][] R_matrisi;
    private int[] obstacle_matrisi;
    private Koordinat start;
    private Koordinat target;
    private FileWriter filewriter;
    Graphics graphics;
    public Board(JPanel panel, int row_number, int column_number, int obstacle_rate,Koordinat start,Koordinat target) {
        this.panel = panel;
        this.row_number = row_number;
        this.column_number = column_number;
        this.obstacle_rate = obstacle_rate;
        this.graphics = panel.getGraphics();
        this.state_number = row_number * column_number;
        this.start = start;
        this.target = target;
        try {
            filewriter = new FileWriter(new File("engel.txt"),false);
        } catch (IOException ex) {
            Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
        }
        createR();
        createBoard();
        createObstacles();
        //createPlayerNTarget();
        print2D(R_matrisi);
        
    }
    
    private void createBoard(){
        //karelerin kenar uzunluğu belirlenir.
        //row = y koordinatı
        //column = x koordinatı
        int maxCornerLength1 = (panel.getWidth() / row_number);
        int maxCornerLength2 = (panel.getHeight() / column_number);
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
        //System.out.println(cornerLength);
        this.cornerLengthX = cornerLength+10;
        this.cornerLengthY = cornerLength;
        
        graphics.setColor(Color.BLACK);
        int tmpx=0;
        int tmpy=0;
        for(int i = row_number;i>=0;i--){
            graphics.drawLine(tmpx, tmpy, tmpx+(cornerLengthX*column_number), tmpy);
            tmpy= tmpy+cornerLengthY;
        }
        
        tmpx=0;
        tmpy=0;
        for(int i = column_number;i>=0;i--){
            graphics.drawLine(tmpx, tmpy, tmpx, tmpy+(cornerLengthY*row_number));
            tmpx= tmpx+cornerLengthX;
        }
        graphics.setColor(Color.RED);
        Font font = new Font("Yu Gothic Medium", Font.PLAIN,(2*cornerLengthY/3)+1);
        graphics.setFont(font);
        
        for(int i = 0;i<row_number;i++){
            for(int j = 0;j<column_number;j++){
                graphics.drawString(String.valueOf((i*50)+j), (j * cornerLengthX), (i * cornerLengthY)+cornerLengthY);
            }
        }
        
    }
    
    private void createObstacles(){
        
        int unit_square_number = row_number * column_number;
        int obstacle_number = unit_square_number * obstacle_rate / 100;
        obstacle_matrisi = new int[obstacle_number];
        System.out.println(obstacle_number);
        Random randomGenerator = new Random();
        
        graphics.setColor(new Color(240,14,14,50));
        int i = 0;
        while(i<obstacle_number){
            
            int random = randomGenerator.nextInt(state_number);
            boolean isObstacleExists = false;
            for(int obstacle:obstacle_matrisi){
                if(obstacle == random){
                    isObstacleExists = true;
                    break;
                }
            }
            if(isObstacleExists){
                continue;
            }
            //kazanç matrisinde eğer j state'inden gidilebilecek bir state ise değeri -5 yapılır.
            
            for(int j = 0;j<state_number;j++){
                if(R_matrisi[j][random] == 3){
                    R_matrisi[j][random]=-5; 
                }
            }
            obstacle_matrisi[i]=random;
            //x = column , y = row;
            drawObstacle(new Koordinat(random%50,random/50));
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
    
    private void createR(){
        //matrisi oluşturduk
        R_matrisi = new int[state_number][state_number];
        for(int i = 0; i<state_number; i++){
            for(int j = 0; j<state_number; j++){
                R_matrisi[i][j] = 3;
            }
        }
        //bir kutudan diğer kutuya geçebilme durumlarını 
        for(int i = 0; i<state_number; i++){
            for(int j = 0; j<state_number; j++){
                //if'lerin ayrı yazılmasının sebebi solkenardan bir önceki durumun sağüst kenar olma olasılığı ve
                //sağkenardan bir sonraki durumun solaltkenar olma olasılığıdır ve bu durumlar birbirleriye komşu olamazlar.
                
                //solkenar
                if(i%50 == 0){
                    //solkenardaki blokların komşuları(sağ,sol,yukarı,aşağı,çaprazlar) haricideki bloklar -1
                    if((i+1 != j)&&(i+50 != j)&&(i+(51) != j)&&(i-(49) != j)&&(i-50 != j))
                        R_matrisi[i][j] = -1;
                }//sağkenar
                else if((i+1)%50 == 0){
                    //sağkenardaki blokların komşuları(sağ,sol,yukarı,aşağı,çaprazlar) haricindeki bloklar -1
                    if((i-1 != j)&&(i+50 != j)&& (i+(49) != j)&&(i-(51) != j)&& (i-50 != j))
                        R_matrisi[i][j] = -1;
                }//orta
                else{
                    //ortadaki blokların komşuları(sağ,sol,yukarı,aşağı,çaprazlar) haricindeki bloklar -1
                    if((i-1 != j)&&(i+1 != j)&&(i+50 != j)&& (i+49 != j)&&(i+51 != j)&&(i-51 != j)&& (i-50 != j)&&(i-49 != j))
                        R_matrisi[i][j] = -1;
                }
                
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
