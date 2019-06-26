package cn.com.connext.oms.service.impl;

import cn.com.connext.oms.commons.dto.BaseResult;
import cn.com.connext.oms.commons.dto.output.OutRepoOrderDetailDto;
import cn.com.connext.oms.entity.TbOrder;
import cn.com.connext.oms.entity.TbOrderDetails;
import cn.com.connext.oms.entity.TbOutput;
import cn.com.connext.oms.entity.TbReceiver;
import cn.com.connext.oms.mapper.TbOrderMapper;
import cn.com.connext.oms.mapper.TbOutputMapper;
import cn.com.connext.oms.mapper.TbReceiverMapper;
import cn.com.connext.oms.service.OutputService;
import cn.com.connext.oms.web.Api.output.OutputApi;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * <p>Title: OutputServiceImpl</p>
 * <p>Description: 出库模块，包含查询订单详情，出库单详情，更改状态等</p>
 *
 * @author Jay
 * @version 1.0.0
 * @Date 2019/1/7
 */
@Service
public class OutputServiceImpl implements OutputService {
    @Autowired
    private TbOrderMapper tbOrderMapper;
    @Autowired
    private TbReceiverMapper tbReceiverMapper;
    @Autowired
    private TbOutputMapper tbOutputMapper;
    // 定义状态
    private static String STATUS1="待预检";
    private static String STATUS2="待路由";
    private static String STATUS3="待出库";
    private static String STATUS4="已出库";
    private static String STATUS5="已发货";
    private static String STATUS6="出库异常";
    private static String STATUS7="处理中";
    private static String STATUS8="已完成";


    /**
     *
     * 功能描述: 根据订单id将待路由状态改为待出库
     *
     * @param: id数组
     * @return: 返回修改结果
     * @auther:Jay
     * @date:  2018/1/7
     */
    @Override
    public BaseResult UpdateOrderIntoWaitOutPut(int[] id) {
        // 定义 m 作为统计返回"路由成功"的条数
        int m = 0;
        // 定义 n 作为统计返回"路由异常"的条数
        int n = 0;
        // 定义 k 作为统计返回"不符合状态"的条数
        int k = 0;
        // 遍历数组 id
        for (int id1:id){
            // 根据订单id查询整个订单数据
            TbOrder tbOrder = tbOrderMapper.selectByPrimaryKey(id1);
            // 获取订单状态
            String orderState = tbOrder.getOrderState();
            if (orderState.equals(STATUS2)){
                // 设置订单状态为待出库
                tbOrder.setOrderState(STATUS3);
                // 更改订单状态
                int t = tbOrderMapper.updateByPrimaryKeySelective(tbOrder);
                if (t==1){
                    m++;
                } else {
                    n++;
                }
            } else {
                k++;
            }
        }
        ArrayList<Integer> list = new ArrayList<>();
        list.add(m);
        list.add(n);
        list.add(k);
        return BaseResult.success("mes",list);
    }

    /**
     *
     * 功能描述: 根据传入的数组id生成出库单，并根据WMS返回值修改订单状态为已出库或者出库异常
     *
     * @param: 订单id
     * @return: 返回修改结果
     * @auther: Jay
     * @date: 2019/1/7
     */
    @Override
    public BaseResult Output(int[] id1) {
        // 定义 m 作为统计返回"出库成功"的条数
        int m = 0;
        // 定义 n 作为统计返回"出库异常"的条数
        int n = 0;
        // 定义 k 作为统计返回"不符合状态"的条数
        int k = 0;
        // 遍历前台选中的id数组
        for (int id:id1) {
            // 根据id查询出所有的订单信息
            TbOrder tbOrder = tbOrderMapper.selectByPrimaryKey(id);
            List<OutRepoOrderDetailDto> repoOrderDetailDto = tbOutputMapper.getOutRepoOrderDetailDto(id);
            if (tbOrder.getOrderState().equals(STATUS3) || tbOrder.getOrderState().equals(STATUS6)) {
                // 根据收获人信息查询收获人信息
                TbReceiver tbReceiver = tbReceiverMapper.selectByPrimaryKey(tbOrder.getReceiverId());
                TbOutput tbOutput = this.getOneTbOutput(id);
                //传送出库单并接受返回值类型判断是否接收成功
                String s = null;
                try {
                    s = OutputApi.post(tbOutput, tbOrder, tbReceiver, repoOrderDetailDto);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                // 判断接收的结果 200 表示接收成功
                if ("200".equals(s)) {
                    tbOutput.setOutputState(STATUS7);
                    tbOutputMapper.updateByPrimaryKeySelective(tbOutput);
                    tbOrder.setOrderState(STATUS4);
                    tbOrderMapper.updateByPrimaryKeySelective(tbOrder);
                    m++;
                } else {
                    // 状态不是200的一切情况
                    tbOrder.setOrderState(STATUS6);
                    tbOrderMapper.updateByPrimaryKeySelective(tbOrder);
                    tbOutput.setOutputState(STATUS6);
                    tbOutputMapper.updateByPrimaryKeySelective(tbOutput);
                    n++;
                }
            } else {
                k++;
            }
        }
        ArrayList<Integer> list = new ArrayList<>();
        list.add(m);
        list.add(n);
        list.add(k);
        return BaseResult.success("mes",list);
    }
    /**
     *
     * 功能描述: 根据订单id查询出所有出库单的详情
     *
     * @param: 订单id
     * @return: 返回订单的详情
     * @auther: Jay
     * @date: 2019/1/8
     */
    @Override
    public List<TbOrderDetails> orderDetails(int orderId) {
        return tbOutputMapper.orderDetails(orderId);
    }
    /**
     * 功能描述: 点击出库单列表，显示所有已出库的订单,以及模糊查询选择符合条件的订单,默认显示所有已出库的订单
     *
     * @return: 返回所有状态是已出库的订单，以及模糊查询选择符合条件的订单
     * @param: currentPage: 当前页， pageSize： 总页数, orderId： 订单id，outputCode ：出库单号, deliveryCode ：快递单号
     * @auther: Jay
     * @date: 2019/1/13
     */
    @Override
    public PageInfo<TbOrderDetails> getAllOrderByStatusAndSeacrch(int currentPage,int pageSize,String orderId, String outputCode, String deliveryCode) {
        // 从第 1 页开始，每一页 5 条数据
        PageHelper.startPage(currentPage, pageSize);

        // 返回所有状态是已出库的订单，模糊查询选择符合条件的订单，默认显示所有已出库订单
        List<TbOrderDetails> allOrder = tbOutputMapper.getOutputOrdersBySearch(STATUS4,STATUS5,STATUS8, orderId,outputCode,deliveryCode);
        PageInfo<TbOrderDetails> pageInfo = new PageInfo<>(allOrder);
        return pageInfo;
    }
    @Override
    public TbOrder getOrderById(Integer orderId) {
        return tbOrderMapper.selectByPrimaryKey(orderId);
    }
    /**
     *
     * 功能描述: 根据传过来的状态更改状态
     *
     * @param: 出库状态
     * @return: 返回200表示修改成功
     * @auther: Jay
     * @date: 2019/1/9
     */
    /**
     *
     * 功能描述: 根据传过来的发货信息更新订单数据
     *
     * @param: 订单发货信息
     * @return 返回200表示接收并更新成功
     * @auther: Jay
     * @date: 2019/1/9
     */
    @Override
    public String updateOrder(TbOrder tbOrder) {
        int t = tbOrderMapper.updateByPrimaryKeySelective(tbOrder);
        if (t==1){
        return "200";
        }
        return "error";
    }

    @Override
    public TbOutput getOutputOrder(int orderId) {
        TbOutput tbOutput = tbOutputMapper.getOutputOrderById(orderId);
        return tbOutput;
    }
    /**
     *
     * 功能描述: 更改出库单的状态
     *
     * @param:
     * @return:
     * @auther: Jay
     * @date: 2019/1/9
     */
    @Override
    public String updateOutput(TbOutput tbOutput) {
        int t = tbOutputMapper.updateByPrimaryKeySelective(tbOutput);
        if (t==1){
            return "200";
        }
        return "error";
    }

    /**
     * 功能描述: 确认收货后将订单状态修改为已完成
     *
     * @param: 订单id
     * @return: 修改成功或者修改失败
     * @auther: Jay
     * @date: 2019/1/21
     */
    @Override
    public BaseResult confirmReceiptUpdateOrderState(int orderId) {
        TbOrder tbOrder = tbOrderMapper.selectByPrimaryKey(orderId);
        String state = tbOrder.getOrderState();
        if (state.equals(STATUS5)){
            tbOrder.setOrderState(STATUS8);
            tbOrderMapper.updateByPrimaryKeySelective(tbOrder);
            return BaseResult.success("确认收货成功！");
        }
        return BaseResult.fail("订单状态必须为已发货，请重新选择！");
    }
    /*
     *
     * 功能描述: 判断是否生成新的出库单，当订单异常时，不生成新的出库单
     *
     * @param:  订单id
     * @return:  返回出库单
     * @auther: Jay
     * @date: 2019/1/16
     * */

    public TbOutput getOneTbOutput(int id){
        TbOrder tbOrder = tbOrderMapper.selectByPrimaryKey(id);
        if (tbOrder.getOrderState().equals(STATUS6)){
            TbOutput tbOutput = tbOutputMapper.getOutputByOrderId(id);
            return tbOutput;
        } else {
            // 生成出库单，设置出库单属性
            TbOutput tbOutput = new TbOutput();
            String outputCode = id+""+UUID.randomUUID().toString().substring(1,8);
            Date date = new Date();
            //生成出库单
            tbOutput.setOutputCode(outputCode);
            tbOutput.setOrderId(id);
            tbOutput.setOutputState(STATUS3);
            tbOutput.setCreated(date);
            tbOutput.setUpdated(date);
            tbOutputMapper.insertSelective(tbOutput);
            return tbOutput;
        }
    }
}
