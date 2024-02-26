import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class TrainingCamp {
    private static final List<String> CLASSES = List.of("Mage", "Paladin", "Warrior", "Shaman", "Hunter");
    private static final int NUMBER_OF_TRAINEES = 50;
    private static final int NUMBER_OF_VOLUNTEERS = 5;
    private static final int QUEUE_CAPACITY = 5;
    private static final int WAIT_TIME = 2000;
    private static List<String> TRAINEES = Collections.synchronizedList(new ArrayList<>());
    private static Map<String, Integer> SORTED_TRAINEES = Collections.synchronizedMap(new HashMap<>());
    // TODO Keszitsunk egy queue-t QUEUE_CAPACITY-vel limitkent
    private static BlockingQueue<String> TRAINEE_QUEUE = new ArrayBlockingQueue<String>(QUEUE_CAPACITY); 
    // TODO Keszitsunk TRAINEES_SORTED nevvel egy szamlalot, hogy nyomon kovessuk a kivalogatott trainee-ket
    private static AtomicInteger TRAINEES_SORTED = new AtomicInteger(0);
    
    public static void main(String[] args) {
        setupTrainees();
        
        
        
        var pool = Executors.newFixedThreadPool(NUMBER_OF_VOLUNTEERS + QUEUE_CAPACITY + 1);
        
        for(int i = 0; i < NUMBER_OF_VOLUNTEERS; ++i){
            // TODO Hivjuk meg a putTraineesInLine fuggvenyt egy kulon szalon
            Thread t = new Thread(()->putTraineesInLine());
            pool.submit(t);
        }

        for(int i = 0; i < QUEUE_CAPACITY; ++i){
            // TODO Hivjuk meg a sortTrainees fuggvenyt egy kulon szalon
            Thread t = new Thread(()->sortTrainees());
            pool.submit(t);
        }

        Future<String> checkMessage;
        
        checkMessage = pool.submit(new Callable<String>(){
            
            public String call(){
                return selfCheck();
            }
        });
        
        // TODO A checkMessage a selfCheck() fuggveny ertekevel kell, hogy visszaterjen
        // TODO Hasznaljuk ezt a kiiratashoz: System.out.println(checkMessage.get());
        try {
            System.out.println(checkMessage.get());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // TODO Miutan kiirtuk a checkMessage erteket, maximum WAIT_TIME eltelte utan alljon le a szimulacio
        
        try {
            pool.shutdown();
            pool.awaitTermination(WAIT_TIME,TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        

    }

    /**
     * Setting up trainees randomly
     */
    private static void setupTrainees(){
        Random rand =  ThreadLocalRandom.current();
        for(int i = 0; i < NUMBER_OF_TRAINEES; ++i) {
            String traineeClass = CLASSES.get(rand.nextInt(0, CLASSES.size()));
            TRAINEES.add(traineeClass);
        }
        CLASSES.forEach(str -> SORTED_TRAINEES.put(str, 0));
    }

    /**
     * The volunteers put the trainees in a queue for further sorting
     */
    private static void putTraineesInLine(){
        // TODO Addig fusson, amig kiurul a TRAINEES lista
        
        // TODO Vegyunk ki egy veletlenszeru jelentkezot a TRAINEE listabol 
        
        // TODO Rakjuk be az iment megkapott jelentkezot a TRAINEE_QUEUE-ba - ha a queue teli van,
        // TODO varjunk WAIT_TIME-ot maximum, majd probaljuk ujra
        while(TRAINEES.size()>0){
            
                if(TRAINEES.size()>0){
            try{
                String trainee;
                synchronized(TRAINEES){
                int randomIndex = ThreadLocalRandom.current().nextInt(TRAINEES.size());
                    trainee = TRAINEES.remove(randomIndex);
                }
                Boolean success = false;
                
                while(!success){
                    success = TRAINEE_QUEUE.offer(trainee,WAIT_TIME,TimeUnit.MILLISECONDS);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
    }
    
        //TRAINEE_QUEUE.offer("");

    }
    
    //System.out.println("--put meghalt");
    
    

    /**
     * Taking a trainee from the queue and determining where it belongs (map)
     */
    private static void sortTrainees(){
        // TODO Addig fusson, amig a TRAINEES lista nem ures ES a TRAINEE_QUEUE sem ures
        
        // TODO Szedjuk ki a kovetkezo jelentkezot a queue-bol - amennyiben a queue ures, varjunk
        // TODO maximum WAIT_TIME-ot es probaljuk ujra
        
        // TODO Ha sikerult kiszedni egy jelentkezot a queue-bol, noveljuk meg a SORTED_TRAINEES map 
        // TODO megfelelo erteket eggyel, majd noveljuk meg a TRAINEES_SORTED szamlalot eggyel
        
        // TODO Varjunk WAIT_TIME-ot a kovetkezo csekk elott
        
      


        while(!TRAINEES.isEmpty() || !TRAINEE_QUEUE.isEmpty()){
            try {
            
                String trainee = null;
                while(trainee == null){
                trainee = TRAINEE_QUEUE.poll(WAIT_TIME, TimeUnit.MILLISECONDS);
                }
                synchronized(SORTED_TRAINEES){
                if(trainee != null){
                    SORTED_TRAINEES.replace(trainee, SORTED_TRAINEES.get(trainee)+1);
                    TRAINEES_SORTED.incrementAndGet();
                }}
                Thread.sleep(WAIT_TIME);
            
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } 
   
    }
    

    /**
     * Checking at the end of the simulation if we indeed sorted as many as we had to
     * @return String based on result
     */
    private static String selfCheck() {
        // TODO Nezzuk meg WAIT_TIME intervallumonkent, hogy a TRAINEES_SORTED szamlalo erteke
        // TODO megegyezik-e a NUMBER_OF_TRAINEES ertekevel
        
        // TODO Adjuk ossze a kulonbozo kasztokhoz rendelt szamot a SORTED_TRAINEES map-bol 
        
        // TODO Ha az osszeadott ertek megegyezik a TRAINEES_SORTED szamlalo ertekevel:
        // TODO     return "Everything adds up";
        
        // TODO Ha nem egyezik meg a ket szam, terjunk vissza:
        // TODO     return "Something's wrong";
        

        while(TRAINEES_SORTED.get() != NUMBER_OF_TRAINEES){
            try {
                Thread.sleep(WAIT_TIME);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }   
        }
        int sum = 0;
        for (int value : SORTED_TRAINEES.values()){
            sum += value;
        }
        if(sum == TRAINEES_SORTED.get()){
            return "Everything adds up";
        }else{
            return "Something's wrong";
        }
        
    }

}