package com.walkmates.lab2;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.walkmates.model.Booking;
import com.walkmates.model.Listing;
import com.walkmates.model.Seeker;
import com.walkmates.model.TrustTier;
import com.walkmates.repository.BookingRepository;
import com.walkmates.repository.ListingRepository;
import com.walkmates.repository.ProviderRepository;
import com.walkmates.repository.SeekerRepository;
import com.walkmates.service.BookingService;
import com.walkmates.service.NotificationService;
import com.walkmates.service.PricingCalculator;

class BookingServiceTest {

    @Test
    void bookingIsRejectedWhenSeekerAlreadyHasMaximumActiveBookings() {

        // Arrange
        SeekerRepository seekers = mock(SeekerRepository.class);
        ListingRepository listings = mock(ListingRepository.class);
        ProviderRepository providers = mock(ProviderRepository.class);
        BookingRepository bookings = mock(BookingRepository.class);
        PricingCalculator pricing = mock(PricingCalculator.class);
        NotificationService notifications = mock(NotificationService.class);

        BookingService service = new BookingService(
                seekers,
                listings,
                providers,
                bookings,
                pricing,
                notifications
        );

        Seeker seeker = new Seeker(
                "seeker@example.com",
                "Test Seeker",
                "0701234567"
        );
        seeker.setTrustTier(TrustTier.NEW);

        Listing listing = mock(Listing.class);
        when(listing.isAvailable()).thenReturn(true);

        Booking existingBooking = mock(Booking.class);
        when(existingBooking.isActive()).thenReturn(true);

        when(seekers.findById("seeker-1"))
                .thenReturn(Optional.of(seeker));

        when(listings.findById("listing-1"))
                .thenReturn(Optional.of(listing));

        when(bookings.findBySeekerId("seeker-1"))
                .thenReturn(List.of(existingBooking));

        // Act + Assert
        assertThatThrownBy(
                () -> service.createBooking("seeker-1", "listing-1", 60)
        )
                .isInstanceOf(BookingService.BookingRejectedException.class)
                .hasMessageContaining("booking limit");
    }
}