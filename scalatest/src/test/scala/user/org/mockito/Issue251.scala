package user.org.mockito

import org.mockito.{ ArgumentMatchersSugar, IdiomaticMockito, MockitoSugar }
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

class Issue251 extends AnyFlatSpec with IdiomaticMockito with Matchers with ArgumentMatchersSugar {

  case class Car(id: Int)
  class VehicleService {
    private var cars = Map.empty[Int, Car]
    cars += (0 -> Car(0))

    def getCar(id: Int): Option[Car] = cars.get(id)
    def saveCar(car: Car) = {
      cars += car.id -> car;
      car
    }
  }

  it should "allow to stub checked exceptions" in {
    val vehicleService = mock[VehicleService]
    val error          = new Exception("A wild exception appears")
    error willBe thrown by vehicleService.getCar(any[Int])
  }

  it should "allow to stub checked exceptions class" in {
    val vehicleService = mock[VehicleService]
    MockitoSugar.doThrow[Exception].when(vehicleService).getCar(any[Int])
  }

}
