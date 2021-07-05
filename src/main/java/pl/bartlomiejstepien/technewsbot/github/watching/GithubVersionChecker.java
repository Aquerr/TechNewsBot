package pl.bartlomiejstepien.technewsbot.github.watching;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public final class GithubVersionChecker
{
    private static final Logger LOGGER = LoggerFactory.getLogger(GithubVersionChecker.class);

    private static final HttpClient HTTP_CLIENT = HttpClient.newHttpClient();

    private GithubVersionChecker()
    {

    }

    public static GithubRelease getLatestRelease(URL projectUrl)
    {
        String projectReleasesUrl = "https://api." + projectUrl.getHost() + "/repos" + projectUrl.getPath() + "/releases/latest";

        JsonElement latestJsonData = sendGetRequest(projectReleasesUrl);

        if (latestJsonData != null)
        {
            JsonObject jsonObject = latestJsonData.getAsJsonObject();
            return parseJsonToRelease(projectUrl.getPath().substring(projectUrl.getPath().lastIndexOf("/") + 1), jsonObject);
        }

        return null;
    }

    public static GithubRelease getReleaseForTag(URL projectUrl, String tag)
    {
        String currentTagUrl = "https://api." + projectUrl.getHost() + "/repos" + projectUrl.getPath() + "/releases/tags/" + tag;

        JsonElement jsonElement = sendGetRequest(currentTagUrl);

        if (jsonElement == null)
            return null;

        return parseJsonToRelease(projectUrl.getPath().substring(projectUrl.getPath().lastIndexOf("/") + 1), jsonElement.getAsJsonObject());
    }

    private static GithubRelease parseJsonToRelease(String projectName, JsonObject jsonObject)
    {
        String tag = jsonObject.get("tag_name").getAsString();
        String releaseNotes = jsonObject.get("body").getAsString();
        Date latestReleaseDate = Date.from(Instant.parse(jsonObject.get("published_at").getAsString()));
        String releaseUrl = jsonObject.get("html_url").getAsString();

        GithubRelease githubRelease = new GithubRelease(projectName, tag,
                releaseNotes,
                LocalDateTime.ofInstant(latestReleaseDate.toInstant(), ZoneId.systemDefault()),
                releaseUrl);
        return githubRelease;
    }

    private static JsonElement sendGetRequest(String url)
    {
        LOGGER.info("GET " + url);

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url))
                .build();
        try
        {
            HttpResponse<String> response =  HTTP_CLIENT.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            LOGGER.info(response.body());
            if (response.statusCode() == 404)
            {
                return null;
            }
            return JsonParser.parseString(response.body());
        }
        catch (IOException | InterruptedException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
