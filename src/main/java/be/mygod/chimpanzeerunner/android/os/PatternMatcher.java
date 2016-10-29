package be.mygod.chimpanzeerunner.android.os;

import java.util.Objects;
import java.util.regex.Matcher;

public class PatternMatcher {
    public static final int
            PATTERN_LITERAL = 0,
            PATTERN_PREFIX = 1,
            PATTERN_SIMPLE_GLOB = 2;

    public String path;
    public int type;

    public PatternMatcher(String path, int type) {
        this.path = path;
        this.type = type;
    }
    public PatternMatcher(Matcher matcher) {
        switch (matcher.group(1)) {
            case "LITERAL":
                type = 0;
                break;
            case "PREFIX":
                type = 1;
                break;
            case "GLOB":
                type = 2;
                break;
        }
        path = matcher.group(2);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PatternMatcher that = (PatternMatcher) o;
        return type == that.type &&
                Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path, type);
    }
}
