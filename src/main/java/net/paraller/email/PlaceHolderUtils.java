package net.paraller.email;

import java.util.Map;

import org.slf4j.Logger;

/**
 * @author liaozhida 占位符处理工具 2016年4月9日
 */
public class PlaceHolderUtils {

	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(PlaceHolderUtils.class);

	public static final String PLACEHOLDER_PREFIX = "${";

	public static final String PLACEHOLDER_SUFFIX = "}";

	public static String resolvePlaceholders(String text,
			Map<String, String> parameter) {
		if (parameter == null || parameter.isEmpty()) {
			return text;
		}
		StringBuffer buf = new StringBuffer(text);
		int startIndex = buf.indexOf(PLACEHOLDER_PREFIX);
		while (startIndex != -1) {
			int endIndex = buf.indexOf(PLACEHOLDER_SUFFIX, startIndex
					+ PLACEHOLDER_PREFIX.length());
			if (endIndex != -1) {
				String placeholder = buf.substring(startIndex
						+ PLACEHOLDER_PREFIX.length(), endIndex);
				int nextIndex = endIndex + PLACEHOLDER_SUFFIX.length();
				try {
					String propVal = parameter.get(placeholder);
					if (propVal != null) {
						buf.replace(startIndex,
								endIndex + PLACEHOLDER_SUFFIX.length(), propVal);
						nextIndex = startIndex + propVal.length();
					} else {
						logger.error("Could not resolve placeholder '"
								+ placeholder + "' in [" + text + "] ");
					}
				} catch (Exception ex) {
					logger.error("Could not resolve placeholder '" + placeholder
							+ "' in [" + text + "]: " + ex);
				}
				startIndex = buf.indexOf(PLACEHOLDER_PREFIX, nextIndex);
			} else {
				startIndex = -1;
			}
		}
		return buf.toString();
	}
}
