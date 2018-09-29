/*
 * Copyright (c) 2018 Red Hat, Inc. and/or its affiliates.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.jberet.samples.thorntail;

import javax.annotation.PostConstruct;
import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.ejb.Singleton;
import javax.ejb.Startup;

/**
 * A singleton EJB that is activated and launches batch job execution upon server startup.
 */
@Singleton
@Startup
public class StartUpBean {
    private static final String JOB_NAME = "numbers";
    private static final JobOperator jobOperator = BatchRuntime.getJobOperator();

    @PostConstruct
    private void postConstruct() {
        final long jobExecutionId = jobOperator.start(JOB_NAME, null);
        System.out.printf("Starting job %s with execution id %s%n", JOB_NAME, jobExecutionId);
    }
}
