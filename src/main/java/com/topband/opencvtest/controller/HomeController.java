package com.topband.opencvtest.controller;

import com.topband.opencvtest.common.*;
import com.topband.opencvtest.mode.IdConfidence;
import com.topband.opencvtest.mode.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.opencv.core.Mat;
import org.opencv.face.FaceRecognizer;
import org.opencv.face.LBPHFaceRecognizer;
import org.opencv.imgproc.Imgproc;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * @author ludi
 * @version 1.0
 * @date 2020/9/24 10:15
 * @remark
 */
@Api(description = "opencv测试 接口")
@RestController
@Slf4j
public class HomeController {


    @Value("${config.filepath.headdb2}")
    public String filePathHeadDb2;

    @Value("${config.filepath.model2}")
    public String filePathModel2;


    @Value("${config.filepath.headdb}")
    public String filePathHeadDb;

    @Value("${config.filepath.model}")
    public String filePathModel;


    @ApiOperation("检测人脸")
    @PostMapping(value = "/detectface", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void detectface(@RequestParam("facefile") MultipartFile file, HttpServletResponse response) {
        if (null != file) {
            try {
                BufferedImage img = ImageIO.read(FileUtil.multipartFileToFile(file));
                //创建一个mat
                Mat img_mat = OpenCvUtil.bufImg2Mat(img);
                img_mat = FaceUtils.detectFace(img_mat);//检测人脸
                BufferedImage img2paint = OpenCvUtil.mat2BI(img_mat);
                writeImage(img2paint, response);

            } catch (Exception e) {
                e.printStackTrace();
                log.error("detectface error", e);
            }
        }
        String nonaddress = FileUtil.getResourceAbsolutePath("non.png");
        writeImage(OpenCvUtil.loadImage(nonaddress), response);
        // ResultModeHelper.fail(ConstantResult.UPLOAD_FAILED, "上传失败");
    }

    @ApiOperation("人脸对比A 库里每人只有一个特征值")
    @PostMapping(value = "/aRecognize", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String aRecognize(@RequestParam("facefile") MultipartFile file, HttpServletResponse response) {
        if (null != file) {
            try {
                BufferedImage img = ImageIO.read(FileUtil.multipartFileToFile(file));
                //创建一个mat
                Mat img_mat = OpenCvUtil.bufImg2Mat(img);
                img_mat = FaceUtils.detectFaceAndCut(img_mat);//检测剪切出人脸

                Mat grayImage = new Mat();
                // 灰度化
                Imgproc.cvtColor(img_mat, grayImage, Imgproc.COLOR_BGR2GRAY);

                FaceRecognizer faceRecognizer = LBPHFaceRecognizer.create();
                faceRecognizer.read(filePathModel2 + File.separator + FaceTrainAndRecognise.commonModeFileName);//加载特征

                int[] labels = new int[1];
                double[] confidences = new double[1];
                faceRecognizer.predict(grayImage, labels, confidences);//与入库模型 对比
                int label = labels[0];//标签
                double confidence = confidences[0];//可信度
                String id = faceRecognizer.getLabelInfo(label);//保存时的id

                String name = idToname(id);

                String Text = "姓名:" + name + "\r\n 相似度:";
                if (confidence <= 100) {
                    Text += (100 - confidence);
                } else {
                    Text += "nukown";
                }
                return Text;

                /*

                String srcAddr = filePathHeadDb + File.separator + id + ".jpg";//源图片
                Mat src = OpenCvUtil.bufImg2Mat(OpenCvUtil.loadImage(srcAddr));//对出最相近的图片源图片
                Mat concatM = OpenCvUtil.concat(src, img_mat);//对比目标与源进行合并  用于显示
                Mat dst = concatM.clone();
                //复制矩阵进入dst
                Point p = new Point(20.0, concatM.rows() / 2);
                //定义文本输入点
                Imgproc.putText(dst, Text, p, FONT_HERSHEY_SIMPLEX, 0.5, new Scalar(50, 60, 80), 2);
                writeImage(OpenCvUtil.mat2BI(dst), response);
                */
            } catch (Exception e) {
                e.printStackTrace();
                log.error("detectface error", e);
            }
        }
        String nonaddress = FileUtil.getResourceAbsolutePath("non.png");
        writeImage(OpenCvUtil.loadImage(nonaddress), response);
        // ResultModeHelper.fail(ConstantResult.UPLOAD_FAILED, "上传失败");
        return "系统出错";
    }


    @ApiOperation("头像训练入库A 每人只保存一个特征值")
    @PostMapping(value = "/aTrainIn", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "姓名", required = true, dataType = "String")
    })
    public boolean aTrainIn(@RequestParam("facefile") MultipartFile file, String name) {
        if (null != file) {
            try {
                BufferedImage img = ImageIO.read(FileUtil.multipartFileToFile(file));
                Mat img_mat = OpenCvUtil.bufImg2Mat(img);
                img_mat = FaceUtils.detectFaceAndCut(img_mat);//检测剪切人脸

                if (img_mat != null) {
                    BufferedImage img2paint = OpenCvUtil.mat2BI(img_mat);//转换图片

                    String id = nameToId(name);//id 与姓名 一一对应
                    String saveAddr = filePathHeadDb2 + File.separator + id + ".jpg";
                    boolean res = OpenCvUtil.saveImage(img2paint, saveAddr);//保存图片
                    if (res) {
                        FaceTrainAndRecognise.train(filePathHeadDb2, filePathModel2, FaceTrainAndRecognise.commonModeFileName);//训练
                        return true;
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                log.error("detectface error", e);
            }

            return false;
        } else {
            return false;//ResultModeHelper.fail(ConstantResult.UPLOAD_FAILED, "上传失败，请选择要上传的文件!");
        }
        // ResultModeHelper.fail(ConstantResult.UPLOAD_FAILED, "上传失败");
    }


    @ApiOperation("人脸对比B 每个人多个样本")
    @PostMapping(value = "/bRecognize", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String bRecognize(@RequestParam("facefile") MultipartFile file, HttpServletResponse response) {
        if (null != file) {
            try {
                BufferedImage img = ImageIO.read(FileUtil.multipartFileToFile(file));
                //创建一个mat
                Mat img_mat = OpenCvUtil.bufImg2Mat(img);
                img_mat = FaceUtils.detectFaceAndCut(img_mat);//检测剪切出人脸

                IdConfidence idConfidence = FaceTrainAndRecognise.recognizerFace(img_mat);
                String name = idToname(idConfidence.getId());

                String Text = "姓名:" + name + "\r\n 相似度:" + idConfidence.getConfidence();
                return Text;
            } catch (Exception e) {
                e.printStackTrace();
                log.error("detectface error", e);
                return "系统出错:" + e.getMessage();
            }
        }
        //String nonaddress = FileUtil.getResourceAbsolutePath("non.png");
        //  writeImage(OpenCvUtil.loadImage(nonaddress), response);
        return "系统出错";
    }


    @ApiOperation("头像训练入库B 每个人保存多个样本")
    @PostMapping(value = "/bTrainIn", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiImplicitParams({
            @ApiImplicitParam(name = "name", value = "姓名", required = true, dataType = "String")
    })
    public boolean bTrainIn(@RequestParam("facefile") MultipartFile file, String name) {
        if (null != file) {
            try {
                BufferedImage img = ImageIO.read(FileUtil.multipartFileToFile(file));
                Mat img_mat = OpenCvUtil.bufImg2Mat(img);
                img_mat = FaceUtils.detectFaceAndCut(img_mat);//检测剪切人脸

                if (img_mat != null) {
                    String id = nameToId(name);//id 与姓名 一一对应
                    boolean res = FaceTrainAndRecognise.trainFace(img_mat, id);//保存与训练
                    return res;
                }
            } catch (Exception e) {
                e.printStackTrace();
                log.error("detectface error", e);
            }
            return false;
        } else {
            return false;//ResultModeHelper.fail(ConstantResult.UPLOAD_FAILED, "上传失败，请选择要上传的文件!");
        }
        // ResultModeHelper.fail(ConstantResult.UPLOAD_FAILED, "上传失败");
    }

    /**
     * 生成32 位md5
     *
     * @param plainText
     * @return
     */
    public static String stringToMD5(String plainText) {
        byte[] secretBytes = null;
        try {
            secretBytes = MessageDigest.getInstance("md5").digest(
                    plainText.getBytes());
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("没有这个md5算法！");
        }
        String md5code = new BigInteger(1, secretBytes).toString(16);
        for (int i = 0; i < 32 - md5code.length(); i++) {
            md5code = "0" + md5code;
        }
        return md5code.toUpperCase();
    }


    public void writeImage(BufferedImage image, HttpServletResponse response) {
        OutputStream os = null;
        try {
//        读取图片
            response.setContentType("image/png");
            os = response.getOutputStream();
            if (image != null) {
                ImageIO.write(image, "png", os);
            }
        } catch (IOException e) {
            log.error("获取图片异常{}", e.getMessage());
        } finally {
            if (os != null) {
                try {
                    os.flush();
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
    }


    String nameToId(String name) {
        try {
            byte[] data = name.getBytes("utf-8");
            String id = Base64.getEncoder().encodeToString(data);
            return id;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    String idToname(String id) {
        try {
            byte[] data = Base64.getDecoder().decode(id);
            String name = new String(data);
            return  name;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
