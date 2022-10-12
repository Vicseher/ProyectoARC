import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.logging.*;


public class Servidor {
    public static void main(String args[]) throws IOException {
        
        System.out.print("Inicializando servidor... ");
        try {
            
            ServerSocket ss;
            ss = new ServerSocket(10578);
            System.out.println("\t[OK]");
            Scanner myInput = new Scanner( System.in );
            System.out.print( "Introduce el numero de clientes: " );
            int numClientes = myInput.nextInt();
            System.out.print("Introduce el numero de grupos: ");
            int numGrupos = myInput.nextInt();
            System.out.print("Introduce el numero de repeticiones: ");
            int numRepeticiones = myInput.nextInt();
            
         
            System.out.println("Nueva conexión entrante:");
            
            //Crea un numero determinado de hilos hijos dependiendo de el numero de grupos que existen
            for(int idServidor=0;idServidor<numGrupos;idServidor++)
                    ((ServidorHilo) new ServidorHilo(ss,numClientes/numGrupos,numRepeticiones,idServidor)).start();
            
            while(true)
            {}
        } catch (IOException ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
} 
