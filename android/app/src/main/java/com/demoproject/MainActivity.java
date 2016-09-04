package com.demoproject;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

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
//        if (1 != requestCode || RESULT_OK != resultCode) return;
//
//        Uri contactData = data.getData();
//
//        Cursor cursor = managedQuery(contactData, null, null, null, null);
//        cursor.moveToFirst();
//
//        String num = getContactPhone(cursor);
//
//        //Log.i("饶佳的测试",num);
//
//        //把num发给RN侧
//        MainApplication.getRjPackage().getRjNativeModule().sendMsgToRN(num);

    }


//    private String getContactPhone(Cursor cursor) {
//        int phoneColum = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
//        int phoneNum = cursor.getInt(phoneColum);
//        String result = "";
//        if (phoneNum > 0) {
//            //获得联系人的ID号
//            int idColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID);
//            String contactId = cursor.getString(idColumn);
//            // 获得联系人电话的cursor
//            Cursor phone = getContentResolver().query(
//                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//                    null,
//                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contactId, null, null
//            );
//            if (phone.moveToFirst()) {
//                for (; !phone.isAfterLast(); phone.moveToNext()) {
//                    int index = phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
//                    int typeindex = phone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
//                    int phone_type = phone.getInt(typeindex);
//                    String phoneNumber = phone.getString(index);
//                    result = phoneNumber;
////                  switch (phone_type) {//此处请看下方注释
////                  case 2:
////                      result = phoneNumber;
////                      break;
////
////                  default:
////                      break;
////                  }
//                }
//                if (!phone.isClosed()) {
//                    phone.close();
//                }
//            }
//        }
//        return result;
//    }
}
