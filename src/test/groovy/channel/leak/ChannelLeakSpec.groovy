package channel.leak

import io.micronaut.http.client.DefaultHttpClient
import io.micronaut.http.client.interceptor.HttpClientIntroductionAdvice
import io.micronaut.test.annotation.MicronautTest
import io.netty.channel.pool.AbstractChannelPoolMap
import io.netty.channel.pool.FixedChannelPool
import spock.lang.Specification

import javax.inject.Inject
import java.lang.reflect.Field

@MicronautTest
class ChannelLeakSpec extends Specification {
    
    @Inject
    TestClient testClient

    @Inject
    HttpClientIntroductionAdvice introductionAdvice

    def "get price happy path"() {
        when:
        Price price = testClient.getPrice().join()

        then:
        price.value == '$5'
    }

    def "demonstrate channel leak"() {
        when:
        (1..1000).collect {
            testClient.getPrice()
        }.each {
            try {
                it.join()
            } catch (Throwable e){ }
        }

        then:
        getPool().acquiredChannelCount() == 0
    }

    FixedChannelPool getPool() {
        DefaultHttpClient dhc = introductionAdvice.clients.get("http://localhost:8080")
        AbstractChannelPoolMap poolMap = dhc.poolMap
        Field mapField = AbstractChannelPoolMap.getDeclaredField("map")
        mapField.setAccessible(true)
        Map innerMap = mapField.get(poolMap)
        return innerMap.values().first()
    }
}
