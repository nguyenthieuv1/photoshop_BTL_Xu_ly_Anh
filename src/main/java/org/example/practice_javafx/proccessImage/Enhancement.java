package org.example.practice_javafx.proccessImage;

import javafx.scene.image.Image;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class Enhancement {

    public Image cannyEnhancement(String srcImg) {
        // Load thư viện OpenCV
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Đọc ảnh gốc
        Mat src = Imgcodecs.imread(srcImg);
        if (src.empty()) {
            System.out.println("Không thể đọc ảnh");
            throw new RuntimeException("Không thể tải ảnh");
        }

        // Chuyển ảnh sang thang độ xám
        Mat gray = new Mat();
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);

        // Làm mờ ảnh để giảm nhiễu
        Mat blurred = new Mat();
        Imgproc.GaussianBlur(gray, blurred, new Size(5, 5), 1.5);

        // Áp dụng thuật toán Canny
        Mat canny = new Mat();
        double threshold1 = 50;  // Ngưỡng thấp
        double threshold2 = 150; // Ngưỡng cao
        Imgproc.Canny(blurred, canny, threshold1, threshold2);

        Mat cannyColor = new Mat();
        Imgproc.cvtColor(canny, cannyColor, Imgproc.COLOR_GRAY2BGR);

        Mat result = new Mat();
        Core.add(src, cannyColor, result);
        return matToImage(result);
    }

    public Image sobelEnhancement(String imgSrc){
        // Load thư viện OpenCV
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Đọc ảnh từ file (ảnh xám)
        Mat src = Imgcodecs.imread(imgSrc);

        // Kiểm tra ảnh có tải được không
        if (src.empty()) {
            System.out.println("Không thể tải ảnh");
            throw new RuntimeException("Không thể tải ảnh");
        }

        // Áp dụng bộ lọc Sobel theo hướng x và y
        Mat gradX = new Mat();
        Mat gradY = new Mat();
        Imgproc.Sobel(src, gradX, CvType.CV_64F, 1, 0); // Gradient theo x
        Imgproc.Sobel(src, gradY, CvType.CV_64F, 0, 1); // Gradient theo y

        // Chuyển đổi sang kiểu 8-bit để hiển thị
        Core.convertScaleAbs(gradX, gradX);
        Core.convertScaleAbs(gradY, gradY);

        // Kết hợp hai gradient x và y
        Mat edgeDetected = new Mat();
        Core.addWeighted(gradX, 0.5, gradY, 0.5, 0, edgeDetected);

        Mat result = new Mat();
        Core.add(src, edgeDetected, result);
        return matToImage(result);
    }

    public Image LaplacianEnhancement(String imgSrc){
        // Load thư viện OpenCV
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Đọc ảnh gốc
        Mat src = Imgcodecs.imread(imgSrc);
        if (src.empty()) {
            System.out.println("Không thể đọc ảnh");
            throw new RuntimeException("Không thể tải ảnh");
        }

        // Chuyển ảnh sang thang độ xám
        Mat gray = new Mat();
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);

        // Áp dụng bộ lọc Laplacian
        Mat laplacian = new Mat();
        Imgproc.Laplacian(gray, laplacian, CvType.CV_64F);

        // Tính toán giá trị tuyệt đối của ảnh Laplacian để loại bỏ các giá trị âm
        Core.convertScaleAbs(laplacian, laplacian);

        // Chuyển ảnh laplacian thành 3 kênh để khớp với ảnh gốc
        Mat laplacianColor = new Mat();
        Imgproc.cvtColor(laplacian, laplacianColor, Imgproc.COLOR_GRAY2BGR);

        // Tăng cường ảnh bằng cách cộng Laplacian vào ảnh gốc
        Mat enhanced = new Mat();
        Core.add(src, laplacianColor, enhanced);

        return matToImage(enhanced);
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
