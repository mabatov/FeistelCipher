import java.time.Instant;

/**
 * @author Мабатов Никита
 * @version 1.0
 * @see DynamicKey - Сущность - динамический ключ
 * При создании нового объекта данного класса конструктор генерирует ключ.
 */
public class DynamicKey {

    /**
     * @see DynamicKey#key - атрибут, который хранит значение сгенерированного ключа
     * @see DynamicKey#generatedTime - атрибут, хранящий текущий слепок времени
     * @see DynamicKey#generatedEpochTime - generatedTime, переведенный в UNIX-время
     */
    private String key;
    private Instant generatedTime = Instant.now();
    private long generatedEpochTime;

    /**
     * @see DynamicKey#DynamicKey() - конструктор класса. При создании нового объекта
     * - формирует все атрибуты
     */
    public DynamicKey() {
        //key = Long.toBinaryString(Instant.now().toEpochMilli());

        generatedEpochTime = Instant.now().toEpochMilli();
        key = Long.toBinaryString(generatedEpochTime);
    }

    /**
     * @see DynamicKey#getKey() - геттер
     * @return - возвращает значение сгенерированного ключа
     */
    public String getKey() {
        return key;
    }

    /**
     * @see DynamicKey#getInfo() - метод, отображающий информацию по всем сформированным
     * атрибутам в конструкторе класса:
     * @see DynamicKey
     */
    public void getInfo(){
        System.out.println("The current time is: " + generatedTime);
        System.out.println("The current epoch-time is: " + generatedEpochTime);
        System.out.println("Binary epoch-time value is: " + key);
    }
}
