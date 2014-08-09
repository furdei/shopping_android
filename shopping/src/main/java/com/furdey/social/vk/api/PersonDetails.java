package com.furdey.social.vk.api;

import com.furdey.social.model.Person;

public class PersonDetails {
	private Long uid;
	private String first_name;
	private String last_name;
	private String nickname;
	private String sex;
	private String bdate;
	private String city;
	private String country;
	private String timezone;
	private String photo;
	private String photo_medium;
	private String photo_big;
	private Integer online;
	private Integer[] lists;
	private String domain;
	private String home_phone;
	private String mobile_phone;
	private String university;
	private String university_name;
	private String faculty;
	private String faculty_name;
	private String graduation;

	public Long getUid() {
		return uid;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}

	public String getFirst_name() {
		return first_name;
	}

	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}

	public String getLast_name() {
		return last_name;
	}

	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getBdate() {
		return bdate;
	}

	public void setBdate(String bdate) {
		this.bdate = bdate;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getPhoto_medium() {
		return photo_medium;
	}

	public void setPhoto_medium(String photo_medium) {
		this.photo_medium = photo_medium;
	}

	public String getPhoto_big() {
		return photo_big;
	}

	public void setPhoto_big(String photo_big) {
		this.photo_big = photo_big;
	}

	public Integer getOnline() {
		return online;
	}

	public void setOnline(Integer online) {
		this.online = online;
	}

	public Integer[] getLists() {
		return lists;
	}

	public void setLists(Integer[] lists) {
		this.lists = lists;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

	public String getHome_phone() {
		return home_phone;
	}

	public void setHome_phone(String home_phone) {
		this.home_phone = home_phone;
	}

	public String getMobile_phone() {
		return mobile_phone;
	}

	public void setMobile_phone(String mobile_phone) {
		this.mobile_phone = mobile_phone;
	}

	public String getUniversity() {
		return university;
	}

	public void setUniversity(String university) {
		this.university = university;
	}

	public String getUniversity_name() {
		return university_name;
	}

	public void setUniversity_name(String university_name) {
		this.university_name = university_name;
	}

	public String getFaculty() {
		return faculty;
	}

	public void setFaculty(String faculty) {
		this.faculty = faculty;
	}

	public String getFaculty_name() {
		return faculty_name;
	}

	public void setFaculty_name(String faculty_name) {
		this.faculty_name = faculty_name;
	}

	public String getGraduation() {
		return graduation;
	}

	public void setGraduation(String graduation) {
		this.graduation = graduation;
	}

	public Person toSocial() {
		Person res = new Person();
		res.setId(getUid().toString());
		res.setFirstName(getFirst_name());
		res.setLastName(getLast_name());
		res.setPhotoUrl(getPhoto());
		return res;
	}
}
