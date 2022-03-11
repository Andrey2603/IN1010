import java.util.ArrayList;


public class Pasient {
    private static int id_teller = 0;

    public int Id;
    public String navn;
    public String foodselsdato;
    public ArrayList<Resept> resepter = new ArrayList<>();

    public Pasient(String navn, String foodselsdato) {
        this.navn = navn;
        this.foodselsdato = foodselsdato;

        this.Id = id_teller;
        id_teller++;
    }

    public void leggTilResept(Resept resept) {
        this.resepter.add(resept);
    }
}