package org.example.practice_javafx.proccessImage;

import javafx.scene.image.Image;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

public class RemoveBackground {
    public Image cannyRemoveBackground(String srcImg){
        // Tải thư viện OpenCV
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Đọc ảnh gốc
//        String srcImg ="D:\\ki7\\xu ly anh\\img\\xoa_phong.jpg";
//        String srcImg2 ="D:\\ki7\\xu ly anh\\img\\voi.png";
//        String srcImg3 ="D:\\ki7\\xu ly anh\\img\\cu.png";
        Mat src = Imgcodecs.imread(srcImg);
        if (src.empty()) {
            System.out.println("Không thể tải ảnh");
            throw new RuntimeException("Không thể tải ảnh");
        }

        // Bước 1: Chuyển ảnh sang ảnh xám
        Mat gray = new Mat();
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);

        // Bước 2: Áp dụng bộ lọc Canny để phát hiện cạnh
        Mat edges = new Mat();
        Imgproc.Canny(gray, edges, 100, 200);  // Ngưỡng thấp và cao có thể thay đổi để điều chỉnh kết quả

        // Bước 3: Tìm đường viền (Contours)
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(edges, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // Bước 4: Tạo mặt nạ với vùng vật thể (foreground)
        Mat mask = Mat.zeros(src.size(), CvType.CV_8UC1); // Tạo mặt nạ đen
        Imgproc.drawContours(mask, contours, -1, new Scalar(255), -1);  // Vẽ các đường viền vào mặt nạ

        // Bước 5: Sao chép vật thể vào ảnh mới, thay nền bằng màu trắng (hoặc trong suốt)
        Mat result = new Mat(src.size(), CvType.CV_8UC3, new Scalar(255, 255, 255));  // Màu trắng làm nền
        src.copyTo(result, mask);  // Sao chép vật thể vào ảnh kết quả
        return matToImage1(result);
    }

    public Image removeBackgroundColor(String srcImg) {
        // Tải thư viện OpenCV
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Đọc ảnh đầu vào
        Mat src = Imgcodecs.imread(srcImg);
        if (src.empty()) {
            System.out.println("Không thể tải ảnh");
            throw new RuntimeException("Không thể tải ảnh");
        }

        // Bước 1: Làm mờ ảnh để giảm nhiễu
        Mat blurred = new Mat();
        Imgproc.GaussianBlur(src, blurred, new Size(5, 5), 0);

        // Bước 2: Chuyển ảnh sang xám
        Mat gray = new Mat();
        Imgproc.cvtColor(blurred, gray, Imgproc.COLOR_BGR2GRAY);

        // Bước 3: Áp dụng phát hiện cạnh Canny
        Mat edges = new Mat();
        Imgproc.Canny(gray, edges, 50, 150); // Thay đổi ngưỡng nếu cần

        // Bước 4: Mở rộng các cạnh để tạo vùng đối tượng
        Mat dilatedEdges = new Mat();
        Mat kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(3, 3));
        Imgproc.dilate(edges, dilatedEdges, kernel);

        // Bước 5: Tìm kiếm các vùng đối tượng lớn nhất
        Mat mask = new Mat();
        Imgproc.threshold(dilatedEdges, mask, 0, 255, Imgproc.THRESH_BINARY);

        // Tìm contours để làm mịn vùng đối tượng (tùy chọn)
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(mask, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // Tạo mask chỉ chứa đối tượng lớn nhất
        mask.setTo(new Scalar(0)); // Xóa toàn bộ mask
        Imgproc.drawContours(mask, contours, -1, new Scalar(255), Core.FILLED);

        // Bước 6: Áp dụng mask lên ảnh gốc
        Mat result = new Mat(src.size(), src.type(), new Scalar(255, 255, 255)); // Nền trắng
        src.copyTo(result, mask);

        // Chuyển đổi ảnh kết quả sang định dạng hiển thị được
        return matToImage1(result);
    }

    public Image removeBackgroundWithGrabCut(String srcImg) {
        // Tải thư viện OpenCV
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Đọc ảnh đầu vào
        Mat src = Imgcodecs.imread(srcImg);
        if (src.empty()) {
            System.out.println("Không thể tải ảnh");
            throw new RuntimeException("Không thể tải ảnh");
        }

        // Bước 1: Xác định ROI (Region of Interest)
        // ROI là một hình chữ nhật bao quanh đối tượng
        Rect roi = new Rect(50, 50, src.cols() - 100, src.rows() - 100); // Điều chỉnh tọa độ ROI theo ảnh của bạn

        // Bước 2: Tạo mặt nạ ban đầu
        Mat mask = new Mat(src.size(), CvType.CV_8UC1, new Scalar(Imgproc.GC_PR_BGD)); // Mặc định nền có thể là nền
        Mat bgModel = new Mat(); // Mô hình nền
        Mat fgModel = new Mat(); // Mô hình tiền cảnh (foreground)

        // Bước 3: Áp dụng thuật toán GrabCut
        Imgproc.grabCut(src, mask, roi, bgModel, fgModel, 5, Imgproc.GC_INIT_WITH_RECT);

        // Chuyển đổi mask từ các giá trị GrabCut (GC_BGD, GC_PR_BGD, GC_FGD, GC_PR_FGD)
        // thành mask nhị phân: 0 = nền, 255 = đối tượng
        Mat binMask = new Mat();
        Core.compare(mask, new Scalar(Imgproc.GC_PR_FGD), binMask, Core.CMP_EQ); // Chỉ giữ GC_PR_FGD và GC_FGD
        binMask.convertTo(binMask, CvType.CV_8U, 255); // Chuyển sang giá trị nhị phân 0 và 255

        // Bước 4: Áp dụng mặt nạ lên ảnh gốc
        Mat result = new Mat(src.size(), src.type(), new Scalar(255, 255, 255)); // Nền trắng
        src.copyTo(result, binMask);

        // Chuyển đổi kết quả sang Image để hiển thị
        return matToImage1(result);
    }

    // Phương thức hỗ trợ: Chuyển Mat sang Image
    private Image matToImage1(Mat mat) {
        MatOfByte buffer = new MatOfByte();
        Imgcodecs.imencode(".png", mat, buffer);
        return new Image(new ByteArrayInputStream(buffer.toArray()));
    }




    public Image grabCutRemoveBackground(String srcImg) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Đọc ảnh đầu vào

        Mat src = Imgcodecs.imread(srcImg);
        if (src.empty()) {
            System.out.println("Không thể tải ảnh");
            throw new RuntimeException("Không thể tải ảnh");
        }

        // Tạo mặt nạ và mô hình nền / mô hình vật thể cho GrabCut
        Mat mask = new Mat();
        Mat bgdModel = new Mat();
        Mat fgdModel = new Mat();

        // Xác định bounding box (có thể chỉnh lại để bao quanh vật thể)
        Rect rect = new Rect(50, 50, src.width() - 100, src.height() - 100);

        // Áp dụng thuật toán GrabCut
        Imgproc.grabCut(src, mask, rect, bgdModel, fgdModel, 5, Imgproc.GC_INIT_WITH_RECT);

        // Chuyển đổi các pixel được phân loại là xác định foreground (GC_FGD) hoặc probable foreground (GC_PR_FGD) thành 1, còn lại là 0
        Core.compare(mask, new Scalar(Imgproc.GC_PR_FGD), mask, Core.CMP_EQ);

        // Tạo ảnh kết quả bằng cách giữ lại những pixel foreground trong ảnh gốc
        Mat foreground = new Mat(src.size(), CvType.CV_8UC3, new Scalar(255, 255, 255)); // Màu trắng cho nền
        src.copyTo(foreground, mask);
        return matToImage1(foreground);
    }
    public Image sobelRemoveBackground(String srcImg){
        // Tải thư viện OpenCV
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Đọc ảnh đầu vào
        Mat src = Imgcodecs.imread(srcImg);
        if (src.empty()) {
            System.out.println("Không thể tải ảnh");
            throw new RuntimeException("Không thể tải ảnh");
        }

        // Bước 1: Chuyển ảnh sang ảnh xám
        Mat gray = new Mat();
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);

        // Bước 2: Áp dụng bộ lọc Sobel để phát hiện biên
        Mat gradX = new Mat();
        Mat gradY = new Mat();

        // Sobel theo chiều dọc (x)
        Imgproc.Sobel(gray, gradX, CvType.CV_32F, 1, 0, 3);
        // Sobel theo chiều ngang (y)
        Imgproc.Sobel(gray, gradY, CvType.CV_32F, 0, 1, 3);

        // Bước 3: Tính toán độ mạnh gradient (magnitude)
        Mat magnitude = new Mat();
        Core.magnitude(gradX, gradY, magnitude);

        // Bước 4: Áp dụng ngưỡng hóa để tạo mặt nạ
        Mat edges = new Mat();
        Imgproc.threshold(magnitude, edges, 50, 255, Imgproc.THRESH_BINARY);  // Ngưỡng có thể điều chỉnh

        // Chuyển mặt nạ edges sang kiểu CV_8U
        edges.convertTo(edges, CvType.CV_8U);  // Chuyển đổi sang kiểu CV_8U (8-bit unsigned)

        // Bước 5: Tạo mặt nạ và sao chép vật thể vào ảnh kết quả
        Mat mask = new Mat();
        edges.copyTo(mask);

        // Tạo ảnh kết quả với nền trắng
        Mat result = new Mat(src.size(), CvType.CV_8UC3, new Scalar(255, 255, 255)); // Màu trắng cho nền
        src.copyTo(result, mask);  // Sao chép vật thể vào ảnh kết quả
        return matToImage1(result);
    }

    public Image laplacianRemoveBackground(String srcImg){
        // Tải thư viện OpenCV
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Đọc ảnh đầu vào
        Mat src = Imgcodecs.imread(srcImg);
        if (src.empty()) {
            System.out.println("Không thể tải ảnh");
            throw new RuntimeException("Không thể tải ảnh");
        }

        // Bước 1: Chuyển ảnh sang ảnh xám
        Mat gray = new Mat();
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_BGR2GRAY);

        // Bước 2: Áp dụng bộ lọc Laplacian
        Mat laplacian = new Mat();
        Imgproc.Laplacian(gray, laplacian, CvType.CV_32F);

        // Bước 3: Tính toán độ mạnh gradient (magnitude)
        Mat absLaplacian = new Mat();
        Core.absdiff(laplacian, new Scalar(0), absLaplacian);

        // Bước 4: Áp dụng ngưỡng hóa để tạo mặt nạ
        Mat edges = new Mat();
        Imgproc.threshold(absLaplacian, edges, 50, 255, Imgproc.THRESH_BINARY);  // Ngưỡng có thể điều chỉnh

        // Chuyển mặt nạ edges sang kiểu CV_8U
        edges.convertTo(edges, CvType.CV_8U);

        // Bước 5: Tạo mặt nạ và sao chép vật thể vào ảnh kết quả
        Mat mask = new Mat();
        edges.copyTo(mask);

        // Tạo ảnh kết quả với nền trắng
        Mat result = new Mat(src.size(), CvType.CV_8UC3, new Scalar(255, 255, 255)); // Màu trắng cho nền
        src.copyTo(result, mask);  // Sao chép vật thể vào ảnh kết quả
        return matToImage1(result);
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
