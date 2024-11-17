package org.example.practice_javafx.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;

import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.practice_javafx.proccessImage.Enhancement;
import org.example.practice_javafx.proccessImage.FilterImage;
import org.example.practice_javafx.proccessImage.Morphology;
import org.example.practice_javafx.proccessImage.RemoveBackground;
import org.opencv.core.Mat;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.IOException;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

public class MainViewController {
    private String mainImagePath;
    private FilterImage proccessIMG;
    private RemoveBackground removeBackgroundClass;
    private Image originImage;
    private Enhancement enhancementClass;
    private Morphology morphologyClass;

    @FXML
    private ImageView mainImage;

    @FXML
    private ImageView image1;

    @FXML
    private ImageView image2;

    @FXML
    private ImageView image3;

    @FXML
    private ImageView image4;

    @FXML
    private ImageView image5;

    @FXML
    private ImageView image6;

    @FXML
    private Button importImageButton;

    @FXML
    private Button save;

    @FXML
    private Button smooth;

    @FXML
    private Button enhancement;

    @FXML
    private Button morphology;

    @FXML
    private Label imageLabel1;

    @FXML
    private Label imageLabel2;

    @FXML
    private Label imageLabel3;

    @FXML
    private Label imageLabel4;

    @FXML
    private Label imageLabel5;

    @FXML
    private Label imageLabel6;

    @FXML
    private Button removeBackground;

    @FXML
    private StackPane box1;

    @FXML
    private StackPane box2;

    @FXML
    private StackPane box3;

    @FXML
    private StackPane box4;

    @FXML
    private StackPane box5;

    @FXML
    private StackPane box6;

    private Image compressedImage;

//    @FXML
//    private Button compressButton;

//    @FXML
//    private Button downloadButton;

//    @FXML
//    private Button formatButton;

    @FXML
    private Button whiteBalanceButton;

    @FXML
    public void initialize() {
        importImageButton.setOnAction(event -> importImage());
        whiteBalanceButton.setOnAction(event -> applyWhiteBalance());
        smooth.setOnAction(event -> smoothImage());
        actionBtnThumbnail();
        removeBackground.setOnAction(event -> removeBackgroundImage());
        enhancement.setOnMouseClicked(event -> enhancementImage());
        save.setOnMouseClicked(event -> saveImg());
        morphology.setOnMouseClicked(event -> morphologyImage());
//        formatButton.setOnAction(event -> convertAndSaveImage());
//        compressButton.setOnAction(event -> compressImage());
//        downloadButton.setOnAction(event -> downloadCompressedImage());
    }

    private void applyWhiteBalance() {
        if (mainImage.getImage() == null) {
            System.out.println("Không có ảnh nào để cân bằng trắng.");
            return;
        }

        WritableImage balancedImage = applyWhiteBalanceToImage(mainImage.getImage());
        mainImage.setImage(balancedImage);
        System.out.println("Đã áp dụng cân bằng trắng.");
    }

    private WritableImage applyWhiteBalanceToImage(Image image) {
        PixelReader pixelReader = image.getPixelReader();
        int width = (int) image.getWidth();
        int height = (int) image.getHeight();

        long totalRed = 0, totalGreen = 0, totalBlue = 0;
        int totalPixels = width * height;

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = pixelReader.getArgb(x, y);
                totalRed += (argb >> 16) & 0xFF;
                totalGreen += (argb >> 8) & 0xFF;
                totalBlue += argb & 0xFF;
            }
        }

        double avgRed = totalRed / (double) totalPixels;
        double avgGreen = totalGreen / (double) totalPixels;
        double avgBlue = totalBlue / (double) totalPixels;

        double redFactor = avgRed / avgGreen;
        double blueFactor = avgBlue / avgGreen;

        WritableImage writableImage = new WritableImage(width, height);
        var pixelWriter = writableImage.getPixelWriter();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = pixelReader.getArgb(x, y);

                int red = (int) (((argb >> 16) & 0xFF) / redFactor);
                int green = (argb >> 8) & 0xFF;
                int blue = (int) ((argb & 0xFF) / blueFactor);

                red = Math.min(255, Math.max(0, red));
                green = Math.min(255, Math.max(0, green));
                blue = Math.min(255, Math.max(0, blue));

                int newArgb = (argb & 0xFF000000) | (red << 16) | (green << 8) | blue;
                pixelWriter.setArgb(x, y, newArgb);
            }
        }

        return writableImage;
    }


    @FXML
    private void convertAndSaveImage() {
        if (mainImage.getImage() == null) {
            System.out.println("Không có ảnh nào để lưu.");
            return;
        }

        // Mở cửa sổ chọn file
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Lưu ảnh với định dạng mới");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PNG Files", "*.png"),
                new FileChooser.ExtensionFilter("JPEG Files", "*.jpg"),
                new FileChooser.ExtensionFilter("WebP Files", "*.webp")
        );

        File file = fileChooser.showSaveDialog(new Stage());
        if (file == null) {
            System.out.println("Người dùng đã hủy chọn file.");
            return;
        }

        try {
            // Xác định định dạng dựa trên phần mở rộng của file
            String format;
            if (file.getPath().endsWith(".png")) {
                format = "png";
            } else if (file.getPath().endsWith(".jpg")) {
                format = "jpg";
            } else if (file.getPath().endsWith(".webp")) {
                format = "webp";
            } else {
                System.out.println("Định dạng không hỗ trợ.");
                return;
            }

            System.out.println("Bắt đầu lưu ảnh vào file: " + file.getAbsolutePath());

            // Chuyển đổi Image của JavaFX thành BufferedImage
            BufferedImage bufferedImage = convertToBufferedImage(mainImage.getImage());

            // Ghi ảnh vào file với định dạng đã chọn
            boolean saved = ImageIO.write(bufferedImage, format, file);
            if (saved) {
                System.out.println("Đã lưu ảnh thành công ở định dạng " + format.toUpperCase());
            } else {
                System.out.println("Lỗi khi ghi ảnh: Định dạng không được hỗ trợ.");
            }

        } catch (IOException e) {
            System.out.println("Lỗi khi lưu ảnh: " + e.getMessage());
            e.printStackTrace();
        }
    }




    private void compressImage() {
        if (mainImagePath == null) {
            System.out.println("No image to compress.");
            return;
        }

        try {
            File inputFile = new File(mainImagePath);
            BufferedImage image = ImageIO.read(inputFile);

            // Giảm độ phân giải xuống một nửa
            int newWidth = image.getWidth() / 2;
            int newHeight = image.getHeight() / 2;
            BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_BYTE_GRAY);
            Graphics2D g = resizedImage.createGraphics();
            g.drawImage(image, 0, 0, newWidth, newHeight, null);
            g.dispose();

            // Tạo ImageWriter cho định dạng JPEG
            ImageWriter jpgWriter = ImageIO.getImageWritersByFormatName("jpg").next();
            ImageWriteParam jpgWriteParam = jpgWriter.getDefaultWriteParam();

            // Thiết lập mức độ nén xuống 20%
            if (jpgWriteParam.canWriteCompressed()) {
                jpgWriteParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                jpgWriteParam.setCompressionQuality(0.2f); // Giảm chất lượng xuống còn 20%
            }

            // Ghi ảnh nén vào file tạm thời
            File tempFile = new File("compressed_image.jpg");
            try (ImageOutputStream outputStream = ImageIO.createImageOutputStream(tempFile)) {
                jpgWriter.setOutput(outputStream);
                jpgWriter.write(null, new javax.imageio.IIOImage(resizedImage, null, null), jpgWriteParam);
            }

            // Đóng ImageWriter sau khi sử dụng
            jpgWriter.dispose();

            // Cập nhật ảnh đã nén để có thể tải xuống sau
            compressedImage = new Image(tempFile.toURI().toString());
            System.out.println("Image compressed successfully with reduced resolution and 20% quality.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private BufferedImage convertToRGB(BufferedImage image) {
        if (image.getColorModel().getColorSpace().getType() != ColorSpace.TYPE_RGB) {
            BufferedImage rgbImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
            new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_sRGB), null).filter(image, rgbImage);
            return rgbImage;
        }
        return image;
    }

    private File openFileChooser(String title, String... extensions) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files", extensions));
        return fileChooser.showSaveDialog(new Stage());
    }


    private void downloadCompressedImage() {
        if (mainImage.getImage() == null) {
            System.out.println("No image to save.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Files", "*.png"));

        // Hiển thị hộp thoại lưu file và đảm bảo người dùng chọn một tên file hợp lệ
        File file = fileChooser.showSaveDialog(new Stage());
        if (file != null) {
            try {
                BufferedImage bufferedImage = convertToBufferedImage(mainImage.getImage());
                // Đảm bảo file có phần mở rộng ".png"
                if (!file.getPath().endsWith(".png")) {
                    file = new File(file.getPath() + ".png");
                }
                ImageIO.write(bufferedImage, "png", file);
                System.out.println("Image saved successfully.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void saveImage() {
        if (mainImage.getImage() == null) {
            System.out.println("No image to save.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Files", "*.png"));

        // Hiển thị hộp thoại lưu file và đảm bảo người dùng chọn một tên file hợp lệ
        File file = fileChooser.showSaveDialog(new Stage());
        if (file != null) {
            try {
                BufferedImage bufferedImage = convertToBufferedImage(mainImage.getImage());
                // Đảm bảo file có phần mở rộng ".png"
                if (!file.getPath().endsWith(".png")) {
                    file = new File(file.getPath() + ".png");
                }
                ImageIO.write(bufferedImage, "png", file);
                System.out.println("Image saved successfully.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private void morphologyImage() {
        Image imgMorphology1 = morphologyClass.erosionColor(mainImagePath);
        image1.setImage(imgMorphology1);
        imageLabel1.setText("Erosion (Co ảnh)");

        Image imgMorphology2 = morphologyClass.dilationColor(mainImagePath);
        image2.setImage(imgMorphology2);
        imageLabel2.setText("Dilation (dãn ảnh");

        Image imgMorphology3 = originImage;
        image3.setImage(imgMorphology3);
        imageLabel3.setText("Original Image");

        box4.setVisible(false);
        box5.setVisible(false);
        box6.setVisible(false);

    }

    public void saveImg(){
        // Kiểm tra nếu mainImage không có ảnh
        if (mainImage.getImage() == null) {
            System.out.println("No image to save.");
            return;
        }

        // Sử dụng FileChooser để chọn nơi lưu ảnh
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Files", "*.png"));

        // Hiển thị hộp thoại lưu file
        File file = fileChooser.showSaveDialog(new Stage());

        if (file != null) {
            try {
                // Chuyển đổi Image của JavaFX sang BufferedImage
                BufferedImage bufferedImage = convertToBufferedImage(mainImage.getImage());
                ImageIO.write(bufferedImage, "png", file);
                System.out.println("Image saved successfully.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private BufferedImage convertToBufferedImage(Image img) {
        int width = (int) img.getWidth();
        int height = (int) img.getHeight();
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        PixelReader pixelReader = img.getPixelReader();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int argb = pixelReader.getArgb(x, y);
                bufferedImage.setRGB(x, y, argb);
            }
        }
        return bufferedImage;
    }

    private void enhancementImage() {
        Image imgEnhance1 = enhancementClass.cannyEnhancementColor(mainImagePath);
        image1.setImage(imgEnhance1);
        imageLabel1.setText("Canny enhancement");

        Image imgEnhance2 = enhancementClass.LaplacianEnhancementColor(mainImagePath);
        image2.setImage(imgEnhance2);
        imageLabel2.setText("Laplacian enhancement");

        Image imgEnhance3 = enhancementClass.sobelEnhancementColor(mainImagePath);
        image3.setImage(imgEnhance3);
        imageLabel3.setText("Sobel enhancement");

        Image imgEnhance4 = originImage;
        image4.setImage(imgEnhance4);
        imageLabel4.setText("Origin Image");

        box5.setVisible(false);
        box6.setVisible(false);
    }
    public void setAllBoxVisible(){
        box1.setVisible(true);
        box2.setVisible(true);
        box3.setVisible(true);
        box4.setVisible(true);
        box5.setVisible(true);
        box6.setVisible(true);
    }

    private void removeBackgroundImage() {
        setAllBoxVisible();
        Image imgRemove = removeBackgroundClass.cannyRemoveBackground(mainImagePath);
        image1.setImage(imgRemove);
        imageLabel1.setText("Remove Background canny");

        Image imgRemove2 = removeBackgroundClass.removeBackgroundWithGrabCut(mainImagePath);
        image2.setImage(imgRemove2);
        imageLabel2.setText("Remove Background grabCut");

        Image imgRemove3 = removeBackgroundClass.sobelRemoveBackground(mainImagePath);
        image3.setImage(imgRemove3);
        imageLabel3.setText("Remove Background sobel");

        Image imgRemove4 = removeBackgroundClass.laplacianRemoveBackground(mainImagePath);
        image4.setImage(imgRemove4);
        imageLabel4.setText("Remove Background laplacian");

        Image imgRemove5 = removeBackgroundClass.removeBackgroundColor(mainImagePath);
        image5.setImage(imgRemove5);
        imageLabel5.setText("Remove Background keep color");

        Image imgRemove6 = originImage;
        image6.setImage(imgRemove6);
        imageLabel6.setText("Original Image");

    }


    public void actionBtnThumbnail(){
        image1.setOnMouseClicked(event -> {
            mainImage.setImage(image1.getImage());
        });

        image2.setOnMouseClicked(event -> {
            mainImage.setImage(image2.getImage());
        });

        image3.setOnMouseClicked(event -> {
            mainImage.setImage(image3.getImage());
        });

        image4.setOnMouseClicked(event -> {
            mainImage.setImage(image4.getImage());
        });

        image5.setOnMouseClicked(event -> {
            mainImage.setImage(image5.getImage());
        });

        image6.setOnMouseClicked(event -> {
            mainImage.setImage(image6.getImage());
        });
    }

    public MainViewController() {

    }

    private void smoothImage() {
        setAllBoxVisible();
        Image trungVi = proccessIMG.locTrungViColor();
        image1.setImage(trungVi);
        imageLabel1.setText("Trung vị");

        Image trungBinh = proccessIMG.locTrungBinh();
        image2.setImage(trungBinh);
        imageLabel2.setText("Trung bình");

        Image max = proccessIMG.locMax();
        image3.setImage(max);
        imageLabel3.setText("Max");

        Image min = proccessIMG.locMin();
        image4.setImage(min);
        imageLabel4.setText("Min");

        Image amBan = proccessIMG.locAmBan();
        image5.setImage(amBan);
        imageLabel5.setText("Âm bản");

        Image origin = originImage;
        image6.setImage(origin);
        imageLabel6.setText("Original Image");

    }
    public void initialFunction(){
        this.proccessIMG = new FilterImage(mainImagePath);
        this.removeBackgroundClass = new RemoveBackground();
        this.enhancementClass = new Enhancement();
        this.morphologyClass = new Morphology();
    }

    private void importImage() {
        // Mở cửa sổ chọn file
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn hình ảnh");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        // Lấy file được chọn
        File selectedFile = fileChooser.showOpenDialog(new Stage());
//         lấy đường dẫn
        mainImagePath = selectedFile.getAbsolutePath();
        initialFunction();
        // khởi tạo proccessIMG
//        proccessIMG.setImgSrc(mainImagePath);
//        // lọc ảnh


        // gắn main image
        if (selectedFile != null) {
            // Tạo đối tượng Image từ file
            Image image = new Image(selectedFile.toURI().toString());
            originImage = image;

            // Đặt ảnh vào mainImage
            mainImage.setImage(image);
        }
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
