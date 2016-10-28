package be.mygod.chimpanzeerunner.view;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Bounds {
    private static final Pattern PATTERN = Pattern.compile("^\\[(-?\\d+),(-?\\d+)\\]\\[(-?\\d+),(-?\\d+)\\]$");

    public final int left, top, right, bottom;

    public Bounds(String value) throws MalformedSourceException {
        Matcher matcher = PATTERN.matcher(value);
        if (matcher.matches()) {
            left = Integer.parseInt(matcher.group(1));
            top = Integer.parseInt(matcher.group(2));
            right = Integer.parseInt(matcher.group(3));
            bottom = Integer.parseInt(matcher.group(4));
        } else throw new MalformedSourceException("Malformed bounds: " + value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bounds bounds = (Bounds) o;
        return left == bounds.left &&
                top == bounds.top &&
                right == bounds.right &&
                bottom == bounds.bottom;
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, top, right, bottom);
    }

    @Override
    public String toString() {
        return String.format("[%d,%d][%d,%d]", left, top, right, bottom);
    }
}
