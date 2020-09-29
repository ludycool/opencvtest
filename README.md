# opencv+opencv_contrib的java 实现人脸识别，训练、匹配

## 环境运行

如果不想编译安装，可简单的引用dll或so库
### window
只需要引用opencv_java440.dll库文件 <br/>
### linux
```
#解压opencv4.zip 文件
#把libopencv_java440.so 复制到任意位置
#java 应用里设置 so库 引用位置


#把lib里的 so 文件复制 到/usr/local/lib

# 安装Build tools:该命令将安装一堆新包，包括gcc，g ++
sudo apt-get install -y build-essential

# 安装 Media I/O:
sudo apt-get install -y zlib1g-dev libjpeg-dev libwebp-dev libpng-dev libtiff5-dev libjasper-dev libopenexr-dev libgdal-dev

# 安装 Video I/O:
sudo apt-get install -y libdc1394-22-dev libavcodec-dev libavformat-dev libswscale-dev libtheora-dev libvorbis-dev libxvidcore-dev libx264-dev yasm libopencore-amrnb-dev libopencore-amrwb-dev libv4l-dev libxine2-dev
 
```

 ## docker 使用打包好的环境镜像
```
docker pull ludycool/ubuntu1604-opencv
```
