package be.mygod.chimpanzeerunner.android.os;

import be.mygod.chimpanzeerunner.android.action.AndroidAction;
import be.mygod.chimpanzeerunner.android.action.BroadcastIntent;
import be.mygod.chimpanzeerunner.android.action.PlacePhoneCall;
import be.mygod.chimpanzeerunner.android.action.SendSms;
import be.mygod.chimpanzeerunner.device.Device;
import org.springframework.util.xml.DomUtils;
import org.w3c.dom.Element;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BroadcastFilter {
    private static final HashSet<String>
            SCHEMES_DEFAULT = new HashSet<>(),
            SCHEMES_NULL = new HashSet<>(),
            SCHEMES_EMPTY = new HashSet<>();
    static {
        SCHEMES_DEFAULT.add("");
        SCHEMES_DEFAULT.add("content");
        SCHEMES_DEFAULT.add("file");
        SCHEMES_NULL.add(null);
        SCHEMES_EMPTY.add("");
    }

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
                actions.add(child.getAttribute("android:componentName"));
                break;
            case "category":
                categories.add(child.getAttribute("android:componentName"));
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

    public Stream<AndroidAction> getActions(Device device, String packageName, String componentName) {
        HashSet<String> schemes = this.schemes, datas, types;
        if (this.types.isEmpty() && schemes.isEmpty()) datas = types = SCHEMES_NULL; else {
            if (schemes.isEmpty()) schemes = SCHEMES_DEFAULT;
            datas = new HashSet<>();
            for (PatternMatcher ssp : this.ssps) for (String scheme : schemes)
                datas.add(scheme.isEmpty() ? getMatchingString(ssp) : scheme + ':' + getMatchingString(ssp));
            if (!authorities.isEmpty()) {
                Set<String> paths = this.paths.isEmpty() ? SCHEMES_EMPTY
                        : this.paths.stream().map(BroadcastFilter::getMatchingString).collect(Collectors.toSet());
                for (String scheme : schemes) if (!scheme.isEmpty())
                    for (AuthorityEntry authority : authorities) for (String path : paths) {
                        StringBuilder url = new StringBuilder(scheme);
                        url.append("://");
                        url.append(authority.host);
                        if (authority.port >= 0) {
                            url.append(':');
                            url.append(authority.port);
                        }
                        url.append(path);
                        datas.add(url.toString());
                    }
            }
            types = this.types.isEmpty() ? SCHEMES_NULL : this.types;
        }
        return actions.stream().flatMap(action -> {
            switch (action) {
                case "android.appwidget.action.APPWIDGET_UPDATE":
                case "android.intent.action.MEDIA_SCANNER_FINISHED":
                case "android.intent.action.UMS_CONNECTED":
                case "android.intent.action.UMS_DISCONNECTED":
                    return datas.stream().flatMap(data -> types.stream().map(type ->
                            new BroadcastIntent(device, packageName, componentName, action, data, type, categories)));
                // Actions that need su. Source: https://android.googlesource.com/platform/frameworks/base/+/c573661/core/res/AndroidManifest.xml
                case "android.media.AUDIO_BECOMING_NOISY":
                case "android.intent.action.BATTERY_CHANGED":
                case "android.intent.action.BATTERY_LOW":
                case "android.intent.action.BATTERY_OKAY":
                case "android.intent.action.BOOT_COMPLETED":
                case "android.net.conn.CONNECTIVITY_CHANGE":
                case "android.intent.action.DATE_CHANGED":
                case "android.intent.action.INPUT_METHOD_CHANGED":
                case "android.intent.action.MEDIA_EJECT":
                case "android.intent.action.MEDIA_MOUNTED":
                case "android.intent.action.MEDIA_UNMOUNTED":
                case "android.intent.action.NEW_OUTGOING_CALL":
                case "android.intent.action.PACKAGE_ADDED":
                case "android.intent.action.PACKAGE_REMOVED":
                case "android.intent.action.PACKAGE_REPLACED":
                case "android.intent.action.ACTION_POWER_CONNECTED":    // todo: replace these with other better adb commands
                case "android.intent.action.ACTION_POWER_DISCONNECTED":
                case "android.intent.action.ACTION_SHUTDOWN":
                case "android.intent.action.TIME_SET":
                case "android.intent.action.TIMEZONE_CHANGED":
                case "android.intent.action.USER_PRESENT":
                    return datas.stream().flatMap(data -> types.stream().map(type -> new BroadcastIntent(
                            device, packageName, componentName, action, data, type, categories, true)));
                // TODO: what about the rest of the data in intent?
                case "android.intent.action.PHONE_STATE": return Stream.of(new PlacePhoneCall(device));
                case "android.provider.Telephony.SMS_RECEIVED": return Stream.of(new SendSms(device));
                default: return null;
            }
        }).filter(intent -> intent != null);
    }

    private static String getMatchingString(PatternMatcher matcher) {
        if (matcher.type == 2) {
            // PatternMatcher supports [.*\]. So what we need to do is to ignore '.'s, throw away '*'s and unescape '\'s
            StringBuilder builder = new StringBuilder();
            boolean escaped = false;
            for (char c : matcher.path.toCharArray())
                if (escaped) {
                    builder.append(c);
                    escaped = false;
                } else switch (c) {
                    case '*':
                        break;
                    case '\\':
                        escaped = true;
                        break;
                    default:
                        builder.append(c);
                        break;
                }
            return builder.toString();
        } else return matcher.path;
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
