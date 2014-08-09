package com.furdey.social.vk.api;

/**
 * Возвращает список параметров пользователей
 * 
 * @author Stepan Furdey
 */
public class UsersGetRequest extends Request {

	/**
	 * перечисленные через запятую ID пользователей или их короткие имена
	 * (screen_name). Максимум 1000 пользователей.
	 */
	private String uids;

	public static final String FIELD_UID = "uid";
	public static final String FIELD_FIRST_NAME = "first_name";
	public static final String FIELD_LAST_NAME = "last_name";
	public static final String FIELD_NICKNAME = "nickname";
	public static final String FIELD_SEX = "sex";
	public static final String FIELD_BDATE = "bdate";
	public static final String FIELD_CITY = "city";
	public static final String FIELD_COUNTRY = "country";
	public static final String FIELD_TIMEZONE = "timezone";
	public static final String FIELD_PHOTO = "photo";
	public static final String FIELD_PHOTO_MEDIUM = "photo_medium";
	public static final String FIELD_PHOTO_BIG = "photo_big";
	public static final String FIELD_DOMAIN = "domain";
	public static final String FIELD_HAS_MOBILE = "has_mobile";
	public static final String FIELD_RATE = "rate";
	public static final String FIELD_CONTACTS = "contacts";
	public static final String FIELD_EDUCATION = "education";

	/**
	 * перечисленные через запятую поля анкет, необходимые для получения.
	 * Доступные значения: uid, first_name, last_name, nickname, screen_name, sex,
	 * bdate (birthdate), city, country, timezone, photo, photo_medium, photo_big,
	 * has_mobile, rate, contacts, education, online, counters.
	 */
	private String fields;

	public static final String NAME_CASE_NOM = "nom";
	public static final String NAME_CASE_GEN = "gen";
	public static final String NAME_CASE_DAT = "dat";
	public static final String NAME_CASE_ACC = "acc";
	public static final String NAME_CASE_INS = "ins";
	public static final String NAME_CASE_ABL = "abl";

	/**
	 * падеж для склонения имени и фамилии пользователя. Возможные значения:
	 * именительный – nom, родительный – gen, дательный – dat, винительный – acc,
	 * творительный – ins, предложный – abl. По умолчанию nom.
	 */
	private String name_case;

	@Override
	public String getRestUrl() {
		return "users.get";
	}

	@Override
	public Class<? extends Response> getResponseClass() {
		return UsersGetResponse.class;
	}

	public String getUids() {
		return uids;
	}

	public void setUids(Long[] uids) {
		if (uids.length == 0) {
			this.uids = null;
			return;
		}

		this.uids = uids[0].toString();

		for (int i = 1; i < uids.length; i++) {
			this.uids = this.uids.concat(",").concat(uids[i].toString());
		}
	}

	public String getFields() {
		return fields;
	}

	public void setFields(String[] fields) {
		if (fields.length == 0) {
			this.fields = null;
			return;
		}

		this.fields = fields[0];

		for (int i = 1; i < fields.length; i++) {
			this.fields = this.fields.concat(",").concat(fields[i]);
		}
	}

	public String getName_case() {
		return name_case;
	}

	public void setName_case(String name_case) {
		this.name_case = name_case;
	}

}
