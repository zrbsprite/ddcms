package com.daimengshi.ddcms.admin.controller;

import com.alibaba.fastjson.JSON;
import com.daimengshi.ddcms.admin.model.DmsMenu;
import com.daimengshi.ddcms.admin.service.impl.DmsMenuServiceImpl;
import com.daimengshi.ddcms.admin.service.impl.DmsMenuTypeServiceImpl;
import com.daimengshi.ddcms.pub.TableDataRequest;
import com.daimengshi.ddcms.pub.TableDate;
import com.daimengshi.ddcms.pub.TablePage;
import com.jfinal.plugin.activerecord.Page;
import com.xiaoleilu.hutool.date.DateUtil;
import com.xiaoleilu.hutool.util.StrUtil;
import io.jboot.web.controller.JbootController;
import io.jboot.web.controller.annotation.RequestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by zhoufeng on 2017/12/6.
 * 后台主页
 */
@RequestMapping("/admin/menu")
public class AdminMenuController extends JbootController {
    private final static Logger logger = LoggerFactory.getLogger(AdminMenuController.class);

    @Inject
    private DmsMenuServiceImpl menuService;
    @Inject
    private DmsMenuTypeServiceImpl menuTypeService;

    /**
     * 菜单管理
     */
    public void index() {
        //获取菜单列表
        List<DmsMenu> menus = menuService.findAll();
        setAttr("menus", menus);
        setAttr("title", "菜单管理");
        setAttr("mainTP", "/htmls/admin/menu/index.html");

        //调用通用模板
        renderTemplate("/htmls/admin/global.html");
    }


    /**
     * 获取菜单列表
     */
    public void list() {
        //获取所有请求参数
        Map<String, String[]> reqParas = getParaMap();
        //获取数据表格请求
        TableDataRequest tableDataRequest = getBean(TableDataRequest.class, "");

        TablePage tablePage = pageFind();
        renderJson(tablePage);

//        ResponseData.ok().putDataValue(this, "menus", menusPage);
//        paginate(int pageNumber, int pageSize, String select, String sqlExceptSelect, Object... paras)
//        其中的参数含义分别为：当前页的页号、每页数据条数、sql语句的select部分、sql语句除了select以外的部分、查询参数。绝大多数情况下使用这个API即可。以下是使用示例：
    }

    /**
     * 分页查询 返回layui 数据表格 格式数据
     */
    private TablePage pageFind() {
        Page<DmsMenu> menusPage;

        //分页
        int size = getParaToInt("size", 10);            //每页返回条数
        int page = getParaToInt("page", 1);             //第几页
        //时间范围
        String dateStartStr = getPara("dateStart", "");  //开始时间
        String dateEndStr = getPara("dateEnd", "");      //结束时间
        //搜素关键字
        String searchKey = getPara("searchKey", "");      //结束时间
        String dateValue = getPara("dateValue", "");      //时间范围值


        if (StrUtil.isNotEmpty(dateValue)) {
            TableDate tableDateStart = JSON.parseObject(dateStartStr, TableDate.class);
            TableDate tableDateEnd = JSON.parseObject(dateEndStr, TableDate.class);
            //转换成Date 对象
            Date startDate = TableDate.toDate(tableDateStart);
            Date endDate = TableDate.toDate(tableDateEnd);
            //格式化
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            dateStartStr = formatter.format(startDate);
            dateEndStr = formatter.format(endDate);
        }

        //如果有日期 则查询日期范围内数据
        if (StrUtil.isEmpty(dateValue)) {
            //当前时间
            dateEndStr = DateUtil.now();
        }

        //分页查询
        menusPage = menuService.DAO.paginate(page, size,
                "select *", "from dms_menu WHERE dms_menu.`name` LIKE ? AND dms_menu.create_time BETWEEN ? AND ? ",
                "%" + searchKey + "%", dateStartStr, dateEndStr);


        //返回转换格式
        return TablePage.ok(menusPage);
    }
}