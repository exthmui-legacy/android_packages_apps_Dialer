/*
 * Copyright (C) 2021 The exTHmUI Open Source Project
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

import com.android.dialer.phonenumbercache.ContactInfo;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import org.exthmui.yellowpage.misc.Constants;

public class PhoneNumberTag {

    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_SPAM = -1;
    public static final int TYPE_SERVICE = 1;

    public static class PhoneNumberInfo {
        public String number;
        public int type = TYPE_NORMAL;
        public String tag;
    }

    public static PhoneNumberInfo getPhoneNumberInfo(Context context, String number, String countryIso) {
        PhoneNumberInfo info = new PhoneNumberInfo();
        info.number = number.replace(" ", "");
        if (number == null) return info;
        Cursor cursor = context.getContentResolver().query(Constants.PHONE_NUMBER_TAG_PROVIDER_URI_QUERY, Constants.PhoneNumberTagData.DATA_PROJECTION, info.number, null, null);
        if (cursor != null) {
            if (cursor.moveToNext()) {
                info.tag = cursor.getString(Constants.PhoneNumberTagData.COLUMN_TAG);
                info.type = cursor.getInt(Constants.PhoneNumberTagData.COLUMN_TYPE);
            }
            cursor.close();
        }
        return info;
    }

    public static ListenableFuture<PhoneNumberInfo> getPhoneNumberInfoLF(Context context, String number, String countryIso) {
        ListeningExecutorService executorService = MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
        final ListenableFuture<PhoneNumberInfo> listenableFuture = executorService.submit(new Callable<PhoneNumberInfo>() {
            @Override
            public PhoneNumberInfo call() throws Exception {
                return getPhoneNumberInfo(context, number, countryIso);
            }
        });
        return listenableFuture;
    }
}