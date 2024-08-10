package eu.skyrex.maps.tools;

import eu.skyrex.maps.CanvasManager;
import eu.skyrex.maps.PaintTool;
import eu.skyrex.maps.Pixel;

public class RedoTool implements PaintTool {

    @Override
    public void tick(CanvasManager canvasManager, Pixel pos) {

    }

    @Override
    public boolean onUse(CanvasManager canvasManager, Pixel pos) {
        canvasManager.redo();
        return false;
    }

    @Override
    public String name() {
        return "Redo";
    }
}
