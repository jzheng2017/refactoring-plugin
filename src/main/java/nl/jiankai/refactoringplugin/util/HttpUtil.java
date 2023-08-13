package nl.jiankai.refactoringplugin.util;

import com.intellij.openapi.diagnostic.Logger;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class HttpUtil {
    private static final Logger LOGGER = Logger.getInstance(HttpUtil.class);

    public static boolean validUrl(String url) {
        if (url.isBlank()) {
            return false;
        }

        try {
            new URL(url).toURI();
            return true;
        } catch (MalformedURLException | URISyntaxException e) {
            LOGGER.warn("Invalid url: %s".formatted(url));
            return false;
        }
    }
}
