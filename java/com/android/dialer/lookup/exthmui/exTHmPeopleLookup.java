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

package com.android.dialer.lookup.exthmui;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

import com.android.dialer.phonenumbercache.ContactInfo;
import com.android.dialer.lookup.ContactBuilder;
import com.android.dialer.lookup.PeopleLookup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.exthmui.yellowpage.YellowPageReader;

public class exTHmPeopleLookup extends PeopleLookup {
    private static final String TAG = exTHmPeopleLookup.class.getSimpleName();

    public exTHmPeopleLookup(Context context) {
    }

    @Override
    public List<ContactInfo> lookup(Context context, String filter) {
        return YellowPageReader.queryForwardContactInfo(context, filter);
    }

}
