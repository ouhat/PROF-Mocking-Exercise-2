package es.grise.upm.profundizacion.mocking.exercise2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class EngineControllerTest {

    private Logger loggerMock;
    private Speedometer speedometerMock;
    private Gearbox gearboxMock;
    private Time timeMock;
    private EngineController engineController;

    @BeforeEach
    void setup() {
        loggerMock = mock(Logger.class);
        speedometerMock = mock(Speedometer.class);
        gearboxMock = mock(Gearbox.class);
        timeMock = mock(Time.class);
        engineController = new EngineController(loggerMock, speedometerMock, gearboxMock, timeMock);
    }

    @Test
    void testRecordGear_LogMessageHasCorrectFormat() {
        // Arrange: simulamos un timestamp
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        when(timeMock.getCurrentTime()).thenReturn(timestamp);

        // Act: invocamos el método recordGear
        engineController.recordGear(GearValues.FIRST);

        // Assert: verificamos que el mensaje tiene el formato esperado
        ArgumentCaptor<String> logCaptor = ArgumentCaptor.forClass(String.class);
        verify(loggerMock).log(logCaptor.capture());
        String expectedLog = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp) + " Gear changed to FIRST";
        assertEquals(expectedLog, logCaptor.getValue());
    }

    @Test
    void testGetInstantaneousSpeed_CalculatesCorrectly() {
        // Arrange: simulamos tres lecturas de velocidad
        when(speedometerMock.getSpeed()).thenReturn(10.0, 20.0, 30.0);

        // Act: calculamos la velocidad promedio
        double speed = engineController.getInstantaneousSpeed();

        // Assert: verificamos que el promedio es correcto y que el sensor se invocó tres veces
        assertEquals(20.0, speed);
        verify(speedometerMock, times(3)).getSpeed();
    }

    @Test
    void testAdjustGear_InvokesGetInstantaneousSpeedThreeTimes() {
        // Arrange: simulamos lecturas de velocidad
        when(speedometerMock.getSpeed()).thenReturn(10.0, 20.0, 30.0);

        // Act: ajustamos la marcha
        engineController.adjustGear();

        // Assert: verificamos que getInstantaneousSpeed se invocó tres veces
        verify(speedometerMock, times(3)).getSpeed();
    }

    @Test
    void testAdjustGear_LogsNewGear() {
        // Arrange: simulamos un timestamp y lecturas de velocidad
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        when(timeMock.getCurrentTime()).thenReturn(timestamp);
        when(speedometerMock.getSpeed()).thenReturn(10.0, 20.0, 30.0);

        // Act: ajustamos la marcha
        engineController.adjustGear();

        // Assert: capturamos el mensaje de log y verificamos que tiene el formato correcto
        ArgumentCaptor<String> logCaptor = ArgumentCaptor.forClass(String.class);
        verify(loggerMock).log(logCaptor.capture());
        String expectedLog = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timestamp) + " Gear changed to SECOND";
        assertEquals(expectedLog, logCaptor.getValue());
    }

    @Test
    void testAdjustGear_AssignsCorrectGear() {
        // Arrange: simulamos lecturas de velocidad
        when(speedometerMock.getSpeed()).thenReturn(10.0, 20.0, 30.0);

        // Act: ajustamos la marcha
        engineController.adjustGear();

        // Assert: verificamos que se invocó setGear con el valor correcto
        verify(gearboxMock).setGear(GearValues.SECOND);
    }
}
