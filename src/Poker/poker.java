import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;
import java.util.Iterator;

public class poker {
    static private Random random = new Random();
    static private Scanner sc = new Scanner (System.in);
    public static void main(String[] args) {
        ArrayList<String> baraja = new ArrayList<>();

        String[] palos = {"\u2660", "\u2665", "\u2666", "\u2663"}; // ♠ ♥ ♦ ♣
        String[] valores = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};
        String[] manosPoker = {"Carta","Par","Doble","Trio","Escalera","Color","Full","Poker"}; //,"Escalera de color","Escalera real"
        ArrayList <String> mesa = new ArrayList<>();
        ArrayList <String []> manos = new ArrayList<>();
        int numJugadores;

        for (String palo : palos) {
            for (String valor : valores) {
                baraja.add(valor + palo);
            }
        }

        System.out.println("NUMERO DE JUGADORES?: ");
        numJugadores = 4;

        for (int i = 0; i < numJugadores; i++) {
            manos.add(new String [2]);
            generarMano(manos.get(i), baraja);
        }
        System.out.println("\nPREFLOP");
        for (int i = 0; i < manos.size(); i++) {
            System.out.println((i+1)+": "+manos.get(i)[0]+" "+manos.get(i)[1]);
        }
        generar_carta_mesa(mesa, baraja, 3);
        imprimir_pantalla(mesa, "FLOP");

        //FOURTH STREET
        // sc.nextLine();
        generar_carta_mesa(mesa, baraja, 1);
        imprimir_pantalla(mesa, "FOURTH STREET");

        //FIFTH STREET
        // sc.nextLine(); 
        generar_carta_mesa(mesa, baraja, 1);
        imprimir_pantalla(mesa, "FIFTH STREET");
        
        System.out.println(comprobar_ganador(manos, mesa, valores, manosPoker));
    }
    static private String comprobar_ganador (ArrayList <String []> manos, ArrayList <String> mesa, String [] valores, String [] manosPoker) {
        String ganador = "1";
        ArrayList <String> combinaciones = new ArrayList <>();
        ArrayList <Integer> combinaciones_jug = new ArrayList <>();
        String mejorCombinacion = "";
        String combinacion;

        for (int i = 0; i < manos.size(); i++) {
            combinacion = comprobar_mano(mesa, manos.get(i), valores);
            System.out.println(i+1+". \t"+manos.get(i)[0]+" "+manos.get(i)[1]+"\t"+combinacion);

            if (i == 0) mejorCombinacion = combinacion.split(" ")[0];
            else if (Arrays.asList(manosPoker).indexOf(mejorCombinacion)<Arrays.asList(manosPoker).indexOf(combinacion.split(" ")[0])) {
                mejorCombinacion = combinacion.split(" ")[0];
                combinaciones.clear();
                combinaciones_jug.clear();
                ganador = String.valueOf(i+1); //TEMPORAL
            }
            if (mejorCombinacion.equals(combinacion.split(" ")[0])){
                combinaciones.add(combinacion);
                combinaciones_jug.add(i+1);
            }
        }

        if (combinaciones.size()>1){ //SI HAY MAS DE UNO CON LA MISMA COMBINACION DE CARTAS
            ArrayList <String []> manosGanadoras = new ArrayList<>();
            for (int i = 0; i < combinaciones_jug.size(); i++) {
                manosGanadoras.add(manos.get(combinaciones_jug.get(i)-1));
            }
            //CARTA ALTA/PAR/DOBLE PAR/TRIO/POKER
            if (mejorCombinacion.equals("Carta") || mejorCombinacion.equals("Par") || mejorCombinacion.equals("Doble") || mejorCombinacion.equals("Trio") || mejorCombinacion.equals("Poker"))desempatar(manosGanadoras, mesa, combinaciones, combinaciones_jug, valores);
            else {
                ArrayList <String []> desempateManos = new ArrayList<>();
                for (int i = 0; i < combinaciones.size(); i++) {
                    //FULL
                    if (mejorCombinacion.equals("Full")) {
                        desempateManos.add(new String[5]);
                        desempateManos.get(i)[0] = combinaciones.get(i).split(" ")[combinaciones.get(i).split(" ").length-3];
                        desempateManos.get(i)[1] = desempateManos.get(i)[0];
                        desempateManos.get(i)[2] = desempateManos.get(i)[0];
                        desempateManos.get(i)[3] = combinaciones.get(i).split(" ")[combinaciones.get(i).split(" ").length-1];
                        desempateManos.get(i)[4] = desempateManos.get(i)[3];
                    }
                    //ESCALERA / ESCALERA DE COLOR
                    else if (mejorCombinacion.equals("Escalera") || mejorCombinacion.equals("Escalera_de_color")) {
                        desempateManos.add(new String [1]);
                        if (mejorCombinacion.equals("Escalera")) desempateManos.get(i)[0] = combinaciones.get(i).split(" ")[combinaciones.get(i).split(" ").length-1];
                        else desempateManos.get(i)[0] = tkValor(combinaciones.get(i).split(" ")[combinaciones.get(i).split(" ").length-1]);
                    }
                    //COLOR
                    else if (mejorCombinacion.equals("Color")){
                        desempateManos.add(new String [5]);
                        for (int j = 0; j < desempateManos.get(i).length; j++) {
                            desempateManos.get(i)[j]=tkValor(combinaciones.get(i).split(" ")[combinaciones.get(i).split(" ").length-5+j]);
                        }
                        ordenar(desempateManos.get(i), 0, valores);
                    }
                }
                descartarPerdedores(desempateManos, combinaciones_jug, valores);

            }
            if (combinaciones_jug.size()>1){
                for (int i = 0; i < combinaciones_jug.size(); i++) {
                    if (i==0) ganador = ""+combinaciones_jug.get(i);
                    else ganador = ganador +", "+ combinaciones_jug.get(i);
                }
            }
            else ganador = ""+combinaciones_jug.get(0);  
        }

         
        return ganador;
    }
    static private void descartarPerdedores (ArrayList <String []> list, ArrayList <Integer> combinaciones_jug, String [] valores) {
        String[] mejor = list.get(0);

        for (int i = 1; i < list.size(); i++) {
            if (comparar(list.get(i), mejor, valores) == 1) {
                mejor = list.get(i);
            }
        }

        Iterator<String[]> it = list.iterator();
        Iterator<Integer> jugs = combinaciones_jug.iterator(); 
        while (it.hasNext()) {
            jugs.next();
            String[] actual = it.next();
            if (comparar(actual, mejor, valores) != 0) {
                it.remove();
                jugs.remove();
            }
        }
    }
    static private void desempatar (ArrayList<String []> manosGanadoras, ArrayList <String> mesa, ArrayList <String> combinaciones, ArrayList <Integer> combinaciones_jug, String [] valores) {
        ArrayList <String []> kickers = kickers(manosGanadoras, mesa, combinaciones, valores);
        descartarPerdedores(kickers, combinaciones_jug, valores);
    }
    static private void ordenar (String [] list, int pos, String [] valores){
        for (int i = pos; i < list.length-1; i++) {
            for (int j = i + 1; j < list.length; j++) {
                if (esMayorQue(list[j], list[i], valores)) {
                    String c = list[i];
                    list[i] = list[j];
                    list[j] = c;
                }
            }
        }
    }
    static private void ordenar (ArrayList <String> list, int pos, String [] valores){
        for (int i = pos; i < list.size()-1; i++) {
            for (int j = i + 1; j < list.size(); j++) {
                if (esMayorQue(tkValor(list.get(j)), tkValor(list.get(i)), valores)) {
                    String c = list.get(i);
                    list.set(i, list.get(j));
                    list.set(j, c);
                }
            }
        }
    }
    static private ArrayList <String> todasCartasOrdenadas (String [] mano, ArrayList <String> mesa, String [] valores) {
        ArrayList <String> todasCartasOrdenadas = new ArrayList<>();

        for (int i = 0; i < mano.length; i++) {
            todasCartasOrdenadas.add(mano[i]);
        }
        for (int i = 0; i < mesa.size(); i++) {
            todasCartasOrdenadas.add(mesa.get(i));
        }
        ordenar(todasCartasOrdenadas, 0, valores);

        return todasCartasOrdenadas;
    }
    static private ArrayList<String []> kickers (ArrayList <String []> manos, ArrayList <String> mesa, ArrayList <String> combinaciones, String [] valores) {
        String mejorCombinacion = combinaciones.get(0).split(" ")[0];
        ArrayList <String []> kickers = new ArrayList <>();

        if (mejorCombinacion.equals("Carta")) {
            kicker_alta(kickers, manos, mesa, combinaciones, valores);
        }
        else if(mejorCombinacion.equals("Par")) {
            kicker_par(kickers, manos, mesa, combinaciones, valores);
        }
        else if(mejorCombinacion.equals("Doble")) {
            kicker_doble_par(kickers, manos, mesa, combinaciones, valores);
        }
        else if(mejorCombinacion.equals("Trio")) {
            kicker_trio(kickers, manos, mesa, combinaciones, valores);
        }
        else if(mejorCombinacion.equals("Full")) {
  
        }
        else if(mejorCombinacion.equals("Poker")) {
            kicker_poker(kickers, manos, mesa, combinaciones, valores);
        }
        else if(mejorCombinacion.equals("Escalera")) {

        }
        else if(mejorCombinacion.equals("Color")) {

        }
        return kickers;
    }
    static private void kicker_poker (ArrayList <String []> kickers, ArrayList <String []> manos, ArrayList <String> mesa, ArrayList <String> combinaciones, String [] valores) {
        ArrayList <String> cartasAgrupadas;
        ArrayList <String> valoresAgrupados = new ArrayList<>();


        for (int i = 0; i < manos.size(); i++) {
            kickers.add(new String [5]);
            kickers.get(i)[0] = combinaciones.get(i).split(" ")[combinaciones.get(i).split(" ").length-1];
            kickers.get(i)[1] = kickers.get(i)[0];
            kickers.get(i)[2] = kickers.get(i)[0];
            kickers.get(i)[3] = kickers.get(i)[0];
            
            cartasAgrupadas = todasCartasOrdenadas(manos.get(i), mesa, valores);
            for (int j = 0; j < cartasAgrupadas.size(); j++) {valoresAgrupados.add(tkValor(cartasAgrupadas.get(j)));}
            valoresAgrupados.removeIf(s -> s.equals(kickers.get(0)[0]));
            valoresAgrupados.removeIf(s -> s.equals(kickers.get(0)[2]));

            kickers.get(i)[4] = valoresAgrupados.get(0);

            valoresAgrupados.clear();
        }     

    }
    static private void kicker_doble_par (ArrayList <String []> kickers, ArrayList <String []> manos, ArrayList <String> mesa, ArrayList <String> combinaciones, String [] valores) {
        ArrayList <String> cartasAgrupadas;
        ArrayList <String> valoresAgrupados = new ArrayList<>();
        for (int i = 0; i < manos.size(); i++) {
            kickers.add(new String [5]);
            kickers.get(i)[0] = combinaciones.get(i).split(" ")[combinaciones.get(i).split(" ").length-1];
            kickers.get(i)[1] = kickers.get(i)[0];
            kickers.get(i)[2] = combinaciones.get(i).split(" ")[combinaciones.get(i).split(" ").length-3];
            kickers.get(i)[3] = kickers.get(i)[2];
            ordenar(kickers.get(i), 0, valores);

            cartasAgrupadas = todasCartasOrdenadas(manos.get(i), mesa, valores);
            for (int j = 0; j < cartasAgrupadas.size(); j++) {valoresAgrupados.add(tkValor(cartasAgrupadas.get(j)));}

            final int k=i;
            valoresAgrupados.removeIf(s -> s.equals(kickers.get(k)[0]));
            valoresAgrupados.removeIf(s -> s.equals(kickers.get(k)[2]));

            kickers.get(i)[4] = valoresAgrupados.get(0);

            valoresAgrupados.clear();
        }     
    }
    static private void kicker_trio (ArrayList <String []> kickers, ArrayList <String []> manos, ArrayList <String> mesa, ArrayList <String> combinaciones, String [] valores) {
        ArrayList <String> cartasAgrupadas;
        ArrayList <String> valoresAgrupados = new ArrayList<>();

        for (int i = 0; i < manos.size(); i++) {
            kickers.add(new String [5]);
            kickers.get(i)[0] = combinaciones.get(i).split(" ")[combinaciones.get(i).split(" ").length-1];
            kickers.get(i)[1] = kickers.get(i)[0];
            kickers.get(i)[2] = kickers.get(i)[0];

            cartasAgrupadas = todasCartasOrdenadas(manos.get(i), mesa, valores);
            for (int j = 0; j < cartasAgrupadas.size(); j++) {valoresAgrupados.add(tkValor(cartasAgrupadas.get(j)));}

            valoresAgrupados.removeIf(s -> s.equals(kickers.get(0)[0]));
        
            for (int j = 0; j < 2; j++) {
                kickers.get(i)[j+3] = valoresAgrupados.get(j);
            }

            valoresAgrupados.clear();
        }
    }
    static private void kicker_alta (ArrayList <String []> kickers, ArrayList <String []> manos, ArrayList <String> mesa, ArrayList <String> combinaciones, String [] valores) {
        ArrayList <String> cartasAgrupadas;
        ArrayList <String> valoresAgrupados = new ArrayList<>();

        for (int i = 0; i < manos.size(); i++) {
            cartasAgrupadas = todasCartasOrdenadas(manos.get(i), mesa, valores);
            kickers.add(new String [5]);

            for (int j = 0; j < 5; j++) {
                kickers.get(i)[j]=tkValor(cartasAgrupadas.get(j));
            }

        }
    }
    static private void kicker_par (ArrayList <String []> kickers, ArrayList <String []> manos, ArrayList <String> mesa, ArrayList <String> combinaciones, String [] valores) {
        ArrayList <String> cartasAgrupadas;
        ArrayList <String> valoresAgrupados = new ArrayList<>();

        for (int i = 0; i < manos.size(); i++) {
            kickers.add(new String [5]);
            kickers.get(i)[0] = combinaciones.get(i).split(" ")[combinaciones.get(i).split(" ").length-1];
            kickers.get(i)[1] = kickers.get(i)[0];
                
            cartasAgrupadas = todasCartasOrdenadas(manos.get(i), mesa, valores);
            for (int j = 0; j < cartasAgrupadas.size(); j++) {valoresAgrupados.add(tkValor(cartasAgrupadas.get(j)));}
            
            valoresAgrupados.removeIf(s -> s.equals(kickers.get(0)[0]));

            for (int j = 0; j < 3; j++) {
                kickers.get(i)[j+2] = valoresAgrupados.get(j);
            }

            valoresAgrupados.clear();
        }
    }
    static private void generar_carta_mesa (ArrayList <String> mesa, ArrayList <String> baraja, int numCartas) {
        int n;
        for (int i = 0; i < numCartas; i++) {
            n = random.nextInt(baraja.size());
            mesa.add(baraja.get(n)); baraja.remove(n);
        }   
    }
    static private void generarMano(String [] mano, ArrayList <String> baraja) {
        int n = random.nextInt(baraja.size());
        mano[0] = baraja.get(n); baraja.remove(n);
        
        n = random.nextInt(baraja.size());
        mano[1] = baraja.get(n); baraja.remove(n);
    }
    static private void imprimir_pantalla (ArrayList <String> mesa, String fase) {
        System.out.println(fase);
        System.out.print("\tMESA: ");
        for (int i = 0; i < mesa.size(); i++) {System.out.print(mesa.get(i)+" ");}        System.out.println("\n");
    }
    static private String comprobar_color (ArrayList <String> mesa, String [] mano, String [] valores, String resultado) {
        ArrayList <String> todosPalos = new ArrayList<String>();

        //RECOGER TODAS LAS CARTAS
        for (int i = 0; i < mano.length; i++) {
            todosPalos.add(mano[i]);
        }

        for (int i = 0; i < mesa.size(); i++) {
            todosPalos.add(mesa.get(i));       
        }

        for (int i = 0; i < todosPalos.size()-1; i++) {
            for (int j = i+1; j < todosPalos.size(); j++) {
                if (esMayorQue(tkValor(todosPalos.get(i)), tkValor(todosPalos.get(j)), valores)) {
                    String c="";
                    c=todosPalos.get(i);
                    todosPalos.set(i, todosPalos.get(j));
                    todosPalos.set(j, c);
                }
            }
        }        
        
        int [] color = {0,0,0,0};
        String [] palos = {"\u2660","\u2665","\u2663","\u2666"};
        int x;
        boolean ok;

        for (int i = 0; i < todosPalos.size(); i++) {
            x = 0;
            ok = false;
            while(ok==false && x<palos.length){//MIRO TODAS LAS CARTAS Y HAGO RECUENTO DE PALOS REPETIDOS
                if (todosPalos.get(i).split("")[todosPalos.get(i).length()-1].equals(palos[x])){
                    ok=true;
                    color[x] = color[x]+1;
                }
                else x++;
            }
        }
        
        boolean hayColor=false;
        x = 0;
        String palo = "";

        while(hayColor==false && x<color.length){
            if(color[x]>=5){
                hayColor=true;
                palo = palos[x];
            }
            else x++;
        } 
        String cartas = cartas_de_palo(todosPalos, palo);
        if (hayColor) resultado = "Color de "+palo+" con "+cartas;
        return resultado;
    }
    static private String cartas_de_palo (ArrayList<String> cartas, String palo) {
        String f = "";
        String [] auxf;
        for (int i = 0; i < cartas.size(); i++) {
            if (cartas.get(i).split("")[cartas.get(i).length()-1].equals(palo)) {
                if (f.split(" ").length < 5)f = f + cartas.get(i)+" ";
                else {
                    auxf = f.split(" ");
                    f = auxf[1]+" "+auxf[2]+" "+auxf[3]+" "+auxf[4]+" "+cartas.get(i);
                }
            }
        }
        return f;
    }
    static private ArrayList<String> cartas_de_palo_sin_corte (ArrayList<String> cartas, String palo) {
        ArrayList<String> f = new ArrayList<>();
        for (int i = 0; i < cartas.size(); i++) {
            if (cartas.get(i).split("")[cartas.get(i).length()-1].equals(palo)) {
                f.add(cartas.get(i));
            }
        }
        return f;
    }    
    static private String comprobar_escalera (ArrayList <String> mesa, String [] mano, String [] valores, String resultado) {
        ArrayList <String> todosValores = new ArrayList<String>();

        //RECOGER TODAS LAS CARTAS
        for (int i = 0; i < mano.length; i++) {
            if (todosValores.indexOf(tkValor(mano[i])) == -1){
                todosValores.add(tkValor(mano[i]));
            }
        }

        for (int i = 0; i < mesa.size(); i++) {
            if (todosValores.indexOf(tkValor(mesa.get(i))) == -1){
                todosValores.add(tkValor(mesa.get(i)));
            }        
        }

        //ORDENAR
        for (int i = 0; i < todosValores.size()-1; i++) {
            for (int j = i+1; j < todosValores.size(); j++) {
                if (esMayorQue(todosValores.get(i), todosValores.get(j), valores)) {
                    String c="";
                    c=todosValores.get(i);
                    todosValores.set(i, todosValores.get(j));
                    todosValores.set(j, c);
                }
            }
        }

        //SI HAY UN AS PONERLO AL PRINCIPIO TAMBIEN
        if (todosValores.get(todosValores.size()-1).equals("A")) todosValores.add(0, "A");

        //CALCULAR ESCALERA
        int escalera=1;
        String valoresEscalera="";
        String [] mayorEscalera=new String [5];
        for (int i = 0; i < todosValores.size()-1; i++) {
            // System.out.println(valoresEscalera+" num:"+escalera);
            if (Arrays.asList(valores).indexOf(todosValores.get(i))-Arrays.asList(valores).indexOf(todosValores.get(i+1))==-1){
                valoresEscalera = valoresEscalera +" "+ todosValores.get(i);
                if (escalera < 5) escalera++;
                if (escalera==5) {
                    mayorEscalera = valoresEscalera.split(" ");
                    valoresEscalera = mayorEscalera[1]+" "+mayorEscalera[2]+" "+mayorEscalera[3]+" "+mayorEscalera[4];
                    valoresEscalera = valoresEscalera +" "+ todosValores.get(i+1);
                }

            }
            else if ((Arrays.asList(valores).indexOf(todosValores.get(i))-13)-Arrays.asList(valores).indexOf(todosValores.get(i+1))==-1) {
                escalera++;
                valoresEscalera = valoresEscalera +" "+ todosValores.get(i);
            }
            else {
                if (escalera==5) break;
                escalera=1;    
                valoresEscalera=""; 
            }
        }
        if (escalera == 5) resultado = "Escalera de "+valoresEscalera;
        return resultado;
    }
    static private String comprobar_pares (ArrayList <String> mesa, String [] mano, String [] valorCartas) {
        String resultado = "";
        //¡¡¡¡¡FALTA COGER EL DOBLE PAR O FULL MAS GRANDE POSIBLE!!!!!
        ArrayList <String> valores = new ArrayList<>();
        ArrayList <Integer> repetidos = new ArrayList<>();
        ArrayList <String> todasCartas = new ArrayList <String> ();

        //RECOGER TODAS LAS CARTAS PARA HACER RECUENTO
        for (int i = 0; i < mano.length; i++) {todasCartas.add(mano[i]);}
        for (int i = 0; i < mesa.size(); i++) {todasCartas.add(mesa.get(i));}
        
        for (int i = 0; i < todasCartas.size()-1; i++) {
            for (int j = i+1; j < todasCartas.size(); j++) {
                if (esMayorQue(todasCartas.get(i), todasCartas.get(j), valorCartas)) {
                    String c="";
                    c=todasCartas.get(i);
                    todasCartas.set(i, todasCartas.get(j));
                    todasCartas.set(j, c);
                }
            }
        }

        for (int i = 0; i < todasCartas.size(); i++) {
            if (valores.indexOf(tkValor(todasCartas.get(i))) == -1) {
                valores.add(tkValor(todasCartas.get(i)));
                repetidos.add(1);
            }
            else{
                repetidos.set(valores.indexOf(tkValor(todasCartas.get(i))), repetidos.get(valores.indexOf(tkValor(todasCartas.get(i))))+1);
            }
        }
        
        //RECUENTO DE VALORES Y CALCULAR RESULTADO
        for (int i = 0; i < valores.size(); i++) {
            // System.out.println("\ni: "+i+"\nmano: "+mano[0]+" "+mano[1]+"\nR: "+resultado);
            if (repetidos.get(i)>1) { //MIRAR QUE VALORES SE REPITEN 
                if (repetidos.get(i)==2){//SI SE ENCUENTRA DOS VECES (PAR)
                    if (!resultado.split(" ")[0].equals("Poker") && !resultado.split(" ")[0].equals("Full")) { //SI HAY POKER/FULL NO MODIFICAR
                        if (resultado.split(" ")[0].equals("Par")){ //SI YA HAY PAR PONER DOBLE PAR
                            resultado = "Doble par de "+valores.get(i)+" "+valores.get(i)+" "+resultado.split(" ")[resultado.split(" ").length-1]+" "+resultado.split(" ")[resultado.split(" ").length-1];
                        }
                        else if (resultado.split(" ")[0].equals("Trio")){ //SI YA HAY TRIO PONER FULL
                            resultado = "Full de "+resultado.split(" ")[resultado.split(" ").length-1]+" "+resultado.split(" ")[resultado.split(" ").length-1]+" "+resultado.split(" ")[resultado.split(" ").length-1]+" "+valores.get(i)+" "+valores.get(i);
                        }
                        else if (resultado.split(" ")[0].equals("Doble")) {
                            resultado = "Doble par de "+valores.get(i)+" "+valores.get(i)+" "+resultado.split(" ")[resultado.split(" ").length-1]+" "+resultado.split(" ")[resultado.split(" ").length-1];
                        }
                        else {
                            resultado = "Par de "+valores.get(i)+" "+valores.get(i); //SI EN EL RESULTADO NO HAY NADA PONER PAR
                        }
                    }
                }
                else if (repetidos.get(i)==3){//SI SE ENCUENTRA 3 VECES (TRIO)
                    if (!resultado.split(" ")[0].equals("Poker") && !resultado.split(" ")[0].equals("Full")) { //SI HAY POKER NO MODIFICAR
                        if (resultado.split(" ")[0].equals("Par")) { //SI YA HAY PAR PONER FULL
                            resultado = "Full de "+valores.get(i)+" "+valores.get(i)+" "+valores.get(i)+" "+resultado.split(" ")[resultado.split(" ").length-1]+" "+resultado.split(" ")[resultado.split(" ").length-1];
                        }
                        else if (resultado.split(" ")[0].equals("Trio")){ //SI YA HAY TRIO
                            if (esMayorQue(resultado.split(" ")[resultado.split(" ").length-1], valores.get(i), valorCartas)) {
                                resultado = "Full de "+resultado.split(" ")[resultado.split(" ").length-1]+" "+resultado.split(" ")[resultado.split(" ").length-1]+" "+resultado.split(" ")[resultado.split(" ").length-1]+" "+valores.get(i)+" "+valores.get(i);
                            }
                            else {
                                resultado = "Full de "+valores.get(i)+" "+valores.get(i)+" "+valores.get(i)+" "+resultado.split(" ")[resultado.split(" ").length-1]+" "+resultado.split(" ")[resultado.split(" ").length-1];
                            }
                        }
                        else resultado = "Trio de "+valores.get(i)+" "+valores.get(i)+" "+valores.get(i);
                    }
                }//SI SE ENCUENTRA 4 VECES (POKER)
                else {
                    resultado = "Poker de "+valores.get(i)+" "+valores.get(i)+" "+valores.get(i)+" "+valores.get(i);
                }
            }
        }
        return resultado;
    }
    static private String tkValor(String carta) {
        return carta.substring(0, carta.length()-1);
    }
    static private String comprobar_mano (ArrayList <String> mesa, String [] mano, String [] valores) {
        String resultado = comprobar_pares(mesa, mano, valores);
        
        if (!resultado.split(" ")[0].equals("Poker") && !resultado.split(" ")[0].equals("Full")) {
            resultado = comprobar_escalera(mesa, mano, valores, resultado);
        }

        resultado = comprobar_color(mesa, mano, valores, resultado);
        resultado = comprobar_carta_alta(resultado, valores, mano);
        resultado = comprobar_escalera_color(mesa, mano, valores, resultado);
        resultado = comprobar_escalera_real(mesa, mano, valores, resultado);
        return resultado;
    }
    static private String comprobar_escalera_real (ArrayList <String> mesa, String [] mano, String [] valores, String resultado) {
        String [] auxResultado;
        if (resultado.split(" ")[0].equals("Escalera_de_color") && tkValor(resultado.split(" ")[resultado.split(" ").length-1]).equals("A")) {
            auxResultado = resultado.split(" ");

            resultado = "Escalera_real de ";
            for (int i = 0; i < 5; i++) {
                resultado = resultado + auxResultado[auxResultado.length-5+i]+" ";
            }
        }
        return resultado;
    }
    static private String comprobar_escalera_color (ArrayList <String> mesa, String [] mano, String [] valores, String resultado) {
        if (resultado.split(" ")[0].equals("Color")){
            ArrayList <String> todas_cartas_color = todas_cartas_color(mesa, mano, valores);

            //SI HAY UN AS PONERLO AL PRINCIPIO TAMBIEN
            if (tkValor(todas_cartas_color.get(todas_cartas_color.size()-1)).equals("A")) todas_cartas_color.add(0, todas_cartas_color.get(todas_cartas_color.size()-1));

            //CALCULAR ESCALERA
            int escalera=1;
            String valoresEscalera="";
            String [] mayorEscalera=new String [5];
            for (int i = 0; i < todas_cartas_color.size()-1; i++) {
                // System.out.println(valoresEscalera+" num:"+escalera);
                if (Arrays.asList(valores).indexOf(tkValor(todas_cartas_color.get(i)))-Arrays.asList(valores).indexOf(tkValor(todas_cartas_color.get(i+1)))==-1){
                    valoresEscalera = valoresEscalera +" "+ todas_cartas_color.get(i);
                    if (escalera < 5) escalera++;
                    if (escalera==5) {
                        mayorEscalera = valoresEscalera.split(" ");
                        valoresEscalera = mayorEscalera[1]+" "+mayorEscalera[2]+" "+mayorEscalera[3]+" "+mayorEscalera[4];
                        valoresEscalera = valoresEscalera +" "+ todas_cartas_color.get(i+1);
                    }

                }
                else if ((Arrays.asList(valores).indexOf(tkValor(todas_cartas_color.get(i)))-13)-Arrays.asList(valores).indexOf(tkValor(todas_cartas_color.get(i+1)))==-1) {
                    escalera++;
                    valoresEscalera = valoresEscalera +" "+ todas_cartas_color.get(i);
                }
                else {
                    if (escalera==5) break;
                    escalera=1;    
                    valoresEscalera=""; 
                }
            }
            if (escalera == 5) resultado = "Escalera_de_color de "+valoresEscalera;
        }
        return resultado;
    }
    static private ArrayList <String> todas_cartas_color (ArrayList <String> mesa, String [] mano, String [] valores) {
        ArrayList <String> todosPalos = new ArrayList<String>();

        //RECOGER TODAS LAS CARTAS
        for (int i = 0; i < mano.length; i++) {
            todosPalos.add(mano[i]);
        }

        for (int i = 0; i < mesa.size(); i++) {
            todosPalos.add(mesa.get(i));       
        }

        for (int i = 0; i < todosPalos.size()-1; i++) {
            for (int j = i+1; j < todosPalos.size(); j++) {
                if (esMayorQue(tkValor(todosPalos.get(i)), tkValor(todosPalos.get(j)), valores)) {
                    String c="";
                    c=todosPalos.get(i);
                    todosPalos.set(i, todosPalos.get(j));
                    todosPalos.set(j, c);
                }
            }
        }        
        
        int [] color = {0,0,0,0};
        String [] palos = {"\u2660","\u2665","\u2663","\u2666"};
        int x;
        boolean ok;

        for (int i = 0; i < todosPalos.size(); i++) {
            x = 0;
            ok = false;
            while(ok==false && x<palos.length){//MIRO TODAS LAS CARTAS Y HAGO RECUENTO DE PALOS REPETIDOS
                if (todosPalos.get(i).split("")[todosPalos.get(i).length()-1].equals(palos[x])){
                    ok=true;
                    color[x] = color[x]+1;
                }
                else x++;
            }
        }
        
        boolean hayColor=false;
        x = 0;
        String palo = "";

        while(hayColor==false && x<color.length){
            if(color[x]>=5){
                hayColor=true;
                palo = palos[x];
            }
            else x++;
        } 
        ArrayList <String> cartas = cartas_de_palo_sin_corte(todosPalos, palo);

        return cartas;
    }
    static private boolean esMayorQue (String valor1, String valor2, String [] valores) {
        return Arrays.asList(valores).indexOf(valor1)>Arrays.asList(valores).indexOf(valor2);
    }
    static private String comprobar_carta_alta (String resultado, String [] valores, String [] mano) {
        if (resultado.equals("")){
            if (esMayorQue(tkValor(mano[0]), tkValor(mano[1]), valores)){ resultado = "Carta alta de "+mano[0];}
            else resultado = "Carta alta de "+mano[1];
        }

        return resultado;
    }
    static int comparar(String[] a, String[] b, String[] valores) {
        for (int i = 0; i < a.length; i++) {
            int posA = valorIndex(a[i], valores);
            int posB = valorIndex(b[i], valores);

            if (posA > posB) return 1;  
            if (posA < posB) return -1;  
        }
        return 0; 
    }
    static int valorIndex(String v, String[] valores) {
        for (int i = 0; i < valores.length; i++) {
            if (valores[i].equals(v)) return i;
        }
        return -1;
    }
}
