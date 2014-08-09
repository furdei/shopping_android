package com.furdey.social.vk.connector;

import com.furdey.social.vk.api.Response;

public interface VkConnector {

	/**
	 * Вызвать REST-сервис VK
	 * 
	 * @param request
	 *          - запрос к REST-сервису VK
	 * @param responseType
	 *          - тип ответа
	 * 
	 * @return Ответ REST-сервиса VK
	 */
	public Response callVk(String request, Class<? extends Response> responseType);

}