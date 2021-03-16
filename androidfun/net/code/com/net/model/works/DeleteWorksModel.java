package com.net.model.works;

import com.mei.orc.http.response.BaseResponse;
import com.net.model.chick.friends.ProductBean;

/**
 * @ProjectName: dove
 * @Package: com.net.model.works
 * @ClassName: DeleteWorksModel
 * @Description:
 * @Author: zxj
 * @CreateDate: 2020/6/5 15:35
 * @UpdateUser:
 * @UpdateDate: 2020/6/5 15:35
 * @UpdateRemark:
 * @Version:
 */
public class DeleteWorksModel {
    public ProductBean works;

    public static class Response extends BaseResponse<DeleteWorksModel> {

    }
}
