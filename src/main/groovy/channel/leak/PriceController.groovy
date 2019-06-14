package channel.leak

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get

@Controller
class PriceController {
    
    @Get("/price")
    Price getPrice() {
        new Price(value: '$5')
    }
}
