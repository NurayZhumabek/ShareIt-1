package by.nuray.shareit.booking;



import by.nuray.shareit.user.User;
import by.nuray.shareit.user.UserService;
import by.nuray.shareit.util.BookingException;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/booking")
public class BookingController {


    private final BookingService bookingService;
    private final ModelMapper modelMapper;
    private final UserService userService;

    public BookingController(BookingService bookingService, ModelMapper modelMapper, UserService userService) {
        this.bookingService = bookingService;
        this.modelMapper = modelMapper;
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<BookingDTO> createBooking(
            @RequestBody @Valid BookingDTO bookingDTO,
            @RequestHeader("bookerId") int bookerId) {

        Booking booking = modelMapper.map(bookingDTO, Booking.class);

        User booker = userService.getById(bookerId);
        if (booker == null) {
            throw new BookingException("User not found.");
        }
        booking.setBooker(booker);

        Booking createdBooking = bookingService.createBooking(booking);

        return new ResponseEntity<>(modelMapper.map(createdBooking, BookingDTO.class), HttpStatus.CREATED);
    }




    @PatchMapping("/cancel/{id}")
    public ResponseEntity<BookingDTO> cancelBooking(@PathVariable("id") int bookingId,
                                    @RequestHeader("bookerId") int bookerId) {
        Booking cancelledBooking = bookingService.cancelBooking(bookingId, bookerId);
        return new ResponseEntity<>( modelMapper.map(cancelledBooking, BookingDTO.class), HttpStatus.OK);

    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<BookingDTO> updateBooking(@PathVariable int id,
                                                    @RequestHeader("X-Sharer-User-Id") int ownerId,
                                                    @RequestParam boolean updatedStatus) {
        Booking updatedBooking = bookingService.updateBookingStatus(id, updatedStatus, ownerId);
        return new ResponseEntity<>(modelMapper.map(updatedBooking, BookingDTO.class), HttpStatus.OK);
    }













}
