/*
 * Copyright (C) 2017 The Android Open Source Project
 * Copyright (C) 2021 The exTHmUI Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.android.dialer.searchfragment.nearbyplaces;

import android.content.Context;
import android.net.Uri;
import android.provider.ContactsContract;
import android.provider.ContactsContract.Contacts;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.QuickContactBadge;
import android.widget.TextView;
import com.android.contacts.common.util.Constants;
import com.android.dialer.contactphoto.ContactPhotoManager;
import com.android.dialer.lettertile.LetterTileDrawable;
import com.android.dialer.searchfragment.common.Projections;
import com.android.dialer.searchfragment.common.QueryBoldingUtil;
import com.android.dialer.searchfragment.common.R;
import com.android.dialer.searchfragment.common.RowClickListener;
import com.android.dialer.searchfragment.common.SearchCursor;

/** ViewHolder for a nearby place row. */
public final class NearbyPlaceViewHolder extends RecyclerView.ViewHolder
    implements View.OnClickListener {

  private final Context context;
  private final TextView placeName;
  private final TextView placeLabel;
  private final QuickContactBadge photo;
  private final RowClickListener listener;

  private String number;
  private int position;

  public NearbyPlaceViewHolder(View view, RowClickListener listener) {
    super(view);
    view.setOnClickListener(this);
    photo = view.findViewById(R.id.photo);
    placeName = view.findViewById(R.id.primary);
    placeLabel = view.findViewById(R.id.secondary);
    context = view.getContext();
    this.listener = listener;
  }

  /**
   * Binds the ViewHolder with a cursor from {@link NearbyPlacesCursorLoader} with the data found at
   * the cursors set position.
   */
  public void bind(SearchCursor cursor, String query) {
    number = cursor.getString(Projections.PHONE_NUMBER);
    position = cursor.getPosition();
    String name = cursor.getString(Projections.DISPLAY_NAME);
    String label = cursor.getString(Projections.PHONE_LABEL);

    placeName.setText(QueryBoldingUtil.getNameWithQueryBolded(query, name, context));
    if (label.startsWith("[NOADDR]")) {
      String sublabel = label.substring(8);
      label = TextUtils.isEmpty(sublabel) ? number : context.getString(R.string.call_subject_type_and_number, sublabel, number);
      placeLabel.setText(QueryBoldingUtil.getNumberWithQueryBolded(query, label));
    } else {
      placeLabel.setText(QueryBoldingUtil.getNameWithQueryBolded(query, label, context));
    }


    if (shouldShowPhoto(cursor)) {
      placeName.setVisibility(View.VISIBLE);
      photo.setVisibility(View.VISIBLE);
      String photoUri = cursor.getString(Projections.PHOTO_URI);
      ContactPhotoManager.getInstance(context)
          .loadDialerThumbnailOrPhoto(
              photo,
              getContactUri(cursor),
              cursor.getLong(Projections.PHOTO_ID),
              photoUri == null ? null : Uri.parse(photoUri),
              name,
              LetterTileDrawable.TYPE_BUSINESS);
    } else {
      placeName.setVisibility(View.GONE);
      photo.setVisibility(View.INVISIBLE);
    }
  }

  // Show the contact photo next to only the first number if a contact has multiple numbers
  private boolean shouldShowPhoto(SearchCursor cursor) {
    int currentPosition = cursor.getPosition();
    String currentLookupKey = cursor.getString(Projections.LOOKUP_KEY);
    cursor.moveToPosition(currentPosition - 1);

    if (!cursor.isHeader() && !cursor.isBeforeFirst()) {
      String previousLookupKey = cursor.getString(Projections.LOOKUP_KEY);
      cursor.moveToPosition(currentPosition);
      return !currentLookupKey.equals(previousLookupKey);
    }
    cursor.moveToPosition(currentPosition);
    return true;
  }

  private static Uri getContactUri(SearchCursor cursor) {
    // Since the lookup key for Nearby Places is actually a JSON representation of the information,
    // we need to pass it in as an encoded fragment in our contact uri.
    // It includes information like display name, photo uri, phone number, ect.
    String businessInfoJson = cursor.getString(Projections.LOOKUP_KEY);
    return Contacts.CONTENT_LOOKUP_URI
        .buildUpon()
        .appendPath(Constants.LOOKUP_URI_ENCODED)
        .appendQueryParameter(
            ContactsContract.DIRECTORY_PARAM_KEY, String.valueOf(cursor.getDirectoryId()))
        .encodedFragment(businessInfoJson)
        .build();
  }

  @Override
  public void onClick(View v) {
    listener.placeVoiceCall(number, position);
  }
}
