package de.rretzbach.redditflow;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.awt.Image;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 *
 * @author rretzbach
 */
class ImageFetcher {
    private String url;

    public ImageFetcher() {
    }

    void setUrl(String url) {
        this.url = url;
        if (!this.url.endsWith(".json")) {
            this.url = this.url + ".json";
        }
    }

    public List<String> fetchImageUrls() {
        List<String> urls = null;

        try {
            URI url = new URI(this.url);
            DefaultHttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet(url);
            HttpResponse response = client.execute(get);
            InputStream in = response.getEntity().getContent();
            JsonParser parser = new JsonParser();
            JsonElement elem = parser.parse(new InputStreamReader(in));
            JsonObject obj = elem.getAsJsonObject();
            JsonArray foo = obj.get("data").getAsJsonObject().get("children").getAsJsonArray();
            urls = new ArrayList<String>();
            for (Iterator<JsonElement> it = foo.iterator(); it.hasNext(); ) {
                JsonElement post = it.next();
                String link = post.getAsJsonObject().get("data").getAsJsonObject().get("url").getAsString();

                if (link.contains("i.imgur.com") ) {
                    urls.add(link);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(ImageFetcher.class.getName()).log(Level.SEVERE, null, ex);
        }

        return urls;
    }
    
}
