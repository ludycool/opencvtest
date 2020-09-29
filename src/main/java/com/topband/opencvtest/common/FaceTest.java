package com.topband.opencvtest.common;

import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Size;
import org.opencv.face.FaceRecognizer;
import org.opencv.face.LBPHFaceRecognizer;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import javax.swing.*;
import java.io.File;



public class FaceTest {


    public final static String BASE_FILE_PATH = "D:\\Documents\\pic\\model\\";

    public final static String BASE_FILE_NAME = "face_model.yml";


    public static void testetect() {

//通过模型存放的文件名来获取用户对应的模型文件

        String readPath = BASE_FILE_PATH + BASE_FILE_NAME;
        System.out.println("file path---------->" + readPath);
        File testFile = new File(readPath);
        if (!testFile.exists()) {
            JOptionPane.showMessageDialog(null, "模型文件不存在！请确认操作是否正确！", "提示", JOptionPane.ERROR_MESSAGE);
            System.out.println("file path---------->模型文件不存在！请确认操作是否正确！");
        }
        FaceRecognizer faceRecognizer = LBPHFaceRecognizer.create();
        faceRecognizer.read(readPath);
        Mat img_mat = FaceUtils.getFaceAndConv("D:\\Documents\\pic\\lulu.jpg");
        FaceTrainAndRecognise.recognizerFace(img_mat, faceRecognizer);

    }

    public static MatOfRect getFace(Mat src) {
        Mat result = src.clone();
        if (src.cols() > 1000 || src.rows() > 1000) {
            Imgproc.resize(src, result, new Size(src.cols() / 3, src.rows() / 3));
        }
        CascadeClassifier faceDetector = new CascadeClassifier(FaceUtils.cascadeClassifierXml);
        MatOfRect objDetections = new MatOfRect();
        faceDetector.detectMultiScale(result, objDetections);
        return objDetections;
    }


}
