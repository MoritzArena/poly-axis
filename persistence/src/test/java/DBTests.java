import entity.Fruit;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.test.TestReactiveTransaction;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.vertx.RunOnVertxContext;
import io.quarkus.test.vertx.UniAsserter;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class DBTests {

    @Test
    @TestReactiveTransaction // enable Vert.x context
    void testCreateNewEntity(UniAsserter ua) {
        ua.execute(() -> // use UniAsserter
                Panache.withTransaction(() ->
                        new Fruit("watermelon")
                                .persistAndFlush()
                                .invoke(() -> System.out.println("Created watermelon"))
                                .onFailure().invoke(e -> System.err.println("Failed: " + e))
                )
        );
        ua.assertNotEquals(Fruit::count, 0L);
    }
}
