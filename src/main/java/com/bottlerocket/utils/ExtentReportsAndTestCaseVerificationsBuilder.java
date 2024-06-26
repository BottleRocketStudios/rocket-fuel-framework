package com.bottlerocket.utils;

import com.bottlerocket.config.AutomationConfigProperties;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.groupingBy;

//TODO: Per conversations about the dual purpose nature of this class, it would be better to break test methods
// dealing with extent report and verifications out separately, and add each to their own class.
/**
 * The intent of this class is to provide a set of output utilities for client projects.
 * These include:
 *   - The ability to compare test durations in two extent reports and create a chart in an HTML report
 *   - The ability to pull a list of tests with durations from a single extent report and write this data as console output
 *   - The ability to output all verifications in a project to an HTML report
 *   - The ability to fetch a list of all the tests in a project and write this data as console output
 */
public class ExtentReportsAndTestCaseVerificationsBuilder {

    /**
     * This method takes two Extent Reports and compares test time durations, plotting the output in a chart
     * The output is an HTML file created with Apache Freemarker which is written to the machine's temp directory
     * The chart is created using rGraph, a JavaScript charting library
     * This method can be used for the following cases:
     *  - Comparing extent reports from two different environments
     *  - Comparing extent reports to find a performance trend using two reports from different time periods
     *  - Comparing extent reports to find a specific slow test
     * To determine which specific test is slow, use the next method in this class - reportLogDurationOutputTest
     * @param file1 is the first extent report to chart
     * @param file2 is the second extent report to chart
     * @param sampleSetName1 unique report data key for the first report, this can be something generic such as "OCT-001"
     * @param sampleSetName2 unique report data key for the second report, this can be something generic such as "OCT-002"
     * @return
     * @throws Exception if there is an issue reading the files, or creating the report output
     */
    public String createRGraphJson(File file1, File file2, String sampleSetName1, String sampleSetName2, String projectName) throws Exception {
        final String title = String.format("    title:'%s vs %s Performance (in seconds)',", sampleSetName1, sampleSetName2);
        final String key = String.format("    key:['%s','%s'],", sampleSetName1, sampleSetName2);

        ReportInfo report1 = parseFileForTotalDuration(file1);
        ReportInfo report2 = parseFileForTotalDuration(file2);

        // NOTE: filtering for a specific duration can be added with a filter such as:
        // .filter(x -> x >= 75d && x <= 125d)
        // or
        // .filter(x -> x >= 3000d)

        List<String> tests1Filtered = report1.getTests()
                .stream()
                .map(x -> x.durationInSeconds)
                .map(Double::parseDouble)
                .map(Object::toString)
                .collect(Collectors.toList());

        List<String> tests2Filtered = report2.getTests()
                .stream()
                .map(x -> x.durationInSeconds)
                .map(Double::parseDouble)
                .map(Object::toString)
                .collect(Collectors.toList());

        List<String> tests1Sorted = report1.getTests()
                .stream()
                .map(x -> x.durationInSeconds)
                .map(Double::parseDouble)
                .sorted(Comparator.naturalOrder())
                .map(Object::toString)
                .collect(Collectors.toList());

        List<String> tests2Sorted = report2.getTests()
                .stream()
                .map(x -> x.durationInSeconds)
                .map(Double::parseDouble)
                .sorted(Comparator.naturalOrder())
                .map(Object::toString)
                .collect(Collectors.toList());

        List<String> labels = IntStream.range(1, report1.getTests().size() + 1)
                .mapToObj(String::valueOf)
                .collect(Collectors.toList());

        StringBuilder sb1 = new StringBuilder();
        sb1.append("let json = {");
        sb1.append(title);
        sb1.append(key);
        sb1.append(String.format("    data:[[%s],[%s]],", StringUtils.join(tests1Filtered, ","), StringUtils.join(tests2Filtered, ",")));
        sb1.append(String.format("    labels:[%s],", StringUtils.join(labels, ",")));
        sb1.append("}");

        StringBuilder sb2 = new StringBuilder();

        sb2.append("let json2 = {");
        sb2.append(title);
        sb2.append(key);
        sb2.append(String.format("    data:[[%s],[%s]],", StringUtils.join(tests1Sorted, ","), StringUtils.join(tests2Sorted, ",")));
        sb2.append(String.format("    labels:[%s],", StringUtils.join(labels, ",")));
        sb2.append("}");

        Map templateMap = new HashMap();
        templateMap.put("title", String.format("%s Project Performance Information", StringUtils.capitalize(projectName)));
        templateMap.put("json1", sb1);
        templateMap.put("json2", sb2);

        return createFreeMarkerOutput(templateMap, "rgraph/index.ftlh");
    }

    /**
     * This method pulls a list of tests from a submitted extent report file, and filters the tests based on duration.
     * Any test with a duration shorter than the duration submitted is filtered out of the test.
     * The purpose of this method is to look for tests with a long duration to diagnose slow test performance.
     * Output is written to the console, and can be copied into Excel for more analysis.
     * Spreadsheet: Excel > (select column) > Data > Text to Columns > Delimited > Delimiter Other: Pipe > Finish
     * @param file the extent report to parse for tests
     * @param durationLongThan is the duration in seconds that should be used to filter test, this method will display tests with durations longer than this parameter
     * @return a string containing tests with durations longer than the parameter
     * @throws IOException if there is an issue reading the extent report
     * @throws ParseException if there is an issue parsing the data in the extent report
     */
    public String getTestsFilteredByDuration(File file, Double durationLongThan) throws IOException, ParseException {
        LogBuilder logBuilder = new LogBuilder();
        ExtentReportsAndTestCaseVerificationsBuilder.ReportInfo report = parseFileForTotalDuration(file);

        final List<String> list = report.getTests().stream()
                .filter(x -> Double.parseDouble(x.getDurationInSeconds()) >= durationLongThan)
                .map(x -> String.format("%s|%s|%s|%s",
                        x.getTestName(), x.getStatus(), x.getDurationFormatted(), x.getDurationInSeconds()))
                .collect(Collectors.toList());

        return logBuilder
                .appendLineBreak()
                .appendPrettyLineSeparator()
                .appendLine("TestName|Status|DurationFormatted|DurationInSeconds")
                .appendLines(list)
                .appendPrettyLineSeparator()
                .appendLineBreak()
                .logAndGetMessage();
    }

    /**
     * This method parses the code of a project and collects all of the verifications from assertions
     * An HTML report with the verification information which is written to the machine's temp directory
     * @return
     * @throws IOException if there is a issue parsing the .java files that contain the assertions
     * @throws TemplateException if there is an issue generating the HTML report with the Apache Freemarker template
     */
    public String printAllVerifications(String projectName) throws IOException, TemplateException {
        List<String> list = readFiles(this::parseFileForVerification);
        List<String> lines = new ArrayList<>();
        String file = "";

        for (String item : list) {
            if (!item.startsWith("|")) {
                file = item;
            } else {
                item = file + item;
                lines.add(item);
            }
        }

        List<VerificationInfo> verifications = new ArrayList<>();
        for (String item : lines) {
            VerificationInfo info = new VerificationInfo();
            String[] parts = item.split("\\|");
            info.setCategory(parts[1]);
            info.setTestClass(parts[0]);
            info.setVerification(parts[2]);
            verifications.add(info);
        }

        Map<String, List<VerificationInfo>> verificationByCategory = verifications
                .stream()
                .collect(groupingBy(VerificationInfo::getCategory));

        Map templateMap = new HashMap();
        java.time.LocalDateTime dateTime = java.time.LocalDateTime.now();
        java.time.format.DateTimeFormatter formatDateTime = java.time.format.DateTimeFormatter.ofPattern("E, MMM dd yyyy HH:mm:ss");
        templateMap.put("project", String.format("%s", StringUtils.capitalize(projectName)));
        templateMap.put("date", dateTime.format(formatDateTime));
        List<VerificationCategoryInfo> categories = new ArrayList<>();

        for (Map.Entry<String, List<VerificationInfo>> entry : verificationByCategory.entrySet()) {
            String currentTestClass = "";
            VerificationCategoryInfo categoryInfo = new VerificationCategoryInfo();
            categoryInfo.setCategoryName(entry.getKey());
            //Logger.log(entry.getKey());

            VerificationClassInfo classInfo = null;
            for (VerificationInfo info : entry.getValue()) {
                if (!info.getTestClass().equals(currentTestClass)) {
                    classInfo = new VerificationClassInfo();
                    classInfo.setClassName(info.getTestClass());
                    categoryInfo.getClasses().add(classInfo);
                    //Logger.log("  " + info.getTestClass());
                    currentTestClass = info.getTestClass();
                }
                if (classInfo != null) classInfo.getVerifications().add(info.getVerification());
                //Logger.log("    " + info.getVerification());
            }

            categories.add(categoryInfo);
        }

        templateMap.put("categories", categories);
        return createFreeMarkerOutput(templateMap, "verify/index.ftlh");
    }

    /**
     * This method parses the code of a project and collects all of the tests names
     * This method is usually passed via delegate to {@link ExtentReportsAndTestCaseVerificationsBuilder#readFiles(Function<List<String>, List<String>>)}
     * This information is written out as console output
     *
     * @param fileLines the lines contained in a .java file
     * @return a list of tests in that file
     */
    public List<String> parseFileForTest(List<String> fileLines) {
        List<String> keepList = new ArrayList<>();
        int index = 0;
        for (String line : fileLines) {
            if (line.contains("@Test") && !line.contains("@TestCaseId")) {
                String testMethod = fileLines.get(index + 1).trim();
                if (!testMethod.contains("public")) testMethod = fileLines.get(index + 2).trim();
                String[] parts = testMethod.split(" ");
                if (parts.length <= 2) continue;
                String methodName = parts[2].replace("()", "");
                if (line.contains("enabled") && (line.contains("false"))) methodName += " ***disabled***";
                keepList.add("  " + methodName);
            }
            index++;
        }

        return keepList;
    }

    /**
     * This method generates an HTML report using the Apache Freemarker templating engine
     * The template is stored in .ftlh format under src/main/resources/view/{template-type}
     * @param templateMap
     * @param templateLocation
     * @return the contents of the HTML file generated (for test assertion purposes)
     * @throws IOException if there is an issue reading the ftlh template file
     * @throws TemplateException if there is an issue generating the template
     */
    public String createFreeMarkerOutput(Map templateMap, String templateLocation) throws IOException, TemplateException {
        /* Gather Performance Information */
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_22);
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        configuration.setClassLoaderForTemplateLoading(loader, "view/");
        configuration.setDefaultEncoding("UTF-8");

        /* Get the template (uses cache internally) */
        Template template = configuration.getTemplate(templateLocation);
        File htmlOutputFile = File.createTempFile("comparison_", ".html");

        /* Merge data-model with template */
        StringWriter out = new StringWriter();
        template.process(templateMap, out);
        String source = out.toString();
        write(htmlOutputFile, source);
        out.close();

        LogBuilder logBuilder = new LogBuilder();

        logBuilder
                .appendLineBreak()
                .appendLine("COMPARISON REPORT LOCATION")
                .appendPrettyLineSeparator()
                .appendLine(htmlOutputFile.toString())
                .appendPrettyLineSeparator()
                .appendLineBreak()
                .log();

        return htmlOutputFile.toString();
    }

    /**
     * This method reads all test .java file in a client project and applies a function to these files via delegate
     * This is a utility method that can be used to read tests method names, parse out verifications, etc.
     * By default this method writes out the name of all tests and test methods as console output
     * @param function
     * @return a list of files and the results of the function delegate
     * @throws IOException if there are issues reading files in the project
     */
    public List<String> readFiles(Function<List<String>, List<String>> function) throws IOException {
        LogBuilder logBuilder = new LogBuilder();
        Path currentRelativePath = Paths.get("");
        String currentPath = currentRelativePath.toAbsolutePath().toString();
        Logger.log(String.format("Current relative path is: %s", currentPath));

        List<String> list = new ArrayList<>();

        List<Path> paths = Files.find(Paths.get(currentPath + "/src/test"),
                Integer.MAX_VALUE,
                (filePath, attribute) -> attribute.isRegularFile())
                .collect(Collectors.toList());

        for (Path filePath : paths) {
            if (!filePath.toString().toLowerCase().endsWith(".java")) continue;

            String fileName = filePath.getFileName().toString().replace(".java", "");
            List<String> lines = FileUtils.readLines(new File(filePath.toString()), Charset.defaultCharset());
            List<String> fileResults = function.apply(lines);
            if (fileResults.size() <= 0 || fileName.equals("QuickTest")) continue;

            list.add(fileName);
            list.addAll(fileResults);
        }

        logBuilder
                .appendLineBreak()
                .appendLine("LIST OF ALL TESTS")
                .appendPrettyLineSeparator()
                .appendLines(list)
                .appendPrettyLineSeparator()
                .appendLineBreak()
                .log();

        return list;
    }

    /**
     * This is a helper method to find the user's home folder
     * @return the user's home folder
     */
    public File getHomeFolder() {
        String home = System.getProperty("user.home");
        if (StringUtils.isNotBlank(home)) {
            return new File(home);
        }
        return new File("").getAbsoluteFile();
    }

    /**
     * This is a helper method to read a file from the project's resources
     * @param resourceLocation the location of the resource to read in /src/main/resources
     * @return a file reference to the resource
     * @throws URISyntaxException if the resource cannot be read, or if the resource location is malformed
     */
    public File getFileFromResource(String resourceLocation) throws URISyntaxException {
        final URL resource = Thread.currentThread().getContextClassLoader().getResource(resourceLocation);
        return new File(resource.toURI());
    }

    /**
     * This method parse an extent report file and returns a ReportInfo object that contains:
     *   - The report name
     *   - The report date (formatted)
     *   - The report duration (formatted)
     *   - The report duration (converted to seconds)
     *   - A list of the tests and corresponding durations that were found in the report
     * @param file this is the extent report to parse, it is expected that a report containing extent report HTML will be passed
     * @return a ReportInfo object containing report metadata
     * @throws IOException if there extent report passed cannot be read
     * @throws ParseException if there is an issue parsing the data in the extent report
     */
    public ReportInfo parseFileForTotalDuration(File file) throws IOException, ParseException {
        List<String> lines = FileUtils.readLines(file, Charset.defaultCharset());
        String[] strings = new String[]{"suite-start-time", "test-name", "test-status", "time-take", "</table>", "panel-lead"};

        List<String> keepList = lines.stream()
                .filter(line -> StringUtils.indexOfAny(line, strings) > 0)
                .map(String::trim)
                .collect(Collectors.toList());

        List<ReportTestInfo> list = new ArrayList<>();
        ReportInfo reportInfo = new ReportInfo();
        ReportTestInfo info = null;

        reportInfo.setFileName(file.getName());

        for (String line : keepList) {
            if (line.contains("suite-start-time")) {
                reportInfo.setDateFormatted(StringUtils.substringBetween(line, ">", "<"));
                reportInfo.setDate(DateUtils.parseDate(reportInfo.getDateFormatted(), "MMM d, yyyy HH:mm:ss a"));
            }

            if (line.contains("test-name")) {
                info = new ReportTestInfo();
                info.setTestName(StringUtils.substringBetween(line, ">", "<"));
            }

            if (line.contains("test-status") && info != null) {
                info.setStatus(line.contains(">pass<") ? "pass" : "fail");
            }

            if (line.contains("time-take") && info != null) {
                info.setDurationFormatted(StringUtils.substringBetween(line, ">", "<"));
                info.setDurationInSeconds(durationInSeconds(info.getDurationFormatted()));
            }

            if (line.contains("</table>")) {
                if (info != null && info.getStatus() != null) {
                    info.setTestName(Optional.ofNullable(info.getTestName()).orElse(""));
                    info.setStatus(Optional.ofNullable(info.getStatus()).orElse(""));
                    info.setDurationFormatted(Optional.ofNullable(info.getDurationFormatted()).orElse(""));
                    info.setDurationInSeconds(Optional.ofNullable(info.getDurationInSeconds()).orElse(""));
                    list.add(info);
                    info = new ReportTestInfo();
                }
            }

            if (line.contains("panel-lead") && line.contains("+")) {
                reportInfo.setDurationFormatted(StringUtils.substringBetween(line, ">", "<"));
                reportInfo.setDurationInSeconds(durationInSeconds(reportInfo.getDurationFormatted()));
            }
        }

        reportInfo.setTests(list);
        return reportInfo;
    }

    /**
     * This helper method takes a duration in extent report format [Xh Xm Xs+Xms] and converts it to seconds
     * @param duration the duration in extent report format
     * @return a string with the duration in seconds
     */
    private String durationInSeconds(String duration) {
        long hours = Long.parseLong(StringUtils.substringBefore(duration.trim(), "h").trim());
        long minutes = Long.parseLong(StringUtils.substringBetween(duration.trim(), "h", "m").trim());
        long seconds = Long.parseLong(StringUtils.substringBetween(duration.trim(), "m", "s").trim());
        long millis = Long.parseLong(StringUtils.substringBetween(duration.trim(), "+", "ms").trim());

        Long totalMillis = TimeUnit.HOURS.toMillis(hours) + TimeUnit.MINUTES.toMillis(minutes) + TimeUnit.SECONDS.toMillis(seconds) + millis;
        Double totalSeconds = (totalMillis.doubleValue()) / 1000;

        return totalSeconds.toString();
    }

    /**
     * This helper method takes a lines of lines from a .java test file and parses out the verifications found in assertions within
     * @param fileLines a list of lines in a .java test file
     * @return a list of verifications found in the test file
     */
    private List<String> parseFileForVerification(List<String> fileLines) {
        List<String> keepList = new ArrayList<>();
        int index = 0;
        for (String line : fileLines) {
            if (line.contains("\"Verify ")) {
                String category = "";
                String cleanLine = line.trim();
                if (cleanLine.startsWith("\"") && cleanLine.endsWith("\"));")) {
                    cleanLine = cleanLine.substring(1, cleanLine.length() - 4);
                } else {
                    cleanLine = cleanLine.replace("\"", "");
                }
                keepList.add(String.format("|%s|%s", category.replace(",", ""), cleanLine));
            }
            index++;
        }

        return keepList;
    }

    /**
     * This helper method writes text passed as a parameter into a file passed by reference
     * @param f a file reference to an output file
     * @param text the text to write to this file
     */
    private void write(final File f, String text) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(f))) {
            writer.write(text);
        } catch (Exception e) {
            Logger.log(e.getMessage());
        }
    }

    public class ReportInfo {
        private String fileName;
        private String dateFormatted;
        private Date date;
        private String durationFormatted;
        private String durationInSeconds;
        private List<ReportTestInfo> tests;

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getDateFormatted() {
            return dateFormatted;
        }

        public void setDateFormatted(String dateFormatted) {
            this.dateFormatted = dateFormatted;
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public String getDurationFormatted() {
            return durationFormatted;
        }

        public void setDurationFormatted(String durationFormatted) {
            this.durationFormatted = durationFormatted;
        }

        public String getDurationInSeconds() {
            return durationInSeconds;
        }

        public void setDurationInSeconds(String durationInSeconds) {
            this.durationInSeconds = durationInSeconds;
        }

        public List<ReportTestInfo> getTests() {
            tests = (Optional.ofNullable(tests).orElse(new ArrayList<>()));
            return tests;
        }

        public void setTests(List<ReportTestInfo> tests) {
            this.tests = tests;
        }
    }

    public class ReportTestInfo {
        private String testName;
        private String durationFormatted;
        private String durationInSeconds;
        private String status;

        public String getTestName() {
            return testName;
        }

        public void setTestName(String testName) {
            this.testName = testName;
        }

        public String getDurationFormatted() {
            return durationFormatted;
        }

        public void setDurationFormatted(String durationFormatted) {
            this.durationFormatted = durationFormatted;
        }

        public String getDurationInSeconds() {
            return durationInSeconds;
        }

        public void setDurationInSeconds(String durationInSeconds) {
            this.durationInSeconds = durationInSeconds;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }
    }

    public class VerificationInfo {
        private String category;
        private String testClass;
        private String verification;

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getTestClass() {
            return testClass;
        }

        public void setTestClass(String testClass) {
            this.testClass = testClass;
        }

        public String getVerification() {
            return verification;
        }

        public void setVerification(String verification) {
            this.verification = verification;
        }
    }

    public class VerificationCategoryInfo {
        private String categoryName;
        List<VerificationClassInfo> classes;

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        public List<VerificationClassInfo> getClasses() {
            classes = (Optional.ofNullable(classes).orElse(new ArrayList<>()));
            return classes;
        }

        public void setClasses(List<VerificationClassInfo> classes) {
            this.classes = classes;
        }
    }

    public class VerificationClassInfo {
        private String className;
        private List<String> verifications;

        public String getClassName() {
            return className;
        }

        public void setClassName(String className) {
            this.className = className;
        }

        public List<String> getVerifications() {
            verifications = (Optional.ofNullable(verifications).orElse(new ArrayList<>()));
            return verifications;
        }

        public void setVerifications(List<String> verifications) {
            this.verifications = verifications;
        }
    }
}
