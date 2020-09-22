package com.topband.opencvtest.common;

import javax.swing.*;
import java.awt.image.BufferedImage;

/**
 * @author ludi
 * @version 1.0
 * @date 2020/9/21 15:48
 * @remark
 */
public class Myframe extends JFrame {
    public Myframe(){
        this.setDefaultCloseOperation(3);
        this.setBounds(0,0,800,800);
        this.setSize(800,800);
        this.setVisible(true);
    }
    public void draw(BufferedImage img){
        while(true){
            this.getGraphics().drawImage(img,100,100,img.getWidth(),img.getHeight(),null);
        }
    }
}
