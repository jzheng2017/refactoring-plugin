package nl.jiankai.refactoringplugin.configuration;

import com.intellij.openapi.components.Service;

@Service
public final class PluginConfiguration {
    private final String pluginAssetsBaseDirectory = "/home/jiankai/Documents";

    public String pluginAssetsBaseDirectory() {
        return pluginAssetsBaseDirectory;
    }
}
