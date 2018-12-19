package com.example.mosaab.orderfoods.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import com.example.mosaab.orderfoods.Model.Favorites;
import com.example.mosaab.orderfoods.Model.Order;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

public class Database  extends SQLiteAssetHelper {
    private static final  String DB_NAME="orderFoodDB";
    private static final int DB_VER=8;
    private static final String TAG = "Database";


    public Database(Context context) {

        super(context, DB_NAME, null, DB_VER);
        setForcedUpgrade();
    }


    public List<Order> getCarts(String User_phone)
    {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qp = new SQLiteQueryBuilder();

        //PRIMARY KEY(`User_Phone`,`ProductId`)
        String[] sqlSelect = { "User_Phone" , "ProudctName" , "ProductId", "Quantity", "Price", "Discount", "Image" };

        String sqlTable = "OrderDetail";

        qp.setTables(sqlTable);

        Cursor c = qp.query(db, sqlSelect, "User_Phone=?", new String[]{User_phone}, null, null, null);

        final List<Order> result = new ArrayList<>();
        if(c.moveToFirst())
        {
            do{
                result.add(new Order(
                        c.getString(c.getColumnIndex("User_Phone")),
                        c.getString(c.getColumnIndex("ProductId")),
                        c.getString(c.getColumnIndex("ProudctName")),
                        c.getString(c.getColumnIndex("Quantity")),
                        c.getString(c.getColumnIndex("Price")),
                        c.getString(c.getColumnIndex("Discount")),
                        c.getString(c.getColumnIndex("Image"))
                        ));

            }while (c.moveToNext());

        }
            return result;

    }

    public void addToCart (Order order)
    {
        Log.d(TAG, "addToCart: ");
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT OR REPLACE INTO OrderDetail (User_Phone,ProductId, ProudctName, Quantity, Price, Discount, Image)" +
                " VALUES( '%s','%s','%s','%s','%s','%s', '%s' );",
                order.getUser_Phone(),
                order.getProudctId(),
                order.getProductName(),
                order.getQuantity(),
                order.getPrice(),
                order.getDiscount(),
                order.getImage());
        db.execSQL(query);

    }

    public void CleanCart (String user_phone)
    {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format(" DELETE FROM OrderDetail WHERE User_Phone = '%s' ",user_phone);

        db.execSQL(query);

    }
    public int getCountCart(String user_phone)
    {
        Log.d(TAG, "getCountCart: ");
        int count = 0;

        SQLiteDatabase db = getReadableDatabase();
        String query =String.format("SELECT COUNT(*) FROM OrderDetail WHERE User_Phone = '%s' ",user_phone);
        Cursor cursor =db.rawQuery(query,null);
        if(cursor.moveToFirst())
        {
            do {
                count = cursor.getInt(0);

            }while (cursor.moveToNext());
        }

        return count;
    }

    public void updateCart(Order order)
    {
        Log.d(TAG, "updateCart: ");
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("UPDATE OrderDetail SET Quantity= '%s' WHERE User_Phone = '%s' AND ProductId = '%s'", order.getQuantity(), order.getUser_Phone(),order.getProudctId());
        db.execSQL(query);
    }

    public void increase_Cart(String user_phone,String food_id)
    {
        Log.d(TAG, "increase_Cart: ");
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("UPDATE OrderDetail SET Quantity= Quantity+1 WHERE User_Phone = '%s' AND ProductId = '%s'",user_phone,food_id);
        db.execSQL(query);
    }

    public void removeFromCart(String proudctId, String phone)
    {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format(" DELETE FROM OrderDetail WHERE User_Phone = '%s' AND ProductId = '%s'",phone,proudctId);

        db.execSQL(query);
    }
    public boolean Check_if_Food_exist_inCART(String food_id, String user_phone)
    {
        Log.d(TAG, "Check_if_Food_exist_inCART: "  );  
        boolean flag = false;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String SQLQuery = String.format("SELECT * FROM OrderDetail WHERE User_Phone = '%s' AND ProductId = '%s' ",user_phone,food_id);

        cursor = db.rawQuery(SQLQuery,null);
        if(cursor.getCount()>0)
        {
            flag = true;
        }
        else
        {
            flag = false;

        }
        cursor.close();
        return flag;
    }

    //Favorites
    public  void addToFavorites(Favorites favorites_food)
    {
        SQLiteDatabase db = getReadableDatabase();
        String query =String.format("INSERT INTO Favorets(" +
                        "Foodid, User_Phone, FoodName, FoodPrice, FoodMenuId, FoodImage, FoodDiscount, FoodDescription) " +
                        "VALUES('%s','%s','%s','%s','%s','%s','%s','%s');",
                favorites_food.getFoodId(),favorites_food.getUserPhone(),favorites_food.getFoodName(),favorites_food.getFoodPrice(),
                favorites_food.getFoodMenuId(),favorites_food.getFoodIamge(),favorites_food.getFoodDiscount(),favorites_food.getFoodDescription());
        db.execSQL(query);
    }

    public  void removeFromFavorites(String foodId,String user_phone)
    {
        SQLiteDatabase db = getReadableDatabase();
        String query =String.format("DELETE FROM Favorets WHERE Foodid='%s'and User_Phone ='&s';",foodId,user_phone);
        db.execSQL(query);
    }

    public  boolean isFavorites(String foodId,String user_phone)
    {
        SQLiteDatabase db = getReadableDatabase();
        String query =String.format("SELECT * FROM Favorets WHERE Foodid='%s' and User_Phone = '%s';",foodId,user_phone);
        Cursor cursor =db.rawQuery(query,null);
        if(cursor.getCount()<=0)
        {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public List<Favorites> get_all_favorties(String User_phone)
    {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qp = new SQLiteQueryBuilder();

        //PRIMARY KEY(`User_Phone`,`ProductId`)
        String[] sqlSelect = { "Foodid" , "User_Phone" , "FoodName", "FoodPrice", "FoodMenuId", "FoodImage", "FoodDiscount","FoodDescription" };

        String sqlTable = "Favorets";

        qp.setTables(sqlTable);

        Cursor c = qp.query(db, sqlSelect, "User_Phone=?", new String[]{User_phone}, null, null, null);

        final List<Favorites> result = new ArrayList<>();
        if(c.moveToFirst())
        {
            do{
                result.add(new Favorites(
                        c.getString(c.getColumnIndex("Foodid")),
                        c.getString(c.getColumnIndex("FoodName")),
                        c.getString(c.getColumnIndex("FoodPrice")),
                        c.getString(c.getColumnIndex("FoodMenuId")),
                        c.getString(c.getColumnIndex("FoodImage")),
                        c.getString(c.getColumnIndex("FoodDiscount")),
                        c.getString(c.getColumnIndex("FoodDescription")),
                        c.getString(c.getColumnIndex("User_Phone"))
                ));

            }while (c.moveToNext());

        }
        return result;

    }


}
