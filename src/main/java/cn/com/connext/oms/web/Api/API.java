package cn.com.connext.oms.web.Api;

/**
 * <p>Title: API</p>
 * <p>Description: </p>
 *
 * @author caps
 * @version 1.0.0
 * @Date 2019/1/4 14:28
 */
public class API {
    // OMS主机地址
    public static final String HOST = "http://localhost:8502";

    //WMS主机地址
    public static final String WMS_HOST="http://10.129.100.32:8080";

    // 消息列表数据接口
    public static final String API_ORDER = HOST + "/getAllOrder";

    //退换货模块交互接口
    public static final String API_RETURN=WMS_HOST+"api/inRepertoryOrder";

}
