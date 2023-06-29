package com.batch.skip_policy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.step.skip.SkipLimitExceededException;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.stereotype.Component;

@Component
public class RecordSkipPolicy implements SkipPolicy {

	private static final Logger LOG = LoggerFactory.getLogger(RecordSkipPolicy.class);

	@Override
	public boolean shouldSkip(Throwable t, int skipCount) throws SkipLimitExceededException {
		if (t instanceof RuntimeException) {
			LOG.error(t.getMessage() + " " + t.getLocalizedMessage());
			return true;
		}
		return false;
	}

}
