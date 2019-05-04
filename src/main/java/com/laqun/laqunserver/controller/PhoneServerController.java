package com.laqun.laqunserver.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.laqun.laqunserver.common.config;
import com.laqun.laqunserver.common.utils;
import com.laqun.laqunserver.dao.*;
import com.laqun.laqunserver.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/api/phoneServer")
public class PhoneServerController {

    @Resource
    private CustomerRepository customerRepository;
    @Resource
    private LoginWxRepository loginWxRepository;
    @RequestMapping("/checkWxUseTime")
    private JSONObject getGlobalConfig(HttpServletRequest req) {
        JSONObject resJo = new JSONObject();
        String wxid = req.getParameter("wxid");
        String sn = req.getParameter("sn");
        LoginWx loginWx = loginWxRepository.findByWxidAndSn(wxid, sn);
        if(loginWx != null) {
            if((System.currentTimeMillis() / 1000) - ((long) (Integer.valueOf(config.get("loginWxUseTime")).intValue() * 60)) > ((long) loginWx.getLastGetTime())) {
                loginWx.setLastGetTime(System.currentTimeMillis() / 1000);
                loginWxRepository.flush();
                resJo.put("res", "success");
            }else {
                resJo.put("res", "fail");
                resJo.put("errInfo", "时间未超过设置的登录微信使用时间");
            }
        }else{
            resJo.put("res", "fail");
            resJo.put("errInfo", "没有这个微信");
        }

        return resJo;
    }

    @Resource
    private AddQunRepository addQunRepository;
    @RequestMapping("/getAddQun")
    private JSONObject getAddQun(HttpServletRequest req) {
        JSONObject resJo = new JSONObject();
        String[] customerArr = req.getParameter("customerArr").split(",");
        int qunUseTimeout = Integer.valueOf(config.get("qunUseTimeout")).intValue() * 60;
        AddQun addQun = addQunRepository.getAdd(customerArr, qunUseTimeout);
        if (addQun != null) {
            JSONObject dataJo = new JSONObject();
            dataJo.put("qunQr", addQun.getQunQr());
            dataJo.put("customer", addQun.getCustomer());
            dataJo.put("canLaNum", addQun.getLaNum() - addQun.getLaedNum());
            resJo.put("res", "群获取成功");
            resJo.put("data", dataJo);
            addQun.setIsUse(1);
            addQun.setLastGetTime((int) System.currentTimeMillis()/1000);
            addQunRepository.flush();
        }else{
            resJo.put("res", "fail");
            resJo.put("errInfo", "群获取失败");
        }
        addQun = addQunRepository.getAddQunByCustomerAndLaNumGreaterThanLaedNum(customerArr);
        if(addQun != null) {
            resJo.put("res", "请等待群被使用完毕");
        }else{
            resJo.put("res", "全部群被拉满");
        }

        return resJo;
    }

    @Resource
    private AddWxRepository addWxRepository;
    @RequestMapping("/getAddWx")
    private JSONObject getAddWx(HttpServletRequest req) {
        JSONObject resJo = new JSONObject();
        List<AddWx> addWxList = addWxRepository.findByIsUseOrderByPriorityAsc(0, PageRequest.of(0, 1));
        if(addWxList.size() > 0) {
            AddWx addWx = addWxList.get(0);
            JSONObject dataJo = new JSONObject();
            dataJo.put("phone", addWx.getPhone());
            dataJo.put("customer", addWx.getCustomer());
            resJo.put("res", "success");
            resJo.put("data", dataJo);
            String loginWx = req.getParameter("loginWx");
            addWxRepository.updateAddWxIsUseAndLoginWxByPhone(1, loginWx, addWx.getPhone());
            addWxRepository.flush();
            customerRepository.updateCustomerAddNumAndOddNumByName(addWx.getCustomer());
            customerRepository.flush();
            loginWxRepository.updateLoginWxAddNumBywxid(loginWx);
            loginWxRepository.flush();
        }else {
            resJo.put("res", "fail");
            resJo.put("errInfo", "没有可用添加微信");
        }

        return resJo;
    }

    @Resource
    private IpConfRepository ipConfRepository;
    @RequestMapping("/getIp")
    private JSONObject getIp(HttpServletRequest req) {
        JSONObject resJo = new JSONObject();
        String ipAddr = "";
        if (req.getParameter("isPostIp").equals("1")) {
            ipAddr = req.getParameter("ipAddr");
        } else {
            ipAddr = req.getRemoteAddr();
        }
        IpConf ipConf = ipConfRepository.findIpConfBy(PageRequest.of(0, 1)).get(0);
        if(ipConf != null) {
            if((System.currentTimeMillis() / 1000 -  ipConf.getLastUseTime()) > ((long) (Integer.valueOf(config.get("ipUseTime")).intValue() * 60))) {
                resJo.put("res", "success");
                ipConfRepository.updateIpConfUseNumAndLastUseTimeByIpAddr(System.currentTimeMillis()/1000, ipAddr);
                ipConfRepository.flush();
            }else {
                resJo.put("res", "fail");
                resJo.put("errInfo", "时间未到期");
            }
        }else {
            ipConfRepository.saveAndFlush(new IpConf(ipAddr, System.currentTimeMillis()/1000));
            resJo.put("res", "success");
        }
        return resJo;
    }

    @Resource
    private SnRepository snRepository;
    @RequestMapping("/getJob")
    private JSONObject getJob(HttpServletRequest req) {
        JSONObject resJo = new JSONObject();
        String snStr = req.getParameter("sn");

        snRepository.updateSnLastHttpTimeBySn(utils.sdf.format(new Date()), snStr);
        snRepository.flush();
        Sn sn = snRepository.findBySn(snStr);
        if(sn != null) {
            JSONObject dataJo = new JSONObject();
            dataJo.put("jobName", sn.getJobName());
            dataJo.put("jobContent", sn.getJobContent());
            resJo.put("res", "success");
            resJo.put("data", dataJo);
        }else{
            resJo.put("res", "fail");
            resJo.put("errInfo", "noSn" + snStr);
        }
        return resJo;
    }

    @RequestMapping("/getLoginWx")
    private JSONObject getLoginWx(HttpServletRequest req) {
        JSONObject resJo = new JSONObject();
        String sn = req.getParameter("sn");
        List<LoginWx> loginWxList = loginWxRepository.findBySn(sn);
        if (loginWxList.size() == Integer.valueOf(config.get("loginWxNum"))) {
            resJo.put("res", "fail");
            resJo.put("errInfo", "本手机登录微信数量已达上限");
        }else {
            LoginWx loginWx = loginWxRepository.getLoginWxesBySn(sn);
            if (loginWx != null) {
                JSONObject dataJo = new JSONObject();
                dataJo.put("wxName", loginWx.getWxName());
                dataJo.put("wxPassword", loginWx.getWxPassword());
                dataJo.put("yjInfo", loginWx.getYjInfo());
                dataJo.put("wxid", loginWx.getWxid());
                loginWx.setState("正在登录");
                loginWx.setSn(sn);
                loginWx.setLastGetTime(System.currentTimeMillis()/1000);
                loginWxRepository.flush();
            }
        }
        return resJo;
    }

    @Resource
    private NewsRepository newsRepository;
    @RequestMapping("/getNews")
    private JSONObject getNews(HttpServletRequest req) {
        JSONObject resJo = new JSONObject();
        List<News> newsList = newsRepository.findAll();
        if(newsList.size() > 0) {
            News news = newsList.get(new Random().nextInt(newsList.size()) + 1);
            resJo.put("data", news);
            resJo.put("res", "success");
        }else {
            resJo.put("res", "fail");
            resJo.put("errInfo", "没有可用新闻");
        }
        return resJo;
    }

    @RequestMapping("/getPhoneAppVer")
    private JSONObject getPhoneAppVer(HttpServletRequest req) {
        JSONObject resJo = new JSONObject();
        resJo.put("res", "success");
        resJo.put("appVer", config.get("appVer"));
        return resJo;
    }

    @Resource
    private ResourceRepository resourceRepository;
    @RequestMapping("/getResource")
    private JSONObject getResource(HttpServletRequest req) {
        JSONObject resJo = new JSONObject();
        int resourcesNum = Integer.valueOf(req.getParameter("resourcesNum"));
        String type = req.getParameter("resourcesType");
        List<com.laqun.laqunserver.entity.Resource> resourceList = resourceRepository.findResourcesByType(type, resourcesNum);
        if(resourceList.size() > 0) {
            resJo.put("res", "success");
            resJo.put("data", resourceList);
        } else {
            resJo.put("res", "fail");
            resJo.put("errInfo", "没有对应资源");
        }
        return resJo;
    }

    @Resource
    private TalkChatRoomRepository talkChatRoomRepository;
    @RequestMapping("/getTalkChatRoom")
    private JSONObject getTalkChatRoom(HttpServletRequest req) {
        JSONObject resJo = new JSONObject();
        List<TalkChatRoom> talkChatRoomList = talkChatRoomRepository.findByFriendNumLessThan(35, PageRequest.of(0, 1));
        if (talkChatRoomList.size() > 0) {
            TalkChatRoom talkChatRoom = talkChatRoomList.get(0);
            resJo.put("res", "success");
            resJo.put("data", talkChatRoom);
            talkChatRoom.setFriendNum(talkChatRoom.getFriendNum() + 1);
            talkChatRoomRepository.flush();
        }else {
            resJo.put("res", "没有可用互聊群");
        }
        return resJo;
    }

    @RequestMapping("/getWxState")
    private JSONObject getWxState(HttpServletRequest req) {
        JSONObject resJo = new JSONObject();
        LoginWx loginWx = loginWxRepository.findOneByWxid(req.getParameter("wxid"));
        if (loginWx != null) {
            resJo.put("data", loginWx);
            resJo.put("res", "success");
        } else {
            resJo.put("res", "fail");
            resJo.put("errInfo", "没有这个微信");
        }
        return resJo;
    }

    @RequestMapping("/releaseAddWx")
    private JSONObject releaseAddWx(HttpServletRequest req) {
        JSONObject resJo = new JSONObject();
        String addWxStr = req.getParameter("addWx");
        AddWx addWx = addWxRepository.findOneByPhone(addWxStr);
        if (addWx != null) {
            String customer = addWx.getCustomer();
            addWxRepository.updateAddWxIsUseAndLoginWxByPhone(addWxStr);
            addWxRepository.flush();
            customerRepository.updateCustomerAddNumAndOddNumByNameRelease(customer);
            customerRepository.flush();
            LoginWx loginWx = loginWxRepository.findOneByWxid(req.getParameter("loginWx"));
            loginWx.setAddNum(loginWx.getAddNum() - 1);
            loginWxRepository.flush();
        }else {

        }

        return resJo;
    }

    @RequestMapping("/setWxIsFriend")
    private JSONObject setWxIsFriend(HttpServletRequest req) {
        JSONObject resJo = new JSONObject();
        String[] wxidArr = req.getParameter("laWxidS").split(",");
        addWxRepository.updateAddWxIsLaAndLaTimeAndLaQunIdByWxid(utils.sdf.format(new Date()), req.getParameter("qunid"), wxidArr);
        resJo.put("res", "success");

        return resJo;
    }

    @RequestMapping("/updateAddQun")
    private JSONObject updateAddQun(HttpServletRequest req) {
        JSONObject resJo = new JSONObject();
        String qunQr = req.getParameter("qunQr");
        AddQun addQun = addQunRepository.findOneByQunQr(qunQr);
        addQun.setNick(req.getParameter("nick"));
        addQun.setLaedNum(Integer.valueOf(req.getParameter("laedNum")));
        addQun.setQunid(req.getParameter("qunid"));
        addQun.setIsUse(Integer.valueOf(req.getParameter("isUse")));;
        addQun.setFriendNum(Integer.valueOf(req.getParameter("friendNum")));
        addQun.setIsBad(Integer.valueOf(req.getParameter("isBad")));
        addQun.setQunQr(req.getParameter("qunQr"));
        addQunRepository.flush();

        addWxRepository.updateAddWxIsLaAndLaTimeAndLaQunIdByWxid(utils.sdf.format(new Date()), req.getParameter("qunid"), req.getParameter("laWxidArr").split(","));
        addWxRepository.flush();

        resJo.put("res", "success");

        return resJo;
    }

    @RequestMapping("/updateAddWx")
    private JSONObject updateAddWx(HttpServletRequest req) {
        JSONObject resJo = new JSONObject();
        AddWx addWx = addWxRepository.findOneByPhone(req.getParameter("phone"));
        addWx.setWxid(req.getParameter("wxid"));
        addWx.setSex(Integer.valueOf(req.getParameter("sex")));
        addWx.setNick(req.getParameter("nick"));
        addWx.setCity(req.getParameter("city"));
        addWx.setProvince(req.getParameter("province"));
        addWx.setAvatar(req.getParameter("avatar"));
        addWxRepository.flush();

        resJo.put("res", "success");

        return resJo;
    }

    @Resource
    JobStopLogRepository jobStopLogRepository;
    @RequestMapping("/updateJob")
    private JSONObject updateJob(HttpServletRequest req) {
        JSONObject resJo = new JSONObject();
        String snStr = req.getParameter("sn");
        String jobNameStr = req.getParameter("jobName");
        Sn sn = snRepository.findBySn(snStr);
        if(sn != null) {
            sn.setJobName(jobNameStr);
            snRepository.flush();
            if(req.getParameter("jobName").equals("任务已停止")) {
                jobStopLogRepository.saveAndFlush(new JobStopLog(req.getParameter("sn"), req.getParameter("jobName"), req.getParameter("stopContent")));
            }
            resJo.put("res", "success");
        }else {
            resJo.put("res", "fail");
        }

        return resJo;
    }

    @Resource
    private LoginWxFriendChangeRepository loginWxFriendChangeRepository;
    @RequestMapping("/updateLoginWx")
    private JSONObject updateLoginWx(HttpServletRequest req) {
        JSONObject resJo = new JSONObject();
        String wxid = req.getParameter("wxid");
        LoginWx loginWx = loginWxRepository.findOneByWxid(wxid);
        String whereWxid = loginWx != null ? wxid : "_";
        loginWx = loginWxRepository.findOneByWxNameAndWxid(req.getParameter("wxName"), whereWxid);
        loginWx.setWxPassword(req.getParameter("wxPassword"));
        loginWx.setAvatarBase64(req.getParameter("avatarBase64"));
        loginWx.setNick(req.getParameter("nick"));
        loginWx.setState(req.getParameter("state"));
        loginWx.setFriendNum(Integer.valueOf(req.getParameter("friendNum")));
        loginWx.setWxid(req.getParameter("wxid"));
        loginWx.setSn(req.getParameter("sn"));
        loginWxRepository.flush();

        loginWxFriendChangeRepository.saveAndFlush(new LoginWxFriendChange(wxid, Integer.valueOf(req.getParameter("friendNum")), (int) System.currentTimeMillis()/1000));
        resJo.put("res", "success");

        return resJo;
    }

    @RequestMapping("/updateTalkChatRoom")
    private JSONObject updateTalkChatRoom(HttpServletRequest req) {
        JSONObject resJo = new JSONObject();
        TalkChatRoom talkChatRoom = talkChatRoomRepository.findOneByQunQr(req.getParameter("qunQr"));
        talkChatRoom.setQunid(req.getParameter("qunid"));
        talkChatRoom.setIsClose(Integer.valueOf(req.getParameter("isClose")));
        talkChatRoom.setNick(req.getParameter("nick"));
        talkChatRoom.setFriendNum(Integer.valueOf(req.getParameter("friendNum")));
        talkChatRoomRepository.flush();
        resJo.put("res", "success");

        return resJo;
    }

    @RequestMapping("/uploadFile")
    private JSONObject addImgResource(@RequestParam("file") MultipartFile file, @RequestParam("fileName") String fileName) {
        JSONObject resJo = new JSONObject();
        try {
            file.transferTo(new File(utils.webPath + fileName));
            resJo.put("res", "success");
        }catch (Exception e){
            resJo.put("res", "fail");
            resJo.put("errInfo", e.getMessage());
        }

        return resJo;
    }
}
