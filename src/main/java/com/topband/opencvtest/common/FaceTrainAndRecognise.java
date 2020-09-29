package com.topband.opencvtest.common;

import com.topband.opencvtest.mode.IdConfidence;
import lombok.Getter;
import org.apache.commons.lang3.tuple.Pair;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.face.FaceRecognizer;
import org.opencv.face.LBPHFaceRecognizer;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author ludi
 * @version 1.0
 * @date 2020/9/28 16:48
 * @remark 每个人训练多个样本，识别最相近的特征值
 */
public class FaceTrainAndRecognise {


    static AtomicBoolean start = new AtomicBoolean(false);
    public final static String commonModeFileName = "face_model.yml";

    @Getter
    static final ConcurrentHashMap<String, FaceRecognizer> faceRecognizerConcurrentHashMap = new ConcurrentHashMap<>();

    static List<Pair<String, FaceRecognizer>> getRecognizerAll() {
        List<Pair<String, FaceRecognizer>> list = new ArrayList<>();
        Iterator<Map.Entry<String, FaceRecognizer>> entries = faceRecognizerConcurrentHashMap.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<String, FaceRecognizer> entry = entries.next();
            list.add(Pair.of(entry.getKey(), entry.getValue()));
        }
        return list;
    }


    @Getter
    static String filePathHeadDb;
    @Getter
    static String filePathModel;

    static {
        filePathHeadDb = SpringContextBeanService.getValueBykey("config.filepath.headdb");
        filePathModel = SpringContextBeanService.getValueBykey("config.filepath.model");

        File[] files = new File(filePathModel).listFiles();
        for (int index = 0; index < files.length; index++) {
// 解析文件名 获取名称
            File file = files[index];
            String fileName = file.getName();
            int startidx = fileName.lastIndexOf("_");
            int endidx = fileName.lastIndexOf(".");
            String userId = fileName.substring(startidx + 1, endidx);
            FaceRecognizer faceRecognizer = LBPHFaceRecognizer.create();
            faceRecognizer.read(file.getAbsolutePath());
            faceRecognizerConcurrentHashMap.put(userId, faceRecognizer);//添加到缓存
        }

    }

    public static IdConfidence recognizerFace(Mat target, FaceRecognizer faceRecognizer) {
        int[] label = new int[1];
        double[] confidences = new double[1];
        faceRecognizer.predict(target, label, confidences);

        String name = faceRecognizer.getLabelInfo(label[0]);
        System.out.println("detectAndDisplay name:" + name);
        System.out.println("detectAndDisplay label=" + label[0] + "可信度confidence:" + confidences[0]);

        IdConfidence idConfidence = new IdConfidence();
        idConfidence.setLabel(label[0]);
        idConfidence.setId(name);
        idConfidence.setConfidence(confidences[0]);
        return idConfidence;
    }


    /**
     * 训练模型的方法，传入人脸图片所在的文件夹路径，和模型输出的路径
     * 训练结束后模型文件会在模型输出路径里边
     **/
    public static void train(String imageFolder, String saveFolder, String modeFileName) throws IOException {
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

    /**
     * 样本库里识别
     * @param faceMat  剪切后的人脸 矩阵
     * @return
     */
    public static IdConfidence recognizerFace(Mat faceMat) {

        Mat grayImage = new Mat();
        // 灰度化
        Imgproc.cvtColor(faceMat, grayImage, Imgproc.COLOR_BGR2GRAY);

        IdConfidence idConfidence = new IdConfidence();
        List<Pair<String, FaceRecognizer>> list = getRecognizerAll();
        for (int i = 0; i < list.size(); i++) {//遍历特征值
            FaceRecognizer faceRecognizer = list.get(i).getValue();
            int[] label = new int[1];
            double[] confidences = new double[1];
            faceRecognizer.predict(grayImage, label, confidences);
            String name = faceRecognizer.getLabelInfo(label[0]);
            double confidence = confidences[0];

            if (confidence <= 100) {
                confidence = (100 - confidence);
            } else {
                confidence = 0;
            }

            if (confidence > idConfidence.getConfidence())//记录最大值
            {
                idConfidence.setLabel(label[0]);
                idConfidence.setId(name);
                idConfidence.setConfidence(confidence);
            }
        }
        System.out.println("detectAndDisplay name:" + idConfidence.getId());
        System.out.println("detectAndDisplay label=" + idConfidence.getLabel() + "可信度confidence:" + idConfidence.getConfidence());
        return idConfidence;
    }

    /**
     * 保存一个人脸，且把个人的文件夹里所有的样本进行训练，每次都是全量更新
     *
     * @param faceMat 剪切后的人脸 矩阵
     * @param userId
     * @throws IOException
     */
    public static boolean trainFace(Mat faceMat, String userId) throws IOException {
        synchronized (userId.intern()) {
            String imageFolder = filePathHeadDb + File.separator + userId;
            FileUtil.checkAndCreateDirectory(imageFolder);//创建文件夹

            //region 保存图片
            int count = FileUtil.getDirectoryFileNumber(imageFolder);//当前文件数量
            String filename = userId + "_" + (count) + ".jpg";
            BufferedImage img2paint = OpenCvUtil.mat2BI(faceMat);//转换图片
            String saveAddr = imageFolder + File.separator + filename;
            boolean res = OpenCvUtil.saveImage(img2paint, saveAddr);//保存图片
            if (!res) {
                return false;
            }
            //endregion

            String modeFileName = "face_model_" + userId + ".yml";

            // OpenCV 自带了三个人脸识别算法：Eigenfaces，Fisherfaces 和局部二值模式直方图 (LBPH)
            FaceRecognizer faceRecognizer = LBPHFaceRecognizer.create();
            // CascadeClassifier faceCascade = new CascadeClassifier();
// opencv的模型
            //  faceCascade.load(FaceUtils.cascadeClassifierXml);
// 读取文件于数组中

            File[] files = new File(imageFolder).listFiles();
// 图片集合
            List<Mat> images = new ArrayList<Mat>(files.length);
            int[] idsInt = new int[files.length];
            for (int index = 0; index < files.length; index++) {
// 解析文件名 获取名称
                File file = files[index];

                int startidx = file.getName().lastIndexOf("_");
                int endidx = file.getName().lastIndexOf(".");
                String name = file.getName().substring(0, startidx);
                int id = Integer.valueOf(file.getName().substring(startidx + 1, endidx));

                idsInt[index] = id;
                faceRecognizer.setLabelInfo(id, name);

                Mat mat = Imgcodecs.imread(file.getCanonicalPath());
                Mat gray = new Mat();
                // 图片预处理
                Imgproc.cvtColor(mat, gray, Imgproc.COLOR_BGR2GRAY);
                images.add(gray);
                System.out.println("add total " + images.size());
            }
// 显示标签
            MatOfInt labels = new MatOfInt(idsInt);
// 调用训练方法
            faceRecognizer.train(images, labels);
// 输出持久化模型文件 训练一次后就可以一直调用
            faceRecognizer.save(filePathModel + File.separator + modeFileName);
            faceRecognizerConcurrentHashMap.put(userId, faceRecognizer);//更新

            return true;
        }
    }
}
