package pl.marim.quarkus.mp.auth_jwt;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.json.JsonString;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.eclipse.microprofile.jwt.Claim;
import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;

// Required for native image until https://github.com/quarkusio/quarkus/issues/1292 is fixed
@RegisterForReflection
@Dependent
public class LottoNumbersResource {
	
    @Inject
    JsonWebToken jwt;
    
    @Inject
    @Claim(standard = Claims.birthdate)
    Optional<JsonString> birthdate;

    @GET
    @Path("winners")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed("Subscriber")
    public LottoNumbers winners() {
        int remaining = 6;
        ArrayList<Integer> numbers = new ArrayList<>();

        if (birthdate.isPresent()) {
            JsonString bdayString = birthdate.get();
            LocalDate bday = LocalDate.parse(bdayString.getString());
            numbers.add(bday.getDayOfMonth());
            remaining --;
        }
        while(remaining > 0) {
            int pick = (int) Math.rint(64 * Math.random());
            numbers.add(pick);
            remaining --;
        }
        LottoNumbers winners = new LottoNumbers();
        winners.setNumbers(numbers);
        return winners;
    }
}
