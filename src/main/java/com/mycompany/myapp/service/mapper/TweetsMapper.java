package com.mycompany.myapp.service.mapper;

import com.mycompany.myapp.domain.Customer;
import com.mycompany.myapp.domain.Tweets;
import com.mycompany.myapp.service.dto.CustomerDTO;
import com.mycompany.myapp.service.dto.TweetsDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Tweets} and its DTO {@link TweetsDTO}.
 */
@Mapper(componentModel = "spring")
public interface TweetsMapper extends EntityMapper<TweetsDTO, Tweets> {
    @Mapping(target = "customer", source = "customer", qualifiedByName = "customerId")
    TweetsDTO toDto(Tweets s);

    @Named("customerId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    CustomerDTO toDtoCustomerId(Customer customer);
}
