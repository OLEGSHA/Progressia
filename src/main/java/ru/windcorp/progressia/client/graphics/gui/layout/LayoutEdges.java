package ru.windcorp.progressia.client.graphics.gui.layout;

import glm.vec._2.i.Vec2i;
import ru.windcorp.progressia.client.graphics.gui.Component;
import ru.windcorp.progressia.client.graphics.gui.Layout;
import ru.windcorp.progressia.client.localization.MutableStringLocalized;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class LayoutEdges implements Layout {

    private int margin;

    public LayoutEdges(int margin) {
        this.margin = margin;
    }

    @Override
    public void layout(Component c) {
        for (int i=0;i<2;i++)
        {
            Component child = c.getChild(i);

            Vec2i size = child.getPreferredSize();

            int cWidth = c.getWidth() - 2 * margin;
            int cHeight = c.getHeight() - 2 * margin;

            size.x = min(size.x, cWidth);
            size.y = min(size.y, cHeight);

            if (i==0) {
                child.setBounds(
                        c.getX() + margin,
                        c.getY(),
                        size
                );
            } else {
                child.setBounds(
                        1920 - size.x - margin,
                        c.getY(),
                        size
                );
            }

        }
    }

    @Override
    public Vec2i calculatePreferredSize(Component c) {
        Vec2i result = new Vec2i(1920,0);
        c.getChildren().stream()
                .map(child -> child.getPreferredSize())
                .forEach(size -> {
                    result.y = max(Math.abs(size.y), result.y);
                });
        return result;
    }
}
