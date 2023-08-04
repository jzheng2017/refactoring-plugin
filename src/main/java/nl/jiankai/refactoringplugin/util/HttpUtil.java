package nl.jiankai.refactoringplugin.util;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Logger;

public class HttpUtil {
    private static final Logger LOGGER = Logger.getLogger(HttpUtil.class.getName());

    public static boolean validUrl(String url) {
        if (url.isBlank()) {
            return false;
        }

        try {
            new URL(url).toURI();
            return true;
        } catch (MalformedURLException | URISyntaxException e) {
            LOGGER.warning("Invalid url: %s".formatted(url));
            return false;
        }
    }
}
