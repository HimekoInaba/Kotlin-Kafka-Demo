import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.ser.std.StringSerializer
import com.fasterxml.jackson.databind.util.StdDateFormat
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.github.javafaker.Faker
import lombok.extern.slf4j.Slf4j
import model.Person
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.clients.producer.Producer
import org.apache.kafka.clients.producer.ProducerRecord
import java.util.*
import java.util.logging.LogManager

public fun main(args: Array<String>) {

}

@Slf4j
class KafkaProducer {
    private fun createProducer(brokers: String): Producer<String, String> {
        val props = Properties()
        props["bootstrap.servers"] = brokers
        props["key.serializer"] = StringSerializer::class.java
        props["value.serializer"] = StringSerializer::class.java
        return KafkaProducer<String, String>(props)
    }

    val faker = Faker()
    val fakePerson = Person(
        firstName = faker.name().firstName(),
        lastName = faker.name().lastName(),
        birthDate = faker.date().birthday()
    )

    val jsonMapper = ObjectMapper().apply {
        registerKotlinModule()
        disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        dateFormat = StdDateFormat()
    }

    val fakePersonJson = jsonMapper.writeValueAsString(fakePerson)

    var producer = createProducer("localhost:9092")
    var personsTopic = "localhost:9090"

    private fun makeRequest() {
        val futureResult = producer.send(ProducerRecord(personsTopic, fakePersonJson))
        futureResult.get()
    }
}

