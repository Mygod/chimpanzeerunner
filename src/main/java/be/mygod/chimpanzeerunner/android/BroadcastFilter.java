package be.mygod.chimpanzeerunner.android;

import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BroadcastFilter {
    public HashSet<String>
            actions = new HashSet<>(),
            categories = new HashSet<>(),
            schemes = new HashSet<>(),
            types = new HashSet<>();
    public String requiredPermission;
    public HashSet<AuthorityEntry> authorities = new HashSet<>();
    public HashSet<PatternMatcher>
            ssps = new HashSet<>(),
            paths = new HashSet<>();
    public int priority;
    public boolean hasPartialTypes, autoVerify;

    private static final Pattern
            PATTERN_ACTION = Pattern.compile("^      Action: \"(.*)\"$"),
            PATTERN_CATEGORY = Pattern.compile("^      Category: \"(.*)\"$"),
            PATTERN_SCHEME = Pattern.compile("^      Scheme: \"(.*)\"$"),
            PATTERN_SSP = Pattern.compile("^      Ssp: \"PatternMatcher\\{(LITERAL|PATTERN|GLOB): (.*)\\}\"$"),
            PATTERN_AUTHORITY = Pattern.compile("^      Authority: \"(.*)\": (-?\\d)+( WILD)?$"),
            PATTERN_PATH = Pattern.compile("^      Path: \"PatternMatcher\\{(LITERAL|PATTERN|GLOB): (.*)\\}\"$"),
            PATTERN_TYPE = Pattern.compile("^      Type: \"(.*)\"$"),
            PATTERN_PRIORITY = Pattern.compile("^      mPriority=(-?\\d+)(, mHasPartialTypes=(true|false))?$"),
            PATTERN_AUTO_VERIFY = Pattern.compile("^      AutoVerify=(true|false)$"),
            PATTERN_REQUIRED_PERMISSION = Pattern.compile("^      requiredPermission=(.*)$");

    public BroadcastFilter(Element element) {
        for (Element child : DomUtils.getChildElements(element)) switch (child.getNodeName()) {
            case "action":
                actions.add(child.getAttribute("android:name"));
                break;
            case "category":
                categories.add(child.getAttribute("android:name"));
                break;
            case "data":
                String val = child.getAttribute("android:scheme");
                if (!val.isEmpty()) schemes.add(val);
                if (!(val = child.getAttribute("android:host")).isEmpty())
                    authorities.add(new AuthorityEntry(val, child.getAttribute("android:port")));
                if (!(val = child.getAttribute("android:path")).isEmpty())
                    paths.add(new PatternMatcher(val, PatternMatcher.PATTERN_LITERAL));
                if (!(val = child.getAttribute("android:pathPattern")).isEmpty())
                    paths.add(new PatternMatcher(val, PatternMatcher.PATTERN_SIMPLE_GLOB));
                if (!(val = child.getAttribute("android:pathPrefix")).isEmpty())
                    paths.add(new PatternMatcher(val, PatternMatcher.PATTERN_PREFIX));
                if (!(val = child.getAttribute("android:mimeType")).isEmpty()) types.add(val);
                break;
            default: throw new IllegalArgumentException("Unexpected child of intent-filter: " + child.getNodeName());
        }
        String p = element.getAttribute("android:priority");
        if (!p.isEmpty()) priority = Integer.parseInt(p);
    }
    public BroadcastFilter() { }
    public boolean takeDump(CharSequence dump) {
        Matcher matcher = PATTERN_ACTION.matcher(dump);
        if (matcher.matches()) {
            actions.add(matcher.group(1));
            return true;
        }
        matcher = PATTERN_CATEGORY.matcher(dump);
        if (matcher.matches()) {
            categories.add(matcher.group(1));
            return true;
        }
        matcher = PATTERN_SCHEME.matcher(dump);
        if (matcher.matches()) {
            schemes.add(matcher.group(1));
            return true;
        }
        matcher = PATTERN_SSP.matcher(dump);
        if (matcher.matches()) {
            ssps.add(new PatternMatcher(matcher));
            return true;
        }
        matcher = PATTERN_AUTHORITY.matcher(dump);
        if (matcher.matches()) {
            authorities.add(new AuthorityEntry(matcher));
            return true;
        }
        matcher = PATTERN_PATH.matcher(dump);
        if (matcher.matches()) {
            paths.add(new PatternMatcher(matcher));
            return true;
        }
        matcher = PATTERN_TYPE.matcher(dump);
        if (matcher.matches()) {
            types.add(matcher.group(1));
            return true;
        }
        matcher = PATTERN_PRIORITY.matcher(dump);
        if (matcher.matches()) {
            priority = Integer.parseInt(matcher.group(1));
            hasPartialTypes = "true".equals(matcher.group(3));
            return true;
        }
        matcher = PATTERN_AUTO_VERIFY.matcher(dump);
        if (matcher.matches()) {
            autoVerify = "true".equals(matcher.group(1));
            return true;
        }
        matcher = PATTERN_REQUIRED_PERMISSION.matcher(dump);
        if (matcher.matches()) {
            requiredPermission = matcher.group(1);
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder du = new StringBuilder();
        StringBuilder sb = new StringBuilder(256);
        if (actions.size() > 0) {
            for (String action : actions) {
                sb.setLength(0);
                sb.append("\t\t\t");
                sb.append("Action: \"");
                sb.append(action);
                sb.append("\"");
                du.append(sb.toString()).append('\n');
            }
        }
        if (categories != null) {
            for (String category : categories) {
                sb.setLength(0);
                sb.append("\t\t\t");
                sb.append("Category: \"");
                sb.append(category);
                sb.append("\"");
                du.append(sb.toString()).append('\n');
            }
        }
        if (schemes != null) {
            for (String scheme : schemes) {
                sb.setLength(0);
                sb.append("\t\t\t");
                sb.append("Scheme: \"");
                sb.append(scheme);
                sb.append("\"");
                du.append(sb.toString()).append('\n');
            }
        }
        if (ssps != null) {
            for (PatternMatcher pe : ssps) {
                sb.setLength(0);
                sb.append("\t\t\t");
                sb.append("Ssp: \"");
                sb.append(pe);
                sb.append("\"");
                du.append(sb.toString()).append('\n');
            }
        }
        if (authorities != null) {
            for (AuthorityEntry ae : authorities) {
                sb.setLength(0);
                sb.append("\t\t\t");
                sb.append("Authority: \"");
                sb.append(ae.host);
                sb.append("\": ");
                sb.append(ae.port);
                if (ae.wild) sb.append(" WILD");
                du.append(sb.toString()).append('\n');
            }
        }
        if (paths != null) {
            for (PatternMatcher pe : paths) {
                sb.setLength(0);
                sb.append("\t\t\t");
                sb.append("Path: \"");
                sb.append(pe);
                sb.append("\"");
                du.append(sb.toString()).append('\n');
            }
        }
        if (types != null) {
            for (String type : types) {
                sb.setLength(0);
                sb.append("\t\t\t");
                sb.append("Type: \"");
                sb.append(type);
                sb.append("\"");
                du.append(sb.toString()).append('\n');
            }
        }
        if (priority != 0 || hasPartialTypes) {
            sb.setLength(0);
            sb.append("\t\t\t"); sb.append("mPriority="); sb.append(priority);
            sb.append(", mHasPartialTypes="); sb.append(hasPartialTypes);
            du.append(sb.toString()).append('\n');
        }
        {
            sb.setLength(0);
            sb.append("\t\t\t"); sb.append("AutoVerify="); sb.append(autoVerify);
            du.append(sb.toString()).append('\n');
        }
        return du.toString();
    }

    public final static class AuthorityEntry {
        public String host;
        public int port;
        public boolean wild;

        public AuthorityEntry(String host, String port) {
            wild = host.length() > 0 && host.charAt(0) == '*';
            this.host = wild ? host.substring(1).intern() : host;
            this.port = port != null ? Integer.parseInt(port) : -1;
        }
        public AuthorityEntry(String host, int port, boolean wild) {
            this.host = host;
            this.port = port;
            this.wild = wild;
        }
        public AuthorityEntry(Matcher matcher) {
            host = matcher.group(1);
            String p = matcher.group(2);
            port = p == null || p.isEmpty() ? -1 : Integer.parseInt(p);
            wild = matcher.group(3) != null;
        }
    }
}
