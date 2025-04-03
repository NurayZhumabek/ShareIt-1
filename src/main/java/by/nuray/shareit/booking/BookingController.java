package by.nuray.shareit.booking;


import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService bookingService;
    private final ModelMapper modelMapper;

    public BookingController(BookingService bookingService, ModelMapper modelMapper) {
        this.bookingService = bookingService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    public ResponseEntity<?> createBooking(
            @RequestBody @Valid BookingDTO bookingDTO,
            BindingResult bindingResult,
            @RequestHeader("X-Sharer-User-Id") int bookerId) {

        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(bindingResult.getAllErrors().stream()
                            .map(ObjectError::getDefaultMessage)
                            .collect(Collectors.toList()));
        }

        Booking booking = modelMapper.map(bookingDTO, Booking.class);
        bookingService.createBooking(booking, bookerId, bookingDTO.getItemId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(modelMapper.map(booking, BookingDTO.class));
    }

    @PatchMapping("/cancel/{id}")
    public ResponseEntity<BookingDTO> cancelBooking(
            @PathVariable("id") int bookingId,
            @RequestHeader("X-Sharer-User-Id") int bookerId) {

        Booking cancelledBooking = bookingService.cancelBooking(bookingId, bookerId);
        return ResponseEntity.ok(modelMapper.map(cancelledBooking, BookingDTO.class));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BookingDTO> updateBooking(
            @PathVariable int id,
            @RequestHeader("X-Sharer-User-Id") int ownerId,
            @RequestParam boolean updatedStatus) {

        Booking updatedBooking = bookingService.updateBookingStatus(id, updatedStatus, ownerId);
        return ResponseEntity.ok(modelMapper.map(updatedBooking, BookingDTO.class));
    }

    @GetMapping("/owner")
    public List<BookingDTO> getBookingByOwner(
            @RequestHeader("X-Sharer-User-Id") int ownerId,
            @RequestParam(value = "state", defaultValue = "ALL") State state,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {

        return bookingService.getBookingsByOwner(ownerId, state, from, size)
                .stream()
                .map(b -> modelMapper.map(b, BookingDTO.class))
                .collect(Collectors.toList());
    }

    @GetMapping()
    public List<BookingDTO> getBookingByBooker(
            @RequestHeader("X-Sharer-User-Id") int bookerId,
            @RequestParam(defaultValue = "ALL") State state,
            @RequestParam(defaultValue = "0") int from,
            @RequestParam(defaultValue = "10") int size) {

        return bookingService.getBookingsByBooker(bookerId, state, from, size)
                .stream()
                .map(b -> modelMapper.map(b, BookingDTO.class))
                .collect(Collectors.toList());
    }


    @GetMapping("/{id}")
    public ResponseEntity<BookingDTO> getBookingById(
            @PathVariable("id") int bookingId,
            @RequestHeader("X-Sharer-User-Id") int userId) {  // Добавлен userId

        Booking booking = bookingService.getBookingById(bookingId, userId);
        return ResponseEntity.ok(modelMapper.map(booking, BookingDTO.class));
    }
}



