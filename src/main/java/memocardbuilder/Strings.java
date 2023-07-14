package memocardbuilder;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Locale;

public abstract class Strings {
    private static ResourceBundle stringsBundle = null;

    private static void setStrings (String lang) {
        Locale locale = new Locale (lang == null? "en" : lang);

        try {
            //strings folder, strings_xx.properties with locale.
            stringsBundle = ResourceBundle.getBundle("strings.strings", locale);
        } catch (MissingResourceException e) {
            stringsBundle = ResourceBundle.getBundle("strings.strings", new Locale("en"));
        }
    }

    public static ResourceBundle getStrings() {
        if (stringsBundle == null) {
            setStrings(null);
        }

        return stringsBundle;
    }

    public static String get(String key) {
        return getStrings().getString(key);
    }
}
