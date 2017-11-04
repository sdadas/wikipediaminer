package org.wikipedia.miner.util.config;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.Collection;

/**
 * @author Sławomir Dadas
 */
public final class WikiPaths {

    public static File findConfig(String dirStartsWith) {
        File dir = findWikiDirectory(dirStartsWith);
        File[] files = dir.listFiles();
        files = files != null ? files : new File[0];
        for (File file : files) {
            String name = file.getName().toLowerCase();
            if(StringUtils.contains(name, "config") && name.endsWith(".xml")) {
                return file;
            } else if(StringUtils.equals("wikipedia-template.xml", name)) {
                return file;
            }
        }
        throw new IllegalArgumentException("Couldn't find config file in directory " + dir.getAbsolutePath());
    }

    public static File findWikiDirectory(String dirStartsWith) {
        File file = new File(findWikiHome());
        IOFileFilter filter = DirectoryFileFilter.INSTANCE;
        Collection<File> dirs = FileUtils.listFilesAndDirs(file, filter, filter);
        for (File dir : dirs) {
            if(StringUtils.startsWithIgnoreCase(dir.getName(), dirStartsWith)) {
                return dir;
            }
        }
        throw new IllegalArgumentException("Couldn't find wiki data directory starting with " + dirStartsWith);
    }

    public static String findWikiHome() {
        String env = "WIKI_HOME";
        String path = System.getenv(env);
        if(StringUtils.isBlank(path)) {
            String error = "Please set WIKI_HOME env variable to directory containing wikipedia dumps";
            throw new IllegalArgumentException(error);
        }
        return path;
    }
}
