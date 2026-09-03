package com.dacia1704.truyenonline.module.payment.mapper;

import com.dacia1704.truyenonline.module.payment.dto.response.TransactionResponse;
import com.dacia1704.truyenonline.module.payment.entity.Transaction;
import com.dacia1704.truyenonline.module.user.mapper.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = {SubscriptionPlanMapper.class, UserMapper.class})
public interface TransactionMapper {
    @Mapping(target = "subscriptionPlan", source = "subscriptionPlan")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "amountVnd", source = "vnpAmount")
    TransactionResponse toTransactionResponse(Transaction transaction);
}
