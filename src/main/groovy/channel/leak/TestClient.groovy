package channel.leak

import io.micronaut.http.annotation.Get
import io.micronaut.http.client.annotation.Client

import java.util.concurrent.CompletableFuture

@Client("http://localhost:8080")
interface TestClient {
    @Get("/price")
    CompletableFuture<Price> getPrice()
}
