package com.topband.opencvtest.common;


import org.opencv.core.*;
import org.opencv.face.FaceRecognizer;
import org.opencv.face.LBPHFaceRecognizer;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author ludi
 * @version 1.0
 * @date 2020/9/22 14:33
 * @remark
 */
public class TrainUtil {

    public static void testDetectAndCollect() {
        Mat img_mat = OpenCvUtil.bufImg2Mat(OpenCvUtil.loadImage("D:\\Documents\\pic\\morefases.png"));
        detectAndCollect(img_mat, FaceUtils.faceDetector);

    }

    private static int startFrom = 0;

    private static int sample = 0;

    //配置保存路径
    public static String path = "F:/face/imagedb";

    public static String id = "";

    static AtomicBoolean start = new AtomicBoolean(false);

    public static long startTime;

    public final static String modeFileName = "face_model.yml";


    /**
     * 采集人脸并保存到本地的方法
     **/
    public static void detectAndCollect(Mat frame, CascadeClassifier faceCascade) {
        MatOfRect faces = new MatOfRect();
        Mat grayFrame = new Mat();

        Imgproc.cvtColor(frame, grayFrame, Imgproc.COLOR_BGR2GRAY);

        // 采集人脸
        faceCascade.detectMultiScale(grayFrame, faces);

        Rect[] facesArray = faces.toArray();

// 连续采集50张，并保存
        if (facesArray.length >= 1) {
            if ((sample == 0 && System.currentTimeMillis() - startTime > 10000) || (sample > 0
                    && sample < 50 && System.currentTimeMillis() - startTime > 300)) {
                startTime = System.currentTimeMillis();
                sample++;
                System.out.println("image: " + sample);
                Imgcodecs.imwrite(path + "/image." + id + "." + (startFrom + sample) + ".jpg",
                        frame.submat(facesArray[0]));
            }
        }

        for (int i = 0; i < facesArray.length; i++) {
            Imgproc.rectangle(frame, facesArray[i].tl(), facesArray[i].br(), new Scalar(0, 255, 0), 2);
        }
    }


    /**
     * 训练模型的方法，传入人脸图片所在的文件夹路径，和模型输出的路径
     * 训练结束后模型文件会在模型输出路径里边
     **/
    public static void train(String imageFolder, String saveFolder)
            throws IOException {

        // OpenCV 自带了三个人脸识别算法：Eigenfaces，Fisherfaces 和局部二值模式直方图 (LBPH)
        FaceRecognizer faceRecognizer = LBPHFaceRecognizer.create();
        // CascadeClassifier faceCascade = new CascadeClassifier();
// opencv的模型
        //  faceCascade.load(FaceUtils.cascadeClassifierXml);
// 读取文件于数组中
        File[] files = new File(imageFolder).listFiles();
        Map<String, Integer> nameMapId = new HashMap<String, Integer>(10);
// 图片集合
        List<Mat> images = new ArrayList<Mat>(files.length);
// 名称集合
        List<String> names = new ArrayList<String>(files.length);
        List<Integer> ids = new ArrayList<Integer>(files.length);
        for (int index = 0; index < files.length; index++) {
// 解析文件名 获取名称
            File file = files[index];
            String name = file.getName().split("\\.")[0];
            Integer id = nameMapId.get(name);
            if (id == null) {
                id = names.size();
                names.add(name);
                nameMapId.put(name, id);
                faceRecognizer.setLabelInfo(id, name);
            }

            Mat mat = Imgcodecs.imread(file.getCanonicalPath());
            Mat gray = new Mat();
// 图片预处理
            Imgproc.cvtColor(mat, gray, Imgproc.COLOR_BGR2GRAY);
            images.add(gray);
            System.out.println("add total " + images.size());
            ids.add(id);
        }
        int[] idsInt = new int[ids.size()];
        for (int i = 0; i < idsInt.length; i++) {
            idsInt[i] = ids.get(i).intValue();
        }
// 显示标签
        MatOfInt labels = new MatOfInt(idsInt);
// 调用训练方法
        faceRecognizer.train(images, labels);
// 输出持久化模型文件 训练一次后就可以一直调用
        faceRecognizer.save(saveFolder + File.separator + modeFileName);
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
