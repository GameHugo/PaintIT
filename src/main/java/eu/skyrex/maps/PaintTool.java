package eu.skyrex.maps;

public interface PaintTool {

    void tick(CanvasManager canvasManager, Pixel pixel);
    boolean onUse(CanvasManager canvasManager, Pixel pixel);
    String name();

    default int diff(int a, int b) {
        return Math.abs(a - b);
    }
}
