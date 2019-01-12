package unipd.se18.ocrcamera;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


public class StatCalculator extends AppCompatActivity {

    private HashMap<String, Integer> stats= new HashMap(); //HashMap<ingrediente, quantità>

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stat_layout);
        Log.e("hash1",stats.toString());
        stats.put("aqua",2);
        saveMap(stats);
        //showHashmap();

        stats= loadMap();

        Log.e("hash2",stats.toString());
        ArrayList<String> ingredienti= new ArrayList<>();
        ingredienti.add("aqua");
        ingredienti.add("tbc");
        stats = updateMap(ingredienti);
        Log.e("hash3", stats.toString());


    }

    @Override
    protected void onResume() {
        super.onResume();
        //carico la map salvata
        stats = loadMap();
        Log.e("hash",stats.toString());



    }

    @Override
    protected void onStop() {
        super.onStop();
        saveMap(stats);
    }


    /**
     * method to save the hashmap
     * @param inputMap
     * @author Leonardo Pratesi
     */
    public void saveMap(HashMap inputMap){
        SharedPreferences pSharedPref = getApplicationContext().getSharedPreferences("MyVariables", Context.MODE_PRIVATE);
        if (pSharedPref != null){
            JSONObject jsonObject = new JSONObject(inputMap);
            String jsonString = jsonObject.toString();
            SharedPreferences.Editor editor = pSharedPref.edit();
            editor.remove("My_map").commit();
            editor.putString("My_map", jsonString);
            editor.commit();
        }
    }

    /**
     * method to load the hashmap
     * @return Hashmap
     * @author Leonardo Pratesi
     */
    public HashMap loadMap(){
        HashMap outputMap = new HashMap<>();
        SharedPreferences pSharedPref = getApplicationContext().getSharedPreferences("MyVariables", Context.MODE_PRIVATE);
        try{
            if (pSharedPref != null){
                String jsonString = pSharedPref.getString("My_map", (new JSONObject()).toString());
                JSONObject jsonObject = new JSONObject(jsonString);
                Iterator<String> keysItr = jsonObject.keys();
                while(keysItr.hasNext()) {
                    String key = keysItr.next();
                    Integer value = (int) jsonObject.get(key);
                    outputMap.put(key, value);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return outputMap;
    }

    public HashMap updateMap(ArrayList<String> ingredients) {
        int i=0;
        for (String s : ingredients) {
            if (stats.containsValue(s))
                stats.put(s, stats.get(s) + 1);

            else {
                stats.put(s, 1);
            }
        }
        return stats;

    }

}