package sample;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.net.CookieHandler;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import com.dimaslanjaka.kotlin.ConsoleColors.Companion.println;

/*package sample;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

public class Facebook extends Application {
    private WebView webView;
    private WebEngine webEngine;
    private StackPane stackPane;
    private Stage stage;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        webView = new WebView();

        webEngine = webView.getEngine();
        webEngine.setJavaScriptEnabled(true);
        webEngine.setUserAgent("Mozilla/5.0 (compatible; ABrowse 0.4; Syllable)");

        stackPane = new StackPane();
        stackPane.getChildren().add(webView);

        stage = new Stage();
        stage.setScene(new Scene(stackPane, 600, 600));
        stage.show();

        //loadCredentials();
        CookieUtils.loadCookies();
        //webEngine.getLoadWorker().stateProperty().addListener(changeListener);
        WebViewListener.SetListener(webView);
        webEngine.load("https://fb.com/");
    }
}
*/
public class CookieUtils {
    private static final String[] DAYS = {"Sat", "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
    private static final String[] MONTHS = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec", "Jan"};
    public static String fileCookie = "cookies/default.json";
    static java.net.CookieManager manager = new java.net.CookieManager();

    static {
        try {
            java.net.CookieHandler.setDefault(manager);
            resolveFileCookie();
            Preferences.init(CookieUtils.class).put("cookie_file", getFileCookie().toAbsolutePath().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Path getFileCookie() {
        return Paths.get(fileCookie);
    }

    public static void setFileCookie(String name) {
        setFileCookie(name, false);
    }

    public static void setFileCookie(String name, boolean debug) {
        fileCookie = "cookies/" + name + ".json";
        if (debug) {
            println(name);
        }
    }

    public static void resolveFileCookie() throws Exception {
        if (fileCookie.isEmpty()) throw new Exception("Cookie path should not be empty");
        File file = new File(fileCookie);
        File folder = file.getParentFile();
        if (!folder.exists()) if (!folder.mkdirs()) throw new Exception("Cannot create folder cookie");
        if (!file.exists()) if (!file.createNewFile()) throw new Exception("Cannot create file cookie");
    }

    public static void saveCookies() {
        try {
            CookieHandler cookieManager = CookieHandler.getDefault();
            Field f = cookieManager.getClass().getDeclaredField("store");
            f.setAccessible(true);
            Object cookieStore = f.get(cookieManager);

            Field bucketsField = Class.forName("com.sun.webkit.network.CookieStore").getDeclaredField("buckets");
            bucketsField.setAccessible(true);
            Map buckets = (Map) bucketsField.get(cookieStore);
            f.setAccessible(true);
            Map<String, Collection> cookiesToSave = new LinkedHashMap<>();
            for (Object o : buckets.entrySet()) {
                Map.Entry entry = (Map.Entry) o;
                String domain = (String) entry.getKey();
                Map cookies = (Map) entry.getValue();
                cookiesToSave.put(domain, cookies.values());
            }

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String json = gson.toJson(cookiesToSave);

            Files.write(getFileCookie(), json.getBytes(StandardCharsets.UTF_8));

            println("Cookie saved " + getFileCookie().toAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadCookies() {
        try {
            byte[] bytes = Files.readAllBytes(getFileCookie());
            String json = new String(bytes, StandardCharsets.UTF_8);
            if (!json.isEmpty()) {
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                Type type = new TypeToken<Map<String, Collection<CookieJson>>>() {
                }.getType();
                Map<String, Collection<CookieJson>> cookiesToLoad = gson.fromJson(json, type);
                for (String domain : cookiesToLoad.keySet()) {
                    Collection<CookieJson> cookies = cookiesToLoad.get(domain);
                    Map<String, List<String>> m = new LinkedHashMap<>();
                    List<String> list = new ArrayList<>();
                    m.put("Set-Cookie", list);
                    for (CookieJson cookie : cookies) {
                        list.add(format(
                                cookie.name,
                                cookie.value,
                                cookie.domain,
                                cookie.path,
                                cookie.expiryTime,
                                cookie.secureOnly,
                                cookie.httpOnly
                        ));
                    }
                    CookieHandler.getDefault().put(new URI("http://" + domain + "/"), m);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String format(
            final String name,
            final String value,
            final String domain,
            final String path,
            final long maxAge,
            final boolean isSecure,
            final boolean isHttpOnly) {

        // Check arguments
        if (name == null || name.length() == 0)
            throw new IllegalArgumentException("Bad cookie name");

        // Name is checked for legality by servlet spec, but can also be passed directly so check again for quoting
        // Per RFC6265, Cookie.name follows RFC2616 Section 2.2 token rules
        //Syntax.requireValidRFC2616Token(name, "RFC6265 Cookie name");
        // Ensure that Per RFC6265, Cookie.value follows syntax rules
        //Syntax.requireValidRFC6265CookieValue(value);

        // Format value and params
        StringBuilder buf = new StringBuilder();
        buf.setLength(0);
        buf.append(name).append('=').append(value == null ? "" : value);

        // Append path
        if (path != null && path.length() > 0)
            buf.append(";Path=").append(path);

        // Append domain
        if (domain != null && domain.length() > 0)
            buf.append(";Domain=").append(domain);

        // Handle max-age and/or expires
        if (maxAge >= 0) {
            // Always use expires
            // This is required as some browser (M$ this means you!) don't handle max-age even with v1 cookies
            buf.append(";Expires=");
            if (maxAge == 0) {
                buf.append(formatCookieDate(0).trim());
            } else {
                buf.append(formatCookieDate(System.currentTimeMillis() + 1000L * maxAge));
            }
            buf.append(";Max-Age=");
            buf.append(maxAge);
        }

        // add the other fields
        if (isSecure) {
            buf.append(";Secure");
        }
        if (isHttpOnly) {
            buf.append(";HttpOnly");
        }

        return buf.toString();
    }

    /**
     * Format "EEE, dd-MMM-yy HH:mm:ss 'GMT'" for cookies
     *
     * @param date the date in milliseconds
     */
    private static String formatCookieDate(long date) {
        GregorianCalendar gc = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        gc.setTimeInMillis(date);

        int day_of_week = gc.get(Calendar.DAY_OF_WEEK);
        int day_of_month = gc.get(Calendar.DAY_OF_MONTH);
        int month = gc.get(Calendar.MONTH);
        int year = gc.get(Calendar.YEAR);
        year = year % 10000;

        int epoch = (int) ((date / 1000) % (60 * 60 * 24));
        int seconds = epoch % 60;
        epoch = epoch / 60;
        int minutes = epoch % 60;
        int hours = epoch / 60;

        StringBuilder buf = new StringBuilder();

        buf.append(DAYS[day_of_week]);
        buf.append(',');
        buf.append(' ');
        append2digits(buf, day_of_month);

        buf.append('-');
        buf.append(MONTHS[month]);
        buf.append('-');
        append2digits(buf, year / 100);
        append2digits(buf, year % 100);

        buf.append(' ');
        append2digits(buf, hours);
        buf.append(':');
        append2digits(buf, minutes);
        buf.append(':');
        append2digits(buf, seconds);
        buf.append(" GMT");

        return buf.toString();
    }

    /* ------------------------------------------------------------ */

    /**
     * Append 2 digits (zero padded) to the StringBuilder
     *
     * @param buf the buffer to append to
     * @param i   the value to append
     */
    public static void append2digits(StringBuilder buf, int i) {
        if (i < 100) {
            buf.append((char) (i / 10 + '0'));
            buf.append((char) (i % 10 + '0'));
        }
    }

    static class CookieJson {
        private String name;
        private String value;
        private Long expiryTime;
        private String domain;
        private String path;
        private boolean secureOnly;
        private boolean httpOnly;
    }
}
