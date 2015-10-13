package org.kagelabs.weather;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

/**
 * Created by Patrick on 10/12/15.
 */


public class SettingsCache {
    private HashMap<String, String> data;

    public SettingsCache() {
        data = new HashMap<String, String>();
    }

    public boolean read(FileInputStream in) {
        Scanner sc = new Scanner(in);
        String out = "";
        while (sc.hasNext()) {
            out += sc.nextLine();
        }
        try {
            JSONObject dict = new JSONObject(out);
            Iterator<String> iter = dict.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                data.put(key,dict.getString(key));
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean write(FileOutputStream out) {
        JSONObject obj = new JSONObject(this.data);

        try {
            out.write(obj.toString().getBytes());
            System.out.println("wrote " + obj.toString());
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public void put(String key, String data) {
        this.data.put(key, data);
    }

    public String get(String key) {
        return this.data.get(key);
    }

    public boolean getbool(String key) {return (this.data.get(key) == null || this.data.get(key) != "true") ? false : true;}

    public void putbool(String key, boolean value) {
        this.data.put(key, value ? "true" : "false");
    }

}
