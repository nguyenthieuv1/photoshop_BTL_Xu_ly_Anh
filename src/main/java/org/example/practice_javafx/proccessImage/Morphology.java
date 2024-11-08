package org.example.practice_javafx.proccessImage;

import javafx.scene.image.Image;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

public class Morphology {
    public Image erosion(String srcImg) {
        // Load thư viện OpenCV
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Đọc ảnh từ file (ảnh xám)
        Mat src = Imgcodecs.imread(srcImg);

        // Kiểm tra ảnh có tải được không
        if (src.empty()) {
            System.out.println("Không thể tải ảnh");
            throw new RuntimeException("Không thể tải ảnh");
        }

        // Khởi tạo kernel (ma trận hình chữ nhật 3x3)
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));

        // Tạo một biến đầu ra
        Mat erodedImage = new Mat();
        // Thực hiện phép Erosion
        Imgproc.erode(src, erodedImage, kernel);

        return matToImage(erodedImage);
    }
    public Image dilation(String srcImg) {
        // Load thư viện OpenCV
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Đọc ảnh từ file (ảnh xám)
        Mat src = Imgcodecs.imread(srcImg);

        // Kiểm tra ảnh có tải được không
        if (src.empty()) {
            System.out.println("Không thể tải ảnh");
            throw new RuntimeException("Không thể tải ảnh");
        }

        // Khởi tạo kernel (ma trận hình chữ nhật 3x3)
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));

        // Tạo một biến đầu ra
        Mat dilateImage = new Mat();
        // Thực hiện phép Erosion
        Imgproc.dilate(src, dilateImage, kernel);

        return matToImage(dilateImage);
    }
    public Image matToImage(Mat mat) {
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
