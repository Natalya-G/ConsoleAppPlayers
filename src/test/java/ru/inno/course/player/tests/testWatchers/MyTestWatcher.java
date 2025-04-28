package ru.inno.course.player.tests.testWatchers;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.*;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MyTestWatcher implements TestWatcher, BeforeAllCallback, AfterAllCallback {

    private Map<String, List<Method>> statuses;
    private static final String SUCCESS = "s";
    private static final String FAIL = "f";
    private static final String ABORT = "a";
    private static final String DISABLE = "d";

    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {

        List<Method> methods = statuses.get(DISABLE);
        methods.add(context.getTestMethod().get());

    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {

        List<Method> methods = statuses.get(ABORT);
        methods.add(context.getTestMethod().get());

    }

    @Override
    public void testSuccessful(ExtensionContext context) {

        List<Method> methods = statuses.get(SUCCESS);
        methods.add(context.getTestMethod().get());

    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {

        List<Method> methods = statuses.get(FAIL);
        methods.add(context.getTestMethod().get());
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        String head = """
                <!DOCTYPE html>
                <html lang="ru">
                <head>
                    <meta charset="UTF-8">
                    <title>Отчет</title>
                </head>
                <body>     
                    <table border="1" cellpadding="10" width="50%" align="center">                
                """;
        String tail = """
                        </table>
                    </body>
                </html>
                """;


        String content = "";
        int allTests = statuses.get(SUCCESS).size() + statuses.get(FAIL).size() + statuses.get(DISABLE).size() + statuses.get(ABORT).size();
        content += "<tr><td><b>Всего тестов</b></td><td><b>" + allTests + "</b></td></tr>";
        content += "<tr><td>Успешных</td><td>" + statuses.get(SUCCESS).size() + "</td></tr>";
        content += "<tr><td>Неуспешных</td><td>" + statuses.get(FAIL).size() + "</td></tr>";
        content += "<tr><td>Отключенных</td><td>" + statuses.get(DISABLE).size() + "</td></tr>";
        content += "<tr><td>Прерванных</td><td>" + statuses.get(ABORT).size() + "</td></tr>";


        content += "</table><br><br><br><br><table border=\"1\" cellpadding=\"10\" width=\"50%\" align=\"center\">";


        content += "<tr><td><b>Тест</b></td><td><b>Метод</b></td></tr>";
        content += "<tr><td colspan=\"2\" align=\"center\"><b>Успешные тесты</b></td></tr>";
        for (Method method : statuses.get(SUCCESS)) {
            content += "<tr><td >" + method.getAnnotation(DisplayName.class).value() + "</td><td>" + method.getName() + "</td></tr>";
        }

        content += "<tr><td colspan=\"2\" align=\"center\"><b>Неуспешные тесты</b></td></tr>";
        for (Method method : statuses.get(FAIL)) {
            content += "<tr><td >" + method.getAnnotation(DisplayName.class).value() + "</td><td>" + method.getName() + "</td></tr>";
        }

        content += "<tr><td colspan=\"2\" align=\"center\"><b>Отключенные тесты</b></td></tr>";
        for (Method method : statuses.get(DISABLE)) {
            content += "<tr><td >" + method.getAnnotation(DisplayName.class).value() + "</td><td>" + method.getName() + "</td></tr>";
        }

        content += "<tr><td colspan=\"2\" align=\"center\"><b>Прерванные тесты</b></td></tr>";
        for (Method method : statuses.get(ABORT)) {
            content += "<tr><td >" + method.getAnnotation(DisplayName.class).value() + "</td><td>" + method.getName() + "</td></tr>";
        }


        String report = head + content + tail;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM-dd-yyyy_HH-mm-ss");
        Files.writeString(Path.of("report_" + context.getTestClass().get().getSimpleName() + "_" + dtf.format(LocalDateTime.now()) + ".html"), report);
    }

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        statuses = new HashMap<>();
        statuses.put(SUCCESS, new ArrayList<>());
        statuses.put(FAIL, new ArrayList<>());
        statuses.put(ABORT, new ArrayList<>());
        statuses.put(DISABLE, new ArrayList<>());
    }
}
