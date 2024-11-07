package org.example.practice_javafx.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.example.practice_javafx.proccessImage.ProccessIMG;
import org.opencv.core.Mat;

import java.io.File;

public class MainViewController {
    private String mainImagePath;
    private ProccessIMG proccessIMG;

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
    private Button importImageButton;

    @FXML
    private Button save;

    @FXML
    private Button smooth;

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
    public void initialize() {
        disappearThumbnail();
        // Thiết lập sự kiện cho nút "Import Image"
        importImageButton.setOnAction(event -> importImage());
        smooth.setOnAction(event -> smoothImage());
        actionBtnThumbnail();


    }

    public void displayThumbnail(){
        image1.setVisible(true);
        image2.setVisible(true);
        image3.setVisible(true);
        image4.setVisible(true);
        image5.setVisible(true);
        imageLabel1.setVisible(true);
        imageLabel2.setVisible(true);
        imageLabel3.setVisible(true);
        imageLabel4.setVisible(true);
        imageLabel5.setVisible(true);
    }

    public void disappearThumbnail(){
        image1.setVisible(false);
        image2.setVisible(false);
        image3.setVisible(false);
        image4.setVisible(false);
        image5.setVisible(false);
        imageLabel1.setVisible(false);
        imageLabel2.setVisible(false);
        imageLabel3.setVisible(false);
        imageLabel4.setVisible(false);
        imageLabel5.setVisible(false);
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
    }

    public MainViewController() {

    }

    private void smoothImage() {
        displayThumbnail();
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
        this.proccessIMG = new ProccessIMG(mainImagePath);
        // khởi tạo proccessIMG
//        proccessIMG.setImgSrc(mainImagePath);
//        // lọc ảnh


        // gắn main image
        if (selectedFile != null) {
            // Tạo đối tượng Image từ file
            Image image = new Image(selectedFile.toURI().toString());

            // Đặt ảnh vào mainImage
            mainImage.setImage(image);
        }
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
