import java.util.HashMap;
import java.util.concurrent.locks.Condition;

public class Monitor2 extends Monitor1 {
    public Condition flettebar = this.thread_lock.newCondition();
    public Boolean aktiv_lesing = false;

    public void finishMerge() {
        // setter aktiv lesing til false
        this.aktiv_lesing = false;
        
        // låser thread locken
        this.thread_lock.lock();

        // signaliser alle flette threads til å stoppe
        try {
            this.flettebar.signalAll();
        }

        // lås opp thread
        finally {
            this.thread_lock.unlock();
        }
    }

    @Override
    public void settInnHashMap(HashMap<String, Subsekvens> map){
        this.thread_lock.lock();

        // legg til den nye hashmappen
        try {
            this.register.settInnHashMap(map);

            // signaliser flettbar kondisjonen hvis
            // antallet hashmaps er mer eller lik 2
            if (this.register.antallHashMaps() >= 2) {
                this.flettebar.signal();
            }
        }

        // lås opp thread locken selv om noe går galt
        finally {
            this.thread_lock.unlock();
        }
    }

    public void mergeHashMaps() {
        // lås thread locken
        this.thread_lock.lock();
        
        try {
            // hvis antall hashmaps er minder enn 2 vent på signal
            if (this.antallHashMaps() < 2) {
                this.flettebar.await();
            }
            
            // dette merger sammen alle hashmapper i registeret
            //
            // !WARNING!
            // dette er ikke den riktige måten å gjøre det på fordi
            // denne metoden skal egentlig hente ut 2 hashmaps som
            // fjernes fra registeret og så låses threaden opp mens
            // de merges og så låses det igjen når den skal legges
            // tilbake. dette gjør merge komputasjonen parralellisert.
            this.mergeInternals();
        }

        catch (InterruptedException e) {
            System.err.println("Error: thread contition await interrupted by user");
            System.exit(1);
        }

        // lås opp thread locken selv om noe går galt
        finally {
            this.thread_lock.unlock();
        }
    }
}