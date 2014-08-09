package com.furdey.social.vk.api;

/**
 * Возвращает список идентификаторов друзей пользователя или расширенную
 * информацию о друзьях пользователя (при использовании параметра fields).
 * 
 * Для вызова этого метода Ваше приложение должно иметь права с битовой маской,
 * содержащей 2. (Подробнее о получении прав)
 * 
 * @author Stepan Furdey
 */
public class GetFriendsRequest extends Request {

	/**
	 * идентификатор пользователя, для которого необходимо получить список друзей.
	 * Если параметр не задан, то считается, что он равен идентификатору текущего
	 * пользователя.
	 */
	private String uid;

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
	 * Доступные значения: uid, first_name, last_name, nickname, sex, bdate
	 * (birthdate), city, country, timezone, photo, photo_medium, photo_big,
	 * domain, has_mobile, rate, contacts, education.
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

	/**
	 * количество друзей, которое нужно вернуть. (по умолчанию – все друзья)
	 */
	private Integer count;

	/**
	 * смещение, необходимое для выборки определенного подмножества друзей.
	 */
	private Integer offset;

	/**
	 * идентификатор списка друзей, полученный методом friends.getLists, друзей из
	 * которого необходимо получить. Данный параметр учитывается, только когда
	 * параметр uid равен идентификатору текущего пользователя.
	 * 
	 * Данный параметр доступен только для Desktop-приложений.
	 */
	private String lid;

	public static final String ORDER_NAME = "name";
	public static final String ORDER_HINTS = "hints";

	/**
	 * Порядок в котором нужно вернуть список друзей. Допустимые значения: name -
	 * сортировать по имени (работает только при переданном параметре fields).
	 * hints - сортировать по рейтингу, аналогично тому, как друзья сортируются в
	 * разделе Мои друзья (данный параметр доступен только для
	 * Desktop-приложений).
	 */
	private String order;

	public GetFriendsRequest() {
		fields = FIELD_UID;
	}

	@Override
	public String getRestUrl() {
		return "friends.get";
	}

	@Override
	public Class<? extends Response> getResponseClass() {
		return GetFriendsResponse.class;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
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

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public Integer getOffset() {
		return offset;
	}

	public void setOffset(Integer offset) {
		this.offset = offset;
	}

	public String getLid() {
		return lid;
	}

	public void setLid(String lid) {
		this.lid = lid;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

}
