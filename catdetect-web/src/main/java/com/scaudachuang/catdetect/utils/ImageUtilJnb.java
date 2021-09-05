package com.scaudachuang.catdetect.utils;

import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.logging.log4j.util.Base64Util;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Arrays;

@Slf4j
public class ImageUtilJnb {

    public static void main(String[] args) throws IOException {

        String fullPath="D:\\123.jpg";
        String newPath="D:\\321322198511205873F.jpg";

        long l = System.currentTimeMillis();
        File img = new File(fullPath);
        BufferedImage bim = ImageIO.read(img);
        int srcWidth = bim.getWidth();
        int srcHeight = bim.getHeight();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Thumbnails.of(img).size(srcWidth, srcHeight)
                .toOutputStream(out);
        byte[] bytes = out.toByteArray();
        System.out.println(bytes.length);
        System.out.println("time: " + (System.currentTimeMillis() - l));
        bytes = compressPicCycle(bytes, 100, 0.8);
        System.out.println(bytes.length);
        System.out.println("time: " + (System.currentTimeMillis() - l));
        File file = new File(newPath);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(bytes);
        fileOutputStream.close();
    }

    public static byte[] compressMMultipartFile(MultipartFile file) throws IOException {
        return compressPicCycle(file.getBytes(), 100/*kb*/, 0.8);
    }

    private static byte[] compressPicCycle(byte[] bytes, long desFileSize, double accuracy) throws IOException {
        long srcFileSizeJPG = bytes.length;
        // 2、判断大小，如果小于500kb，不压缩；如果大于等于500kb，压缩
        if (srcFileSizeJPG <= desFileSize * 1024) {
            return bytes;
        }
        // 计算宽高
        BufferedImage bim = ImageIO.read(new ByteArrayInputStream(bytes));
        int srcW = bim.getWidth();
        int srcH= bim.getHeight();
        int desWidth = new BigDecimal(srcW).multiply(
                new BigDecimal(accuracy)).intValue();
        int desHeight = new BigDecimal(srcH).multiply(
                new BigDecimal(accuracy)).intValue();

        ByteArrayOutputStream baos = new ByteArrayOutputStream(); //字节输出流（写入到内存）
        Thumbnails.of(new ByteArrayInputStream(bytes)).size(desWidth, desHeight).outputQuality(accuracy).toOutputStream(baos);
        return compressPicCycle(baos.toByteArray(), desFileSize, accuracy);
    }
}