/*
 * Copyright (C) 2020 The exTHmUI Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.exthmui.yellowpage;

import android.content.Context;
import android.database.Cursor;

import com.android.dialer.lookup.ContactBuilder;
import com.android.dialer.phonenumbercache.ContactInfo;

import org.exthmui.yellowpage.misc.Constants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.ArrayList;

public class YellowPageReader {
    private static final String TAG = YellowPageReader.class.getSimpleName();

    public static List<ContactInfo> queryForwardContactInfo(Context context, String filter) {
        ArrayList<ContactInfo> list = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(Constants.YELLOWPAGE_PROVIDER_URI_FORWARD, Constants.YellowPageData.DATA_PROJECTION, filter, null, null, null);
        if (cursor == null) return null;
        while (cursor.moveToNext()) {
            try {
                ContactBuilder builder = ContactBuilder.forPeopleLookup("");
                list.addAll(parsePeopleContactDataList(dataRowToInfo(cursor, builder), cursor.getString(Constants.YellowPageData.COLUMN_PHONE_JSON), filter));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return list;
    }

    public static ContactInfo queryReverseContactInfo(Context context, String normalizedNumber, String formattedNumber) {
        Cursor cursor = context.getContentResolver().query(Constants.YELLOWPAGE_PROVIDER_URI_REVERSE, Constants.YellowPageData.DATA_PROJECTION, normalizedNumber, null, null, null);
        if (cursor == null || !cursor.moveToPosition(0)) return null;
        ContactInfo info = null;
        ContactBuilder builder = ContactBuilder.forReverseLookup(normalizedNumber, formattedNumber);
        try {
            info = dataRowToInfo(cursor, builder);
            List<ContactBuilder.PhoneNumber> phoneNumberList = createPhoneNumberList(cursor.getString(Constants.YellowPageData.COLUMN_PHONE_JSON));
            for (ContactBuilder.PhoneNumber tNumber : phoneNumberList) {
                if (tNumber.number.equals(normalizedNumber)) {
                    info.number = tNumber.number;
                    info.normalizedNumber = tNumber.number;
                    info.label = tNumber.label;
                    info.type = tNumber.type;
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } 
        cursor.close();
        return info;
    }

    private static List<ContactInfo> parsePeopleContactDataList(ContactInfo info, String phoneJson, String filter) {
        List<ContactInfo> list = new ArrayList();
        boolean filterIsPhoneNumber = filter.matches("[0-9]+");
        try {
            List<ContactBuilder.PhoneNumber> phoneNumberList = createPhoneNumberList(phoneJson);
            for (ContactBuilder.PhoneNumber tNumber : phoneNumberList) {
                if (!filterIsPhoneNumber || tNumber.number.startsWith(filter)) {
                    ContactInfo tInfo = (ContactInfo) info.clone();
                    tInfo.number = tNumber.number;
                    tInfo.normalizedNumber = tNumber.number;
                    tInfo.label = tNumber.label;
                    tInfo.type = tNumber.type;
                    list.add(tInfo);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (list.isEmpty()) {
            list.add(info);
        }
        return list;
    }

    private static List<ContactBuilder.PhoneNumber> createPhoneNumberList(String phoneJson) throws JSONException {
        JSONArray jsonArray = new JSONArray(phoneJson);
        List<ContactBuilder.PhoneNumber> list = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            list.add(new ContactBuilder.PhoneNumber(jsonObject));
        }
        return list;
    }

    private static ContactInfo dataRowToInfo(Cursor cursor, ContactBuilder builder) throws JSONException {
        builder.setName(ContactBuilder.Name.createDisplayName(cursor.getString(Constants.YellowPageData.COLUMN_NAME)))
                .setPhotoUri(cursor.getString(Constants.YellowPageData.COLUMN_AVATAR));
        List<ContactBuilder.PhoneNumber> phoneNumberList = createPhoneNumberList(cursor.getString(Constants.YellowPageData.COLUMN_PHONE_JSON));
        for (ContactBuilder.PhoneNumber pn : phoneNumberList) {
            builder.addPhoneNumber(pn);
        }
        JSONArray jsonArray = new JSONArray(cursor.getString(Constants.YellowPageData.COLUMN_WEBSITE_JSON));
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            builder.addWebsite(new ContactBuilder.WebsiteUrl(jsonObject));
        }
        jsonArray = new JSONArray(cursor.getString(Constants.YellowPageData.COLUMN_ADDRESS_JSON));
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            builder.addAddress(new ContactBuilder.Address(jsonObject));
        }
        return builder.build();
    }
}

