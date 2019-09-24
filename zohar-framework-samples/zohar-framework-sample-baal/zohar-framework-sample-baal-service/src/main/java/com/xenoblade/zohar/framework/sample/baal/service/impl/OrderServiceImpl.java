/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xenoblade.zohar.framework.sample.baal.service.impl;

import cn.hutool.core.lang.Snowflake;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.xenoblade.zohar.framework.commons.log.api.annotation.AccessLogger;
import com.xenoblade.zohar.framework.sample.baal.api.dto.OrderDTO;
import com.xenoblade.zohar.framework.sample.baal.api.service.OrderService.GetOrderRequest;
import com.xenoblade.zohar.framework.sample.baal.api.service.OrderService.SaveOrderRequest;
import com.xenoblade.zohar.framework.sample.baal.converter.OrderConverter;
import com.xenoblade.zohar.framework.sample.baal.dao.entity.OrderDO;
import com.xenoblade.zohar.framework.sample.baal.manager.OrderDaoManager;
import com.xenoblade.zohar.framework.sample.baal.service.IOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * OrderServiceImpl
 * @author xenoblade
 * @since 1.0.0
 */
@Service
@AccessLogger
@Slf4j
public class OrderServiceImpl implements IOrderService{

    @Autowired
    private Snowflake snowflake;

    @Autowired
    private OrderDaoManager orderDaoManager;

    @Override public OrderDTO getOrder(GetOrderRequest getOrderRequest) {
        OrderDO orderDO = orderDaoManager.getOne(Wrappers.<OrderDO>lambdaQuery().eq(OrderDO::getOrderId, getOrderRequest.getOrderId()));
        return OrderConverter.INSTANCE.orderDOToOrderDTO(orderDO);
    }

    @Override public OrderDTO saveOrder(SaveOrderRequest saveOrderRequest) {
        OrderDO orderDO = new OrderDO()
                .setUserId(saveOrderRequest.getUserId())
                .setUserName(saveOrderRequest.getUserName())
                .setOrderId(snowflake.nextId());

        orderDaoManager.save(orderDO);
        return OrderConverter.INSTANCE.orderDOToOrderDTO(orderDO);
    }
}
