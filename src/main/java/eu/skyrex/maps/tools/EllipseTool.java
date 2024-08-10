package eu.skyrex.maps.tools;

import eu.skyrex.maps.CanvasManager;
import eu.skyrex.maps.PaintTool;
import eu.skyrex.maps.Pixel;

import java.awt.*;

public class EllipseTool implements PaintTool {

    private Pixel start = null;

    int strokeSize = 100;

    @Override
    public void tick(CanvasManager canvasManager, Pixel pixel) {
        if (start == null) {
            return;
        }

        final Graphics2D graphics = canvasManager.getGraphics();
        graphics.setColor(canvasManager.getCurrentColor());
        graphics.fillOval(Math.min(start.x(), pixel.x()), Math.min(start.y(), pixel.y()), diff(start.x(), pixel.x()), diff(start.y(), pixel.y()));

    }

    @Override
    public boolean onUse(CanvasManager canvasManager, Pixel pixel) {
        if (start == null) {
            start = pixel;
            return false;
        }

        final Graphics2D graphics = canvasManager.getGraphics();
        graphics.setColor(canvasManager.getCurrentColor());
        graphics.fillOval(Math.min(start.x(), pixel.x()), Math.min(start.y(), pixel.y()), diff(start.x(), pixel.x()), diff(start.y(), pixel.y()));
        start = null;
        return true;
    }

    @Override
    public String name() {
        return "Ellipse";
    }
}
