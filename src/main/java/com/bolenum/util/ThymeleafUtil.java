package com.bolenum.util;

import java.util.Iterator;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
public class ThymeleafUtil {

	@Autowired
	private TemplateEngine tempTemplateEngine;

	private static TemplateEngine templateEngine;

	@PostConstruct
	void init() {
		templateEngine = tempTemplateEngine;
	}

	/**
	 * This method is use get Processed Html
	 * 
	 * @param templateName
	 * @param dynamicValueMap
	 * @return
	 */
	public static String getProcessedHtml(Map<String, Object> model, String templateName) {

		Context context = new Context();

		if (model != null) {
			model.forEach((key, value) -> context.setVariable(key, value));
			return templateEngine.process(templateName, context);
		}
		return "";

	}
}
