package com.furdey.shopping.fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.ImageView;
import android.widget.TextView;

import com.furdey.shopping.cache.BitmapUrlCache;
import com.furdey.shopping.R;
import com.furdey.social.model.Person;

public class ConfirmFriendFragment extends DialogFragment {

	private Person person;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.setContentView(R.layout.friends_li);
		setRetainInstance(true);
		return dialog;
	}

	@Override
	public void onStart() {
		TextView friendsLiName = (TextView) getView().findViewById(R.id.friendsLiName);
		friendsLiName.setText(person.getFirstName().concat(" ").concat(person.getLastName()));
		BitmapUrlCache bitmapCache = new BitmapUrlCache(getActivity());
		ImageView friendsLiPhoto = (ImageView) getView().findViewById(R.id.friendsLiPhoto);
		bitmapCache.loadBitmap(person.getPhotoUrl(), friendsLiPhoto);
		super.onStart();
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

}
