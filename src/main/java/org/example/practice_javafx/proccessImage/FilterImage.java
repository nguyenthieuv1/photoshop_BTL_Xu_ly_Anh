package org.example.practice_javafx.proccessImage;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class FilterImage {
    private Mat src;
    private String imgSrc;
    private Mat dst;

    public FilterImage(String imgSrc) {
        this.imgSrc = imgSrc;
        // Load thư viện OpenCV
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Đọc ảnh từ file
        src = Imgcodecs.imread(imgSrc);

        // Kiểm tra ảnh có tải được không
        if (src.empty()) {
            System.out.println("Không thể tải ảnh");
            throw new RuntimeException("Không thể tải ảnh");
        }
    }

    public Image locAmBan(){
        // Áp dụng bộ lọc Sobel theo hướng x và y
        Mat gradX = new Mat();
        Mat gradY = new Mat();
        Imgproc.Sobel(src, gradX, CvType.CV_64F, 1, 0); // Gradient theo x
        Imgproc.Sobel(src, gradY, CvType.CV_64F, 0, 1); // Gradient theo y

        // Chuyển đổi sang kiểu 8-bit để hiển thị
        Core.convertScaleAbs(gradX, gradX);
        Core.convertScaleAbs(gradY, gradY);

        // Kết hợp hai gradient x và y
        Mat negativeImage = new Mat();
        Core.bitwise_not(src, negativeImage);
        return matToImage(negativeImage);
    }


    public Image locTrungVi(){
        // Tạo ma trận ảnh đầu ra cho kết quả sau khi làm mịn
        Mat dst = new Mat();
        // Áp dụng bộ lọc trung vị với kích thước kernel 5
        int kernelSize = 5; // Kích thước kernel là số lẻ, ví dụ 3, 5, 7, ...
        Imgproc.medianBlur(src, dst, kernelSize);
        return matToImage(dst);
    }
    public Image locTrungBinh(){
        // Tạo ma trận ảnh đầu ra cho kết quả sau khi làm mịn
        Mat dst = new Mat();
        // Áp dụng bộ lọc trung bình với kích thước kernel 5x5
        Imgproc.blur(src, dst, new org.opencv.core.Size(5, 5));
        return matToImage(dst);
    }

    public Image locMax(){
        // Tạo kernel kích thước 3x3
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));

        // Ảnh đầu ra cho kết quả sau khi áp dụng bộ lọc Max
        Mat maxFiltered = new Mat();
        Imgproc.dilate(src, maxFiltered, kernel); // Bộ lọc Max - Dilation
        return matToImage(maxFiltered);
    }
    public Image locMin(){
        // Tạo kernel kích thước 3x3
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));

        // Ảnh đầu ra cho kết quả sau khi áp dụng bộ lọc Min
        Mat minFiltered = new Mat();
        Imgproc.erode(src, minFiltered, kernel); // Bộ lọc Min - Erosion
        return matToImage(minFiltered);
    }

    private Image matToImage(Mat mat) {
        // Convert Mat to Image (WritableImage)
        int width = mat.width();
        int height = mat.height();
        int channels = mat.channels();

        byte[] pixels = new byte[width * height * channels];
        mat.get(0, 0, pixels); // Get pixel data from Mat

        // Create a WritableImage and set its pixel data
        javafx.scene.image.WritableImage image = new javafx.scene.image.WritableImage(width, height);
        image.getPixelWriter().setPixels(0, 0, width, height,
                javafx.scene.image.PixelFormat.getByteRgbInstance(), pixels, 0, width * channels);

        return image;
    }


}
