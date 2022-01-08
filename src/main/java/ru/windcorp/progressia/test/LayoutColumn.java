package ru.windcorp.progressia.test;

import ru.windcorp.progressia.client.graphics.gui.Component;
import ru.windcorp.progressia.client.graphics.gui.layout.LayoutVertical;

public class LayoutColumn extends LayoutVertical {

    protected int maxWidth;
    private final int margin;

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
