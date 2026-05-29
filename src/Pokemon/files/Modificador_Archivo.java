package files;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class Modificador_Archivo {
    public static void main(String[] args) throws IOException {
        File f = new File("src/files/PokemonDB.csv");

        try (
            Scanner sc = new Scanner(f);
            BufferedWriter bw = new BufferedWriter(new FileWriter("src/files/pokemons", false));
            BufferedWriter bwE = new BufferedWriter(new FileWriter("src/files/evoluciones", false))
        ) {
            String [] reg = {"","","","","","","","","","","","","","",};
            String [] evolucion = new String[3];

            sc.nextLine();

            while (sc.hasNextLine()) {
                evolucion[0] = reg[13];
                evolucion[1] = reg[12];
                evolucion[2] = reg[1];
                reg = sc.nextLine().split(",");
                if (!Boolean.parseBoolean(reg[2]))bw.write(reg[1]+";"+(reg[4].length() > 0 ? tipoTraducido(reg[3])+":"+tipoTraducido(reg[4]) : tipoTraducido(reg[3]))+";"+reg[11]+";"+reg[6]+";"+reg[7]+";"+reg[8]+";"+reg[9]+";"+reg[10]+"\n");
                if (evolucion[0].equals(reg[1])) bwE.write(evolucion[2]+";"+evolucion[1]+";"+reg[1]+";"+reg[11]+";"+reg[6]+";"+reg[7]+";"+reg[8]+";"+reg[9]+";"+reg[10]+";"+(reg[4].length() > 0 ? tipoTraducido(reg[3])+":"+tipoTraducido(reg[4]) : tipoTraducido(reg[3]))+"\n");
            }
        }
    }
    static String tipoTraducido (String tipo) {
        String tipoTra = "";
        if (tipo.equals("Grass")) tipoTra = "PLANTA";
        else if (tipo.equals("Fire")) tipoTra = "FUEGO";
        else if (tipo.equals("Poison")) tipoTra = "VENENO";
        else if (tipo.equals("Water")) tipoTra = "AGUA";
        else if (tipo.equals("Bug")) tipoTra = "BICHO";
        else if (tipo.equals("Flying")) tipoTra = "VOLADOR";
        else if (tipo.equals("Normal")) tipoTra = "NORMAL";
        else if (tipo.equals("Electric")) tipoTra = "RAYO";
        else if (tipo.equals("Psychic")) tipoTra = "PSIQUICO";
        else if (tipo.equals("Ice")) tipoTra = "HIELO";
        else if (tipo.equals("Steel")) tipoTra = "ACERO";
        else if (tipo.equals("Ground")) tipoTra = "TIERRA";
        else if (tipo.equals("Dark")) tipoTra = "SINIESTRO";
        else if (tipo.equals("Fairy")) tipoTra = "HADA";
        else if (tipo.equals("Rock")) tipoTra = "ROCA";
        else if (tipo.equals("Fighting")) tipoTra = "LUCHA";
        else if (tipo.equals("Dragon")) tipoTra = "DRAGON";
        else if (tipo.equals("Ghost")) tipoTra = "FANTASMA";

        return tipoTra;
    } 
}
