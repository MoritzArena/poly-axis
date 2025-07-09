import entity.Fruit;
import io.quarkus.test.hibernate.reactive.panache.TransactionalUniAsserter;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.vertx.RunOnVertxContext;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import repository.FruitRepository;

@QuarkusTest
public class DBTests {
    @Inject
    FruitRepository fruitRepository;

    @Test
    @RunOnVertxContext
    void testCreateNewEntity(TransactionalUniAsserter asserter) {
        asserter.execute(() -> fruitRepository.persist(new Fruit("watermelon")));
        asserter.assertEquals(Fruit::count, 4L);
    }

}
