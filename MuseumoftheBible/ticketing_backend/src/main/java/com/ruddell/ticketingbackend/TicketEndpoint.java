package com.ruddell.ticketingbackend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;

import java.util.UUID;
import java.util.logging.Logger;

/**
 * An endpoint class we are exposing
 */
@Api(
        name = "ticketApi",
        version = "v1",
        resource = "ticket",
        namespace = @ApiNamespace(
                ownerDomain = "ticketingbackend.ruddell.com",
                ownerName = "ticketingbackend.ruddell.com",
                packagePath = ""
        )
)
public class TicketEndpoint {

    private static final double COST_PER_TICKET = 12.00;
    private static final Logger logger = Logger.getLogger(TicketEndpoint.class.getName());

    @ApiMethod(name = "getCostPerTicket")
    public DoubleHolder getCostPerTicket() {
        DoubleHolder response = new DoubleHolder();
        response.setData(COST_PER_TICKET);
        return response;
    }

    /**
     * This purchases a new <code>Ticket</code> object.
     *
     * @param purchaseObject The object to be added.
     * @return The ticket purchased.
     */
    @ApiMethod(name = "purchaseTicket")
    public Ticket purchaseTicket(PurchaseObject purchaseObject) {
        Ticket ticket = new Ticket();

        double amount = purchaseObject.getNumberOfTickets() * COST_PER_TICKET;
        String token = makePayment(purchaseObject.mCardNumber, purchaseObject.getExpiration(), purchaseObject.getCvc(), amount);
        String ticketUrl = getTicket(token);

        ticket.setTicketUrl(ticketUrl);

        return ticket;
    }


    private String makePayment(String cardNumber, String expiration, String cvc, double amount) {
        String token = UUID.randomUUID().toString();
        return token;
    }

    private String getTicket(String token) {
        String ticketUrl = "https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=" + token;
        return ticketUrl;
    }
}