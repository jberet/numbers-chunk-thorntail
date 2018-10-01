# Overview

This is a sample Java batch-processing web application with REST API, and
is built, packaged and run with Thorntail.  The batch job
contains a single chunk-type step that reads a list of numbers by chunks and
prints them to the console. The 2 batch artifacts used in this application are:

* `arrayItemReader`: implemented in `jberet-support`, reads a list of objects configured in job xml

* `mockItemWriter`: implemented in `jberet-support`, writes the output to the console or other destinations

This application also contains a singleton EJB, `StartUpBean`, which starts execution of batch job `numbers.xml`
upon the application deployment and Thorntail server start.

## How to Build

    mvn clean install

The above step produces both a regular webapp WAR file, and an executable uber jar (fat jar) containing
Thorntail runtime and all dependencies:

    $ ls -l target/
    -rw-r--r--  1 staff     258412 Sep 29 14:46 numbers-chunk-thorntail.war
    -rw-r--r--  1 staff  112089497 Sep 29 14:46 numbers-chunk-thorntail-thorntail.jar

## How to Run the Application Locally with Thorntail

    java -jar target/numbers-chunk-thorntail-thorntail.jar

    2018-09-30 21:57:13,735 INFO  [org.wildfly.swarm] (main) THORN0013: Installed fraction:                   JAX-RS - STABLE          io.thorntail:jaxrs:2.2.0.Final
    2018-09-30 21:57:13,751 INFO  [org.wildfly.swarm] (main) THORN0013: Installed fraction:                  Logging - STABLE          io.thorntail:logging:2.2.0.Final
    2018-09-30 21:57:13,751 INFO  [org.wildfly.swarm] (main) THORN0013: Installed fraction:             Transactions - STABLE          io.thorntail:transactions:2.2.0.Final
    2018-09-30 21:57:13,752 INFO  [org.wildfly.swarm] (main) THORN0013: Installed fraction:        CDI Configuration - STABLE          io.thorntail:cdi-config:2.2.0.Final
    2018-09-30 21:57:13,752 INFO  [org.wildfly.swarm] (main) THORN0013: Installed fraction:                  Elytron - STABLE          io.thorntail:elytron:2.2.0.Final
    2018-09-30 21:57:13,753 INFO  [org.wildfly.swarm] (main) THORN0013: Installed fraction:                    Batch - STABLE          io.thorntail:batch-jberet:2.2.0.Final
    2018-09-30 21:57:13,753 INFO  [org.wildfly.swarm] (main) THORN0013: Installed fraction:                      CDI - STABLE          io.thorntail:cdi:2.2.0.Final
    2018-09-30 21:57:13,753 INFO  [org.wildfly.swarm] (main) THORN0013: Installed fraction:                 Undertow - STABLE          io.thorntail:undertow:2.2.0.Final
    2018-09-30 21:57:13,754 INFO  [org.wildfly.swarm] (main) THORN0013: Installed fraction:                      EJB - STABLE          io.thorntail:ejb:2.2.0.Final
    2018-09-30 21:57:13,754 INFO  [org.wildfly.swarm] (main) THORN0013: Installed fraction:          Bean Validation - STABLE          io.thorntail:bean-validation:2.2.0.Final
    2018-09-30 21:57:13,755 INFO  [org.wildfly.swarm] (main) THORN0013: Installed fraction:              Datasources - STABLE          io.thorntail:datasources:2.2.0.Final
    2018-09-30 21:57:13,755 INFO  [org.wildfly.swarm] (main) THORN0013: Installed fraction:                      JCA - STABLE          io.thorntail:jca:2.2.0.Final
    2018-09-30 21:57:15,382 WARN  [org.wildfly.swarm.datasources] (main) THORN1005: Not creating a default datasource due to lack of JDBC driver
    2018-09-30 21:57:16,103 INFO  [org.jboss.msc] (main) JBoss MSC version 1.2.7.SP1
    2018-09-30 21:57:16,287 INFO  [org.jboss.as] (MSC service thread 1-7) WFLYSRV0049: Thorntail 2.2.0.Final (WildFly Core 3.0.8.Final) starting
    ...
    2018-09-30 21:57:19,861 INFO  [org.wildfly.swarm] (main) THORN99999: Thorntail is Ready
    2018-09-30 21:57:19,920 INFO  [org.jberet.support] (Batch Thread - 1) JBERET060501: Opening resource [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15] in class org.jberet.support.io.ArrayItemReader
    2018-09-30 21:57:19,934 INFO  [stdout] (Batch Thread - 1) 0
    ...
    2018-09-30 21:57:19,942 INFO  [stdout] (Batch Thread - 1) 15
    2018-09-30 21:57:19,943 INFO  [org.jberet.support] (Batch Thread - 1) JBERET060502: Closing resource [0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15] in class org.jberet.support.io.ArrayItemReader

As you can see from the above logs, the application is run with a simple `java -jar` command,
which bootstraps Thorntail runtime, deploys our batch application, and starts the batch job execution.

Alternatively, the batch application can be run with the following mvn command:

    mvn thorntail:run

Apart from this automatically started initial batch job execution, you can perform various batch processing operations
with RESTful API calls, thanks to jberet-rest included in this application.  For instance,

    # to start the job named `numbers`
    curl -s -X POST -H 'Content-Type:application/json' "http://localhost:8080/api/jobs/numbers/start" | jq

    # to get the details and status of the newly started job execution
    curl -s "http://localhost:8080/api/jobexecutions/1" | jq

    # to get all step executions belonging to this job execution
    curl -s "http://localhost:8080/api/jobexecutions/1/stepexecutions" | jq

    # to abandon the above job execution
    curl -X POST -H 'Content-Type:application/json' "http://localhost:8080/api/jobexecutions/1/abandon"

    # to schedule a job execution with initial delay of 1 minute and repeating with 60-minute interval
    curl -s -X POST -H 'Content-Type:application/json' -d '{"jobName":"numbers", "initialDelay":1, "interval":60}' "http://localhost:8080/api/jobs/numbers/schedule" | jq

    # to list all job schedules
    curl -s "http://localhost:8080/api/schedules" | jq

    # to cancel a job schedule
    curl -s -X POST -H 'Content-Type:application/json' "http://localhost:8080/api/schedules/1/cancel" | jq

    # to get details of a job schedule
    curl -s "http://localhost:8080/api/schedules/2" | jq

In above commands, a utility program called `jq` is used to pretty-print the JSON output.
Its usage here is equivalent to `python -m json.tool`.  Press Ctrl-C in the same terminal window to terminate the application.


## How to Build and Deploy to OpenShift Online

    # log into your OpenShift account
    oc login https:xxx.openshift.com --token=xxx

    # create a new project, if there is no existing projects
    oc new-project <projectname>

    # We wil use `openjdk18-openshift` image stream. Check if it is available in the current project
    oc get is

    # If `openjdk18-openshift` is not present, import it
    oc import-image my-redhat-openjdk-18/openjdk18-openshift --from=registry.access.redhat.com/redhat-openjdk-18/openjdk18-openshift --confirm

    # create a new application
    oc new-app openjdk18-openshift~https://github.com/jberet/numbers-chunk-thorntail.git

    # the above command will take a few minutes to complete, and to watch its status, run the following command:
    oc rollout status dc/numbers-chunk-thorntail

    # to expose `numbers-chunk-thorntail` application to external clients, run the command
    oc expose svc numbers-chunk-thorntail

    # to get the URL for external access to the application
    oc get routes

        NAME                      HOST/PORT                                                        PATH      SERVICES                  PORT       TERMINATION   WILDCARD
        numbers-chunk-thorntail   numbers-chunk-thorntail-pr.xxxx.xxxx.openshiftapps.com                     numbers-chunk-thorntail   8080-tcp                 None

    # list pods, and get logs for the pod associated with the application
    # (there are 2 pods associated with the current app: one for building the app and the other is for running the app)
    oc get pod

        numbers-chunk-thorntail-1-build   0/1       Completed          0          1d
        numbers-chunk-thorntail-1-nqnjj   1/1       Running            0          1d

    # view the application runtimne log in an text editor
    oc logs numbers-chunk-thorntail-1-nqnjj | view -

From the above log output, you can see that the application has been successfully built
and deployed to OpenShift online, and the batch job has been started and completed.

## How to Access `numbers-chunk-thorntail` Batch Application on OpenShift through REST API with `curl`

    Once the application is deployed to OpenShift, you can invokes its
    REST API to perform various batch processing operations. The steps are the same
    for both local Thorntail or OpenShift Thorntail runtime. 
    We will also use the command line tool `curl` as the REST client, but any other
    similar REST client tool should work as well.

    # to start the job named `numbers`
    curl -s -X POST -H 'Content-Type:application/json' "http://numbers-chunk-thorntail-pr.xxxx.xxxx.openshiftapps.com/api/jobs/numbers/start" | jq

    # to get the details and status of the newly started job execution
    curl -s "http://numbers-chunk-thorntail-pr.xxxx.xxxx.openshiftapps.com/api/jobexecutions/1" | jq

    # to get all step executions belonging to this job execution
    curl -s "http://numbers-chunk-thorntail-pr.xxxx.xxxx.openshiftapps.com/api/jobexecutions/1/stepexecutions" | jq

    # to abandon the above job execution
    curl -X POST -H 'Content-Type:application/json' "http://numbers-chunk-thorntail-pr.xxxx.xxxx.openshiftapps.com/api/jobexecutions/1/abandon"

    # to schedule a job execution with initial delay of 1 minute and repeating with 60-minute interval
    curl -s -X POST -H 'Content-Type:application/json' -d '{"jobName":"numbers", "initialDelay":1, "interval":60}' "http://numbers-chunk-thorntail-pr.xxxx.xxxx.openshiftapps.com/api/jobs/numbers/schedule" | jq

    # to list all job schedules
    curl -s "http://numbers-chunk-thorntail-pr.xxxx.xxxx.openshiftapps.com/api/schedules" | jq

    # to cancel a job schedule
    curl -s -X POST -H 'Content-Type:application/json' "http://numbers-chunk-thorntail-pr.xxxx.xxxx.openshiftapps.com/api/schedules/1/cancel" | jq

    # to get details of a job schedule
    curl -s "http://numbers-chunk-thorntail-pr.xxxx.xxxx.openshiftapps.com/api/schedules/2" | jq

## How to Run `numbers-chunk-thorntail` Batch Application from OpenShift Command Line with Kubernetes Job

It is possible to run `numbers-chunk-thorntail` batch application from OpenShift command line (`oc`) with Kubernetes job api.
This option can be useful for one-off or ad-hac batch processing on OpenShift cloud platform. We will be using the application image
built in the previous step and saved in OpenShift internal docker registry.

The following yaml file (`numbers-chunk-thorntail-job.yaml`) describes the Kubernetes job configuration:

```yaml
    apiVersion: batch/v1
    kind: Job
    metadata:
      name: numbers-chunk-thorntail-job
    spec:
      parallelism: 1
      completions: 1
      template:
        metadata:
          name: numbers-chunk-thorntail-job
        spec:
          containers:
          - name: numbers-chunk-thorntail-job
            image: docker-registry.default.svc:5000/pr/numbers-chunk-thorntail
            command: ["java",  "-Xms64m", "-Xmx256m", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-XX:+UseParallelOldGC", "-XX:MinHeapFreeRatio=10", "-XX:MaxHeapFreeRatio=20", "-XX:GCTimeRatio=4", "-XX:AdaptiveSizePolicyWeight=90", "-XX:MaxMetaspaceSize=100m", "-XX:ParallelGCThreads=1", "-Djava.util.concurrent.ForkJoinPool.common.parallelism=1", "-XX:CICompilerCount=2", "-XX:+ExitOnOutOfMemoryError", "-jar", "/deployments/numbers-chunk-thorntail-thorntail.jar"]
          restartPolicy: OnFailure
```

Then, run the following command to tell OpenShift to launch the job execution:

    # to create a Kubernetes job and start running batch application on OpenShift
    oc create -f numbers-chunk-thorntail-job.yaml
        job.batch "numbers-chunk-thorntail-job" created

    # to get all Kubernetes jobs
    oc get job
        NAME      DESIRED   SUCCESSFUL   AGE
        numbers-chunk-thorntail-job   1         0            9s


    # to get more details of the above job
    oc describe job numbers-chunk-thorntail-job
        Name:           numbers-chunk-thorntail-job
        Namespace:      pr
        Selector:       controller-uid=672f6416-c529-11e8-bb19-02e0bae989b4
        Labels:         controller-uid=672f6416-c529-11e8-bb19-02e0bae989b4
                        job-name=numbers-chunk-thorntail-job
        Annotations:    <none>
        Parallelism:    1
        Completions:    1
        Start Time:     Sun, 30 Sep 2018 23:23:45 -0400
        Pods Statuses:  1 Running / 0 Succeeded / 0 Failed
        Pod Template:
          Labels:  controller-uid=672f6416-c529-11e8-bb19-02e0bae989b4
                   job-name=numbers-chunk-thorntail-job
          Containers:
           numbers-chunk-thorntail-job:
            Image:      docker-registry.default.svc:5000/pr/numbers-chunk-thorntail
            Port:       <none>
            Host Port:  <none>
            Command:
              java
              -Xms64m
              -Xmx256m
              -XX:+UnlockExperimentalVMOptions
              -XX:+UseCGroupMemoryLimitForHeap
              -XX:+UseParallelOldGC
              -XX:MinHeapFreeRatio=10
              -XX:MaxHeapFreeRatio=20
              -XX:GCTimeRatio=4
              -XX:AdaptiveSizePolicyWeight=90
              -XX:MaxMetaspaceSize=100m
              -XX:ParallelGCThreads=1
              -Djava.util.concurrent.ForkJoinPool.common.parallelism=1
              -XX:CICompilerCount=2
              -XX:+ExitOnOutOfMemoryError
              -jar
              /deployments/numbers-chunk-thorntail-thorntail.jar
            Environment:  <none>
            Mounts:       <none>
          Volumes:        <none>
        Events:
          Type    Reason            Age   From            Message
          ----    ------            ----  ----            -------
          Normal  SuccessfulCreate  4m    job-controller  Created pod: numbers-chunk-thorntail-job-dr4fd


    # to get all pods including the pod associated with our job
    oc get pods
        NAME                    READY     STATUS             RESTARTS   AGE
        numbers-chunk-thorntail-job-dr4fd   1/1       Running            0          1m

    # to view logs from running batch application
    oc logs numbers-chunk-thorntail-job-dr4fd | view -

    # to delete the Kubernetes job after the batch application is finished
    oc delete job numbers-chunk-thorntail-job
        job.batch "numbers-chunk-thorntail-job-dr4fd" deleted


## How to Schedule `numbers-chunk-thorntail` Batch Application from OpenShift Command Line with Kubernetes Cron Job

Expanding from the job api, Kubernetes also supports scheduling periodic tasks with cron job api.
Similarly, this can also be achieved through OpenShift command line (`oc`).

The following yaml file (`numbers-chunk-thorntail-cron.yaml`) describes the cron job configuration.
The cron expression `schedule: "*/5 * * * *"` specifies to run the task every 5 minutes.

```yaml
apiVersion: batch/v1beta1
kind: CronJob
metadata:
  name: numbers-chunk-thorntail-cron
spec:
  successfulJobsHistoryLimit: 3
  failedJobsHistoryLimit: 1
  schedule: "*/5 * * * *"
  jobTemplate:
    spec:
      template:
        spec:
          containers:
          - name: numbers-chunk-thorntail-cron
            image: docker-registry.default.svc:5000/pr/numbers-chunk-thorntail
            command: ["java",  "-Xms64m", "-Xmx256m", "-XX:+UnlockExperimentalVMOptions", "-XX:+UseCGroupMemoryLimitForHeap", "-XX:+UseParallelOldGC", "-XX:MinHeapFreeRatio=10", "-XX:MaxHeapFreeRatio=20", "-XX:GCTimeRatio=4", "-XX:AdaptiveSizePolicyWeight=90", "-XX:MaxMetaspaceSize=100m", "-XX:ParallelGCThreads=1", "-Djava.util.concurrent.ForkJoinPool.common.parallelism=1", "-XX:CICompilerCount=2", "-XX:+ExitOnOutOfMemoryError", "-jar", "/deployments/numbers-chunk-thorntail-thorntail.jar"]
          restartPolicy: OnFailure
  concurrencyPolicy: Replace

```

Then, run the following commands to tell OpenShift to schedule the job executions:

    # to create a Kubernetes cron job and periodically running batch application on OpenShift
    oc create -f numbers-chunk-thorntail-cron.yaml
        cronjob.batch "numbers-chunk-thorntail-cron" created

    # to list all Kubernetes cron jobs:
    oc get cronjob
        NAME                           SCHEDULE      SUSPEND   ACTIVE    LAST SCHEDULE   AGE
        numbers-chunk-thorntail-cron   */5 * * * *   False     0         <none>          11s

    # to get status of a specific cron job
    oc get cronjob numbers-chunk-thorntail-cron

    # to get continuous status of a specific cron job with --watch option
    oc get cronjob numbers-chunk-thorntail-cron --watch
        NAME                           SCHEDULE      SUSPEND   ACTIVE    LAST SCHEDULE   AGE
        numbers-chunk-thorntail-cron   */5 * * * *   False     1         12s             2m
        numbers-chunk-thorntail-cron   */5 * * * *   False     1         3s        7m
        numbers-chunk-thorntail-cron   */5 * * * *   False     1         3s        12m

    # to list all pods, including the pod associated with the cron job
    oc get pods
        NAME                                            READY     STATUS             RESTARTS   AGE
        numbers-chunk-thorntail-cron-1538365500-6w7kr   1/1       Running            0          2m

    # to view logs from one of the batch job executions started with the Kubernetes cron job
    oc logs numbers-chunk-thorntail-cron-1538365500-6w7kr | view -


    # to get more details of the cron job
    oc describe cronjob numbers-chunk-thorntail-cron
        Name:                       numbers-chunk-thorntail-cron
        Namespace:                  pr
        Labels:                     <none>
        Annotations:                <none>
        Schedule:                   */5 * * * *
        Concurrency Policy:         Replace
        Suspend:                    False
        Starting Deadline Seconds:  <unset>
        Selector:                   <unset>
        Parallelism:                <unset>
        Completions:                <unset>
        Pod Template:
          Labels:  <none>
          Containers:
           numbers-chunk-thorntail-cron:
            Image:      docker-registry.default.svc:5000/pr/numbers-chunk-thorntail
            Port:       <none>
            Host Port:  <none>
            Command:
              java
              -Xms64m
              -Xmx256m
              -XX:+UnlockExperimentalVMOptions
              -XX:+UseCGroupMemoryLimitForHeap
              -XX:+UseParallelOldGC
              -XX:MinHeapFreeRatio=10
              -XX:MaxHeapFreeRatio=20
              -XX:GCTimeRatio=4
              -XX:AdaptiveSizePolicyWeight=90
              -XX:MaxMetaspaceSize=100m
              -XX:ParallelGCThreads=1
              -Djava.util.concurrent.ForkJoinPool.common.parallelism=1
              -XX:CICompilerCount=2
              -XX:+ExitOnOutOfMemoryError
              -jar
              /deployments/numbers-chunk-thorntail-thorntail.jar
            Environment:     <none>
            Mounts:          <none>
          Volumes:           <none>
        Last Schedule Time:  Mon, 01 Oct 2018 00:05:00 -0400
        Active Jobs:         numbers-chunk-thorntail-cron-1538366700
        Events:
          Type    Reason            Age   From                Message
          ----    ------            ----  ----                -------
          Normal  SuccessfulCreate  20m   cronjob-controller  Created job numbers-chunk-thorntail-cron-1538365500
          Normal  SuccessfulDelete  15m   cronjob-controller  Deleted job numbers-chunk-thorntail-cron-1538365500
          Normal  SuccessfulCreate  15m   cronjob-controller  Created job numbers-chunk-thorntail-cron-1538365800
          Normal  SuccessfulDelete  10m   cronjob-controller  Deleted job numbers-chunk-thorntail-cron-1538365800
          Normal  SuccessfulCreate  10m   cronjob-controller  Created job numbers-chunk-thorntail-cron-1538366100
          Normal  SuccessfulDelete  5m    cronjob-controller  Deleted job numbers-chunk-thorntail-cron-1538366100
          Normal  SuccessfulCreate  5m    cronjob-controller  Created job numbers-chunk-thorntail-cron-1538366400
          Normal  SuccessfulDelete  6s    cronjob-controller  Deleted job numbers-chunk-thorntail-cron-1538366400
          Normal  SuccessfulCreate  6s    cronjob-controller  Created job numbers-chunk-thorntail-cron-1538366700


    # After all batch job executions have finished and no more job executions are needed, delete the cron job
    oc delete cronjobs/numbers-chunk-thorntail-cron
        cronjob.batch "numbers-chunk-thorntail-cron" deleted

    # Another variation of the delete command:
    oc delete cronjob numbers-chunk-thorntail-cron

Note that we specify concurrencyPolicy=Replace in `numbers-chunk-thorntail-cron.yaml`, which means
the subsequent pod instance will replace the previous one in the series of job executions triggered
by the cron job. This is to prevent multiple application pods and Thorntail runtime instances from
co-existing and draining system resources.

## More info

[OpenShift Developer Guide on Job](https://docs.openshift.com/online/dev_guide/jobs.html)

[OpenShift Developer Guide on Cronjob](https://docs.openshift.com/online/dev_guide/cron_jobs.html)

[JBERET-450](https://issues.jboss.org/browse/JBERET-450) Launch and schedule batch job executions on OpenShift Thorntail runtime with Kubernetes jobs api

[Thorntail Project Site](http://wildfly-swarm.io/)