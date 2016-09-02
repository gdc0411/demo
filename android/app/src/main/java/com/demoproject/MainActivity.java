package com.demoproject;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.facebook.react.ReactActivity;

public class MainActivity extends ReactActivity {

    /**
     * Returns the name of the main component registered from JavaScript.
     * This is used to schedule rendering of the component.
     */
    @Override
    protected String getMainComponentName() {
        return "DemoProject";
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (1 != requestCode || RESULT_OK != requestCode) return;

        Uri contactData = data.getData();

        Cursor cursor = managedQuery(contactData, null, null, null, null);
        cursor.moveToFirst();

        String num = getContactPhone(cursor);

        //把num发给RN侧
        MainApplication.getRjPackage().getRjNativeModule().callNativeWithResult(num);

    }


    private String getContactPhone( Cursor cursor){
        int phoneColum =  cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
        int phoneNum =  cursor.getInt(phoneColum);
        String result = "";
        if(phoneNum>0){
            //获得联系人的ID号
            int idColumn =  cursor.getColumnIndex(ContactsContract.Contacts._ID);
            String contactId =  cursor.getString(idColumn);

        }
        return result;

    }
}
