package com.furdey.social.vk.api;

import java.util.ArrayList;
import java.util.List;

import com.furdey.social.model.Person;

public class GetFriendsResponse extends Response {

	private PersonDetails[] response;

	public PersonDetails[] getFriends() {
		return response;
	}

	public void setResponse(PersonDetails[] response) {
		this.response = response;
	}

	public List<Person> toSocial() {
		List<Person> res = new ArrayList<Person>();

		for (PersonDetails person : response) {
			res.add(person.toSocial());
		}

		return res;
	}
}
