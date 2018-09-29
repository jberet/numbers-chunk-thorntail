# Overview

This is a sample Java batch-processing web application with REST API. The batch job
contains a single chunk-type step that reads a list of numbers by chunks and
prints them to the console. The 2 batch artifacts used in this application are:

* `arrayItemReader`: implemented in `jberet-support`, reads a list of objects configured in job xml

* `mockItemWriter`: implemented in `jberet-support`, writes the output to the console or other destinations

## How to Build

    mvn clean install

## How to Deploy to or Undeploy from WildFly with `wildfly-maven-plugin`

    mvn wildfly:deploy

    mvn wildfly:undeploy

## How to Deploy to or Undeploy from WildFly with WildFly CLI

    $JBOSS_HOME/bin/jboss-cli.sh -c "deploy --force target/numbers-chunk.war"

    $JBOSS_HOME/bin/jboss-cli.sh -c "undeploy numbers-chunk.war"

## How to Build and Deploy to OpenShift Online

    # log into your OpenShift account
    oc login https:xxx.openshift.com --token=xxx

    # create a new project, if there is no existing projects
    oc new-project <projectname>

    # create a new application
    oc new-app wildfly~https://github.com/jberet/numbers-chunk.git

    # the above command will take a few minutes to complete, and to watch its status, run the following command:
    oc rollout status dc/numbers-chunk

    # to expose `numbers-chunk` application to external clients, run the command
    oc expose svc numbers-chunk

## How to Run `numbers-chunk` Batch Application through REST API with `curl`

    Once the application is deployed to WildFly or OpenShift, you can invokes its
    REST API to perform various batch processing operations. The steps are the same
    for both WildFly or OpenShift deployments. In the following steps, we assume
    the application is deployed to a standalone WildFly instance on local machine.
    We will also use the command line tool `curl` as the REST client, but any other
    similar REST client tool should work as well.

    # to start the job named `numbers`
    curl -s -X POST -H 'Content-Type:application/json' "http://localhost:8080/numbers-chunk/api/jobs/numbers/start" | python -m json.tool

    # to get the details and status of the newly started job execution
    curl -s "http://localhost:8080/numbers-chunk/api/jobexecutions/1" | python -m json.tool

    # to get all step executions belonging to this job execution
    curl -s "http://localhost:8080/numbers-chunk/api/jobexecutions/1/stepexecutions" | python -m json.tool

    # to abandon the above job execution
    curl -X POST -H 'Content-Type:application/json' "http://localhost:8080/numbers-chunk/api/jobexecutions/1/abandon"

    # to schedule a job execution with initial delay of 1 minute and repeating with 60-minute interval
    curl -s -X POST -H 'Content-Type:application/json' -d '{"jobName":"numbers", "initialDelay":1, "interval":60}' "http://localhost:8080/numbers-chunk/api/jobs/numbers/schedule" | python -m json.tool

    # to list all job schedules
    curl -s "http://localhost:8080/numbers-chunk/api/schedules" | python -m json.tool

    # to cancel a job schedule
    curl -s -X POST -H 'Content-Type:application/json' "http://localhost:8080/numbers-chunk/api/schedules/1/cancel" | python -m json.tool

    # to get details of a job schedule
    curl -s "http://localhost:8080/numbers-chunk/api/schedules/2" | python -m json.tool

## More info

[OpenShift Developer Guide](https://docs.openshift.com/online/dev_guide/jobs.html)

[JBERET-447](https://issues.jboss.org/browse/JBERET-447) Create a simple sample batch processing webapp
