package fun.gengzi.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class LogBackTest {

    private static final Logger glog1024 = LoggerFactory.getLogger(LogBackTest.class);

    public static void main(String[] args) {
        glog1024.info("hahah");

        HashMap<String, Object> map = new HashMap<>();
        map.put("1","hh");
        test(map);
    }

    public static void test(HashMap map){

    }



}
