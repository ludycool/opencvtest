package com.topband.opencvtest.common;

import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLDecoder;

/**
 * @author ludi
 * @version 1.0
 * @date 2020/9/22 13:51
 * @remark
 */
public class FileUtil {

    //region 文件

    /**
     * 程序根路径
     */
    static String AppicationPath = "";
    /**
     * 获取程序根路径 如jar的根路径  最后面无斜杠 如d:/app
     *
     * @return
     */
    public static String getAppicationPath() {
        if (!"".equals(AppicationPath))
            return AppicationPath;
        // 获取跟目录
        File path = null;
        try {
            path = new File(ResourceUtils.getURL("classpath:").getPath());
        } catch (FileNotFoundException e) {
            // nothing to do
        }
        if (path == null || !path.exists()) {
            path = new File("");
        }

        String pathStr = path.getAbsolutePath();
        // 如果是在eclipse中运行，则和target同级目录,如果是jar部署到服务器，则默认和jar包同级
        pathStr = pathStr.replace("\\target\\classes", "");
        return pathStr;
    }

    /**
     * 把资源文件复制到程序根目录，返回资源文件物理绝对路径
     * 当打成一个jar包后，整个jar包是一个文件，只能使用流的方式读取资源，这时候就不能通过File来操作资源了。在IDE中之所以能正常运行，是因为IDE中的资源文件在target/classes目录下，是正常的文件系统结构
     * 只能创建临时文件(与jar包相同路径)，用流读取文件内容输出到临时文件中，来获取临时文件路径代替项目中文件路径
     * <p>
     * test   String appPath=FileUtil.getResourceAbsolutePath("sigar"+File.separator+".sigar_shellrc");
     * System.err.println(appPath);
     *
     * @param fileName 资源文件夹里的相对路径 不能以/开头 如：files/filePath.xlsx
     * @return
     */
    public static String getResourceAbsolutePath(String fileName) {
        String appPath = getAppicationPath();
        String fullPath = appPath + File.separator + fileName;
        try {
            InputStream inputStream = getResourceStream(fileName);
            boolean sucee = createFile(fullPath, inputStream, false);
            return fullPath;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    /**
     * 获取资源文件流
     *
     * @param fileName 不能以/开头 如：files/filePath.xlsx
     * @return
     */
    public static InputStream getResourceStream(String fileName) {
        ClassPathResource resource = new ClassPathResource(fileName);
        try {
            return resource.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 是否强制新建文件
     *
     * @param path     文件路径
     * @param isDelete 文件存在后是否删除标记
     * @return 文件创建是否成功标记
     * @throws IOException
     * @see 1.原文件存在的情况下会删除原来的文件，重新创建一个新的同名文件，本方法返回文件创建成功标记
     * @see 2.原文件存在但isDelete参数设置为false，表示不删除源文件，本方法返回文件创建失败标记
     */
    public static boolean createFile(String path, InputStream ins, boolean isDelete) throws IOException {


        // 加载文件
        File file = new File(urlDecoding(path));
        File fileParent = file.getParentFile();
        if (!fileParent.exists()) {//文件夹不存在创建
            fileParent.mkdirs();
        }
        if (file.exists()) {
            if (isDelete) { // 文件存在后删除文件
                // 删除原文件
                file.delete();
            } else {
                return false;
            }
        }

        if (ins != null) {
            OutputStream os = null;
            try {
                os = new FileOutputStream(file);
                int bytesRead = 0;
                byte[] buffer = new byte[1024];
                while ((bytesRead = ins.read(buffer, 0, 1024)) != -1) {
                    os.write(buffer, 0, bytesRead);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (os != null) {
                    os.close();
                }
                if (ins != null) {
                    ins.close();
                }
            }
        }
        file.createNewFile();
        return true;
    }
    /**
     * 将utf-8编码的汉字转为中文
     * @author zhaoqiang
     * @param str
     * @return
     */
    public static String urlDecoding(String str){
        String result = str;
        try
        {
            result = URLDecoder.decode(str, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        return result;
    }
    //endregion

    /**
     * MultipartFile 转 File
     *
     * @param file
     * @throws Exception
     */
    public static File multipartFileToFile(MultipartFile file) throws Exception {

        File toFile = null;
        if (file.equals("") || file.getSize() <= 0) {
            file = null;
        } else {
            InputStream ins = null;
            ins = file.getInputStream();
            toFile = new File(file.getOriginalFilename());
            inputStreamToFile(ins, toFile);
            ins.close();
        }
        return toFile;
    }
    //获取流文件
    private static void inputStreamToFile(InputStream ins, File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
