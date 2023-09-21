package nl.jiankai.refactoringplugin.configuration;

public class PluginConfiguration {
    private final String pluginAssetsBaseDirectory = "/home/jiankai/Documents/ref-plugin";

    public String pluginAssetsBaseDirectory() {
        return pluginAssetsBaseDirectory;
    }

    public String pluginProjectsToScanLocation() {
        return pluginAssetsBaseDirectory + "/projects.txt";
    }

    public String pluginAllProjectsLocation() {
        return pluginAssetsBaseDirectory + "/projects";
    }
    public String cacheDirectory() {
        return pluginAssetsBaseDirectory + "/cache";
    }
}
