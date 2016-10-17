/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package snakesladders;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.ImageIcon;

/**
 *
 * @author Maxsim Goratiev
 */
@SuppressWarnings("serial")
class MirrorImageIcon extends ImageIcon {

private double scale,divider;
private int mirror, orientation;

     /**
     * This method is an adapter, in accordance with adapter pattern.
     *it parces the input coordinates and outputs coordinates corrected 
     * scaling and orientation of the icon.
      *@param  x is the x coordinate left boundary of the image bust be 
      *@return X coordinate where left boundary of the image is, for use by super.paintIcon
       */
    public MirrorImageIcon(String filename, boolean left_facing, int scaler) {
    	super(filename);
        if(left_facing!=true)
            orientation=-1;
        else
            orientation=1;
        divider=scaler;
    }                                                                                                                                         
    
   
        /*paints image at designated coordinates, and scales it to take 1/10 of the board in width and high  */
        public synchronized void paintIcon(Component c, Graphics2D g, int x, int y, int flipped) {
    	
            mirror=flipped*orientation;
            Graphics2D g2 = (Graphics2D)g.create();
            scale=(((double)c.getWidth()/divider))/(double)(this.getIconWidth());
            g2.scale(mirror*scale, scale);
            super.paintIcon(c, g2,xParse(x), yParse(y));
    }
    

        /**
         * This method is an adapter, in accordance with adapter pattern.
         *it parces the input coordinates and outputs coordinates corrected 
         * scaling and orientation of the icon.
         *@param  x is the x coordinate left boundary of the image bust be 
         *@return X coordinate where left boundary of the image is, for use by super.paintIcon
         */
        private int xParse(int x){
            int mirror_correction;
            if(mirror!=-1)
                mirror_correction =x+(int) (-this.getIconWidth()*scale);
            else    
                mirror_correction=x;
            int realX=mirror*(int)(mirror_correction/scale);
            return realX;
        }
        
         /**
         * This method is an adapter
         *it parses the input coordinates and outputs coordinates corrected 
         * for scaling.
         *@param  y is where the lower boundary of the image must be according to superclass
         *@return y  coordinate where lower boundary of the image is, for use by super.paintIcon
         */
         private int yParse (int y){
                    int realY=(int)((y-(this.getIconHeight()*scale))/scale);
                    return realY;
         }
    
}