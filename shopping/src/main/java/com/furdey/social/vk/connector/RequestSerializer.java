package com.furdey.social.vk.connector;

import java.lang.reflect.Field;

import com.furdey.social.vk.api.Request;

/**
 * Serializes API request into valid request URL
 * 
 * @author Stepan Furdey
 */
public class RequestSerializer {

	private static final String baseUrl = "https://api.vk.com/method/";

	public static String serializeRequest(Request request, VkToken token) {
		String url = baseUrl.concat(request.getRestUrl()).concat("?");
		Field[] fields = request.getClass().getDeclaredFields();
		int fieldsAddedCount = 0;

		for (int i = 0; i < fields.length; i++) {
			fields[i].setAccessible(true);

			if (!java.lang.reflect.Modifier.isStatic(fields[i].getModifiers())) {
				Object val;
				try {
					val = fields[i].get(request);
				} catch (Exception e) {
					e.printStackTrace();
					throw new RuntimeException(
							"Error while generating VK request: can't get a value of the field "
									+ fields[i].getName(), e);
				}

				if (val != null) {
					if (fields[i].getType().equals(String.class)) {
						if (fieldsAddedCount > 0)
							url = url.concat("&");

						url = url.concat(getFieldName(fields[i])).concat("=").concat((String) val);
						fieldsAddedCount++;
					} else if (fields[i].getType().equals(Integer.class)
							|| fields[i].getType().equals(Long.class)) {
						if (fieldsAddedCount > 0)
							url = url.concat("&");

						url = url.concat(getFieldName(fields[i])).concat("=").concat(val.toString());
						fieldsAddedCount++;
					} else
						throw new IllegalArgumentException("VK request field type "
								+ fields[i].getType().getSimpleName() + " is not supported");
				}
			}
		}

		if (fieldsAddedCount > 0)
			url = url.concat("&");

		url = url.concat("access_token=").concat(token.getAccessToken());
		return url;
	}

	private static String getFieldName(Field field) {
		if (field.getName().startsWith("_"))
			return field.getName().substring(1);
		else
			return field.getName();
	}
}
