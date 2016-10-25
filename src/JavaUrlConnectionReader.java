import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;

import java.awt.*;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A complete Java class that demonstrates how to read content (text) from a URL
 * using the Java URL and URLConnection classes.
 *
 * @author alvin alexander, devdaily.com
 */
class JavaUrlConnectionReader {
    public static void main(String[] args) throws IOException, URISyntaxException, InterruptedException {

        HashMap<String, ArrayList> mapOfLatlng = new HashMap<>();
        ArrayList<String> listOfAddress = getShops("東京都", "", "1017","");
//        System.out.print(listOfAddress);
        for(String address : listOfAddress) {
            ArrayList<Double> coordsList = getCoords(address);
            if(!coordsList.contains(null)){
                mapOfLatlng.put(address,coordsList);
            }
            Thread.sleep(1000);
//            System.out.print(mapOfLatlng);
        }
        generateMap(listOfAddress, mapOfLatlng);
    }

    private static ArrayList<String> getShops(String prefecture, String place, String eventId, String freeWord) {
        if (place == "") {
            place = prefecture;
        }
        StringBuilder content = new StringBuilder();
        ArrayList<String> listOfAddress = new ArrayList<>();

        // many of these calls can throw exceptions, so i've just
        // wrapped them all in one try/catch statement.
        try {
            String theUrl = "http://pripara.jp/shop/search_list?pref_name=" + prefecture + "&event_id=" + eventId + "&freeword=" + freeWord;

            // create a url object
            URL url = new URL(theUrl);

            // create a urlconnection object
            URLConnection urlConnection = url.openConnection();

            // wrap the urlconnection in a bufferedreader
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            String line;
            Boolean limit = false;
            Boolean time = false;


            // read from the urlconnection via the bufferedreader
            while ((line = bufferedReader.readLine()) != null) {
                content.append(line + "\n");
//                System.out.println(line);
                if (line.contains(place)) {
                    line = line.replace("<p>", "").replace("</p>", "").replace("玩具売場", "").replace(" ", "");
//                    System.out.println(line);
                    if (!line.contains("<")) {
                        listOfAddress.add(line);
                    }
                }

//                if(line.contains("<h2 class=\"tbl01\">")){
//                    line = line.replace("<h2 class=\"tbl01\">","").replace("</h2>      </div>","").replace(" ","");
//                    System.out.println(line);
//                    listOfShops.add(line);
//                }
//
//                if(limit==true){
//                    line = line.replace("<dd>","").replace("</dd>","").replace(" ","");
//                    System.out.println(line);
//                    limit = false;
//                }
//
//                if(time==true){
//                    line = line.replace("<dd>","").replace("</dd>","").replace(" ","");
//                    System.out.println(line);
//                    time = false;
//                }
//
//                if(line.contains("年齢制限")){
//                    limit = true;
//                }
//
//                if(line.contains("参加方法：")){
//                    time = true;
//                }
            }
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        System.out.print(content);
        return listOfAddress;
    }

    private static ArrayList<Double> getCoords(String address) throws IOException {
        Double latitude = null;
        Double longitude = null;
        ArrayList<Double> pairOfLatLng = new ArrayList<>();

        String stringUrl = "https://maps.googleapis.com/maps/api/geocode/json?address=" + address;
        URL url = new URL(stringUrl);
        URLConnection uc = url.openConnection();

        JsonNode jsonNode = new ObjectMapper().readTree(uc.getInputStream());
        JsonNode results = jsonNode.findPath("results");
        try {
            latitude = results.findPath("location").findValue("lat").asDouble();
            longitude = results.findPath("location").findValue("lng").asDouble();
            System.out.print("\n" + address + "の緯度が" + latitude + "で、経度が" + longitude + "です。");
        } catch (Exception e) {
            System.out.print("\n" + "次の住所の経度、緯度を獲得できませんでした：" + address);
//            System.out.print(e);
        }

        pairOfLatLng.add(latitude);
        pairOfLatLng.add(longitude);
        return pairOfLatLng;
    }

    public static void generateMap(ArrayList<String> list, HashMap map) throws IOException {
        File file = new File("openMap4.html");
        FileInputStream is = new FileInputStream(file);
        BufferedReader bf = new BufferedReader(new InputStreamReader(is));
        StringBuilder content = new StringBuilder();

        String newFile = "resultMap.html";
        FileWriter fw = new FileWriter(new File(newFile));

        int i = 0;
        while (i < map.size()) {
            String key = list.get(i);
            ArrayList<Double> values = (ArrayList<Double>) map.get(key);
            if (values != null) {
                content.append("\r\n" + "[" + "'" + key +"'"+ "," +values.get(0) + "," + values.get(1) + "," + i + "]" + ",");
                i++;
            }
//            System.out.print(content);
        }

        Boolean toReplace = false;
        int times = 0;
        String line;
        while ((line = bf.readLine()) != null) {
            if (!toReplace) {
                fw.append(line + "\n");
            } else if (times == 0) {
                fw.append(content);
                times++;
            } else {
                fw.append(line + "\n");
            }

            if (line.contains("locations")) {
                toReplace = true;
            }
        }

        bf.close();
        is.close();
        fw.close();

        File htmlFile = new File(newFile);
        Desktop.getDesktop().browse(htmlFile.toURI());
    }
}