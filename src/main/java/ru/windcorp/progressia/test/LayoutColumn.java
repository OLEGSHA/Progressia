package ru.windcorp.progressia.test;

import glm.vec._2.i.Vec2i;
import ru.windcorp.progressia.client.graphics.gui.Component;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutVertical;

import static java.lang.Math.min;

public class LayoutColumn extends LayoutVertical {

    protected int maxWidth;
    private int margin;

    public LayoutColumn(int gap, int maxWidth)
    {
        super(gap);
        this.maxWidth = maxWidth;
        margin = gap;
    }

    @Override
    public void layout(Component c) {
        int x = c.getX() + margin,
                y = c.getY() + c.getHeight();

        synchronized (c.getChildren()) {
            for (Component child : c.getChildren()) {

                int height = child.getPreferredSize().y;
                y -= margin + height;
                child.setBounds(x, y, maxWidth, height);

            }
        }
    }
}
