package com.soho.codegen.task;

import com.soho.codegen.domain.ZipMessage;
import com.soho.codegen.service.ZipMessageService;
import com.soho.ex.BizErrorEx;
import com.soho.mybatis.sqlcode.condition.imp.SQLCnd;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by shadow on 2018/7/17.
 */
public class JobTask {

    @Autowired
    private ZipMessageService zipMessageService;

    public void clearExpireData() {
        System.out.println("清理数据定时器执行开始: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        try {
            List<ZipMessage> list = zipMessageService.findByCnd(new SQLCnd().eq("state", 1).lt("ctime", (System.currentTimeMillis() - 1800000l)));
            if (list != null && !list.isEmpty()) {
                for (ZipMessage zipMessage : list) {
                    try {
                        File file = new File(zipMessage.getFilePath());
                        if (file.exists()) {
                            file.delete();
                        }
                        zipMessage.setState(2);
                        zipMessage.setUtime(System.currentTimeMillis());
                        zipMessageService.update(zipMessage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                System.out.println("---暂无数据---");
            }
        } catch (BizErrorEx e) {
            e.printStackTrace();
        }
        System.out.println("清理数据定时器执行结束: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
    }

}
