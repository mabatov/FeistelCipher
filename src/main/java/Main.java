import java.io.IOException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * @author Мабатов Никита
 * @version 1.0
 * @see Main - Класс, содержащий в себе все операции шифрования
 * При создании нового объекта данного класса конструктор генерирует ключ.
 */
public class Main {

    /**
     * @see Main#mtk_2_map - структура данных, хранящая в себе телеграфный трёхрегистровый код МТК-2
     * @see Main#word - атрибут, хранящий набор символов, которые необходимо зашифровать
     * @see Main#letters - структура данных, содержащая разбитое по символам слово
     * @see Main#values - структура данных, содержащая двоичный код для каждого символа из предыдущего массива
     * @see Main#subBlockLength - константа, хранящая количество символов в каждом подблоке
     * @see Main#keyList - структура данных, хранащая сгеннрированные на каждом уровне ключи
     * @see Main#blockL - атрибут, хранящий набор битов левого блока на текущем раунде
     * @see Main#blockR - атрибут, хранящий набор битов правого блока на текущем раунде
     * @see Main#encryptResult - атрибут, хранящий набор битов в результате шифрования
     * @see Main#encryptResultList - структура данных, хранящая набор битов в результате шифрования (посимвольно)
     * @see Main#encryptResultValuesList - структура данных, хранящая набор зашифрованных символов
     */
    private static Map<String, String> mtk_2_map = new HashMap<>();
    private static String word = "БОНЧ-БРУЕВИЧ";
    private static String[] letters = splitWordOnLetters(word);
    private static String[] values = new String[letters.length];
    private static final int subBlockLength = word.length() / 2 * 5;
    private static List<String> keyList = new ArrayList<>();
    private static StringBuilder blockL;
    private static StringBuilder blockR;
    private static String encryptResult;
    private static List<String> encryptResultList = new ArrayList<>();
    private static List<String> encryptResultValuesList = new ArrayList<>();

    /**
     * @see Main#main(String[] args) - основной метод, который служит точкой входа в программу
     * @param args
     * @throws IOException
     *
     * @see Main#start() - вызов метода, который содержит основную логику программы
     */
    public static void main(String[] args) throws IOException {

        start();
    }

    /**
     * @see Main#start() - метод, который инициализирует массив МТК-2,
     * переводит исходное слово в набор символов, затем переводит их
     * в биты. После чего пользователем указывается количество раундов
     * и запускается алгоритм сети Фейстеля.
     */
    public static void start(){

        /**
         * @see Main#start()#initializeMap(Map)
         */
        initializeMap(mtk_2_map);

        for(int i=0; i<letters.length; i++) {
            //System.out.print(letters[i] + " ");
            for (Map.Entry<String, String> entry : mtk_2_map.entrySet()) {
                if (entry.getKey().equals(letters[i])) {
                    values[i] = entry.getValue();
                    //System.out.println(entry.getValue());
                }
            }
        }
        System.out.println("Word " + word + " in binary code:");
        for(String s: values) {
            System.out.print(s + " ");
        }
        System.out.println();

        /**
         * На данном этапе пользователю предлагается ввести количество раундов
         */
        System.out.println("Write a number of the decription rounds:");
        int roundCounter = -1;

        Scanner scanner = new Scanner(System.in);
        if(scanner.hasNextInt()){
            roundCounter = scanner.nextInt();
            if(roundCounter < 1) System.out.println("Incorrect number");
            scanner.nextLine();
        } else System.out.println("Write the correct number!");

        scanner.close();

        /**
         * @see Main#encrypt(int)
         */
        System.out.println("Number of rounds: " + roundCounter + "\n");
        try {
            encrypt(roundCounter);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    /**
     * @see Main#encrypt(int) - метод, принимающий на вход один параметр и запускающий цикл раундов
     * @param roundCounter - параметр, обозначающий количество раундов, которые необходимы для шифрования исходного слова
     * @throws InterruptedException
     */
    public static void encrypt(int roundCounter) throws InterruptedException {

        Main test = new Main();

        /**
         * @see Main#encrypt(int)#blockL - левый подблок, формируется в первой итерации, далее перенимается из результата
         * прохождения раунда
         * @see Main#encrypt(int)#blockR - правый подблок, формируется в первой итерации, далее перенимается из результата
         * прохождения раунда
         */
        blockL = new StringBuilder();
        blockR = new StringBuilder();

        for (int i = 0, j = word.length() / 2 - 1; i < word.length() / 2; i++) {
            blockL.append(values[i]);
            blockR.append(values[j]);
        }

        for(int n=1; n<=roundCounter; n++) {

            System.out.println("Round " + n + ":");

            String key = generateKey();
            //System.out.println("Key = " + key);
            //System.out.println(subBlockLength);
            String shortenedKey = key.substring(key.length() - subBlockLength);
            System.out.println("Key = " + shortenedKey);
            keyList.add(shortenedKey);

            System.out.println("L block: " + blockL);
            System.out.println("R block: " + blockR);

            encryptResult = test.doRound(blockL, blockR, shortenedKey);
        }
        System.out.println(encryptResult);

        for(int i=0; i<encryptResult.length(); i+=5){
            encryptResultList.add(encryptResult.substring(i, i+5));
        }

        for(int i=0; i<encryptResultList.size(); i++) {
            //System.out.print(letters[i] + " ");
            for (Map.Entry<String, String> entry : mtk_2_map.entrySet()) {
                if (entry.getValue().equals(encryptResultList.get(i))) {
                    encryptResultValuesList.add(entry.getKey());
                    break;

                    //System.out.print(entry.getKey() + " ");
                }
            }

        }
        for(String s : encryptResultValuesList){
            System.out.print(s + " ");
        }
    }

    /**
     * @see Main#doRound(StringBuilder, StringBuilder, String) - метод, формирующий логику для каждого конкретного раунда
     * @param blockL - левый подблок для текущего раунда
     * @param blockR - правый подблок для текущего раунда
     * @param key - ключ для текущего раунда
     * @return
     */
    public String doRound(StringBuilder blockL, StringBuilder blockR, String key){
        String moduleSumWithKey = moduleSum(blockL.toString(), key);
        System.out.println("Module sum with key = " + moduleSumWithKey);

        String moduleSumWithBlockR = moduleSum(moduleSumWithKey, blockR.toString());
        System.out.println("Module sum with R-block = " + moduleSumWithBlockR);

        String resultOfRound = moduleSumWithBlockR + blockR;
        //System.out.println("Result of round " + numOfRound + " is " + resultOfRound);

        System.out.println("\n");

        this.blockL = blockR;
        this.blockR = new StringBuilder(moduleSumWithBlockR);

        return resultOfRound;
    }

    /**
     * @see Main#generateKey() - метод, который создает объект класса:
     * @see DynamicKey, в котором генерируется ключ
     * @return
     * @throws InterruptedException
     */
    public static String generateKey() throws InterruptedException {
        System.out.println("Delay " + Instant.now());
        TimeUnit.SECONDS.sleep(1);
        System.out.println("After delay " + Instant.now());
        DynamicKey key1 = new DynamicKey();
        key1.getInfo();

        return key1.getKey();
    }

    /**
     * @see Main#moduleSum(String, String) - метод операции "сложение по модулю 2", необходимой для вычисления левого подблока
     * в каждом раунде алгоритма шифрования
     * @param blockL
     * @param key
     * @return
     */
    public static String moduleSum(String blockL, String key){

        String[] blockLArr = blockL.split("");
        String[] keyArr = key.split("");

        int[] resultArr = new int[blockL.length()];

        for(int i=0; i<blockL.length(); i++){
            int sum = Integer.parseInt(blockLArr[i]) + Integer.parseInt(keyArr[i]);

            if(sum == 2) resultArr[i] = 0;
            else resultArr[i] = sum;
        }

        StringBuilder result = new StringBuilder();
        for(int i: resultArr){
            result.append(i);
        }
        return result.toString();
    }

    public static String[] splitWordOnLetters(String word){
        String[] letters = word.split("");

        return letters;
    }

    /**
     * @see Main#initializeMap(Map) - метод, формирующий структуру данных:
     * @see Main#mtk_2_map для последующего перевода слова в двоичный код и обратно
     * @param map - параметр, который принимает на вход структуру данных
     */
    public static void initializeMap(Map map){
        /*map.put("R","01010");
        map.put("J","11010");
        map.put("N","00110");
        map.put("F","10110");
        map.put("C","01110");
        map.put("K","11110");
        map.put("E","10000");
        map.put("G","01011");
        map.put("M","00111");
        map.put("X","10111");
        map.put("V","01111");
        map.put("A","11000");
        map.put("S","10100");
        map.put("I","01100");
        map.put("U","11100");
        map.put("D","10010");
        map.put("T","00001");
        map.put("Z","10001");
        map.put("L","01001");
        map.put("W","11001");
        map.put("H","00101");
        map.put("Y","10101");
        map.put("P","01101");
        map.put("Q","11101");
        map.put("O","00011");
        map.put("B","10011");*/
        map.put("Возврат каретки","00010");
        map.put("Цифры","11011");
        map.put("Пробел","00100");
        map.put("Перевод строки","00000");
        map.put("Р","01010");
        map.put("Й","11010");
        map.put("Н","00110");
        map.put("Ф","10110");
        map.put("Ц","01110");
        map.put("К","11110");
        map.put("Е","10000");
        map.put("Г","01011");
        map.put("М","00111");
        map.put("Ь","10111");
        map.put("Ж","01111");
        map.put("А","11000");
        map.put("С","10100");
        map.put("И","01100");
        map.put("У","11100");
        map.put("Д","10010");
        map.put("Т","00001");
        map.put("З","10001");
        map.put("Л","01001");
        map.put("В","11001");
        map.put("Х","00101");
        map.put("Ы","10101");
        map.put("П","01101");
        map.put("Я","11101");
        map.put("О","00011");
        map.put("Б","10011");
        map.put("4","01010");
        map.put("Ч","01010");
        map.put("Ю","11010");
        map.put(",","00110");
        map.put("Э","10110");
        map.put("(","11110");
        map.put("3","10000");
        map.put("Ш","01011");
        map.put(".","00111");
        map.put("/","10111");
        map.put("=","01111");
        map.put("-","11000");
        map.put("'","10100");
        map.put("8","01100");
        map.put("7","11100");
        map.put("Кто там?","10010");
        map.put("5","00001");
        map.put("+","10001");
        map.put(")","01001");
        map.put("2","11001");
        map.put("Щ","00101");
        map.put("6","10101");
        map.put("0","01101");
        map.put("1","11101");
        map.put("9","00011");
        map.put("?","10011");
    }
}