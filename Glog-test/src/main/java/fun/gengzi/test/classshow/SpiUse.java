package fun.gengzi.test.classshow;

import java.util.ArrayList;
import java.util.List;

public class SpiUse {

    public static List<SpiService> spiServiceList = new ArrayList<>();

    static {
        try {
            Class<?> aClass = Class.forName("fun.gengzi.test.spishow.SpiServiceImpl", true, Thread.currentThread().getContextClassLoader());
            Object o = aClass.newInstance();
            spiServiceList.add((SpiService) o);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }

    public static String test() {
        for (SpiService spiService : spiServiceList) {
            return spiService.test();
        }
        return null;
    }


}
