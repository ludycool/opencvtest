package com.topband.opencvtest.common;

import lombok.extern.slf4j.Slf4j;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Range;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.*;

/**
 * @author ludi
 * @version 1.0
 * @date 2020/9/24 13:36
 * @remark
 */
@Slf4j
public class OpenCvUtil {

    /**
     * 横向拼接两个图像的数据（Mat），该两个图像的类型必须是相同的类型，如：均为CvType.CV_8UC3类型
     * @author bailichun
     * @since 2020.02.20 15:00
     * @param m1 要合并的图像1（左图）
     * @param m2 要合并的图像2（右图）
     * @return 拼接好的Mat图像数据集。其高度等于两个图像中高度较大者的高度；其宽度等于两个图像的宽度之和。类型与两个输入图像相同。
     * @throws Exception 当两个图像数据的类型不同时，抛出异常
     */
    public static Mat concat(Mat m1, Mat m2) throws Exception{

        System.out.println("图1 width="+m1.size().width);
        System.out.println("图1 height="+m1.size().height);
        System.out.println("图2 width="+m2.size().width);
        System.out.println("图2 height="+m2.size().height);

        if(m1.type() != m2.type()){
            throw new Exception("concat:两个图像数据的类型不同！");
        }
        long time = System.currentTimeMillis();
        //宽度为两图的宽度之和
        double w = m1.size().width + m2.size().width;
        //高度取两个矩阵中的较大者的高度
        double h = m1.size().height > m2.size().height ? m1.size().height : m2.size().height;
        //创建一个大矩阵对象
        Mat des = Mat.zeros((int)h, (int)w, m1.type());

        //在最终的大图上标记一块区域，用于存放复制图1（左图）的数据，大小为从第0列到m1.cols()列
        Mat rectForM1 = des.colRange(new Range(0, m1.cols()));

        //标记出位于rectForM1的垂直方向上中间位置的区域，高度为图1的高度，此时该区域的大小已经和图1的大小相同。（用于存放复制图1（左图）的数据）
        int rowOffset1 = (int)(rectForM1.size().height-m1.rows())/2;
        rectForM1 = rectForM1.rowRange(rowOffset1, rowOffset1 + m1.rows());

        //在最终的大图上标记一块区域，用于存放复制图2（右图）的数据
        Mat rectForM2 = des.colRange(new Range(m1.cols(), des.cols()));

        //标记出位于rectForM2的垂直方向上中间位置的区域，高度为图2的高度，此时该区域的大小已经和图2的大小相同。（用于存放复制图2（右图）的数据）
        int rowOffset2 = (int)(rectForM2.size().height-m2.rows())/2;
        rectForM2 = rectForM2.rowRange(rowOffset2, rowOffset2 + m2.rows());

        //将图1拷贝到des的指定区域 rectForM1
        m1.copyTo(rectForM1);
        //将图2拷贝到des的指定区域 rectForM2
        m2.copyTo(rectForM2);

        System.out.println("图片合并耗时："+(System.currentTimeMillis()-time)+"ms");
        return des;
    }
    /**
     * 加载图片
     *
     * @param add
     * @return 一个bufferedImage
     */
    public static BufferedImage loadImage(String add) {
        try {
            BufferedImage img = ImageIO.read(new File(add));
            return img;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 保存图片
     *
     * @param img
     * @param fileName
     * @return
     */
    public static boolean saveImage(BufferedImage img, String fileName) {
        try {
            int doidx = fileName.lastIndexOf(".");
            String formatName = fileName.substring(doidx + 1);

            File outputfile = new File(fileName);
            ImageIO.write(img, formatName, outputfile);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 将BufferedImage转换为InputStream
     * @param image
     * @return
     */
    public static InputStream bufferedImageToInputStream(BufferedImage image ){
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "jpg", os);
            InputStream input = new ByteArrayInputStream(os.toByteArray());
            return input;
        } catch (IOException e) {
            log.error("提示:",e);
        }
        return null;
    }

    /**
     * BufferedImage转换成Mat
     *
     * @param src 要转换的BufferedImage
     */
    public static Mat bufImg2Mat(BufferedImage src) {
        if (src.getType() != BufferedImage.TYPE_3BYTE_BGR) {
            BufferedImage image = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
            image.getGraphics().drawImage(src, 0, 0, null);
            src = image;
        }
        WritableRaster raster = src.getRaster();
        DataBufferByte buffer = (DataBufferByte) raster.getDataBuffer();
        byte[] pixels = buffer.getData();
        Mat mat = Mat.eye(src.getHeight(), src.getWidth(), CvType.CV_8UC3);
        mat.put(0, 0, pixels);
        return mat;
    }

    /**
     * Mat转换成BufferedImage
     *
     * @param mat 要转换的Mat
     * @return
     */
    public static BufferedImage mat2BI(Mat mat) {
        int dataSize = mat.cols() * mat.rows() * (int) mat.elemSize();
        byte[] data = new byte[dataSize];
        mat.get(0, 0, data);
        int type = mat.channels() == 1 ?
                BufferedImage.TYPE_BYTE_GRAY : BufferedImage.TYPE_3BYTE_BGR;
        if (type == BufferedImage.TYPE_3BYTE_BGR) {
            for (int i = 0; i < dataSize; i += 3) {
                byte blue = data[i + 0];
                data[i + 0] = data[i + 2];
                data[i + 2] = blue;
            }
        }
        BufferedImage image = new BufferedImage(mat.cols(), mat.rows(), type);
        image.getRaster().setDataElements(0, 0, mat.cols(), mat.rows(), data);
        return image;
    }
}
