package nl.jiankai.refactoringplugin.configuration;

import com.intellij.openapi.components.Service;

@Service
public final class PluginConfiguration {
    private final String pluginAssetsBaseDirectory = "/home/jiankai/Documents/ref-plugin";

    public String pluginAssetsBaseDirectory() {
        return pluginAssetsBaseDirectory;
    }

    public String pluginRepositoryUrlsLocation() {
        return pluginAssetsBaseDirectory + "/repositories.txt";
    }

    public String pluginGitRepositoryDirectory() {
        return pluginAssetsBaseDirectory + "/repositories";
    }
}
