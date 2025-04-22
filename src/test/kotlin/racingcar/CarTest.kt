package racingcar

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CarTest {
    @Test
    fun `자동차는 움직인다`() {
        val car = Car()
        car.move()
        assertThat(car.position).isEqualTo(1)
    }
}

class Car {
    var position: Int = 0

    fun move() {
        position++
    }
}
