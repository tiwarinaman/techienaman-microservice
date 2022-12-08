package com.naman.customer;

import com.naman.amqp.RabbitMQMessageProducer;
import com.naman.clients.fraud.FraudClient;
import com.naman.clients.notification.NotificationClient;
import com.naman.clients.notification.NotificationRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final FraudClient fraudClient;
    private final RabbitMQMessageProducer producer;

    public void registerCustomer(CustomerRegistrationRequest request) {
        Customer customer = Customer.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .build();
        // TODO: check if email is valid
        // TODO: check if email is already taken

        customerRepository.saveAndFlush(customer);

        var fraudCheckResponse =
                fraudClient.isFraudster(customer.getId());

        assert fraudCheckResponse != null;
        if (fraudCheckResponse.isFraudster()) {
            throw new IllegalStateException("fraudster");
        }

        var notificationRequest = new NotificationRequest(
                customer.getId(),
                customer.getEmail(),
                String.format("Hi %s, welcome to TechieNaman...",
                        customer.getFirstName())
        );

        producer.publish(notificationRequest,
                "internal.exchange",
                "internal.notification.routing-key");
    }

}
