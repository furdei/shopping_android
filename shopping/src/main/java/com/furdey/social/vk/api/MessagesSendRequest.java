package com.furdey.social.vk.api;

/**
 * Отправляет сообщение пользователю VK
 * 
 * @author Stepan Furdey
 */
public class MessagesSendRequest extends Request {

	/**
	 * (или chat_id) ID пользователя (по умолчанию - текущий пользователь)
	 */
	private Long uid;

	/**
	 * (или uid) ID беседы, к которой будет относиться сообщение
	 */
	private Long chat_id;

	/**
	 * текст личного cообщения (является обязательным, если не задан параметр
	 * attachment)
	 */
	private String message;

	/**
	 * медиа-приложения к личному сообщению, перечисленные через запятую. Каждое
	 * прикрепление представлено в формате: <type><owner_id>_<media_id>
	 * 
	 * <type> - тип медиа-приложения: photo - фотография video - видеозапись audio
	 * - аудиозапись doc - документ wall - запись на стене
	 * 
	 * <owner_id> - идентификатор владельца медиа-приложения <media_id> -
	 * идентификатор медиа-приложения.
	 * 
	 * Например: photo100172_166443618
	 * 
	 * Параметр является обязательным, если не задан параметр message.
	 */
	private String attachment;

	/**
	 * идентификаторы пересылаемых сообщений, перечисленные через запятую.
	 * Перечисленные сообщения отправителя будут отображаться в теле письма у
	 * получателя.
	 * 
	 * Например: 123,431,544
	 */
	private String forward_messages;

	/**
	 * заголовок сообщения.
	 */
	private String title;

	/**
	 * 0 - обычное сообщение, 1 - сообщение из чата. (по умолчанию 0)
	 */
	private Integer type;

	/**
	 * latitude, широта при добавлении местоположения.
	 */
	private String lat;

	/**
	 * longitude, долгота при добавлении местоположения.
	 */
	private String _long;

	/**
	 * уникальный строковой идентификатор, предназначенный для предотвращения
	 * повторной отправки одинакового сообщения.
	 */
	private String guid;

	@Override
	public String getRestUrl() {
		return "messages.send";
	}

	@Override
	public Class<? extends Response> getResponseClass() {
		return MessagesSendResponse.class;
	}

	public Long getUid() {
		return uid;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}

	public Long getChat_id() {
		return chat_id;
	}

	public void setChat_id(Long chat_id) {
		this.chat_id = chat_id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getAttachment() {
		return attachment;
	}

	public void setAttachment(String attachment) {
		this.attachment = attachment;
	}

	public String getForward_messages() {
		return forward_messages;
	}

	public void setForward_messages(String forward_messages) {
		this.forward_messages = forward_messages;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getLat() {
		return lat;
	}

	public void setLat(String lat) {
		this.lat = lat;
	}

	public String getLong() {
		return _long;
	}

	public void setLong(String _long) {
		this._long = _long;
	}

	public String getGuid() {
		return guid;
	}

	public void setGuid(String guid) {
		this.guid = guid;
	}

}
