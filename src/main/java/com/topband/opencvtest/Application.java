package com.topband.opencvtest;


import com.topband.opencvtest.common.*;
import org.opencv.core.*;
import org.opencv.features2d.ORB;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * @author ludi
 * @version 1.0
 * @date 2020/9/21 11:01
 * @remark
 */
public class Application {



    static {
        //直接下载window版，解压，使用里边的dll
        //linux 需要下载源码，生成so库 https://www.52pojie.cn/thread-872736-1-1.html
        System.load(FileUtil.getAppicationPath()+ File.separator +"libs\\opencv_java410.dll");
        System.out.println("opencv\t" + Core.VERSION);
    }

    public static void main(String[] args) {
        System.out.println("Welcome to OpenCV " + Core.VERSION);
        Mat m = new Mat(5, 10, CvType.CV_8UC1, new Scalar(0));
        System.out.println("OpenCV Mat: " + m);
        Mat mr1 = m.row(1);
        mr1.setTo(new Scalar(1));
        Mat mc5 = m.col(5);
        mc5.setTo(new Scalar(5));
        System.out.println("OpenCV Mat data:\n" + m.dump());
//        try {
//            TrainUtil.train("D:\\Documents\\pic\\imagedb","D:\\Documents\\pic\\model");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
     Recognition.testetect();
    }

    public static void cutFace() {
       // String sour =  FileUtil.getResourceAbsolutePath("x3.jfif");
        //String des =  FileUtil.getAppicationPath()+ File.separator +"x3.jpg";
        String sour = "D:\\Documents\\pic\\lulu.jpg";
        String des =  "D:\\Documents\\pic\\ld.jpg";

        boolean res = FaceUtils.detectFaceAndCut(sour, des);
        System.out.println("cutFace:" + res);

    }

    public static void contrast3() {
        String sour ="D:\\Documents\\pic\\x3.jfif";
        String des = "D:\\Documents\\pic\\x3.jpg";
        Mat   src = FaceUtils.bufImg2Mat(FaceUtils.loadImage(sour));


        MatOfKeyPoint keypoints = new MatOfKeyPoint();
        ORB detector = ORB.create();
        detector.detect(src, keypoints);

        HighGui gui = new HighGui();
        gui.imshow("哈妮", src);
        gui.waitKey(1000);
    }
    public static void contrast2() {
        String sour ="D:\\Documents\\pic\\311.jpg";
        String des = "D:\\Documents\\pic\\x1.jpg";
        double db = FaceUtils.compare_image(sour, des);
        System.out.println("相似度:\n" + db);
    }
    public static void contrast() {
        String sour = FileUtil.getResourceAbsolutePath("x1.jpg");
        String des = FileUtil.getResourceAbsolutePath("x1.jpg");
        double db = FaceUtils.cmpPic2(sour, des);
        System.out.println("相似度:\n" + db);
    }

    public static void detectface() {
        String address = FileUtil.getResourceAbsolutePath("x3.jfif");
        try {
            //创建一个mat
            Mat img_mat = new Mat();
            img_mat = FaceUtils.bufImg2Mat(FaceUtils.loadImage(address));
            img_mat = FaceUtils.detectFace(img_mat);//检测人脸
            BufferedImage img2paint = FaceUtils.mat2BI(img_mat);
            Myframe f = new Myframe();
            f.setSize(img2paint.getWidth() + 200, img2paint.getHeight() + 200);
            f.draw(img2paint);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public static void testLoadImg() {
        Mat src = Imgcodecs.imread(FileUtil.getResourceAbsolutePath("0.jfif"));
        HighGui gui = new HighGui();
        gui.imshow("哈妮", src);
        gui.waitKey(1000);
    }

}
