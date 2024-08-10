package eu.skyrex.maps.tools;

import eu.skyrex.maps.CanvasManager;
import eu.skyrex.maps.PaintTool;
import eu.skyrex.maps.Pixel;

public class UndoTool implements PaintTool {

    @Override
    public void tick(CanvasManager canvasManager, Pixel pos) {

    }

    @Override
    public boolean onUse(CanvasManager canvasManager, Pixel pos) {
        canvasManager.undo();
        return false;//true would ruin the change stack
    }

    @Override
    public String name() {
        return "Undo";
    }
}
