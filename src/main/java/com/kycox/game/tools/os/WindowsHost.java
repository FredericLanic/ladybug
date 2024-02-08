package com.kycox.game.tools.os;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Condition Spring pour vérifier si le Système hôte est Windows (Librairies XBox)
 */
public class WindowsHost implements Condition {

	private static final String WINDOWS = "Windows";
	private static final String OS_NAME_PROP = "os.name";

	@Override
	public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
		return System.getProperty(OS_NAME_PROP).startsWith(WINDOWS);
	}
}