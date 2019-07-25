package cn.com.connext.oms.web.Controller;

import cn.com.connext.oms.service.TbExchangeService;
import org.junit.Test;
import org.junit.Before; 
import org.junit.After;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import cn.hutool.json.JSONObject;
/** 
* TbExchangeController Tester. 
* 
* @author <Authors name> 
* @since <pre>һ�� 10, 2019</pre> 
* @version 1.0 
*/
@RunWith(SpringRunner.class)
@SpringBootTest
public class TbExchangeControllerTest {
    @Autowired
    TbExchangeService tbExchangeService;

@Before
public void before() throws Exception { 
} 

@After
public void after() throws Exception { 
} 

/** 
* 
* Method: showAllReturns(Integer currentPage, Integer pageSize, String returnType) 
* 
*/ 
@Test
public void testShowAllReturns() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: toRequest(@RequestParam("orderId")int orderId) 
* 
*/ 
@Test
public void testToRequest() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: toGenerateExchangeOrder(@RequestParam("orderId")int orderId, @RequestParam("goodId")int[] goodId, @RequestParam("num")int[] num) 
* 
*/ 
@Test
public void testToGenerateExchangeOrder() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: exchangeDetails(@RequestParam("orderId")int orderId) 
* 
*/ 
@Test
public void testExchangeDetails() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: toCancel(@RequestParam("ids")Integer [] ids) 
* 
*/ 
@Test
public void testToCancel() throws Exception { 
//TODO: Test goes here... 
} 

/** 
* 
* Method: toAudit(@RequestParam("ids")int [] ids) 
* 
*/ 
@Test
//@Transactional
public void testToAudit() throws Exception {
//TODO: Test goes here...
    int ids[]={1004};
    Date date=new Date();
    int rs=tbExchangeService.AuditTbReturn(ids,"yonyong",date);
    if (rs!=1){
        System.out.println("操作失败！");
    } else System.out.println("1111111111111111111");
    try{
        int rt=tbExchangeService.generateInput(ids);
    }catch (Exception e){
        e.printStackTrace();
    }
}
@Test
    public void test11(){
    String jsonStr ="{responseHeader:{status:0,QTime:0},spellcheck:{suggestions:{中国:{numFound:9,startOffset:0,endOffset:2," +
            "suggestion:[中国工商银行, 中国人民, 中国国际, 中国农业, 中国市场, 中国经济, 中国人, 中国广播, 中国文化]}}," +
            "collations:{collation:中国工商银行}}}";
    JSONObject jsonObject = new JSONObject(jsonStr);
    if (jsonObject.get("spellcheck")==null){
        return ;
    }
    JSONObject params = (JSONObject) jsonObject.get("spellcheck");
    String t = String.valueOf(params.get("collations"));
    System.out.println("111111"+params.get("collations"));
}

} 
