package core.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.Serializable;

public interface MyCallback extends Serializable {
    byte[] onCameraImage();
    byte[] onScreenshot();
}
