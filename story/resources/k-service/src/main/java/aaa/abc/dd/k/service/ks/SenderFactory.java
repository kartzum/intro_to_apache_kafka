package aaa.abc.dd.k.service.ks;

import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SenderFactory {
    public static final String SUBJECT = "subject";
    public static final String SUBJECT_TYPE = "subject_type";
    public static final String BODY = "body";
    public static final String METHOD = "method";
    public static final String EMAIL_METHOD = "email";
    public static final String RECIPIENTS = "recipients";
    public static final String TITLE = "title";
    public static final String MESSAGE = "message";
    public static final String SEND = "send";

    public static String key() {
        return UUID.randomUUID().toString();
    }

    public static String createMessage(String method, String recipients, String title, String message) {
        Map<String, Object> map = new HashMap<>();
        Map<String, Object> subject = new HashMap<>();
        Map<String, Object> body = new HashMap<>();
        map.put(SUBJECT, subject);
        subject.put(SUBJECT_TYPE, SEND);
        subject.put(BODY, body);
        body.put(METHOD, method);
        body.put(RECIPIENTS, recipients);
        body.put(TITLE, title);
        body.put(MESSAGE, message);
        return JSONObject.toJSONString(map);
    }
}
