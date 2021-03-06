package com.topband.opencvtest.common;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.opencv.imgproc.Imgproc.cvtColor;

/**
 * @author ludi
 * @version 1.0
 * @date 2020/9/21 15:44
 * @remark
 */
public class FaceUtils {


    public static final String cascadeClassifierXml = FileUtil.getResourceAbsolutePath("data\\haarcascades\\haarcascade_frontalface_alt2.xml");

    public static final CascadeClassifier faceDetector = new CascadeClassifier(cascadeClassifierXml);

    /**
     * 侦查脸 并把脸画框框
     *
     * @param mat_img
     * @return
     */
    public static Mat detectFace(Mat mat_img) {
        System.out.println("Running DetectFace ... ");
        // 从配置文件lbpcascade_frontalface.xml中创建一个人脸识别器，该文件位于opencv安装目录中
        CascadeClassifier faceDetector = new CascadeClassifier(cascadeClassifierXml);
        // 在图片中检测人脸
        MatOfRect faceDetections = new MatOfRect();
        faceDetector.detectMultiScale(mat_img, faceDetections);
        Rect[] rects = faceDetections.toArray();
        if (rects != null && rects.length >= 1) {
            for (Rect rect : rects) {
                System.out.println(rect);
                Imgproc.rectangle(mat_img, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height),
                        new Scalar(0, 0, 255), 2);
            }
        }
        return mat_img;
    }

    /**
     * 侦查脸 把第一个脸 剪切出来
     *
     * @param mat_img
     * @return
     */
    public static Mat detectFaceAndCut(Mat mat_img) {
        System.out.println("Running DetectFace ... ");
        // 从配置文件lbpcascade_frontalface.xml中创建一个人脸识别器，该文件位于opencv安装目录中
        CascadeClassifier faceDetector = new CascadeClassifier(cascadeClassifierXml);
        // 在图片中检测人脸
        MatOfRect faceDetections = new MatOfRect();
        faceDetector.detectMultiScale(mat_img, faceDetections);
        Rect[] rects = faceDetections.toArray();
        if (rects != null && rects.length >= 1) {
            for (Rect rect : rects) {
                System.out.println(rect);
                //Mat sub = mat_img.submat(rect);
                Mat face = new Mat(mat_img, rect);
               // Mat mat = new Mat();
                //Size size = new Size(200, 200);//统一尺寸 对比更好
               // Imgproc.resize(sub, mat, size);
                return face;
            }
        }
        return null;
    }


    /**
     * 直方图比较两个图片是否相似，可以用来比较两个人脸图片是否是同一个人
     *
     * @param src
     * @param des
     * @return
     * @author 陈阳
     * @date 2019年1月26日
     */
    public static boolean cmpPic(String src, String des) {

        boolean isSame = false;
        System.out.println("\n==========直方图比较==========");
        //自定义阈值
        //相关性阈值，应大于多少，越接近1表示越像，最大为1
        double HISTCMP_CORREL_THRESHOLD = 0.9;
        //卡方阈值，应小于多少，越接近0表示越像
        double HISTCMP_CHISQR_THRESHOLD = 0.5;
        //交叉阈值，应大于多少，数值越大表示越像
        double HISTCMP_INTERSECT_THRESHOLD = 2.5;
        //巴氏距离阈值，应小于多少，越接近0表示越像
        double HISTCMP_BHATTACHARYYA_THRESHOLD = 0.2;

        try {

            long startTime = System.currentTimeMillis();

            org.opencv.core.Mat mat_src = Imgcodecs.imread(src);
            org.opencv.core.Mat mat_des = Imgcodecs.imread(des);
            ;

            if (mat_src.empty() || mat_des.empty()) {
                throw new Exception("no file.");
            }

            org.opencv.core.Mat hsv_src = new org.opencv.core.Mat();
            org.opencv.core.Mat hsv_des = new org.opencv.core.Mat();

            // 转换成HSV
            cvtColor(mat_src, hsv_src, Imgproc.COLOR_BGR2HSV);
            cvtColor(mat_des, hsv_des, Imgproc.COLOR_BGR2HSV);

            List<Mat> listImg1 = new ArrayList<Mat>();
            List<org.opencv.core.Mat> listImg2 = new ArrayList<org.opencv.core.Mat>();
            listImg1.add(hsv_src);
            listImg2.add(hsv_des);

            MatOfFloat ranges = new MatOfFloat(0, 255);
            MatOfInt histSize = new MatOfInt(50);
            MatOfInt channels = new MatOfInt(0);

            org.opencv.core.Mat histImg1 = new org.opencv.core.Mat();
            org.opencv.core.Mat histImg2 = new org.opencv.core.Mat();

            //org.bytedeco.javacpp中的方法不太了解参数，所以直接上org.opencv中的方法，所以需要加载一下dll，System.load("D:\\soft\\openCV3\\opencv\\build\\java\\x64\\opencv_java345.dll");
            //opencv_imgproc.calcHist(images, nimages, channels, mask, hist, dims, histSize, ranges, uniform, accumulate);
            Imgproc.calcHist(listImg1, channels, new org.opencv.core.Mat(), histImg1, histSize, ranges);
            Imgproc.calcHist(listImg2, channels, new org.opencv.core.Mat(), histImg2, histSize, ranges);

            org.opencv.core.Core.normalize(histImg1, histImg1, 0d, 1d, Core.NORM_MINMAX, -1,
                    new org.opencv.core.Mat());
            org.opencv.core.Core.normalize(histImg2, histImg2, 0d, 1d, Core.NORM_MINMAX, -1,
                    new org.opencv.core.Mat());

            double result0, result1, result2, result3;
            result0 = Imgproc.compareHist(histImg1, histImg2, Imgproc.HISTCMP_CORREL);
            result1 = Imgproc.compareHist(histImg1, histImg2, Imgproc.HISTCMP_CHISQR);
            result2 = Imgproc.compareHist(histImg1, histImg2, Imgproc.HISTCMP_INTERSECT);
            result3 = Imgproc.compareHist(histImg1, histImg2, Imgproc.HISTCMP_BHATTACHARYYA);

            System.out.println("相关性（度量越高，匹配越准确 [基准：" + HISTCMP_CORREL_THRESHOLD + "]）,当前值:" + result0);
            System.out.println("卡方（度量越低，匹配越准确 [基准：" + HISTCMP_CHISQR_THRESHOLD + "]）,当前值:" + result1);
            System.out.println("交叉核（度量越高，匹配越准确 [基准：" + HISTCMP_INTERSECT_THRESHOLD + "]）,当前值:" + result2);
            System.out.println("巴氏距离（度量越低，匹配越准确 [基准：" + HISTCMP_BHATTACHARYYA_THRESHOLD + "]）,当前值:" + result3);

            //一共四种方式，有三个满足阈值就算匹配成功
            int count = 0;
            if (result0 > HISTCMP_CORREL_THRESHOLD)
                count++;
            if (result1 < HISTCMP_CHISQR_THRESHOLD)
                count++;
            if (result2 > HISTCMP_INTERSECT_THRESHOLD)
                count++;
            if (result3 < HISTCMP_BHATTACHARYYA_THRESHOLD)
                count++;
            if (count >= 2) {
                //这是相似的图像
                isSame = true;
            }

            long estimatedTime = System.currentTimeMillis() - startTime;

            System.out.println("花费时间= " + estimatedTime + "ms");

            return isSame;
        } catch (Exception e) {
            System.out.println("例外:" + e);
        }
        return isSame;
    }


    /**
     * 直方图比较两个图片是否相似，可以用来比较两个人脸图片是否是同一个人
     * <p>Title: CmpPic</p>
     * <p>Description: </p>
     * Correlation相关性比较（CV_COMP_CORREL）越接近1越相似
     * Chi-Square卡方比较(CV_COMP_CHISQR)越接近0越相似
     * Intersection十字交叉性（CV_COMP_INTERSECT）对于相似度比较这个算法不太好
     * Bhattacharyya distance巴氏距离(CV_COMP_BHATTACHARYYA)越接近1越相似
     *
     * @param src
     * @param des
     * @return
     * @author
     * @date
     */
    public static double cmpPic2(String src, String des) {
        System.out.println("\n==========直方图比较==========");
        //自定义阈值
        //相关性阈值，应大于多少，越接近1表示越像，最大为1
        double HISTCMP_CORREL_THRESHOLD = 0.9;
        //卡方阈值，应小于多少，越接近0表示越像
        double HISTCMP_CHISQR_THRESHOLD = 0.4;
        //交叉阈值，应大于多少，数值越大表示越像
        double HISTCMP_INTERSECT_THRESHOLD = 2.5;
        //巴氏距离阈值，应小于多少，越接近0表示越像
        double HISTCMP_BHATTACHARYYA_THRESHOLD = 0.2;

        try {

            long startTime = System.currentTimeMillis();

            org.opencv.core.Mat mat_src = Imgcodecs.imread(src);
            org.opencv.core.Mat mat_des = Imgcodecs.imread(des);
            ;

            if (mat_src.empty() || mat_des.empty()) {
                throw new Exception("no file.");
            }

            org.opencv.core.Mat hsv_src = new org.opencv.core.Mat();
            org.opencv.core.Mat hsv_des = new org.opencv.core.Mat();

            // 转换成HSV
            cvtColor(mat_src, hsv_src, Imgproc.COLOR_BGR2HSV);
            cvtColor(mat_des, hsv_des, Imgproc.COLOR_BGR2HSV);

            List<Mat> listImg1 = new ArrayList<Mat>();
            List<org.opencv.core.Mat> listImg2 = new ArrayList<org.opencv.core.Mat>();
            listImg1.add(hsv_src);
            listImg2.add(hsv_des);

            MatOfFloat ranges = new MatOfFloat(0, 255);
            //直方图大小， 越大匹配越精确 (越慢)
            MatOfInt histSize = new MatOfInt(1000);
            MatOfInt channels = new MatOfInt(0);

            org.opencv.core.Mat histImg1 = new org.opencv.core.Mat();
            org.opencv.core.Mat histImg2 = new org.opencv.core.Mat();

            //org.bytedeco.javacpp中的方法不太了解参数，所以直接上org.opencv中的方法，所以需要加载一下dll，System.load("D:\\soft\\openCV3\\opencv\\build\\java\\x64\\opencv_java345.dll");
            //opencv_imgproc.calcHist(images, nimages, channels, mask, hist, dims, histSize, ranges, uniform, accumulate);
            Imgproc.calcHist(listImg1, channels, new org.opencv.core.Mat(), histImg1, histSize, ranges);
            Imgproc.calcHist(listImg2, channels, new org.opencv.core.Mat(), histImg2, histSize, ranges);

            org.opencv.core.Core.normalize(histImg1, histImg1, 0d, 1d, Core.NORM_MINMAX, -1,
                    new org.opencv.core.Mat());
            org.opencv.core.Core.normalize(histImg2, histImg2, 0d, 1d, Core.NORM_MINMAX, -1,
                    new org.opencv.core.Mat());

            double result0, result1, result2, result3;
            result0 = Imgproc.compareHist(histImg1, histImg2, Imgproc.HISTCMP_CORREL);
            result1 = Imgproc.compareHist(histImg1, histImg2, Imgproc.HISTCMP_CHISQR);
            result2 = Imgproc.compareHist(histImg1, histImg2, Imgproc.HISTCMP_INTERSECT);
            result3 = Imgproc.compareHist(histImg1, histImg2, Imgproc.HISTCMP_BHATTACHARYYA);
            result0 = (double) Math.round(result0 * 10000) / 10000;//四位小数
            result1 = (double) Math.round(result1 * 10000) / 10000;
            result2 = (double) Math.round(result2 * 10000) / 10000;//四位小数
            result3 = (double) Math.round(result3 * 10000) / 10000;//四位小数
            System.out.println("相关性（度量越高，匹配越准确 [基准：" + HISTCMP_CORREL_THRESHOLD + "]）,当前值:" + result0);
            System.out.println("卡方（度量越低，匹配越准确 [基准：" + HISTCMP_CHISQR_THRESHOLD + "]）,当前值:" + result1);
            System.out.println("交叉核（度量越高，匹配越准确 [基准：" + HISTCMP_INTERSECT_THRESHOLD + "]）,当前值:" + result2);
            System.out.println("巴氏距离（度量越低，匹配越准确 [基准：" + HISTCMP_BHATTACHARYYA_THRESHOLD + "]）,当前值:" + result3);

            double c1 = 0, c2 = 0, c3 = 0, c4 = 0;
            c1 = result0 * 100;//相关性
            if (result1 < 1)
                c2 = (1 - result1) * 100;//卡方
            //c3 =result2; //交叉核
            if (result3 < 1)
                c4 = (1 - result3) * 100;  //巴氏距离

            long estimatedTime = System.currentTimeMillis() - startTime;
            System.out.println("花费时间= " + estimatedTime + "ms");

            return (c1 + c2 + c4) / 3;
        } catch (Exception e) {
            System.out.println("例外:" + e);
        }
        return 0;
    }


    /**
     * 侦查脸 把第一个脸 剪切出来
     *
     * @param srcFileName 源图片 位置
     * @param dstFileName 剪切对像 保存位置
     * @return 是否成功
     */
    public static boolean detectFaceAndCut(String srcFileName, String dstFileName) {
        boolean succ = false;
        try {
            //创建一个mat
            Mat img_mat = OpenCvUtil.bufImg2Mat(OpenCvUtil.loadImage(srcFileName));
            img_mat = FaceUtils.detectFaceAndCut(img_mat);//检测剪切人脸
            if (img_mat != null) {
                BufferedImage img2paint = OpenCvUtil.mat2BI(img_mat);
                return OpenCvUtil.saveImage(img2paint, dstFileName);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return succ;
    }

    // 灰度化人脸
    public static Mat conv_Mat(String img) {
        Mat image0 = Imgcodecs.imread(img);

        Mat image1 = new Mat();
        // 灰度化
        Imgproc.cvtColor(image0, image1, Imgproc.COLOR_BGR2GRAY);
        // 探测人脸
        MatOfRect faceDetections = new MatOfRect();
        faceDetector.detectMultiScale(image1, faceDetections);
        // rect中人脸图片的范围
        for (Rect rect : faceDetections.toArray()) {
            Mat face = new Mat(image1, rect);
            return face;
        }
        return null;
    }

    /**
     * 侦查脸 把第一个脸 取出来 并且灰度化
     *
     * @param srcFileName 源图片 位置
     * @return 是否成功
     */
    public static Mat getFaceAndConv(String srcFileName) {
        boolean succ = false;
        try {
            //创建一个mat
            Mat img_mat = Imgcodecs.imread(srcFileName);
            img_mat = FaceUtils.detectFaceAndCut(img_mat);//检测剪切人脸
            if (img_mat != null) {
                Mat image1 = new Mat();
                // 灰度化
                Imgproc.cvtColor(img_mat, image1, Imgproc.COLOR_BGR2GRAY);
                return image1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    public static double compare_image(String img_1, String img_2) {
        Mat mat_1 = conv_Mat(img_1);
        Mat mat_2 = conv_Mat(img_2);
        Mat hist_1 = new Mat();
        Mat hist_2 = new Mat();

        //颜色范围
        MatOfFloat ranges = new MatOfFloat(0f, 256f);
        //直方图大小， 越大匹配越精确 (越慢)
        MatOfInt histSize = new MatOfInt(1000);

        Imgproc.calcHist(Arrays.asList(mat_1), new MatOfInt(0), new Mat(), hist_1, histSize, ranges);
        Imgproc.calcHist(Arrays.asList(mat_2), new MatOfInt(0), new Mat(), hist_2, histSize, ranges);

        // CORREL 相关系数
        double res = Imgproc.compareHist(hist_1, hist_2, Imgproc.CV_COMP_CORREL);
        return res;
    }

}
