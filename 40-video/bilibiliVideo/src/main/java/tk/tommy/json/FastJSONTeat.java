//package tk.tommy.json;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.alibaba.fastjson.TypeReference;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import tk.tommy.json.model.Student;
//
//public class FastJSONTeat {
//
//    /**
//     * @param args
//     */
//    public static void main(String[] args) throws Exception {
//        //string2Json();
//        //string2Object();
//        //string2List();
//
//        map2json();
//        map2JSON();
//    }
//
//
//    /**
//     * 通过fastjson把字符串转换成JSON数据
//     * TypeReference
//     */
//    public static void string2Json() {
//        StringBuffer buffer = new StringBuffer();
//        buffer.append("{");
//        buffer.append("\"age\":").append("27").append(",");
//        buffer.append("\"userName\":").append("\"龙叔\"").append(",");
//        buffer.append("\"address\":").append("\"广东省云浮市\"");
//        buffer.append("}");
//
//        String jsonText = buffer.toString();
//
//        JSONObject jobj = JSON.parseObject(jsonText);
//        String address = jobj.get("address").toString();
//        System.out.println(address);
//    }
//
//
//    /**
//     * 通过fastjson把字符串转换成对象
//     * TypeReference
//     */
//    public static void string2Object() {
//        StringBuffer buffer = new StringBuffer();
//        buffer.append("{");
//        buffer.append("\"age\":").append("27").append(",");
//        buffer.append("\"userName\":").append("\"龙叔\"").append(",");
//        buffer.append("\"address\":").append("\"广东省云浮市\"");
//        buffer.append("}");
//
//        String jsonText = buffer.toString();
//        //方法一 把json字符串转成Student对象
//        Student stu1 = JSON.parseObject(jsonText, new TypeReference<Student>() {
//        });
//        //方法二 把json字符串转成Student对象
//        Student stu2 = JSON.parseObject(jsonText, Student.class);
//
//        System.out.println(stu1.getAddress());
//        System.out.println(stu2.getAddress());
//    }
//
//    /**
//     * 通过fastjson把字符串转换成泛型数组
//     * TypeReference
//     */
//    public static void string2List() {
//        StringBuffer buffer = new StringBuffer();
//        buffer.append("[{");
//        buffer.append("\"age\":").append("27").append(",");
//        buffer.append("\"userName\":").append("\"龙叔\"").append(",");
//        buffer.append("\"address\":").append("\"广东省云浮市\"");
//        buffer.append("}]");
//
//        String jsonText = buffer.toString();
//        //转成成数组
//        Student[] stu2 = JSON.parseObject(jsonText, new TypeReference<Student[]>() {
//        });
//        List<Student> list = Arrays.asList(stu2);
//
//        for (Student st : list) {
//            System.out.println(st.getAddress());
//        }
//
//        // 转换成ArrayList
//        ArrayList<Student> list2 = JSON.parseObject(jsonText, new TypeReference<ArrayList<Student>>() {
//        });
//
//        for (int i = 0; i < list2.size(); i++) {
//            Student obj = (Student) list2.get(i);
//            System.out.println(obj.getAddress());
//        }
//
//    }
//
//    /**
//     * 通过fastjson把Map换成字符串转
//     */
//    public static void map2json() {
//        //创建一个Map对象
//        Map<String, String> map = new HashMap<String, String>();
//        map.put("username", "周伯通");
//        map.put("address", "广东省仙游谷");
//        map.put("age", "198");
//        String json = JSON.toJSONString(map, true); //转成JSON数据
//
//        Map<String, String> map1 = (Map<String, String>) JSON.parse(json);
//        //遍历数组数据
//        for (String key : map1.keySet()) {
//            System.out.println(key + ":" + map1.get(key));
//        }
//        //        map1.sou
//    }
//
//    /**
//     * 通过fastjson把Map换成字符串转
//     */
//    public static void map2JSON() {
//        Map map = new HashMap();
//        map.put("username", "周伯通");
//        map.put("address", "广东省仙游谷");
//        map.put("age", "198");
//        String json = JSON.toJSONString(map);
//        Map map1 = JSON.parseObject(json);
//        for (Object obj : map.entrySet()) {
//            Map.Entry<String, String> entry = (Map.Entry<String, String>) obj;
//            System.out.println(entry.getKey() + "--->" + entry.getValue());
//        }
//    }
//}
//
