package renderer;

import primitives.Color;

import java.util.List;

public class ColorChecker {
    public static boolean isBelowThreshold(List<Color> colors, double threshold) {
        Color avg = Color.BLACK;
        for (Color c : colors)
            avg = avg.add(c);
        avg = avg.reduce(colors.size());

        for (Color c : colors) {
            if (c.subtract(avg).length() > threshold)
                return false;
        }
        return true;
    }
}

