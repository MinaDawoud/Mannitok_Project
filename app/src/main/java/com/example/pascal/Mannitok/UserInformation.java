package com.example.pascal.Mannitok;

import java.util.ArrayList;

/**
 * Created by Pascal on 15/04/2016.
 */
public class UserInformation {

    private static String macAddress;
    private static String username;
    private static ArrayList<String> favoris;

    public static String getMacAddress(){
        return macAddress;
    }

    public static void setMacAddress(String mac){
        macAddress = mac;
    }

    public static String getUsername(){
        return username;
    }

    public static void setUsername(String name){
        username = name;
    }

    public static void createFavoris(){
        favoris = new ArrayList<String>();
    }

    public static void addFavori(String code){
        favoris.add(code);
    }

    public static void deleteFavori(String code){
        for(int i=0; i < favoris.size(); i++){
            if (favoris.get(i).equals(code)){
                favoris.remove(i);
            }
        }
    }

    public static boolean isFavori(String code){
        for(int i=0; i < favoris.size(); i++){
            if (favoris.get(i).equals(code)){
                return true;
            }
        }
        return false;
    }

}
