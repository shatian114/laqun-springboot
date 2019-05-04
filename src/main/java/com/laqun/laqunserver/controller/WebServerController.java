package com.laqun.laqunserver.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.laqun.laqunserver.common.config;
import com.laqun.laqunserver.common.utils;
import com.laqun.laqunserver.dao.*;
import com.laqun.laqunserver.entity.*;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.util.StringUtils;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileWriter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/webServer")
public class WebServerController {

    @RequestMapping("/getGlobalConf")
    private JSONObject getGlobalConfig(HttpServletRequest req) {
        JSONObject resJo = new JSONObject();
        JSONObject dataJson = new JSONObject();
        LinkedHashMap<String, String> jsonMap = JSON.parseObject(req.getParameter("key"), new TypeReference<LinkedHashMap<String, String>>(){

        });

        for(Map.Entry<String, String> entry: jsonMap.entrySet()) {
            String k = entry.getKey();
            dataJson.put(k, config.get(k));
        }
        resJo.put("res", "success");
        resJo.put("data", dataJson);

        return resJo;
    }

    @RequestMapping("/setGlobalConf")
    private JSONObject setGlobalConfig(HttpServletRequest req) {
        JSONObject resJo = new JSONObject();
        JSONObject dataJson = new JSONObject();
        LinkedHashMap<String, String> jsonMap = JSON.parseObject(req.getParameter("confJson"), new TypeReference<LinkedHashMap<String, String>>(){

        });

        for(Map.Entry<String, String> entry: jsonMap.entrySet()) {
            String k = entry.getKey();
            String v = entry.getValue();
            log.info(k + ": " + v);
            config.set(k, v);
        }
        resJo.put("res", "success");
        resJo.put("data", dataJson);

        return resJo;
    }

    @RequestMapping("/getPhoneAppVer")
    private JSONObject getPhoneAppVer(HttpServletRequest req) {
        JSONObject resJo = new JSONObject();
        resJo.put("res", "success");
        resJo.put("appVer", config.get("appVer"));

        return resJo;
    }

    @RequestMapping("/addPhoneApp")
    private JSONObject addPhoneApp(@RequestParam("appFile") MultipartFile appFile, @RequestParam("appVer") String appVer) {
        JSONObject resJo = new JSONObject();
        try{
            appFile.transferTo(new File(utils.webPath + "app.apk"));
            config.set("appVer", appVer);
            resJo.put("res", "success");
            resJo.put("appVer", config.get("appVer"));
        }catch (Exception e){
            log.error("存储app文件出错：" + e.getMessage());
            resJo.put("res", "fail");
            resJo.put("errInfo", "存储app文件出错：" + e.getMessage());
        }

        return resJo;
    }

    @javax.annotation.Resource
    private SnRepository snRepository;
    @RequestMapping("/addSn")
    private JSONObject addSn(HttpServletRequest req) {
        JSONObject resJo = new JSONObject();
        String[] snArr = req.getParameter("snArr").split(",");
        int saveSn = 0;
        for(int i=0; i<snArr.length; i++) {
            try {
                snRepository.saveAndFlush(new Sn(snArr[i].trim()));
                saveSn++;
            }catch (Exception e){

            }
        }
        snRepository.flush();
        resJo.put("saveSnCount", saveSn);

        return resJo;
    }
    @RequestMapping("/getSn")
    private JSONObject getSn(HttpServletRequest req) {
        JSONObject resJo = new JSONObject();
        List<Sn> snList = snRepository.findBySnContains(req.getParameter("sn"));
        if (snList == null) {
            resJo.put("res", "fail");
            resJo.put("errInfo", "获取sn错误");
        }else{
            resJo.put("res", "success");
            resJo.put("data", snList);
        }
        return resJo;
    }
    @RequestMapping("/setSnRemark")
    private JSONObject setSnRemark(HttpServletRequest req) {
        JSONObject resJo = new JSONObject();
        Sn sn = snRepository.findBySn(req.getParameter("sn"));
        sn.setRemark(req.getParameter("remark"));
        snRepository.flush();
        resJo.put("res", "success");
        return resJo;
    }
    @RequestMapping("/delSn")
    private JSONObject delSn(HttpServletRequest req) {
        JSONObject resJo = new JSONObject();
        try {
            snRepository.deleteBySn(req.getParameter("sn"));
            resJo.put("res", "success");
        }catch (Exception e){
            resJo.put("res", "fail");
            resJo.put("errInfo", e.getMessage());
        }
        return resJo;
    }
    @RequestMapping("/stopJob")
    private JSONObject stopJob(HttpServletRequest req) {
        JSONObject resJo = new JSONObject();
        Sn sn = snRepository.findBySn(req.getParameter("sn"));
        if(sn != null) {
            sn.setJobName("任务已停止");
            sn.setJobContent("");
            snRepository.flush();
            resJo.put("res", "success");
        }else{
            resJo.put("res", "fail");
            resJo.put("errInfo", "sn不存在");
        }
        return resJo;
    }
    @RequestMapping("/releaseJob")
    private JSONObject releaseJob(HttpServletRequest req) {
        JSONObject resJo = new JSONObject();
        log.info("snArr: " + req.getParameter("snArr"));
        String[] snArr = req.getParameter("snArr").split(",");
        snRepository.setSnByJobNameAndSnIn(req.getParameter("job"), req.getParameter("jobContent"), snArr);
        snRepository.flush();
        resJo.put("res", "success");
        return resJo;
    }

    @javax.annotation.Resource
    private SnGroupRepository snGroupRepository;
    @RequestMapping("/addSnGroup")
    private JSONObject addSnGroup(HttpServletRequest req) {
        JSONObject resJo = new JSONObject();
        String groupName = req.getParameter("snGroupName");
        String groupMember = req.getParameter("snArr");
        switch (req.getParameter("operateType")) {
            case "change":
                SnGroup snGroup = snGroupRepository.getSnGroupById(Long.valueOf(req.getParameter("snGroupId")));
                snGroup.setGroupName(groupName);
                snGroup.setGroupMember(groupMember);
                snGroupRepository.flush();
                resJo.put("res", "success");
                break;
            case "add":
                try {
                    snGroupRepository.saveAndFlush(new SnGroup(groupName, groupMember));
                    resJo.put("res", "success");
                }catch (Exception e){
                    resJo.put("res", "fail");
                    resJo.put("errInfo", e.getMessage());
                    e.printStackTrace();
                }
                break;
        }

        return resJo;
    }
    @RequestMapping("/delSnGroup")
    private JSONObject delSnGroup(HttpServletRequest req) {
        JSONObject resJo = new JSONObject();
        try {
            snGroupRepository.deleteByGroupName(req.getParameter("groupName"));
            resJo.put("res", "success");
        }catch (Exception e){
            resJo.put("res", "fail");
            resJo.put("errInfo", e.getMessage());
        }
        return resJo;
    }
    @RequestMapping("/getSnGroup")
    private JSONObject getSnGroup(HttpServletRequest req) {
        JSONObject resJo = new JSONObject();
        String groupName = req.getParameter("groupName");
        List<SnGroup> snGroupList = snGroupRepository.findByGroupNameContains(groupName);
        if(snGroupList != null) {
            resJo.put("res", "success");
            resJo.put("data", snGroupList);
        }else{
            resJo.put("res", "fail");
            resJo.put("errInfo", "获取sn组失败");
        }
        return resJo;
    }

    @javax.annotation.Resource
    private IpConfRepository ipConfRepository;
    @RequestMapping("/getIp")
    private JSONObject getIp(HttpServletRequest req) {
        JSONObject resJo = new JSONObject();
        String ipAddr = req.getParameter("ipAddr");
        List<IpConf> ipConfList = ipConfRepository.findByIpAddrContains(ipAddr);
        if(ipConfList != null) {
            resJo.put("res", "success");
            resJo.put("data", ipConfList);
        }else{
            resJo.put("res", "fail");
            resJo.put("errInfo", "获取ip错误");
        }
        return resJo;
    }
    @RequestMapping("/delIp")
    private JSONObject delIp(HttpServletRequest req) {
        JSONObject resJo = new JSONObject();
        try{
            ipConfRepository.deleteByIpAddr(req.getParameter("ipAddr"));
            resJo.put("res", "success");
        }catch (Exception e){
            resJo.put("res", "fail");
            resJo.put("errInfo", e.getMessage());
        }
        return resJo;
    }

    @javax.annotation.Resource
    private LoginWxRepository loginWxRepository;
    @RequestMapping("/addLoginWx")
    private JSONObject addLoginWx(HttpServletRequest req) {
        JSONObject resJo = new JSONObject();
        int lastGetTime = (int) ((System.currentTimeMillis() / 1000) - ((long) (Integer.valueOf(config.get("loginWxUseTime")).intValue() * 60)));
        String[] loginWxArr = req.getParameter("loginWxArr").split(",");
        int addCount = 0;
        for(int i=0; i<loginWxArr.length; i++) {
            String[] loginWxParamArr = loginWxArr[i].split("----");
            loginWxRepository.saveAndFlush(new LoginWx(loginWxParamArr[0], loginWxParamArr[1], loginWxParamArr[2], loginWxParamArr[3], lastGetTime));
            addCount++;
        }
        resJo.put("res", "success");
        resJo.put("saveWxCount", addCount);
        return resJo;
    }

    @RequestMapping("/getLoginWx")
    private JSONObject getLoginWx(HttpServletRequest req) {
        JSONObject resJo = new JSONObject();
        List<LoginWx> loginWxList = loginWxRepository.findByWxNameContains(req.getParameter("wxName"));
        if(loginWxList == null) {
            resJo.put("res", "fail");
            resJo.put("errInfo", "获取登录微信错误");
        }else{
            resJo.put("res", "success");
            resJo.put("data", loginWxList);
        }
        return resJo;
    }
    @RequestMapping("/delLoginWx")
    private JSONObject delLoginWx(HttpServletRequest req) {
        JSONObject resJo = new JSONObject();
        try{
            loginWxRepository.deleteByWxid(req.getParameter("wxid"));
            resJo.put("res", "success");
        }catch (Exception e){
            resJo.put("res", "fail");
            resJo.put("errInfo", e.getMessage());
        }
        return resJo;
    }

    @RequestMapping("/operateLoginWx")
    private JSONObject operateLoginWx(HttpServletRequest req) {
        JSONObject resJo = new JSONObject();
        switch (req.getParameter("operateType")) {
            case "del":
                loginWxRepository.deleteByState(req.getParameter("wxState"));
                resJo.put("res", "success");
                break;
            case "download":
                List<LoginWx> loginWxList = loginWxRepository.findByStateContains(req.getParameter("wxState"));
                if(loginWxList == null) {
                    resJo.put("res", "fail");
                    resJo.put("errInfo", "获取指定状态的登录微信错误");
                }else{
                    try {
                        FileWriter fw = new FileWriter(utils.webPath + "loginWx.txt");
                        for(int i=0; i<loginWxList.size(); i++) {
                            LoginWx loginWx = loginWxList.get(i);
                            fw.append(loginWx.getWxName() + "----" + loginWx.getWxPassword() + "\r\n");
                            fw.flush();
                        }
                        fw.flush();
                        fw.close();
                        resJo.put("res", "success");
                    }catch (Exception e){
                        resJo.put("res", "fail");
                        resJo.put("errInfo", "文件写入到本地错误");
                    }
                }
                break;
        }
        return resJo;
    }

    @javax.annotation.Resource
    private NewsRepository newsRepository;
    @RequestMapping("/addNews")
    private JSONObject addNews(HttpServletRequest req) {
        JSONObject resJo = new JSONObject();
        String[] newsArr = req.getParameter("newsArr").split(",");
        int addCount = 0;
        for(int i=0; i<newsArr.length; i++) {
            try {
                String[] newsParamArr = newsArr[i].trim().split(" ");
                newsRepository.saveAndFlush(new News(newsParamArr[0], newsParamArr[1]));
                addCount++;
            }catch (Exception e){

            }
        }
        resJo.put("saveNewsCount", addCount);

        return resJo;
    }
    @RequestMapping("/getNews")
    private JSONObject getNews(HttpServletRequest req) {
        JSONObject resJo = new JSONObject();
        List<News> newsList = newsRepository.findByNewsNameContains(req.getParameter("newsName"));
        if (newsList == null) {
            resJo.put("res", "fail");
            resJo.put("errInfo", "获取新闻错误");
        }else{
            resJo.put("res", "success");
            resJo.put("data", newsList);
        }
        return resJo;
    }
    @RequestMapping("/delNews")
    private JSONObject delNews(HttpServletRequest req) {
        JSONObject resJo = new JSONObject();
        try{
            newsRepository.deleteByNewsName(req.getParameter("newsName"));
            resJo.put("res", "success");
        }catch (Exception e){
            resJo.put("res", "fail");
            resJo.put("errInfo", e.getMessage());
        }
        return resJo;
    }

    @javax.annotation.Resource
    private TalkChatRoomRepository talkChatRoomRepository;
    @RequestMapping("/addTalkChatRoom")
    private JSONObject addTalkChatRoom(HttpServletRequest req) {
        JSONObject resJo = new JSONObject();
        String[] qunQrArr = req.getParameter("qunQrArr").split(",");
        int addCount = 0;
        for(int i=0; i<qunQrArr.length; i++) {
            try {
                talkChatRoomRepository.saveAndFlush(new TalkChatRoom(qunQrArr[i].trim()));
                addCount++;
            }catch (Exception e){

            }
        }
        resJo.put("saveQunQrCount", addCount);

        return resJo;
    }
    @RequestMapping("/getTalkChatRoom")
    private JSONObject getTalkChatRoom(HttpServletRequest req) {
        JSONObject resJo = new JSONObject();
        List<TalkChatRoom> talkChatRoomList = talkChatRoomRepository.findByQunQrContains(req.getParameter("qunQr"));
        if (talkChatRoomList == null) {
            resJo.put("res", "fail");
            resJo.put("errInfo", "获取群聊错误");
        }else{
            resJo.put("res", "success");
            resJo.put("data", talkChatRoomList);
        }
        return resJo;
    }
    @RequestMapping("/delTalkChatRoom")
    private JSONObject delTalkChatRoom(HttpServletRequest req) {
        JSONObject resJo = new JSONObject();
        try{
            talkChatRoomRepository.deleteByQunQr(req.getParameter("qunQr"));
            resJo.put("res", "success");
        }catch (Exception e){
            resJo.put("res", "fail");
            resJo.put("errInfo", e.getMessage());
        }
        return resJo;
    }

    @javax.annotation.Resource
    private ResourceRepository resourceRepository;
    @RequestMapping("/addTextResources")
    private JSONObject addTextResource(HttpServletRequest req) {
        JSONObject resJo = new JSONObject();
        String[] resourceArr = req.getParameter("resourceArr").split(",");
        String resourceType = req.getParameter("resourceType");
        //先清空对应的资源
        resourceRepository.deleteByType(resourceType);
        int addCount = 0;
        for(int i=0; i<resourceArr.length; i++) {
            try {
                resourceRepository.saveAndFlush(new Resource(resourceArr[i].trim(), resourceType));
                addCount++;
            }catch (Exception e){

            }
        }
        resJo.put("saveCount", addCount);

        return resJo;
    }
    @RequestMapping("/clearImg")
    private JSONObject clearImg(HttpServletRequest req) {
        JSONObject resJo = new JSONObject();
        String resourceType = req.getParameter("resourceType");
        resourceRepository.deleteByType(resourceType);
        File[] fArr = new File(utils.webPath + resourceType).listFiles();
        for(File f: fArr) {
            f.deleteOnExit();
        }
        resJo.put("res", "success");

        return resJo;
    }
    @RequestMapping("/addImgResources")
    private JSONObject addImgResource(@RequestParam("imgFile") MultipartFile imgFile, @RequestParam("resourceType") String resourceType) {
        JSONObject resJo = new JSONObject();
        String fileName = UUID.randomUUID().toString();
        try{
            imgFile.transferTo(new File(utils.webPath + resourceType + File.separator + fileName + ".jpg"));
            resourceRepository.saveAndFlush(new Resource(fileName, resourceType));
            resJo.put("res", "success");
        }catch (Exception e){
            resJo.put("res", "fail");
            resJo.put("errInfo", "存储图片资源出错");
        }
        return resJo;
    }

    @RequestMapping("/getResources")
    private JSONObject getResources(HttpServletRequest req) {
        JSONObject resJo = new JSONObject();
        List<Resource> textResourceList = resourceRepository.findByType(req.getParameter("resourcesType"));
        if (textResourceList == null) {
            resJo.put("res", "fail");
            resJo.put("errInfo", "获取群聊错误");
        }else{
            resJo.put("res", "success");
            resJo.put("data", textResourceList);
        }
        return resJo;
    }
    @RequestMapping("/getImgList")
    private JSONObject getImgList(HttpServletRequest req) {
        JSONObject resJo = new JSONObject();
        List<Resource> textResourceList = resourceRepository.findByType(req.getParameter("resourcesType"), PageRequest.of(0, 20));
        if (textResourceList == null) {
            resJo.put("res", "fail");
            resJo.put("errInfo", "获取图片资源错误");
        }else{
            resJo.put("res", "success");
            resJo.put("data", textResourceList);
        }
        return resJo;
    }
    @RequestMapping("/delResources")
    private JSONObject delResources(HttpServletRequest req) {
        JSONObject resJo = new JSONObject();
        try {
            resourceRepository.deleteByTypeAndVal(req.getParameter("type"), req.getParameter("val"));
            resJo.put("res", "success");
        }catch (Exception e){
            resJo.put("res", "fail");;
            resJo.put("errInfo", e.getMessage());
        }
        return resJo;
    }

    @javax.annotation.Resource
    private AddWxRepository addWxRepository;
    @RequestMapping("/addAddWx")
    private JSONObject addAddWx(HttpServletRequest req) {
        JSONObject resJo = new JSONObject();
        String customer = req.getParameter("customer");
        int priority = Integer.valueOf(req.getParameter("priority"));
        String[] addWxArr = req.getParameter("addWxArr").split(",");
        int addCount = 0;
        for(int i=0; i<addWxArr.length; i++) {
            try {
                addWxRepository.saveAndFlush(new AddWx(addWxArr[i].trim(), priority, customer));
                addCount++;
            }catch (Exception e){

            }
        }
        resJo.put("res", "success");
        resJo.put("addCount", addCount);
        customerRepository.updateOddNumByName(addCount, customer);
        return resJo;
    }
    @RequestMapping("/getAddWx")
    private JSONObject getAddWx(HttpServletRequest req) {
        JSONObject resJo = new JSONObject();
        String phone = req.getParameter("searchPhone");
        String customer = req.getParameter("customer");
        List<AddWx> addWxList = addWxRepository.findByCustomerAndPhoneContains(customer, phone);
        if(addWxList == null) {
            resJo.put("res", "fail");
            resJo.put("errInfo", "获取添加微信出错");
        }else {
            resJo.put("res", "success");
            resJo.put("data", addWxList);
        }

        return resJo;
    }
    @RequestMapping("/delAddWx")
    private JSONObject delAddWx(HttpServletRequest req) {
        JSONObject resJo = new JSONObject();
        String phone = req.getParameter("searchPhone");
        try {
            addWxRepository.deleteByPhone(phone);
            resJo.put("res", "success");
        }catch (Exception e){
            resJo.put("res", "fail");
            resJo.put("errInfo", e.getMessage());
        }

        return resJo;
    }

    @javax.annotation.Resource
    private AddQunRepository addQunRepository;
    @RequestMapping("/addLaQun")
    private JSONObject addLaQun(HttpServletRequest req) {
        JSONObject resJo = new JSONObject();
        int qunUseTimeout = Integer.valueOf(config.get("qunUseTimeout")).intValue();
        int lastGetTime = (int) ((System.currentTimeMillis() / 1000) - ((long) (qunUseTimeout * 60)));
        int priority = Integer.valueOf(req.getParameter("priority"));
        String customer = req.getParameter("customer");
        String[] qunQrArr = req.getParameter("qunQrArr").split(",");
        int addCount = 0;
        for(int i=0; i<qunQrArr.length; i++) {
            String[] qunQrParamArr = qunQrArr[i].trim().split("----");
            try {
                addQunRepository.saveAndFlush(new AddQun(qunQrParamArr[0], priority, customer, Integer.valueOf(qunQrParamArr[1]), lastGetTime));
                addCount++;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        resJo.put("res", "success");
        resJo.put("addCount", addCount);
        return resJo;
    }
    @RequestMapping("/getLaQun")
    private JSONObject getLaQun(HttpServletRequest req) {
        JSONObject resJo = new JSONObject();
        String customer = req.getParameter("customer");
        String phone = req.getParameter("searchPhone");
        List<AddQun> addQunList = addQunRepository.findByQunQrContains(phone);
        if(addQunList == null) {
            resJo.put("res", "fail");
            resJo.put("errInfo", "获取拉群信息出错");
        }else {
            resJo.put("res", "success");
            resJo.put("data", addQunList);
        }

        return resJo;
    }
    @RequestMapping("/delLaQun")
    private JSONObject delLaQun(HttpServletRequest req) {
        JSONObject resJo = new JSONObject();
        try {
            addQunRepository.deleteByQunQr(req.getParameter("phone"));
            resJo.put("res", "success");
        }catch (Exception e){
            resJo.put("res", "fail");
            resJo.put("errInfo", e.getMessage());
        }

        return resJo;
    }

    @javax.annotation.Resource
    private CustomerRepository customerRepository;
    @RequestMapping("/addCustomer")
    private JSONObject addCustomer(HttpServletRequest req) {
        JSONObject resJo = new JSONObject();
        String name = req.getParameter("customerName");
        try{
            List<Customer> l = customerRepository.findByName(name);
            if (l.size() == 0) {
                customerRepository.saveAndFlush(new Customer(name));
                resJo.put("res", "success");
            }else{
                resJo.put("res", "fail");
                resJo.put("errInfo", "此客户名称已存在");
            }
        }catch (Exception e){
            resJo.put("res", "fail");
            resJo.put("errInfo", e.getMessage());
            e.printStackTrace();
        }

        return resJo;
    }
    @RequestMapping("/getCustomer")
    private JSONObject getCustomer(HttpServletRequest req) {
        JSONObject resJo = new JSONObject();
        String name = req.getParameter("customerName");
        try{
            List<Customer> l = customerRepository.findByNameContains(name);
            resJo.put("res", "success");
            resJo.put("data", l);
        }catch (Exception e){
            resJo.put("res", "fail");
            resJo.put("errInfo", e.getMessage());
            e.printStackTrace();
        }

        return resJo;
    }
    @RequestMapping("/delCustomer")
    private JSONObject delCustomer(HttpServletRequest req) {
        JSONObject resJo = new JSONObject();
        String name = req.getParameter("customerName");
        try{
            customerRepository.deleteByName(name);
            resJo.put("res", "success");
        }catch (Exception e){
            resJo.put("res", "fail");
            resJo.put("errInfo", e.getMessage());
            e.printStackTrace();
        }

        return resJo;
    }

}
