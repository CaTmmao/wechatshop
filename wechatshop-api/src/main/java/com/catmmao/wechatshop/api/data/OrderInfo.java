package com.catmmao.wechatshop.api.data;

import java.io.Serializable;
import java.util.List;

/*
与 "下订单" 接口中前端传入的参数映射
如
<pre>
{
  "goods": [
    {
        "id": 12345,
        "number": 10,
    },
    {
        ...
    }
}
</pre>
*/
public class OrderInfo implements Serializable {
    List<GoodsIdAndNumber> listOfGoodsIdAndNumber;

    public List<GoodsIdAndNumber> getGoodsOnlyContainGoodsIdAndNumbers() {
        return listOfGoodsIdAndNumber;
    }

    public void setGoodsOnlyContainGoodsIdAndNumbers(
        List<GoodsIdAndNumber> listOfGoodsIdAndNumber) {
        this.listOfGoodsIdAndNumber = listOfGoodsIdAndNumber;
    }
}
