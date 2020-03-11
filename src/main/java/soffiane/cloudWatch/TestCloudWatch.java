package soffiane.cloudWatch;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.cloudwatch.model.*;
import com.amazonaws.services.cloudwatchevents.AmazonCloudWatchEvents;
import com.amazonaws.services.cloudwatchevents.AmazonCloudWatchEventsClientBuilder;
import com.amazonaws.services.cloudwatchevents.model.*;

import static java.lang.String.format;

public class TestCloudWatch {

	public static final AmazonCloudWatch AMAZON_CLOUD_WATCH = AmazonCloudWatchClientBuilder.standard().withRegion(Regions.EU_WEST_3).build();
	public static final AmazonCloudWatchEvents AMAZON_CLOUD_WATCH_EVENTS = AmazonCloudWatchEventsClientBuilder.standard().withRegion(Regions.EU_WEST_3).build();

	public static void main(String[] args) {
		getMetricsFromCloudWatch();
		putCustomMetricData();
		createCloudWatchAlarm();
		listCloudWatchAlarms();
		deleteAlarm();
		enableAlarmActions();
		disableAlarmActions();
		addCustomEvents();
		addRules();
		addTargets();
	}

	/**
	 * Listing Metrics
	 * To list CloudWatch metrics, create a ListMetricsRequest and call the AmazonCloudWatchClient’s listMetrics method.
	 * You can use the ListMetricsRequest to filter the returned metrics by namespace, metric name, or dimensions.
	 */
	private static void getMetricsFromCloudWatch() {
		ListMetricsRequest request = new ListMetricsRequest();
				/*.withMetricName(name)
				.withNamespace(namespace);*/
		boolean done = false;
		while (!done) {
			ListMetricsResult response = AMAZON_CLOUD_WATCH.listMetrics(/*request*/);
			/**
			 * The results may be paged. To retrieve the next batch of results, call setNextToken
			 * on the original request object with the return value of the ListMetricsResult object’s
			 * getNextToken method, and pass the modified request object back to another call to listMetrics.
			 */
			for (Metric metric : response.getMetrics()) {
				System.out.println(format(
						"Retrieved metric %s", metric.getMetricName() + " " + metric.getDimensions() + " " + metric.getNamespace()));
			}
			request.setNextToken(response.getNextToken());
			if (response.getNextToken() == null) {
				done = true;
			}
		}
	}

	/**
	 * Publish Custom Metric Data
	 * To publish your own metric data, call the AmazonCloudWatchClient’s putMetricData method with a PutMetricDataRequest.
	 * The PutMetricDataRequest must include the custom namespace to use for the data, and information about the data point itself in a MetricDatum object.
	 * Note : namespace cant start with AWS/ - reserver for AWS
	 */
	private static void putCustomMetricData() {
		Dimension dimension = new Dimension()
				.withName("UNIQUE_PAGES")
				.withValue("URLS");

		MetricDatum datum = new MetricDatum()
				.withMetricName("PAGES_VISITED")
				.withUnit(StandardUnit.None)
				.withValue(/*data_point*/1.0)
				.withDimensions(dimension);

		PutMetricDataRequest request = new PutMetricDataRequest()
				.withNamespace("SITE/TRAFFIC")
				.withMetricData(datum);

		PutMetricDataResult response = AMAZON_CLOUD_WATCH.putMetricData(request);
		System.out.println(response.getSdkResponseMetadata().toString());
	}

	/**
	 * Create an Alarm
	 * To create an alarm based on a CloudWatch metric,
	 * call the AmazonCloudWatchClient’s putMetricAlarm method with a PutMetricAlarmRequest filled with the alarm conditions.
	 */
	private static void createCloudWatchAlarm() {
		Dimension dimension = new Dimension()
				.withName("InstanceId")
				.withValue("i-01be41688146d21bd");

		PutMetricAlarmRequest request = new PutMetricAlarmRequest()
				.withAlarmName("Custom CPU Alarm")
				.withComparisonOperator(
						ComparisonOperator.GreaterThanThreshold)
				.withEvaluationPeriods(1)
				.withMetricName("CPUUtilization")
				.withNamespace("AWS/EC2")
				.withPeriod(60)
				.withStatistic(Statistic.Maximum)
				.withThreshold(70.0)
				.withActionsEnabled(false)
				.withAlarmDescription(
						"Alarm when server CPU utilization exceeds 70%")
				.withUnit(StandardUnit.Seconds)
				.withDimensions(dimension);

		PutMetricAlarmResult response = AMAZON_CLOUD_WATCH.putMetricAlarm(request);
		System.out.println(response.getSdkResponseMetadata().toString());
	}

	/**
	 * List Alarms
	 * To list the CloudWatch alarms that you have created, call the AmazonCloudWatchClient’s describeAlarms
	 * method with a DescribeAlarmsRequest that you can use to set options for the result.
	 */
	private static void listCloudWatchAlarms() {
		boolean done = false;
		DescribeAlarmsRequest request = new DescribeAlarmsRequest();
		while (!done) {
			DescribeAlarmsResult response = AMAZON_CLOUD_WATCH.describeAlarms(request);
			for (MetricAlarm alarm : response.getMetricAlarms()) {
				System.out.println(format("Retrieved alarm %s", alarm.getAlarmName() + " " + alarm.getAlarmDescription() + " " + alarm.getMetricName()));
			}
			request.setNextToken(response.getNextToken());
			if (response.getNextToken() == null) {
				done = true;
			}
		}
	}

	/**
	 * Delete Alarms
	 * To delete CloudWatch alarms, call the AmazonCloudWatchClient’s deleteAlarms method
	 * with a DeleteAlarmsRequest containing one or more names of alarms that you want to delete.
	 */
	private static void deleteAlarm() {
		DeleteAlarmsRequest request = new DeleteAlarmsRequest()
				.withAlarmNames("Custom CPU Alarm");
		DeleteAlarmsResult response = AMAZON_CLOUD_WATCH.deleteAlarms(request);
		System.out.println(response.getSdkResponseMetadata().toString());
	}

	/**
	 * Enable Alarm Actions
	 * To enable alarm actions for a CloudWatch alarm, call the AmazonCloudWatchClient’s enableAlarmActions
	 * with a EnableAlarmActionsRequest containing one or more names of alarms whose actions you want to enable.
	 */
	private static void enableAlarmActions() {
		EnableAlarmActionsRequest request = new EnableAlarmActionsRequest()
				.withAlarmNames("Custom CPU Alarm");
		EnableAlarmActionsResult response = AMAZON_CLOUD_WATCH.enableAlarmActions(request);
		System.out.println(response.getSdkResponseMetadata().toString());
	}

	/**
	 * Disable Alarm Actions
	 * To disable alarm actions for a CloudWatch alarm, call the AmazonCloudWatchClient’s disableAlarmActions
	 * with a DisableAlarmActionsRequest containing one or more names of alarms whose actions you want to disable.
	 */
	private static void disableAlarmActions() {
		DisableAlarmActionsRequest request = new DisableAlarmActionsRequest()
				.withAlarmNames("Custom CPU Alarm");
		DisableAlarmActionsResult response = AMAZON_CLOUD_WATCH.disableAlarmActions(request);
		System.out.println(response.getSdkResponseMetadata().toString());
	}

	/**
	 * Add Events
	 * To add custom CloudWatch events, call the AmazonCloudWatchEventsClient’s putEvents method with a PutEventsRequest object
	 * that contains one or more PutEventsRequestEntry objects that provide details about each event.
	 * You can specify several parameters for the entry such as the source and type of the event, resources associated with the event, and so on.
	 * Note : You can specify a maximum of 10 events per call to putEvents.
	 */
	private static void addCustomEvents() {


		final String EVENT_DETAILS =
				"{ \"key1\": \"value1\", \"key2\": \"value2\" }";

		PutEventsRequestEntry requestEntry = new PutEventsRequestEntry()
				.withDetail(EVENT_DETAILS)
				.withDetailType("sampleSubmitted")
				.withResources("arn:aws:s3:::atelier-rds-fichier-sql", "arn:aws:s3:::atelier-s3-udemy-soffiane", "arn:aws:s3:::elasticbeanstalk-eu-west-3-756539608235")
				.withSource("aws-sdk-java-cloudwatch-example");

		PutEventsRequest request = new PutEventsRequest()
				.withEntries(requestEntry);

		PutEventsResult response = AMAZON_CLOUD_WATCH_EVENTS.putEvents(request);
		System.out.println(response.getSdkResponseMetadata().toString());
	}

	/**
	 * Add Rules
	 * To create or update a rule, call the AmazonCloudWatchEventsClient’s putRule method with a PutRuleRequest with the name of the rule
	 * and optional parameters such as the event pattern, IAM role to associate with the rule,
	 * and a scheduling expression that describes how often the rule is run.
	 *
	 * Note : you have to add a trust relationship for events.amazonaws.com in the trust policy
	 * {
	 *   "Version": "2012-10-17",
	 *   "Statement": [
	 *     {
	 *       "Effect": "Allow",
	 *       "Principal": {
	 *         "Service": [
	 *           "ec2.amazonaws.com",
	 *           "events.amazonaws.com"
	 *         ]
	 *       },
	 *       "Action": "sts:AssumeRole"
	 *     }
	 *   ]
	 * }
	 */
	private static void addRules() {
		PutRuleRequest request = new PutRuleRequest()
				.withName("customRule")
				.withRoleArn("arn:aws:iam::756539608235:role/S3_Admin_Dev")
				.withScheduleExpression("rate(5 minutes)")
				.withState(RuleState.ENABLED);
		PutRuleResult response = AMAZON_CLOUD_WATCH_EVENTS.putRule(request);
		System.out.println(response.getSdkResponseMetadata().toString());
	}

	/**
	 * Add Targets
	 * Targets are the resources that are invoked when a rule is triggered.
	 * Example targets include Amazon EC2 instances, Lambda functions, Kinesis streams, Amazon ECS tasks, Step Functions state machines, and built-in targets.
	 * To add a target to a rule, call the AmazonCloudWatchEventsClient’s putTargets method with a PutTargetsRequest
	 * containing the rule to update and a list of targets to add to the rule.
	 */
	private static void addTargets() {
		Target target = new Target()
				.withArn("arn:aws:sns:eu-west-3:756539608235:UdemyS3")
				.withId("i-0daf5dadac25d4fe4");

		PutTargetsRequest request = new PutTargetsRequest()
				.withTargets(target)
				.withRule("customRule");

		PutTargetsResult response = AMAZON_CLOUD_WATCH_EVENTS.putTargets(request);
		System.out.println(response.getSdkResponseMetadata().toString());
	}
}
