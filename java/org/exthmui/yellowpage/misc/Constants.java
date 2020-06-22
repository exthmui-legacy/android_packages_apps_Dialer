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

package org.exthmui.yellowpage.misc;

import android.net.Uri;
import android.provider.ContactsContract;

public class Constants {

    public static final int COLUMN_ID = 0;
    public static final int COLUMN_NAME = 1;
    public static final int COLUMN_AVATAR = 2;
    public static final int COLUMN_PHONE_JSON = 3;
    public static final int COLUMN_WEBSITE_JSON = 4;
    public static final int COLUMN_ADDRESS_JSON = 5;

    public static final String[] DATA_PROJECTION =
        new String[] {
            "id",
            "name",
            "avatar",
            "phone_json",
            "website_json",
            "address_json"
        };

    public static final Uri YELLOWPAGE_PROVIDER_URI = Uri.parse("content://org.exthmui.yellowpage.YellowPageProvider");
    public static final Uri YELLOWPAGE_PROVIDER_URI_FORWARD = Uri.withAppendedPath(YELLOWPAGE_PROVIDER_URI, "forward");
    public static final Uri YELLOWPAGE_PROVIDER_URI_REVERSE = Uri.withAppendedPath(YELLOWPAGE_PROVIDER_URI, "reverse");

}
