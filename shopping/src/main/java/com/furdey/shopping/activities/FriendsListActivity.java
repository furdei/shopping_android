package com.furdey.shopping.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.furdey.shopping.R;
import com.furdey.shopping.cache.BitmapUrlCache;
import com.furdey.shopping.controllers.SocialController;
import com.furdey.shopping.listeners.OnFriendsLoadedListener;
import com.furdey.social.model.Person;
import com.furdey.social.vk.connector.VkConnection;

import java.util.List;

/**
 * Список друзей.
 * 
 * @author Stepan Furdey
 */
public class FriendsListActivity extends BaseActivity {

	public static final String MESSAGE_PARAM = "message";

	public class ModelListAdapter extends ArrayAdapter<Person> {

		int resource;

		public ModelListAdapter(Context context, int textViewResourceId, List<Person> objects) {
			super(context, textViewResourceId, objects);
			resource = textViewResourceId;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
			View row = inflater.inflate(resource, parent, false);
			row.setSelected(false);

			Person person = getItem(position);
			TextView friendsLiName = (TextView) row.findViewById(R.id.friendsLiName);
			friendsLiName.setText(person.getFirstName().concat(" ").concat(person.getLastName()));
			BitmapUrlCache bitmapCache = new BitmapUrlCache(getContext());
			ImageView friendsLiPhoto = (ImageView) row.findViewById(R.id.friendsLiPhoto);
			bitmapCache.loadBitmap(person.getPhotoUrl(), friendsLiPhoto);

			return row;
		}
	}

	private SocialController controller;
	// private boolean isSendMessageVisible = false;

	private ListView grid;

	public static final String PERSON_ID = "personId";

	@Override
	protected void onCreate(Bundle savedInstanceState) throws RuntimeException {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.base_list);
		grid = (ListView) findViewById(R.id.baseListGrid);

		final Activity activity = this;
		controller = new SocialController(this);
		controller.getFriendsList(VkConnection.class, new OnFriendsLoadedListener() {
			@Override
			public void onFriendsLoaded(List<Person> friends) {
				ModelListAdapter adapter = new ModelListAdapter(activity, R.layout.friends_li, friends);
				grid.setAdapter(adapter);
			}
		});
		// grid.setItemsCanFocus(false);
		// grid.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		grid.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
				AlertDialog.Builder builder = new Builder(FriendsListActivity.this);
				LayoutInflater inflatter = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
				View confirmView = inflatter.inflate(R.layout.friends_li, null, false);
				final Person person = (Person) grid.getAdapter().getItem(arg2);

				TextView friendsLiName = (TextView) confirmView.findViewById(R.id.friendsLiName);
				friendsLiName.setText(person.getFirstName().concat(" ").concat(person.getLastName()));
				BitmapUrlCache bitmapCache = new BitmapUrlCache(FriendsListActivity.this);
				ImageView friendsLiPhoto = (ImageView) confirmView.findViewById(R.id.friendsLiPhoto);
				bitmapCache.loadBitmap(person.getPhotoUrl(), friendsLiPhoto);

				builder.setTitle(R.string.friendsListConfirmTitle).setView(confirmView)
						.setPositiveButton(getString(R.string.formButtonOk), new OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								Intent result = new Intent();
								result.putExtra(PERSON_ID, person.getId());
								result.putExtra(MESSAGE_PARAM, getIntent().getStringExtra(MESSAGE_PARAM));
								FriendsListActivity.this.setResult(RESULT_OK, result);
								FriendsListActivity.this.finish();
							}
						}).setNegativeButton(getString(R.string.formButtonCancel), new OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
							}
						});

				builder.create().show();
				// boolean newState = grid.isItemChecked(arg2);
				//
				// if (!newState) {
				// boolean selected = false;
				// for (int i = 0; i < grid.getCount() && !selected; i++)
				// if (grid.isItemChecked(i))
				// selected = true;
				//
				// if (isSendMessageVisible && !selected) {
				// isSendMessageVisible = false;
				// ActivityCompat.invalidateOptionsMenu(activity);
				// }
				// }
				//
				// if (!isSendMessageVisible && newState) {
				// isSendMessageVisible = true;
				// ActivityCompat.invalidateOptionsMenu(activity);
				// }
			}
		});

		Toast.makeText(this, R.string.friendsListStartPopup, Toast.LENGTH_LONG).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.friends_list, menu);
		return true;
	}

	@Override
	public void onBackPressed() {
		startActivity(new Intent(this, PurchasesActivity.class));
		super.onBackPressed();
	}

	// @Override
	// public boolean onPrepareOptionsMenu(Menu menu) {
	// menu.findItem(R.id.menuFriendsListSendMessage).setVisible(isSendMessageVisible);
	// return super.onPrepareOptionsMenu(menu);
	// }
	//
	// @Override
	// public boolean onOptionsItemSelected(MenuItem item) {
	// switch (item.getItemId()) {
	// case R.id.menuFriendsListSendMessage:
	// ArrayList<String> seletedFriends = new ArrayList<String>();
	//
	// for (int i = 0; i < grid.getCount(); i++)
	// if (grid.isItemChecked(i)) {
	// seletedFriends.add(((ModelListAdapter)
	// grid.getAdapter()).getItem(i).getId());
	// }
	//
	// controller.editSocialMessage(seletedFriends.toArray(new
	// String[seletedFriends.size()]),
	// SocialController.SOCIAL_NETWORK_VK);
	// return true;
	// }
	//
	// return super.onOptionsItemSelected(item);
	// }

	// @Override
	// protected void onActivityResult(int requestCode, int resultCode, Intent
	// data) {
	// if (requestCode == SocialController.REQUEST_MESSAGE && resultCode ==
	// RESULT_OK) {
	// finish();
	// return;
	// }
	// super.onActivityResult(requestCode, resultCode, data);
	// }

}
