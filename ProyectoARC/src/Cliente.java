import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.logging.*;
/*
    Prueba de commit
*/
class Persona extends Thread {
    protected Socket sk;
    protected DataOutputStream dos;
    protected DataInputStream dis;
    private int idCliente;
    private int idGrupo;
    private int numRepeticiones;
    private int numVecinos;
    
    public Persona(int id,int numVecinos,int idgrupo,int numRepeticiones) {
        this.idCliente = id;
        this.numVecinos = numVecinos;
        this.idGrupo = idgrupo;
        this.numRepeticiones = numRepeticiones;
    }
    
    @Override
    public void run() {
        try {
            sk = new Socket("127.0.0.1", 10578);
            dos = new DataOutputStream(sk.getOutputStream());
            dis = new DataInputStream(sk.getInputStream());
            dos.writeUTF("hola"); //Mensaje para conectarse al servidor
            
            idCliente = dis.readInt();//Recibe su id del servidor
            long tiempoIni;
            long tiempoFin;
            long tiempoTotal;
            
            String respuesta = dis.readUTF(); //Aqui recibe la senal para iniciar la fase  de simulacion
            
            tiempoIni = System.nanoTime();
            for(int repeticionActual=0; repeticionActual < numRepeticiones; repeticionActual++)
            {
                int x = (int) (Math.random()*1000);
                int y = (int) (Math.random()*1000);
                int z = (int) (Math.random()*1000);
                respuesta =" = (" + x + "," + y + "," + z + ")";
                dos.writeUTF(respuesta); //Envia las coordenadas
                
                for(int i=0 ; i<numVecinos;i++)//Recibe mensaje y devuelve la confirmacion
                {
                    String mensaje = dis.readUTF();
                    dos.writeInt(idCliente);
                }
                int recibidos = 0;
                while(recibidos < numVecinos -1)
                {
                    try{
                    respuesta = dis.readUTF();
                    recibidos++;
                    }
                    catch(IOException ex){
                        Logger.getLogger(Persona.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                }
                
                System.out.println("Repeticion numero" + repeticionActual);
                
            }
            tiempoFin = System.nanoTime();
            tiempoTotal = tiempoFin - tiempoIni;
            System.out.println("El tiempo total ha sido: " + tiempoTotal);
            /*dis.close();
            dos.close();
            sk.close();*/
        } catch (IOException ex) {
            Logger.getLogger(Persona.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

public class Cliente {
    public static void main(String[] args) {
        try
        {
            //Primera conexion con el servidor para obtener el numero de clientes del grupo,su grupo y el numero de repeticiones
            Socket sk = new Socket("127.0.0.1", 10578);
            DataInputStream dis = new DataInputStream(sk.getInputStream());
            int numClientes = dis.readInt();
            int numGrupo = dis.readInt();
            int numRepeticiones = dis.readInt();
            
            ArrayList<Thread> clients = new ArrayList<Thread>();
            
            //Crea los clientes
            for (int i = 0; i < numClientes; i++) {
                clients.add(new Persona(i,numClientes,numGrupo,numRepeticiones));
            }
            for (Thread thread : clients) {
                thread.start();
            }
        }
        catch(IOException ex) {
            Logger.getLogger(ServidorHilo.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
    }
} 
