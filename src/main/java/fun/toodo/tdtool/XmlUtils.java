package fun.toodo.tdtool;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import lombok.SneakyThrows;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class XmlUtils {
    @SneakyThrows
    public static String jsonToXml(Object object) {
        StringBuilder result = new StringBuilder();
        JSONObject jsonObject = null;
        JSONArray jsonArray = null;
        try {
            jsonObject = new JSONObject(object);
            for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
                result.append("<").append(entry.getKey()).append(">");
                result.append(jsonToXml(entry.getValue()));
                result.append("</").append(entry.getKey()).append(">");
            }
        } catch (Exception ignored) {
            try {
                jsonArray = new JSONArray(object);
                for (Object oj : jsonArray) {
                    result.append("<item>").append(jsonToXml(oj)).append("</item>");
                }
            } catch (Exception e){
                result.append(object);
            }
        }
        return result.toString();
    }

    @SneakyThrows
    public static JSONObject xmlToJson(String xml) {
        JSONObject jsonObject = new JSONObject();
        Document document = DocumentHelper.parseText(xml);
        Element rootElement = document.getRootElement();
        jsonObject.set(rootElement.getName(), iterateElement(rootElement));
        return jsonObject;
    }

    private static Object iterateElement(Element element) {
        List<Element> node = element.elements();
        Set<String> set = new HashSet<>();
        JSONObject jsonObject = null;
        JSONArray jsonArray = null;
        for (Element et : node) {
            set.add(et.getName());
        }
        if (set.size() == node.size()) {
            // 字典解析
            jsonObject = new JSONObject();
            for (Element et : node) {
                if (et.elements().size() == 0) {
                    jsonObject.set(et.getName(), et.getTextTrim());
                } else {
                    jsonObject.set(et.getName(), iterateElement(et));
                }
            }
            return jsonObject;
        } else {
            // 列表解析
            jsonArray = new JSONArray();
            for (Element et : node) {
                if (et.elements().size() != 0) {
                    jsonArray.set(iterateElement(et));
                } else {
                    jsonArray.set(et.getTextTrim());
                }
            }
            return jsonArray;
        }
    }
}
