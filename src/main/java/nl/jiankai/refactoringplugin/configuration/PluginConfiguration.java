package nl.jiankai.refactoringplugin.configuration;

public class PluginConfiguration {
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
    public String cacheDirectory() {
        return pluginAssetsBaseDirectory + "/cache";
    }
}
