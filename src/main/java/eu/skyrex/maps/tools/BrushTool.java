package eu.skyrex.maps.tools;

import eu.skyrex.maps.CanvasManager;
import eu.skyrex.maps.PaintTool;
import eu.skyrex.maps.Pixel;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class BrushTool implements PaintTool {


    int strokeSize = 50;

    private boolean isDrawing = false;
    private final Map<Pixel, Pixel> lines = new HashMap<>();
    private Pixel lastPos = null;

    private final BasicStroke stroke = new BasicStroke(strokeSize, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);

    @Override
    public void tick(CanvasManager canvasManager, Pixel pos) {
        if (lastPos == null) lastPos = pos;

        if (isDrawing) {

            canvasManager.getGraphics().setStroke(stroke);
            canvasManager.getGraphics().setColor(canvasManager.getCurrentColor());
            canvasManager.getGraphics().drawLine(lastPos.x(), lastPos.y(), pos.x(), pos.y());

            lines.put(lastPos, pos);
            lastPos = pos;
        }

        for (Pixel pixel : lines.keySet()) {
            Pixel next = lines.get(pixel);
            canvasManager.getGraphics().setStroke(stroke);
            canvasManager.getGraphics().setColor(canvasManager.getCurrentColor());
            canvasManager.getGraphics().drawLine(pixel.x(), pixel.y(), next.x(), next.y());
        }


    }

    @Override
    public boolean onUse(CanvasManager canvasManager, Pixel pos) {
        if (isDrawing) {
            for (Pixel pixel : lines.keySet()) {
                Pixel next = lines.get(pixel);
                canvasManager.getGraphics().setStroke(stroke);
                canvasManager.getGraphics().setColor(canvasManager.getCurrentColor());
                canvasManager.getGraphics().drawLine(pixel.x(), pixel.y(), next.x(), next.y());
            }
            lines.clear();
            isDrawing = false;
            return true;
        }
        isDrawing = true;
        lastPos = null;
        return false;
    }

    @Override
    public String name() {
        return "Brush";
    }
}
