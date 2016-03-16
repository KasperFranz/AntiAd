package com.github.antiad.AntiAd;

import com.github.antiad.AntiAd.Listeners.UpdateListener;
import com.github.antiad.AntiAd.utils.Version;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

/**
 * This class is a barebones example of how to use the BukkitDev ServerMods API
 * to check for file updates.
 * <br>
 * See the README file for further information of use.
 */
public class Update {

    // The project's unique ID
    private final int projectID;

    // An optional API key to use, will be null if not submitted
    private final String apiKey;
    private final AntiAd plugin;
    // Keys for extracting file information from JSON response
    private static final String API_NAME_VALUE = "name";
    private static final String API_LINK_VALUE = "downloadUrl";
    private static final String API_RELEASE_TYPE_VALUE = "releaseType";
    private static final String API_FILE_NAME_VALUE = "fileName";
    private static final String API_GAME_VERSION_VALUE = "gameVersion";
    private final String linkToDev;
    // Static information for querying the API
    private static final String API_QUERY = "/servermods/files?projectIds=";
    private static final String API_HOST = "https://api.curseforge.com";
    private String versionName, versionLink;

    /**
     * Check for updates anonymously (keyless)
     *
     * @param plugin the plugin.
     * @param projectID The BukkitDev Project ID, found in the "Facts" panel on
     * the right-side of your project page.
     */
    public Update(AntiAd plugin, int projectID) {
        this(plugin, projectID, null);
    }

    /**
     * Check for updates using your Curse account (with key)
     *
     * @param plugin The plugin :)
     * @param projectID The BukkitDev Project ID, found in the "Facts" panel on
     * the right-side of your project page.
     * @param apiKey Your ServerMods API key, found at
     * https://dev.bukkit.org/home/servermods-apikey/
     */
    public Update(AntiAd plugin, int projectID, String apiKey) {
        this.projectID = projectID;
        this.apiKey = apiKey;
        this.plugin = plugin;
        this.linkToDev = plugin.getDescription().getWebsite();
        query();
    }

    /**
     * Query the API to find the latest approved file's details.
     */
    public void query() {
        URL url;

        try {
            // Create the URL to query using the project's ID
            url = new URL(API_HOST + API_QUERY + projectID);
        } catch (MalformedURLException e) {
            return;
        }

        try {
            // Open a connection and query the project
            URLConnection conn = url.openConnection();

            if (apiKey != null) {
                // Add the API key to the request if present
                conn.addRequestProperty("X-API-Key", apiKey);
            }

            // Add the user-agent to identify the program
            conn.addRequestProperty("User-Agent", "ServerModsAPI-Example (by Gravity)");

            // Read the response of the query
            // The response will be in a JSON format, so only reading one line is necessary.
            final BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String response = reader.readLine();

            // Parse the array of files from the query's response
            JSONArray array = (JSONArray) JSONValue.parse(response);
            JSONObject latest = getLatestRelease(array, 0);
            if (latest instanceof JSONObject) {
                // Get the newest file's details
                // Get the version's title
                versionName = (String) latest.get(API_NAME_VALUE);

                // Get the version's link
                versionLink = (String) latest.get(API_LINK_VALUE);

                Version currentVersion = new Version(plugin.getDescription().getVersion().replaceAll("[^\\d.]", ""));
                Version foundVersion = new Version(versionName.replaceAll("[^\\d.]", ""));

                if (currentVersion.compareTo(foundVersion) < 0) {
                    plugin.getLogger().log(Level.INFO, plugin.uncolorfull(plugin.getFromLanguage("updateAvalible").replaceAll("%LINK%", linkToDev).replaceAll("%VERSION%", foundVersion.toString())));
                    plugin.getServer().getPluginManager().registerEvents(new UpdateListener(this), plugin);
                }
            }
        } catch (IOException e) {
        }
    }

    /**
     * A method to get the latest release from a jsonArray
     *
     * @param array
     * @param round
     * @return
     */
    private JSONObject getLatestRelease(JSONArray array, int round) {
        if (array.size() - round > 0) {
            JSONObject latest = (JSONObject) array.get(array.size() - round - 1);

            // Get the version's release type
            String versionType = (String) latest.get(API_RELEASE_TYPE_VALUE);

            if (versionType.equalsIgnoreCase("release")) {
                return latest;
            } else {
                return this.getLatestRelease(array, round + 1);
            }
        }

        return null;
    }

    public AntiAd getPlugin() {
        return plugin;
    }

    public String getVersionName() {
        return versionName;
    }

    public String getLinkToDev() {
        return linkToDev;
    }

}
