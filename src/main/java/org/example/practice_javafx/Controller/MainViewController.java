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
import org.example.practice_javafx.proccessImage.RemoveBackground;
import org.opencv.core.Mat;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MainViewController {
    private String mainImagePath;
    private FilterImage proccessIMG;
    private RemoveBackground removeBackgroundClass;
    private Image originImage;
    private Enhancement enhancementClass;

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

    @FXML
    public void initialize() {

        // Thiết lập sự kiện cho nút "Import Image"
        importImageButton.setOnAction(event -> importImage());
        smooth.setOnAction(event -> smoothImage());
        actionBtnThumbnail();
        removeBackground.setOnAction(event -> {
            removeBackgroundImage();
        });
        enhancement.setOnMouseClicked(event -> {
            enhancementImage();
        });
        save.setOnMouseClicked(event -> {
            saveImg();
        });

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
        Image imgEnhance1 = enhancementClass.cannyEnhancement(mainImagePath);
        image1.setImage(imgEnhance1);
        imageLabel1.setText("Canny enhancement");

        Image imgEnhance2 = enhancementClass.LaplacianEnhancement(mainImagePath);
        image2.setImage(imgEnhance2);
        imageLabel2.setText("Laplacian enhancement");

        Image imgEnhance3 = enhancementClass.sobelEnhancement(mainImagePath);
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

        Image imgRemove2 = removeBackgroundClass.grabCutRemoveBackground(mainImagePath);
        image2.setImage(imgRemove2);
        imageLabel2.setText("Remove Background grabCut");

        Image imgRemove3 = removeBackgroundClass.sobelRemoveBackground(mainImagePath);
        image3.setImage(imgRemove3);
        imageLabel3.setText("Remove Background sobel");

        Image imgRemove4 = removeBackgroundClass.laplacianRemoveBackground(mainImagePath);
        image4.setImage(imgRemove4);
        imageLabel4.setText("Remove Background laplacian");

        Image imgRemove5 = originImage;
        image5.setImage(imgRemove5);
        imageLabel5.setText("Original Image");

        box6.setVisible(false);
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
        Image trungVi = proccessIMG.locTrungVi();
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
