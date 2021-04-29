package com.cbec.order.service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.cbec.internal.api.auth.PlatformAccountDTO;
import com.cbec.internal.api.messager.MessageDTO;
import com.cbec.internal.api.ecommerce_facade.OrderDTO;
import com.cbec.order.dao.entity.OrderEntity;
import com.cbec.order.feign.MessageApiFeign;
import com.cbec.order.feign.OrderSpiderApiFeign;
import com.cbec.order.feign.PlatformAccountApiFeign;
import com.cbec.order.feign.UserApiFeign;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfig;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UnhandledOrderNotifyService {
    private static final String TEMPLATE_NAME = "unhandled-order.html";

    @Autowired
    private PlatformAccountApiFeign platformAccountApiFeign;
    @Autowired
    private UserApiFeign userApiFeign;
    @Autowired
    private OrderSpiderApiFeign orderSpiderApiFeign;
    @Autowired
    private OrderService orderService;
    @Autowired
    private MessageApiFeign messageApiFeign;
    @Autowired
    private FreeMarkerConfig freeMarkerConfig;

    @Value("${notify.internalHour:3}")
    private int notifyInternalHour;

    @Transactional(rollbackFor = Exception.class)
    public void scanOrder() {
        log.info("Start scan......");

        var accountList = platformAccountApiFeign.listAllUserPlatformAccount();
        if (CollectionUtils.isEmpty(accountList)) {
            log.info("No account, skip");
            return;
        }

        accountList.forEach(this::syncUserUnhandledOrder);
        accountList.forEach(this::notifyUserNewOrder);
    }

    private void syncUserUnhandledOrder(PlatformAccountDTO account) {
        var orderList = orderSpiderApiFeign.listUnhandledOrder(account.getPlatform(), account.getApiToken());
        if (CollectionUtils.isEmpty(orderList)) {
            orderService.delete(new EntityWrapper<OrderEntity>()
                    .eq("user", account.getUser())
                    .eq("platform_account", account.getPlatformUser()));
            return;
        }

        // 删除不存在的订单
        var orderIdList = orderList.stream().map(OrderDTO::getId).collect(Collectors.toList());
        orderService.delete(new EntityWrapper<OrderEntity>()
                .eq("user", account.getUser())
                .eq("platform_account", account.getPlatformUser())
                .notIn("id", orderIdList));

        var orderEntityList = orderList.stream()
                .map(s -> OrderEntity.getConverter().doBackward(s))
                .peek(s -> s.setPlatform(account.getPlatform()))
                .peek(s -> s.setPlatformAccount(account.getPlatformUser()))
                .peek(s -> s.setUser(account.getUser()))
                .collect(Collectors.toList());
        orderService.insertOrUpdateBatch(orderEntityList);

        log.info("发现用户{}的{}个未处理订单", account.getPlatformUser(), orderEntityList.size());
    }

    private void notifyUserNewOrder(PlatformAccountDTO platformAccountDTO) {
        String user = platformAccountDTO.getUser();
        String platformAccount = platformAccountDTO.getPlatformUser();

        if (!hasUnNotifyOrder(user, platformAccount)) {
            return;
        }

        // 查询用户订单
        var orderList = orderService.selectList(new EntityWrapper<OrderEntity>()
                .eq("user", user)
                .eq("platform_account", platformAccount)
                .orderBy("add_time", false));
        if (CollectionUtils.isEmpty(orderList)) {
            return;
        }

        var userInfo = userApiFeign.getUserInfo(user);
        var messageDTO = new MessageDTO(userInfo.getEmail(), "跨境电商: 未处理订单通知消息",
                buildNotifyMessage(user, platformAccount, orderList));
        messageApiFeign.sendMail(messageDTO);

        log.info("通知用户{}下的帐号{}有{}个未处理的订单", user, platformAccount, orderList.size());

        // 更新最后通知时间
        var newOrderList = orderList.stream()
                .peek(s -> s.setLastNotifyTime(new Date()))
                .collect(Collectors.toList());
        orderService.updateBatchById(newOrderList);
    }

    private boolean hasUnNotifyOrder(String user, String platformAccount) {
        return orderService.selectCount(new EntityWrapper<OrderEntity>()
                .eq("user", user)
                .eq("platform_account", platformAccount)
                .isNull("last_notify_time")) > 0;
    }

    private String buildNotifyMessage(String user, String platformAccount, List<OrderEntity> orderDTOList) {
        if (CollectionUtils.isEmpty(orderDTOList)) {
            return "";
        }

        String title = String.format("您的账户[%s - %s]有%d个未处理订单，请及时处理！", user, platformAccount, orderDTOList.size());
        Map<String, Object> model = new HashMap<>();
        model.put("title", title);
        model.put("orders", orderDTOList);

        try {
            var template = freeMarkerConfig.getConfiguration().getTemplate(TEMPLATE_NAME);
            return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
