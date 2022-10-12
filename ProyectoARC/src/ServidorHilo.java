import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.*;
public class ServidorHilo extends Thread {
    private Socket socket;
    private ServerSocket ss;
    private DataOutputStream dos;
    private DataInputStream dis;
    private int idServidor;
    private int numRepeticiones;
    private int numVecinos;
    private ArrayList<BlockingQueue> colas;
    private CyclicBarrier barrera;
    
    public ServidorHilo(ServerSocket ss,int numVecinos,int numRepeticiones, int idServidor) {
        
        this.idServidor = idServidor;
        this.numVecinos=numVecinos;
        this.numRepeticiones=numRepeticiones;
        this.ss=ss;
    }
    
    public void desconnectar() { //Falta cambiarlo para desconectar todos los clientes
        try {
            socket.close();
        } catch (IOException ex) {
            Logger.getLogger(ServidorHilo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void run() {
        try {
            //Inicia una primera conexion que pasa a los clientes del grupo cual es su
            //numero de vecinos, id del grupo y el numero de repeticiones
            Socket soc =ss.accept();
            dos = new DataOutputStream(soc.getOutputStream());
            dis = new DataInputStream(soc.getInputStream());
            dos.writeInt(numVecinos);
            dos.writeInt(idServidor);
            dos.writeInt(numRepeticiones);
            barrera = new CyclicBarrier(numVecinos);
            colas = new ArrayList<BlockingQueue>();
            
            //Inicializa las colas por donde se pasará información entre hilos
            for(int i=0;i<numVecinos;i++)
            {
                colas.add( new LinkedBlockingQueue());
            }
            
            //Este bucle crea una conexion con cada uno de los clientes y les envia su id 
            for(int i=0;i<numVecinos;i++)
            {
                soc =ss.accept();
                ((ServidorHiloCliente) new ServidorHiloCliente(soc,numRepeticiones,barrera,colas)).start();    
            }
               
            while(true)
            {
                
            }
            
        } catch (IOException ex) {
            Logger.getLogger(ServidorHilo.class.getName()).log(Level.SEVERE, null, ex);
        }
        //desconnectar();
    }
    
} 
