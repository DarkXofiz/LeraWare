package neva.ware.core.manager.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.List;
import neva.ware.core.manager.IManager;
import neva.ware.features.modules.client.ClientSettings;
import neva.ware.utility.Timer;
import org.apache.commons.compress.utils.Lists;

public class TelemetryManager implements IManager {
   private final Timer pingTimer = new Timer();
   private List<String> onlinePlayers = new ArrayList<>();
   private List<String> allPlayers = new ArrayList<>();

   public void onUpdate() {
      if (this.pingTimer.every(90000L)) {
         this.fetchData();
      }
   }

   public void fetchData() {
      if (ClientSettings.telemetry.getValue()) {
         this.pingServer(mc.method_1548().method_1676());
      }

      this.onlinePlayers = getPlayers(true);
      this.allPlayers = getPlayers(false);
   }

   public void pingServer(String name) {
      HttpRequest req = HttpRequest.newBuilder(URI.create("https://api.nevaware.net/v1/users/online?name=" + name)).POST(BodyPublishers.noBody()).build();

      try (HttpClient client = HttpClient.newHttpClient()) {
         client.send(req, BodyHandlers.ofString());
      } catch (Throwable var8) {
      }
   }

   public static List<String> getPlayers(boolean online) {
      HttpRequest request = HttpRequest.newBuilder(URI.create("https://api.nevaware.net/v1/users" + (online ? "/online" : ""))).GET().build();
      List<String> names = new ArrayList<>();

      try (HttpClient client = HttpClient.newHttpClient()) {
         HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
         JsonArray array = JsonParser.parseString(response.body()).getAsJsonArray();
         array.forEach(e -> names.add(e.getAsJsonObject().get("name").getAsString()));
      } catch (Throwable var8) {
      }

      return names;
   }

   public List<String> getOnlinePlayers() {
      return Lists.newArrayList(this.onlinePlayers.iterator());
   }

   public List<String> getAllPlayers() {
      return Lists.newArrayList(this.allPlayers.iterator());
   }
}
