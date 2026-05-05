package util;

import java.awt.*;
import javax.swing.*;

public class SpringUtilities {

    public static void makeCompactGrid(Container parent,
            int rows, int cols,
            int initialX, int initialY,
            int xPad, int yPad) {
        SpringLayout layout = (SpringLayout) parent.getLayout();
        Spring xPadSpring = Spring.constant(xPad);
        Spring yPadSpring = Spring.constant(yPad);
        Spring initialXSpring = Spring.constant(initialX);
        Spring initialYSpring = Spring.constant(initialY);

        int max = rows * cols;
        Spring maxWidthSpring = Spring.constant(0);
        Spring maxHeightSpring = Spring.constant(0);

        for (int i = 0; i < max; i++) {
            SpringLayout.Constraints cons = layout.getConstraints(
                    parent.getComponent(i));
            maxWidthSpring = Spring.max(maxWidthSpring, cons.getWidth());
            maxHeightSpring = Spring.max(maxHeightSpring, cons.getHeight());
        }
        for (int i = 0; i < max; i++) {
            SpringLayout.Constraints cons = layout.getConstraints(
                    parent.getComponent(i));
            cons.setWidth(maxWidthSpring);
            cons.setHeight(maxHeightSpring);
        }

        Spring y = initialYSpring;
        for (int r = 0; r < rows; r++) {
            Spring x = initialXSpring;
            for (int c = 0; c < cols; c++) {
                SpringLayout.Constraints cons = layout.getConstraints(
                        parent.getComponent(r * cols + c));
                cons.setX(x);
                cons.setY(y);
                x = Spring.sum(x, Spring.sum(maxWidthSpring, xPadSpring));
            }
            y = Spring.sum(y, Spring.sum(maxHeightSpring, yPadSpring));
        }
    }
}
