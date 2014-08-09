package com.furdey.shopping.listeners;

import java.util.List;

import com.furdey.social.model.Person;

public interface OnFriendsLoadedListener {
	public void onFriendsLoaded(List<Person> friends);
}
