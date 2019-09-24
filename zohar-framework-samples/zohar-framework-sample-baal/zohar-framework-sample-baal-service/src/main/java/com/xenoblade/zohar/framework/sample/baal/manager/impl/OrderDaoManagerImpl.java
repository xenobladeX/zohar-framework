package com.xenoblade.zohar.framework.sample.baal.manager.impl;

import com.xenoblade.zohar.framework.sample.baal.dao.entity.OrderDO;
import com.xenoblade.zohar.framework.sample.baal.dao.mapper.OrderMapper;
import com.xenoblade.zohar.framework.sample.baal.manager.OrderDaoManager;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author xenoblade
 * @since 2019-09-24
 */
@Service
public class OrderDaoManagerImpl extends ServiceImpl<OrderMapper, OrderDO> implements
        OrderDaoManager {

}
