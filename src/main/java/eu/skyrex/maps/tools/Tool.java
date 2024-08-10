package eu.skyrex.maps.tools;

import eu.skyrex.maps.PaintTool;

import java.util.function.Supplier;

public enum Tool {
    BRUSH(BrushTool::new),
    RECTANGLE(RectTool::new),
    UNDO(UndoTool::new),
    REDO(RedoTool::new);

    private final Supplier<PaintTool> toolSupplier;

    Tool(Supplier<PaintTool> toolSupplier) {
        this.toolSupplier = toolSupplier;
    }

    public PaintTool create() {
        return toolSupplier.get();
    }
}
