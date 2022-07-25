package org.codelab.batch.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.validation.BindException;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CustomerFieldSetMapper implements FieldSetMapper<Customer> {


    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Override
    public Customer mapFieldSet(FieldSet fieldSet) throws BindException {

        Customer customer = new Customer();

        /**
         * 수동으로 매핑하면 다음과 같이 firstName필드에 전체 풀네임을 합쳐서 set도 가능
         */
        //customer.setFirstName(fieldSet.readString("firstName"));
        customer.setFirstName(fieldSet.readString("firstName") + " "
                +  fieldSet.readString("middleInitial") + " "
                +  fieldSet.readString("lastName") );

        customer.setMiddleInitial(fieldSet.readString("middleInitial"));
        customer.setLastName(fieldSet.readString("lastName"));

        customer.setAddressNumber(Integer.parseInt(fieldSet.readString("addressNumber")));
        customer.setStreet(fieldSet.readString("street"));
        customer.setCity(fieldSet.readString("city"));

        customer.setState(fieldSet.readString("state"));
        customer.setZipCode(Integer.parseInt(fieldSet.readString("zipCode")));

        logger.info("firstName = " + customer.getFirstName() );
        logger.info("customer all  = " + customer.toString() );

        return customer;


    }
}
