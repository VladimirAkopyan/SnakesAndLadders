/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package snakesladders;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

/**
 *
 * @author Maxsim Goratiev
 */
public class BoardJpanel extends javax.swing.JPanel implements Runnable{

    /**
     * Creates new form BoardJpanel
     */
    
     /*this array contains all transitions of snakes and ladders present on the board, not modifiable*/
    private final static long[][] snakesladders ={{6,11,14,21,24,31,35,44,51,56,62,64,73,78,84,91,95,99},
    {16,49,4,60,87,9,54,9,26,67,53,19,92,100,28,71,75,8}};
    private Image board=null;
    private int xs;
    private int ys;
    private static volatile BoardJpanel start_moving;
    private volatile static double[] player_motion = new double [5];
    MirrorImageIcon ship=new MirrorImageIcon("ship.gif", true,10);
    MirrorImageIcon rocket=new MirrorImageIcon("rocket.gif", false,10);
    MirrorImageIcon train=new MirrorImageIcon("train.gif", true,9);
    MirrorImageIcon car=new MirrorImageIcon("Car.gif", true,12);
    MirrorImageIcon tractor=new MirrorImageIcon("Tractor.gif", false,13);
    
    
    public BoardJpanel() {

        initComponents();
        setToolTipText("The game Board");
        ImageIcon BackGroundImage=new ImageIcon("board.png");
        board=BackGroundImage.getImage();
        start_moving=this;
    }
    
    @Override
     public synchronized void paint(Graphics g){
          this.xs = (int)getWidth();
          this.ys = (int)getHeight();
          Graphics2D teh_board = (Graphics2D)g.create();
          RenderingHints rh = new RenderingHints(
          RenderingHints.KEY_RENDERING,
          RenderingHints.VALUE_RENDER_QUALITY);
          teh_board.setRenderingHints(rh);
          teh_board.drawImage(board, 0, 0, xs, ys, this);
          
          position(teh_board,SnakesLaddersGUI.player_locations.get(0),ship);
          position(teh_board,SnakesLaddersGUI.player_locations.get(1),car);
          position(teh_board,SnakesLaddersGUI.player_locations.get(2),train);
          position(teh_board,SnakesLaddersGUI.player_locations.get(3),rocket); 
          position(teh_board,SnakesLaddersGUI.player_locations.get(4),tractor);             
     }
    
    private void position(Graphics2D g, int inputPosition,MirrorImageIcon peg ){
         double i= (double)(inputPosition)/10;
         int mirrored,Xposition,Yposition,row=(int)( 11-i/10);
         
         double column= i%10;
         double Ydivision= ((ys*0.99)/10);
         double Xdivision= ((xs*0.99)/10);       
         if(row%2==0){
            mirrored=-1;
            if(column!=0)
                Xposition=(int)(column*Xdivision);
             else 
             {
                Xposition=(int)(xs);
             }
         }
         else{
             mirrored=1; 
             if(column!=0)
                Xposition=(int)(xs-column*Xdivision+Xdivision);
             else 
             {
                Xposition=(int) Xdivision;
             }
             
         }
         
         Yposition=(int)(row*Ydivision); 
         
         peg.paintIcon(this, g,Xposition+1,Yposition,mirrored);
     }
    
    @Override
    public synchronized void  run() {
        while((player_motion[0]+player_motion[1]+player_motion[2]+player_motion[3]+player_motion[4])>0.5)
         {
             for(int i=0;i<5;i++){
                 while(player_motion[i]>0.1){
                    player_motion[i]-=0.1;
                    SnakesLaddersGUI.player_locations.getAndAdd(i,1);
                    try {
                        wait(15);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(BoardJpanel.class.getName()).log(Level.SEVERE, null, ex);
                    }  
                 } 
                player_motion[i]=0; }
         }
         for(int i=0;i<5;i++)
             moveCheck(i);
    }
     
    
      
   private synchronized void moveCheck(int Player_number)
   {
       for (int i=0;i<18;i++)
           if((SnakesLaddersGUI.player_locations.get(Player_number)/10)==(int)snakesladders[0][i])
              SnakesLaddersGUI.player_locations.set(Player_number,(int)(snakesladders[1][i]*10));
   }
   
    public synchronized static String move_player(int Player_number, int Player_rolled)
   {
       int futurepostion;
       futurepostion=1;
       futurepostion= SnakesLaddersGUI.player_locations.get(Player_number)/10;
       futurepostion+=Player_rolled;
       if((futurepostion)<101)
       {    player_motion[Player_number]=Player_rolled;
            SnakesLaddersGUI.ThreadPool.execute(start_moving);
            
            return("rolled" +" "+   Integer.toString(Player_rolled));
       }
       else
            return("rolled" +" "+   Integer.toString(Player_rolled));
   }

    
    /**
     * This method is called from within the constructor to initialize the form. WARNING: Do NOT modify this code. The content of this
     * method is always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
