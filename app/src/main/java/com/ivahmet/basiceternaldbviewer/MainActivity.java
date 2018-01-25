package com.ivahmet.basiceternaldbviewer;

/*
    Created by ivahmet on 24.1.2018.
*/

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();

    ProgressDialog pDialog;
    ListView mainlistview;
    String url = "https://eternalwarcry.com/content/cards/eternal-cards.json";
    ArrayList<HashMap<String, String>> contactList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contactList = new ArrayList<>();
        mainlistview = findViewById(R.id.mainlistview);

        new GetContacts().execute();
    }

    @SuppressLint("StaticFieldLeak")
    private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    //JSONObject jsonObj = new JSONObject(jsonStr);

                    JSONArray cards = new JSONArray(jsonStr);

                    for (int i = 0; i < cards.length(); i++) {
                        JSONObject incoming_cards = cards.getJSONObject(i);

                        int SetNumber = incoming_cards.getInt("SetNumber");
                        String EternalID = incoming_cards.getString("EternalID");
                        String Name = incoming_cards.getString("Name");
                        String CardText = incoming_cards.getString("CardText");
                        int Cost = incoming_cards.getInt("Cost");
                        String Influence = incoming_cards.getString("Influence");
                        int Attack = incoming_cards.getInt("Attack");
                        int Health = incoming_cards.getInt("Health");
                        String Rarity = incoming_cards.getString("Rarity");
                        String Type = incoming_cards.getString("Type");
                        //String ImageUrl = incoming_cards.getString("ImageUrl");

                        HashMap<String, String> contact = new HashMap<>();

                        contact.put("CardID", "Set " + String.valueOf(SetNumber) + "#" + EternalID);
                        contact.put("Name", Name);
                        contact.put("CardText", CardText);
                        contact.put("Cost", String.valueOf(Cost) + " Power");
                        if(Influence.length() == 3)
                            contact.put("Influence","Required Influence:\t" + Influence);
                        else
                            contact.put("Influence","Required Influences:\t" + Influence);
                        if(!Type.equals("Spell"))
                            contact.put("Stats", String.valueOf(Attack) + "/" + String.valueOf(Health));
                        contact.put("Rarity", Rarity);
                        contact.put("Type", Type);

                        contactList.add(contact);
                    }
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                }
            }
                Log.e(TAG, "Couldn't get json from server.");
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            if (pDialog.isShowing())
                pDialog.dismiss();

            ListAdapter adapter = new SimpleAdapter(
                    MainActivity.this, contactList,
                    R.layout.list_item, new String[]{"CardID", "Name", "CardText", "Cost", "Influence", "Stats", "Rarity","Type",}
                    , new int[]{R.id.item_id, R.id.item_name, R.id.item_text, R.id.item_cost, R.id.item_influence,
                    R.id.item_stats, R.id.item_rarity, R.id.item_type});

            mainlistview.setAdapter(adapter);
        }

    }
}