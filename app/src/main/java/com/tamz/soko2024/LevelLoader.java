package com.tamz.soko2024;

import android.content.Context;
import android.content.res.AssetManager;

import org.json.JSONArray;
import org.json.JSONObject;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class LevelLoader {

    public static int[] loadLevelFromJson(Context context, String levelName) {
        int[] levelArray = null;

        try {
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open("levels.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            String jsonString = new String(buffer, StandardCharsets.UTF_8);

            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObject.getJSONArray(levelName);

            levelArray = new int[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                levelArray[i] = jsonArray.getInt(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return levelArray;
    }

    public static List<String> getLevelNames(Context context) {
        List<String> levelNames = new ArrayList<>();

        try {
            AssetManager assetManager = context.getAssets();
            InputStream inputStream = assetManager.open("levels.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            String jsonString = new String(buffer, StandardCharsets.UTF_8);

            JSONObject jsonObject = new JSONObject(jsonString);
            Iterator<String> keys = jsonObject.keys();

            while (keys.hasNext()) {
                levelNames.add(keys.next());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return levelNames;
    }
}
