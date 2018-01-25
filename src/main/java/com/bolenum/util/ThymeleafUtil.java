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
	
	public static String getProcessedHtml(String templateName,Map<String,Object> dynamicValueMap) {
		
		Context context = new Context();
		
		if (dynamicValueMap != null) {
			Iterator itMap = dynamicValueMap.entrySet().iterator();
			while (itMap.hasNext()) {
				Map.Entry pair = (Map.Entry) itMap.next();
				context.setVariable(pair.getKey().toString(), pair.getValue());
			}
		}
		return templateEngine.process(templateName, context);
		
	}
}
