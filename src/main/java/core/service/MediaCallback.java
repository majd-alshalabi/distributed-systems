package core.service;
import java.io.Serializable;

public interface MediaCallback extends Serializable {
    byte[] onCameraImage();
    byte[] onScreenshot();
}

