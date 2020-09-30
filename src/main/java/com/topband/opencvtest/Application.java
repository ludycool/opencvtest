package com.topband.opencvtest;


import com.topband.opencvtest.common.*;
import com.topband.opencvtest.config.AppConfig;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.*;
import org.opencv.dnn.ClassificationModel;
import org.opencv.dnn.DetectionModel;
import org.opencv.dnn.Dnn;
import org.opencv.dnn.Net;
import org.opencv.features2d.ORB;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static com.topband.opencvtest.common.FaceUtils.cascadeClassifierXml;

/**
 * @author ludi
 * @version 1.0
 * @date 2020/9/21 11:01
 * @remark
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class}
        , scanBasePackages = {"com.topband.opencvtest"})
@Slf4j
public class Application {

    static {
        //直接下载window版，解压，使用里边的dll
        //linux 需要下载源码，生成so库 https://www.52pojie.cn/thread-872736-1-1.html
       System.load(FileUtil.getAppicationPath() + File.separator + "libs\\opencv_java410.dll");
        //System.load("/usr/local/share/java/opencv4/libopencv_java440.so");
        //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        System.out.println("opencv\t" + Core.VERSION);
    }

    public static void main(String[] args) {
        log.info("Welcome to OpenCV " + Core.VERSION);
        Mat m = new Mat(5, 10, CvType.CV_8UC1, new Scalar(0));
        // System.out.println("OpenCV Mat: " + m);
        log.info("OpenCV Mat: " + m);
//        Mat mr1 = m.row(1);
//        mr1.setTo(new Scalar(1));
//        Mat mc5 = m.col(5);
//        mc5.setTo(new Scalar(5));
        //System.out.println("OpenCV Mat data:\n" + m.dump());
       AppConfig.context = SpringApplication.run(Application.class, args);
    }

    public static void cutFace() {
        // String sour =  FileUtil.getResourceAbsolutePath("x3.jfif");
        //String des =  FileUtil.getAppicationPath()+ File.separator +"x3.jpg";
        String sour = "D:\\Documents\\pic\\gyy.jfif";
        String des = "D:\\Documents\\pic\\gyy.jpg";

        boolean res = FaceUtils.detectFaceAndCut(sour, des);
        System.out.println("cutFace:" + res);

    }

    public static void contrast3() {
        String sour = "D:\\Documents\\pic\\x3.jfif";
        String des = "D:\\Documents\\pic\\x3.jpg";
        Mat src = OpenCvUtil.bufImg2Mat(OpenCvUtil.loadImage(sour));


        MatOfKeyPoint keypoints = new MatOfKeyPoint();
        ORB detector = ORB.create();
        detector.detect(src, keypoints);

        HighGui gui = new HighGui();
        gui.imshow("哈妮", src);
        gui.waitKey(1000);
    }

    public static void contrast2() {
        String sour = "D:\\Documents\\pic\\41.png";
        String des = "D:\\Documents\\pic\\hhm.jfif";
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
        String address = FileUtil.getResourceAbsolutePath("morefases.png");
        try {
            //创建一个mat
            Mat img_mat = new Mat();
            img_mat = OpenCvUtil.bufImg2Mat(OpenCvUtil.loadImage(address));
            img_mat = FaceUtils.detectFace(img_mat);//检测人脸
            BufferedImage img2paint = OpenCvUtil.mat2BI(img_mat);
            Myframe f = new Myframe();
            f.setSize(img2paint.getWidth() + 200, img2paint.getHeight() + 200);
            f.draw(img2paint);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     *  识别东西
     */
    public static void detectThing() {
         String[] names = new String[]{
                "aeroplane","bicycle","bird","boat","bottle",
                "bus","car","cat","chair","cow",
                "diningtable","dog","horse","motorbike","person",
                "pottedplant","sheep","sofa","train","tvmonitor"
        };
        Net net = Dnn.readNetFromDarknet("D:\\opencv\\data\\tiny-yolo-voc.cfg", "D:\\opencv\\data\\tiny-yolo-voc.weights");
        if ( net.empty() ) {
            System.out.println("Reading Net error");
        }

        String image_file = "D:\\Documents\\pic\\mao.jfif";//IMG_9452.JPG
        Mat im = Imgcodecs.imread(image_file, Imgcodecs.IMREAD_COLOR);
        if( im.empty() ) {
            System.out.println("Reading Image error");
        }

        Mat frame = new Mat();
        Size sz1 = new Size(im.cols(),im.rows());
        Imgproc.resize(im, frame, sz1);

        Mat resized = new Mat();
        Size sz = new Size(416,416);
        Imgproc.resize(im, resized, sz);

        float scale = 1.0F / 255.0F;
        Mat inputBlob = Dnn.blobFromImage(im, scale, sz, new Scalar(0), false, false);
        net.setInput(inputBlob, "data");
        Mat detectionMat = net.forward("detection_out");
        if( detectionMat.empty() ) {
            System.out.println("No result");
        }

        for (int i = 0; i < detectionMat.rows(); i++)
        {
            int probability_index = 5;
            int size = (int) (detectionMat.cols() * detectionMat.channels());

            float[] data = new float[size];
            detectionMat.get(i, 0, data);
            float confidence = -1;
            int objectClass = -1;
            for (int j=0; j < detectionMat.cols();j++)
            {
                if (j>=probability_index && confidence<data[j])
                {
                    confidence = data[j];
                    objectClass = j-probability_index;
                }
            }

            if (confidence > 0.3)
            {
                System.out.println("Result Object: "+i);
                for (int j=0; j < detectionMat.cols();j++)
                    System.out.print(" "+j+":"+ data[j]);
                System.out.println("");
                float x = data[0];
                float y = data[1];
                float width = data[2];
                float height = data[3];
                float xLeftBottom = (x - width / 2) * frame.cols();
                float yLeftBottom = (y - height / 2) * frame.rows();
                float xRightTop = (x + width / 2) * frame.cols();
                float yRightTop = (y + height / 2) * frame.rows();

                System.out.println("Class: "+ names[objectClass]);
                System.out.println("Confidence: "+confidence);

                System.out.println("ROI: "+xLeftBottom+" "+yLeftBottom+" "+xRightTop+" "+yRightTop+"\n");

                Imgproc.rectangle(frame, new Point(xLeftBottom, yLeftBottom),
                        new Point(xRightTop,yRightTop),new Scalar(0, 255, 0),3);
            }
        }

       // Imgcodecs.imwrite("out.jpg", frame );
        HighGui gui = new HighGui();
        gui.imshow("哈妮", frame);
        gui.waitKey(500);
    }

    private static final String[] classNames = {"background",
            "aeroplane", "bicycle", "bird", "boat",
            "bottle", "bus", "car", "cat", "chair",
            "cow", "diningtable", "dog", "horse",
            "motorbike", "person", "pottedplant",
            "sheep", "sofa", "train", "tvmonitor"};
    /**
     *  识别东西
     */
    public static void detectface2() {

       Net net = Dnn.readNetFromTensorflow("D:\\opencv\\data\\opencv_face_detector_uint8.pb", "D:\\opencv\\data\\opencv_face_detector.pbtxt");
        //Net net = Dnn.readNetFromCaffe("D:\\opencv\\data\\deploy.prototxt", "D:\\opencv\\data\\res10_300x300_ssd_iter_140000_fp16.caffemodel");
        Mat img = Imgcodecs.imread("D:\\Documents\\pic\\morefases.png"); // your data here !
        Mat blob = Dnn.blobFromImage(img, 1.0f,
                new Size(300, 300),
                new Scalar(104, 177, 123, 0), /*swapRB*/false, /*crop*/false);
        net.setInput(blob);
        Mat res = net.forward("");
        Mat faces = res.reshape(1, res.size(2));
        System.out.println("faces" + faces);
        float [] data = new float[7];
        for (int i=0; i<faces.rows(); i++)
        {
            faces.get(i, 0, data);
            float confidence = data[2];
            if (confidence > 0.2)
            {
                int left   = (int)(data[3] * img.cols());
                int top    = (int)(data[4] * img.rows());
                int right  = (int)(data[5] * img.cols());
                int bottom = (int)(data[6] * img.rows());
                System.out.println("("+left + "," + top + ")("+right+","+bottom+") " + confidence);
                Imgproc.rectangle(img, new Point(left,top), new Point(right,bottom), new Scalar(0,200,0), 3);
            }
        }
        Imgcodecs.imwrite("facedet.png", img);
        // Imgcodecs.imwrite("out.jpg", frame );
        HighGui gui = new HighGui();
        gui.imshow("哈妮", faces);
        gui.waitKey(500);
    }




    public static void testLoadImg() {
        Mat src = Imgcodecs.imread(FileUtil.getResourceAbsolutePath("0.jfif"));
        HighGui gui = new HighGui();
        gui.imshow("哈妮", src);
        gui.waitKey(500);
    }
    public static Mat process(Net net, Mat img) {
        Mat blob = Dnn.blobFromImage(img, 1./255, new Size(96,96), Scalar.all(0), true, false);
        net.setInput(blob);
        return net.forward().clone();
    }

    /**
     * 头像对比
     */
    public static void openFaceRecognition() {
        Net net = Dnn.readNetFromTorch("D:\\opencv\\data\\nn4.small2.v1.t7");
        //Net net = Dnn.readNetFromCaffe("D:\\opencv\\data\\deploy.prototxt", "D:\\opencv\\data\\res10_300x300_ssd_iter_140000_fp16.caffemodel");

        String sour = "D:\\Documents\\pic\\y1.png";
        String des = "D:\\Documents\\pic\\lulu.jpg";
        Mat feature1 = process(net, Imgcodecs.imread(sour)); // your data here !
        Mat feature2 = process(net, Imgcodecs.imread(des)); // your data here !
        double dist  = Core.norm(feature1,  feature2);
        System.out.println("distance: " + dist);
        if (dist < 0.6)
            System.out.println("SAME !");
    }
    public static void openFacedetectface() {
        Net net = Dnn.readNetFromTorch("D:\\opencv\\data\\openface.nn4.small2.v1.t7");
        String sour = "D:\\Documents\\pic\\l3.jpg";
        Mat feature1 = process(net, Imgcodecs.imread(sour)); // your data here !
        Imgcodecs.imwrite("feature1.png", feature1);

    }

}
