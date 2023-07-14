package memocardbuilder.scenes;


import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Cursor;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Rectangle;
import javafx.stage.FileChooser;
import javafx.scene.image.ImageView;
import javafx.scene.control.ScrollBar;
import memocardbuilder.Strings;

import java.io.File;
import java.net.MalformedURLException;

public class MainSceneController {

    /**Viewport values*/
    private double xMin, xMax, yMin, yMax;

    /**For moving function*/
    private Point2D firstMousePointWhenMoving;

    private Rectangle rectangle = null;



    @FXML
    private ImageView imageView;

    @FXML
    private BorderPane borderPane;


    @FXML
    private ScrollBar verticalScrollBar;

    @FXML
    private ScrollBar horizontalScrollBar;


    @FXML
    private AnchorPane anchorCenterPane;

    private float zoomImage = 1;

    @FXML
    void initialize() {


        //Binds imageView size to anchorCenterPane
        imageView.fitWidthProperty().bind(anchorCenterPane.widthProperty().map(width -> width.floatValue() -5));
        imageView.fitHeightProperty().bind(anchorCenterPane.heightProperty().map(height -> height.floatValue() -5));

        loadImage("file:/home/xevi/Imatges/gatotRevoy.png");



    }


    @FXML
    void exitAction(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    void openImageAction(ActionEvent event) {
        FileChooser fc = new FileChooser();
        fc.setTitle(Strings.get("open_image"));
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(Strings.get("image_files"),
                "*.png", "*.jpg", "*.jpeg"));
        File file = fc.showOpenDialog( ((MenuItem) event.getTarget()).getParentPopup().getOwnerWindow());



        if (file != null) {
            try {
                loadImage(file.toURI().toURL().toString());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    void saveSelectionAction(ActionEvent event) {

    }

    @FXML
    void imageClicked(MouseEvent event) {

    //ImageView coordinates:    new Point2D (event.getX(), event.getY())
    //image coordinates:        fromImageViewToImageCoordinates(new Point2D (event.getX(), event.getY()))
    //Pane coordinates:         new Point2D(imageView.getLayoutX() + event.getX(), imageView.getLayoutY() + event.getX())

    }



    @FXML
    void imageViewMouseDragged(MouseEvent event) {

        if (event.isControlDown()) {
            drawRectangle (new Point2D (event.getX(), event.getY()));
        } else { //TODO
            moveImage(firstMousePointWhenMoving,
                    fromImageViewToImageCoordinates(new Point2D(event.getX(), event.getY())));
        }
    }

    /**
     * TODO
     * @param mousePosition
     */
    private void drawRectangle(Point2D mousePosition) {
        if (rectangle == null) {

            rectangle = new Rectangle(
                    firstMousePointWhenMoving.getX(),
                    firstMousePointWhenMoving.getY(),
                    mousePosition.getX() - firstMousePointWhenMoving.getX(),
                    mousePosition.getY() - firstMousePointWhenMoving.getY());//TODO width * relation

            rectangle.setOnMouseDragged(mouseEvent -> {
                //TODO width *relation
            });

        }
    }

    @FXML
    void imageViewMousePressed(MouseEvent event) {
        firstMousePointWhenMoving = fromImageViewToImageCoordinates(new Point2D(event.getX(), event.getY()));
    }

    @FXML
    void imageViewMouseReleased(MouseEvent event) {
        imageView.setCursor(Cursor.DEFAULT);

    }



    @FXML
    void imageMouseScrolled(ScrollEvent event) {

        if (event.getDeltaY() > 0 ) {
            setZoomImage(2, (new Point2D (event.getX(), event.getY())));
        } else if (event.getDeltaY() < 0) {
            setZoomImage(-2, (new Point2D (event.getX(), event.getY())));
        }
    }

    /**
     * Converts ImageView point to image point
     * @param imageViewCoordinates
     * @return image point
     */
    private Point2D fromImageViewToImageCoordinates (Point2D imageViewCoordinates) {
        double imageMaxWidth =  imageView.getViewport().getWidth();
        double imageMaxHeight = imageView.getViewport().getHeight();
        double imageRelation = imageMaxHeight / imageMaxWidth;
        double resizedMaxWidth = imageView.getFitWidth();
        double resizedMaxHeight = imageView.getFitHeight();

        double imageViewRelation = imageView.getFitHeight() / imageView.getFitWidth();

        if (imageRelation < imageViewRelation) {
            return new Point2D (
                    imageViewCoordinates.getX() * imageMaxWidth/resizedMaxWidth + imageView.getViewport().getMinX(),
                    imageViewCoordinates.getY() * imageMaxWidth/resizedMaxWidth + imageView.getViewport().getMinY()
            );

        } else {
            return new Point2D (
                    imageViewCoordinates.getX() * imageMaxHeight/resizedMaxHeight + imageView.getViewport().getMinX(),
                    imageViewCoordinates.getY() * imageMaxHeight/resizedMaxHeight + imageView.getViewport().getMinY()
            );

        }
    }

    private void setZoomImage(double zoom, Point2D fromCoordinates){
        zoom = zoom / 100;
        double widthHeightRelation = imageView.getImage().getWidth() / imageView.getImage().getHeight();

        double xCorrection = fromCoordinates.getX() / (imageView.getFitWidth() / 2);
        double yCorrection = fromCoordinates.getY() / (imageView.getFitHeight() / 2);

        double newXMin = xMin + imageView.getImage().getWidth()*zoom * xCorrection;
        double newYMin = yMin + imageView.getImage().getHeight()*zoom * yCorrection;
        double newXMax = xMax - imageView.getImage().getWidth()*zoom*2;
        double newYMax = newXMax / widthHeightRelation;


        //No less zoom than whole image.
        if (newXMin <= 0) {
            newXMax -= newXMin*2;
            newXMin = 0;
            if (newXMax > imageView.getImage().getWidth()) {
                newXMax = imageView.getImage().getWidth();
            }
        }

        if (newYMin <= 0) {
            newYMax -= newYMin*2;
            newYMin = 0;
            if (newYMax > imageView.getImage().getHeight()) {
                newYMax = imageView.getImage().getHeight();
            }

        }

        if (newXMax + newXMin > imageView.getImage().getWidth()) {
            newXMax = imageView.getImage().getWidth() - newXMin;
            newYMax = newXMax / widthHeightRelation;
        }
        if (newYMax + newYMin > imageView.getImage().getHeight()) {
            newYMax = imageView.getImage().getHeight() - newYMin;
            newXMax = newYMax * widthHeightRelation;
        }



        xMin = newXMin;
        xMax = newXMax;
        yMin = newYMin;
        yMax = newYMax;

        imageView.setViewport(new Rectangle2D(xMin, yMin, xMax, yMax));

    }

    /**
     * Returns the coordinates of right and bottom point of the ImageView.
     * @return A point2D with x and y coordinates of the rightest and bottomest point.
     */
    private Point2D getPointRightBottom() {

        //reference side.
        double min = Math.min(imageView.getFitWidth()/imageView.getImage().getWidth(),
                imageView.getFitHeight()/imageView.getImage().getHeight());

        //depends on reference is width or height
        if (min == imageView.getFitHeight() / imageView.getImage().getHeight()) {
            return new Point2D(imageView.getImage().getWidth() * min, imageView.getFitHeight());
        }else {
            return new Point2D(imageView.getFitWidth(), imageView.getImage().getHeight() * min);
        }
    }

    private void moveImage (Point2D from, Point2D to){
        imageView.setCursor(Cursor.MOVE);
        double newMinX = imageView.getViewport().getMinX() + from.getX() - to.getX();
        double newMinY = imageView.getViewport().getMinY() + from.getY() - to.getY();
        if (newMinX < 0) {
            newMinX = 0;
        }
        if (newMinY < 0) {
            newMinY = 0;
        }
        if (newMinX + imageView.getViewport().getWidth() >  imageView.getImage().getWidth()) {
            newMinX = imageView.getViewport().getMinX();
        }
        if (newMinY + imageView.getViewport().getHeight() >  imageView.getImage().getHeight()) {
            newMinY = imageView.getViewport().getMinY();
        }

        imageView.setViewport(new Rectangle2D(
                newMinX,
                newMinY,
                imageView.getViewport().getWidth(),
                imageView.getViewport().getHeight()

        ));
    }

    private void loadImage (String url) {

        imageView.setImage(new Image(url));
        xMin = 0;
        yMin = 0;
        xMax = imageView.getImage().getWidth();
        yMax = imageView.getImage().getHeight();
        imageView.setViewport(new Rectangle2D(0, 0, imageView.getImage().getWidth(), imageView.getImage().getHeight()));



    }


}