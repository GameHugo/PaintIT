package eu.skyrex.maps.tools;

import eu.skyrex.maps.CanvasManager;
import eu.skyrex.maps.PaintTool;
import eu.skyrex.maps.Pixel;

import java.awt.*;

public class LineTool implements PaintTool {

    private Pixel start = null;

    int strokeSize = 50;

    @Override
    public void tick(CanvasManager canvasManager, Pixel pos) {
        if (start == null) {
            return;
        }

        final Graphics2D graphics = canvasManager.getGraphics();
        final Stroke stroke = graphics.getStroke();

        graphics.setStroke(new BasicStroke(strokeSize));
        graphics.setColor(canvasManager.getCurrentColor());

        graphics.drawLine(start.x(), start.y(), pos.x(), pos.y());

        graphics.setStroke(stroke);
    }

    @Override
    public boolean onUse(CanvasManager canvasManager, Pixel pos) {

        if (start == null) {
            start = pos;
            return false;
        }

        final Graphics2D graphics = canvasManager.getGraphics();
        final Stroke stroke = graphics.getStroke();

        graphics.setStroke(new BasicStroke(strokeSize));
        graphics.setColor(canvasManager.getCurrentColor());

        graphics.drawLine(start.x(), start.y(), pos.x(), pos.y());
        start = null;

        graphics.setStroke(stroke);

        return true;
    }

    @Override
    public String name() {
        return "Line";
    }
}
