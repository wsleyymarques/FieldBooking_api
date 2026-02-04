package dev.wesley.fieldbooking.service;

import dev.wesley.fieldbooking.config.RabbitConfig;
import dev.wesley.fieldbooking.dto.BookingCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookingCommandListener {

    private final BookingService bookingService;

    @RabbitListener(queues = RabbitConfig.BOOKING_COMMAND_QUEUE)
    public void handleCreateHold(BookingCommand command) {
        bookingService.createHoldFromCommand(command);
    }
}
