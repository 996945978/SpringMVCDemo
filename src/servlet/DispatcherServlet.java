package servlet;

import com.web.anno.RequestMapping;
import com.web.controller.IndexController;
import util.StringUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

@WebServlet(value = "/*",loadOnStartup = 1)
public class DispatcherServlet extends HttpServlet {
    HashMap<String ,Method> map = new HashMap<>();

    public void doGet(HttpServletRequest request, HttpServletResponse response){
        //1.获取请求路径
        String url = request.getRequestURI();
        String path = url.substring(url.lastIndexOf("/"));
        Method m = map.get(path);
        Class[] clazz =  m.getParameterTypes();
        for(Class cla : clazz){
            try {
                Object tarBean = cla.newInstance();
                Field[] fields =  cla.getDeclaredFields();
                for(Field field :fields){
                    Method method = cla.getMethod("set"+ StringUtil.toUpperCaseFirstOne(field.getName()),field.getType());
                    method.invoke(tarBean,request.getParameter(field.getName()));
                }
                Object tarObj =  m.getDeclaringClass().newInstance();
                m.invoke(tarObj,tarBean);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void init()  {
        //获取磁盘路径
        String path = this.getClass().getResource("/").getPath();

        //加上包名
        String packing = "com/web/controller";
        path += packing;

        File file = new File(path);
        File[] files = file.listFiles();
        for(File fileTmp: files){
            Class clazz = null;
            try {
                clazz = Class.forName("com.web.controller."+fileTmp.getName().replace(".class",""));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            Method[] methods =  clazz.getDeclaredMethods();
            for(Method m : methods){
                RequestMapping requestMapping =  m.getDeclaredAnnotation(RequestMapping.class);
                String value =  requestMapping.value();
                map.put(value,m);
            }
        }
    }

    public static void main(String[] args) {
        System.out.println(IndexController.class.getResource("/"));
    }
}
