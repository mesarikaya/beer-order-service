package com.mes.beerordermicroservice.statemachine.actions;

import com.mes.beerordermicroservice.config.JmsConfig;
import com.mes.beerordermicroservice.domain.BeerOrder;
import com.mes.beerordermicroservice.domain.BeerOrderEventEnum;
import com.mes.beerordermicroservice.domain.BeerOrderStatusEnum;
import com.mes.beerordermicroservice.repositories.BeerOrderRepository;
import com.mes.beerordermicroservice.services.BeerOrderManagerImpl;
import com.mes.beerordermicroservice.web.mappers.BeerOrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Created by mesar on 2/28/2020
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class AllocateOrderAction implements Action<BeerOrderStatusEnum, BeerOrderEventEnum> {

    private BeerOrderRepository beerOrderRepository;
    private final BeerOrderMapper beerOrderMapper;
    private final JmsTemplate jmsTemplate;

    @Override
    public void execute(StateContext<BeerOrderStatusEnum, BeerOrderEventEnum> stateContext) {
        String beerOrderId = (String) stateContext.getMessage().getHeaders().get(BeerOrderManagerImpl.ORDER_ID_HEADER);
        BeerOrder beerOrder = beerOrderRepository.findOneById(UUID.fromString(beerOrderId));

        jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_ORDER_QUEUE, beerOrderMapper.beerOrderToDto(beerOrder));
        log.debug("Send Allocation Request to queue for order id " + beerOrderId);
    }
}
