import entity.Fruit;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class DBTests {
    @Test
    public void testCreateNewEntity() {
        var newFruit = new Fruit("watermelon");
        newFruit.persistAndFlush().subscribe().with(
                unused -> System.out.println("New fruit created: " + newFruit.name),
                failure -> System.err.println("Failed to create fruit: " + failure.getMessage())
        );
    }
}
