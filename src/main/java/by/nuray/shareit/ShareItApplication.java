package by.nuray.shareit;

import by.nuray.shareit.booking.Booking;
import by.nuray.shareit.booking.BookingDTO;
import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ShareItApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShareItApplication.class, args);
    }




    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.typeMap(Booking.class, BookingDTO.class).addMappings(mapper -> {
            mapper.map(src -> src.getItem().getId(), BookingDTO::setItemId);
            mapper.map(src -> src.getBooker().getId(), BookingDTO::setBookerId);
        });

        return modelMapper;
    }
}
