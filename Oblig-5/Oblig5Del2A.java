import java.util.ArrayList;
import java.util.HashMap;

import java.io.File;
import java.util.Scanner;
import java.io.FileNotFoundException;


public class Oblig5Del2A {
    public static void main(String[] args) {
        // skjekker om antall argumenter gitt etter kommandoen 0:
        // java Oblig5Del1 [argumenter]
        if (args.length < 1) {
            System.err.println("Error: mangler filnavn parameter");
            System.exit(1);
        }

        // skjekker om antall argumenter gitt etter kommandoen er mer enn 1
        else if (args.length > 1) {
            System.err.println("Error: programmet tillater bare en parameter");
            System.exit(1);
        }

        // hent det første kommando argumentet og lag en
        // folder variable for å spesifisere test mappen
        String folder = args[0];
        
        // lag et nytt monitor objekt
        Monitor1 monitor = new Monitor1();

        // lag en array list som skal holde på alle threads som blir
        // laget til å lese filer slik at man kan vente på at alle
        // blir ferdig etter at de er startet
        ArrayList<Thread> thread_pool = new ArrayList<>();
        
        try {
            // lag en path til metadata filen ved å bruke mappe navnet
            String meta_file_path = String.format("%s/metadata.csv", folder);
            
            // lag en scanner for å lese fra metadata.csv filen
            Scanner meta_scanner = new Scanner(new File(meta_file_path));

            // looper så lenge metadata filen har en neste linje å lese av
            while (meta_scanner.hasNextLine()) {
                // les neste linje i metadata.csv filen som inneholder
                // det neste filnavnet og lag en path til filen
                String file_name = meta_scanner.nextLine();
                String file_path = String.format("%s/%s", folder, file_name);
                
                // lag en thread som leser filen og oppdaterer
                // monitoren sitt interne subsekvensregister
                
                // lag en ny thread som bruker en instanse av LeseTrad klassen
                Thread new_thread = new Thread(new LeseTrad(file_path, monitor));
                
                // legg til den nye threaden i thread_pool
                thread_pool.add(new_thread);

                // start den nye threaden og la den kjøre i bakgrunnen
                new_thread.start();
            }

            // bruk Thread.join() metoden for å vente på
            // at alle theads blir ferdig med å lese filene
            for (Thread thread: thread_pool) {
                thread.join();
            }

            // print subsekvens monitoret
            System.out.println(monitor);
    
            // lag en merged hashmap som skal brukes til å merge
            // hvert hashmap i monitoret sammen til å finne antall
            // forekomster av hver sekvens
            HashMap<String, Subsekvens> merged = new HashMap<>();
            
            // loop gjennom hver hashmap og merge det med merged hashmappet
            for (int i = 0; i < monitor.antallHashMaps(); i++) {
                merged = monitor.mergeHashMaps(merged, monitor.hentHashMap(i));
            }
            
            // hold en variabel av maks_sekvens 
            Subsekvens max_sekvens = null;
            
            for (Subsekvens sekvens: merged.values()) {
                // hvis maks_sekvens ikke er null og den nye sekvens
                // variablen ikke har et større antall en max_sekvens
                // sitt antall så hopper loopen til neste element
                if (max_sekvens != null) {
                    if (sekvens.hentAntall() < max_sekvens.hentAntall()) {
                        continue;
                    }
                }
                
                // endre maks_sekvens til sekvens hvis sekvens
                // har et større antall en maks_sekvens
                max_sekvens = sekvens;
            }

            // print ut sekvensen som hadde høyest antall
            System.out.println(max_sekvens);
            
            // lukk scanner objektet
            meta_scanner.close();
        }

        // gi feilmelding hvis metadata filen ikke ble funnet og exit programmet
        catch (FileNotFoundException e) {
            System.err.println(String.format("Error: unable to locate %s/metadata.csv", folder));
            System.exit(1);
        }

        // gi feilmelding hvis brukeren velger å avbryte
        // programmet med ctrl+c mens det kjører
        catch (InterruptedException e) {
            System.err.print("Error: program was interrupted by user");
        }
    }
}