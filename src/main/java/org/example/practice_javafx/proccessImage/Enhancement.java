package org.example.practice_javafx.proccessImage;

import javafx.scene.image.Image;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

public class Enhancement {



    public Image cannyEnhancementColor(String srcImg) {
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

        // Áp dụng thuật toán Canny để phát hiện cạnh
        Mat edges = new Mat();
        double threshold1 = 50;  // Ngưỡng thấp
        double threshold2 = 150; // Ngưỡng cao
        Imgproc.Canny(blurred, edges, threshold1, threshold2);

        // Chuyển kết quả phát hiện cạnh sang ảnh màu
        Mat edgesColor = new Mat();
        Imgproc.cvtColor(edges, edgesColor, Imgproc.COLOR_GRAY2BGR);

        // Tăng cường cạnh bằng cách cộng kết quả phát hiện cạnh với ảnh gốc
        Mat enhancedImage = new Mat();
        Core.addWeighted(src, 0.8, edgesColor, 0.2, 0, enhancedImage);

        // Chuyển đổi kết quả sang định dạng hiển thị
        return matToImage1(enhancedImage);
    }
    public Image sobelEnhancementColor(String imgSrc) {
        // Load thư viện OpenCV
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Đọc ảnh gốc (ảnh màu)
        Mat src = Imgcodecs.imread(imgSrc);

        // Kiểm tra ảnh có tải được không
        if (src.empty()) {
            System.out.println("Không thể tải ảnh");
            throw new RuntimeException("Không thể tải ảnh");
        }

        // Tách ảnh thành các kênh màu (B, G, R)
        List<Mat> channels = new ArrayList<>();
        Core.split(src, channels); // Chia ảnh thành 3 kênh

        List<Mat> enhancedChannels = new ArrayList<>();

        // Áp dụng Sobel trên từng kênh
        for (Mat channel : channels) {
            Mat gradX = new Mat();
            Mat gradY = new Mat();

            // Gradient theo x và y
            Imgproc.Sobel(channel, gradX, CvType.CV_64F, 1, 0); // Gradient theo x
            Imgproc.Sobel(channel, gradY, CvType.CV_64F, 0, 1); // Gradient theo y

            // Chuyển đổi sang kiểu 8-bit để hiển thị
            Core.convertScaleAbs(gradX, gradX);
            Core.convertScaleAbs(gradY, gradY);

            // Kết hợp hai gradient x và y
            Mat edgeDetected = new Mat();
            Core.addWeighted(gradX, 0.5, gradY, 0.5, 0, edgeDetected);

            // Tăng cường kênh bằng cách cộng gradient với kênh gốc
            Mat enhancedChannel = new Mat();
            Core.add(channel, edgeDetected, enhancedChannel);

            // Lưu kênh đã xử lý vào danh sách
            enhancedChannels.add(enhancedChannel);
        }

        // Gộp các kênh đã xử lý lại thành ảnh màu
        Mat result = new Mat();
        Core.merge(enhancedChannels, result);

        // Chuyển đổi ảnh kết quả sang định dạng hiển thị
        return matToImage1(result);
    }

    public Image LaplacianEnhancementColor(String imgSrc) {
        // Load thư viện OpenCV
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Đọc ảnh gốc
        Mat src = Imgcodecs.imread(imgSrc);
        if (src.empty()) {
            System.out.println("Không thể đọc ảnh");
            throw new RuntimeException("Không thể tải ảnh");
        }

        // Tách ảnh thành các kênh màu (B, G, R)
        List<Mat> channels = new ArrayList<>();
        Core.split(src, channels);

        List<Mat> enhancedChannels = new ArrayList<>();

        // Áp dụng Laplacian trên từng kênh
        for (Mat channel : channels) {
            Mat laplacian = new Mat();
            Imgproc.Laplacian(channel, laplacian, CvType.CV_64F);

            // Chuyển đổi giá trị tuyệt đối của ảnh Laplacian
            Core.convertScaleAbs(laplacian, laplacian);

            // Tăng cường kênh bằng cách cộng Laplacian vào kênh gốc
            Mat enhancedChannel = new Mat();
            Core.add(channel, laplacian, enhancedChannel);

            // Lưu kênh đã xử lý vào danh sách
            enhancedChannels.add(enhancedChannel);
        }

        // Gộp các kênh đã xử lý lại thành ảnh màu
        Mat enhancedImage = new Mat();
        Core.merge(enhancedChannels, enhancedImage);

        // Chuyển đổi ảnh kết quả sang định dạng hiển thị
        return matToImage1(enhancedImage);
    }

    // Hàm chuyển đổi từ Mat sang Image để hiển thị
    private Image matToImage1(Mat mat) {
        MatOfByte buffer = new MatOfByte();
        Imgcodecs.imencode(".png", mat, buffer);
        return new Image(new ByteArrayInputStream(buffer.toArray()));
    }


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
