package com.topband.opencvtest.common;

import org.opencv.core.Mat;
import org.opencv.face.FaceRecognizer;
import org.opencv.face.LBPHFaceRecognizer;

import javax.swing.*;
import java.io.File;


public class Recognition {



    public final static String BASE_FILE_PATH = "D:\\Documents\\pic\\model\\";

    public final static String BASE_FILE_NAME = "face_model.yml";



    public static void testetect(){

//通过模型存放的文件名来获取用户对应的模型文件

        String readPath = BASE_FILE_PATH  + BASE_FILE_NAME;
        System.out.println("file path---------->" + readPath);
        File testFile = new File(readPath);
        if(!testFile .exists()) {
            JOptionPane.showMessageDialog(null,"模型文件不存在！请确认操作是否正确！","提示", JOptionPane.ERROR_MESSAGE);
            System.out.println("file path---------->模型文件不存在！请确认操作是否正确！");
        }
        FaceRecognizer faceRecognizer = LBPHFaceRecognizer.create();
        faceRecognizer.read(readPath);
        Mat  img_mat = FaceUtils.getFaceAndConv("D:\\Documents\\pic\\lulu.jpg");
        detectAndDisplay(img_mat, faceRecognizer);

    }

    public static int detectAndDisplay(Mat target, FaceRecognizer faceRecognizer) {


        int[] label = new int[1];
        double[] confidences = new double[1];
        faceRecognizer.predict(target, label, confidences);
        String name = faceRecognizer.getLabelInfo(label[0]);
        System.out.println("detectAndDisplay name:"+name);
        System.out.println("detectAndDisplay label="+label[0]+"可信度confidence:"+confidences[0]);
        return  label[0];

    }




}
