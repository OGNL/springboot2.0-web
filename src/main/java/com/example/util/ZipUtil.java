package com.example.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.util.Base64Utils;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Decoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtil {

    /**
     * dpi越大转换后越清晰，相对转换速度越慢
     */
    private static final Integer DPI = 100;
    /**
     * 转换后的图片类型
     */
    private static final String IMG_TYPE_PNG = "png";



    public static Map<String, String> unzipImageToBase64(InputStream inputStream){
        ZipInputStream zis = null;
        Map<String,String> base64 = null;
        try {
            zis = new ZipInputStream(inputStream, Charset.forName("GBK"));
            base64 = new HashMap<>();
            ZipEntry entry = null;
            while ((entry = zis.getNextEntry()) != null) {

                String fullName = suffix2LowerCase(entry.getName());
                //忽略mac 压缩后多余文件
                if(fullName.startsWith("__MACOSX")){
                    continue;
                }

                if(entry.isDirectory()){

                }else{
                    if(fullName.endsWith("pdf") || fullName.endsWith("jpg") || fullName.endsWith("jpeg") || fullName.endsWith("png")){
                        byte[] data = null;
                        if(entry.getSize() == -1){
                            // ZipEntry的size可能为-1，表示未知
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            while (true) {
                                int bytes = zis.read();
                                if (bytes == -1) break;
                                baos.write(bytes);
                            }
                            data = baos.toByteArray();
                        }else{
                            // ZipEntry的size正常
                            int len = new Long(entry.getSize()).intValue();
                            data = new byte[len];
                            int actual = 0;
                            int bytesread = 0;
                            while ((bytesread != len) && (actual != -1)) {
                                actual = zis.read(data, bytesread, len
                                        - bytesread);
                                bytesread += actual;
                            }
                        }

                        String fileStr = null;
//                    if(fullName.endsWith("jpg")){
//                        BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(data));
//                        ByteArrayOutputStream out = new ByteArrayOutputStream();
//                        ImageIO.write(bufferedImage, IMG_TYPE_PNG, out);
//                        fileStr = Base64Utils.encodeToString(out.toByteArray());

//                        generateImage(fileStr, "C:\\Users\\zhangtao\\Desktop\\test08\\" + data.length + ".png");
//                    }else
                        if(fullName.endsWith("pdf")){
                            List<byte[]> imgList = pdfToImage(data);
                            byte[] imageByte = compressImage(imgList.get(0), 720);
                            if(imageByte != null){
                                fileStr = Base64Utils.encodeToString(imageByte);

//                            generateImage(fileStr, "C:\\Users\\zhangtao\\Desktop\\test08\\" + imageByte.length + ".png");
                            }

                        }else if (fullName.endsWith("jpg") || fullName.endsWith("jpeg") || fullName.endsWith("png")){
                            data = compressImage(data, 720);
                            if(data != null){
                                fileStr = Base64Utils.encodeToString(data);

//                            generateImage(fileStr, "C:\\Users\\zhangtao\\Desktop\\test08\\" + data.length + ".png");
                            }

                        }
                        base64.put(fullName,  fileStr);
                    }else{
                        base64.put(fullName, null);
                    }
                }
            }
            zis.closeEntry();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return base64;
    }


    private static String suffix2LowerCase(String fileName){
        String suffix = fileName.substring(fileName.lastIndexOf(".")+1);
        fileName = fileName.substring(0,fileName.lastIndexOf(".")+1) + suffix.toLowerCase();
        return  fileName;
    }


    //保存zip文件
    public static String saveZipFile(MultipartFile file, String path) {
        String resultPath = "";
        ZipInputStream zipInputStream = null;
        FileOutputStream fs = null;
        try {
            resultPath =  path + file.getOriginalFilename();
            File zipFile = new File(resultPath);
            if (!zipFile.exists()) {
                new File(zipFile.getParent()).mkdirs();
                zipFile.createNewFile();
            }
            zipInputStream = new ZipInputStream(file.getInputStream(), Charset.forName("GBK"));
            BufferedInputStream stream = new BufferedInputStream(file.getInputStream());
            fs = new FileOutputStream(zipFile);
            byte[] buffer = new byte[1024];
            int i = -1;
            while ((i = stream.read(buffer)) != -1) {
                fs.write(buffer, 0, i);
                fs.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fs.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                zipInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultPath;
    }

    public static boolean generateImage(String imgStr, String imgFilePath) {// 对字节数组字符串进行Base64解码并生成图片
        if (imgStr == null) // 图像数据为空
            return false;
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            // Base64解码
            byte[] bytes = decoder.decodeBuffer(imgStr);
            for (int i = 0; i < bytes.length; ++i) {
                if (bytes[i] < 0) {// 调整异常数据
                    bytes[i] += 256;
                }
            }
            // 生成jpeg图片
            OutputStream out = new FileOutputStream(imgFilePath);
            out.write(bytes);
            out.flush();
            out.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * PDF转图片
     *
     * @param fileContent PDF文件的二进制流
     * @return 图片文件的二进制流
     */
    public static List<byte[]> pdfToImage(byte[] fileContent) throws IOException {
        List<byte[]> result = new ArrayList<>();
        try (PDDocument document = PDDocument.load(fileContent)) {
            PDFRenderer renderer = new PDFRenderer(document);
            for (int i = 0; i < document.getNumberOfPages(); ++i) {
                BufferedImage bufferedImage = renderer.renderImageWithDPI(i, DPI);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, IMG_TYPE_PNG, out);
                result.add(out.toByteArray());
            }
        }
        return result;
    }


    /**
     * 文件大小压缩
     * @param imageByte
     * @param ppi
     * @return
     */
    public static byte[] compressImage(byte[] imageByte, int ppi) {
        byte[] smallImage = null;
        int width = 0, height = 0;

        if (imageByte == null)
            return null;

        ByteArrayInputStream byteInput = new ByteArrayInputStream(imageByte);
        try {
            Image image = ImageIO.read(byteInput);
            int w = image.getWidth(null);
            int h = image.getHeight(null);
            // adjust weight and height to avoid image distortion
            double scale = 0;
            scale = Math.min((float) ppi / w, (float) ppi / h);
            width = (int) (w * scale);
            width -= width % 4;
            height = (int) (h * scale);

            if (scale >= (double) 1)
                return imageByte;

            BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            buffImg.getGraphics().drawImage(image.getScaledInstance(width, height, Image.SCALE_SMOOTH), 0, 0, null);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ImageIO.write(buffImg, "png", out);
            smallImage = out.toByteArray();
            return smallImage;

        } catch (IOException e) {
            return null;
        }
    }








}
