package eu.skyrex.maps.tools;

import eu.skyrex.maps.CanvasManager;
import eu.skyrex.maps.PaintTool;
import eu.skyrex.maps.Pixel;

public class BrushTool implements PaintTool {

    int strokeSize = 100;

    @Override
    public void tick(CanvasManager canvasManager, Pixel pos) {

    }

    @Override
    public boolean onUse(CanvasManager canvasManager, Pixel pos) {
        canvasManager.getGraphics().setColor(canvasManager.getCurrentColor());
        canvasManager.getGraphics().fillOval(pos.x() - strokeSize / 2, pos.y() - strokeSize / 2, strokeSize, strokeSize);
        return true;
    }

    @Override
    public String name() {
        return "Brush";
    }
}
