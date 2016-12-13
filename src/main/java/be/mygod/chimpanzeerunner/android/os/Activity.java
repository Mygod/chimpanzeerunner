package be.mygod.chimpanzeerunner.android.os;

import org.w3c.dom.Element;

import java.util.Arrays;

public class Activity extends Component {
    public static final String
            ACTION_MAIN = "android.intent.action.MAIN",
            CATEGORY_LAUNCHER = "android.intent.category.LAUNCHER";

    public Activity(Element element) {
        super(element);
    }

    public boolean isMainActivity() {
        return Arrays.stream(filters).anyMatch(filter -> filter.actions.contains(ACTION_MAIN) &&
                filter.categories.contains(CATEGORY_LAUNCHER));
    }
}
