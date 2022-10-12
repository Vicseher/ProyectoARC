
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author vicseher
 */
public class ServidorHiloCliente extends Thread {
    private DataOutputStream dos;
    private DataInputStream dis;
    private static int idCliente=0;
    private CyclicBarrier barrera;
    private final Object lock = new Object();
    private int id;
    private int numRepeticiones;
    private ArrayList<BlockingQueue> colas;
    private BlockingQueue cola = new LinkedBlockingQueue();
    
    public ServidorHiloCliente(Socket soc,int numrepeticiones,CyclicBarrier barrera,ArrayList<BlockingQueue> colas){
    
        try{
            dos = new DataOutputStream(soc.getOutputStream());
            dis = new DataInputStream(soc.getInputStream());
            this.barrera = barrera;
            this.colas = colas;
            this.numRepeticiones = numrepeticiones;
        }
        catch (IOException ex) {
            Logger.getLogger(Persona.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void run() {
        try{
            String accion = dis.readUTF();
            if(accion.equals("hola")){
                
                synchronized (lock) {
                    dos.writeInt(idCliente);
                    id = idCliente;
                    idCliente++;
                }
                
                barrera.await();//Aqui espera a que esten todos los hilos para empezar la iteracion
                dos.writeUTF("Comienza la iteracion");
                
                for(int repeticiones=0; repeticiones < numRepeticiones; repeticiones++){
                    System.out.println("Repeticion numero: " + repeticiones);
                    String mensaje = dis.readUTF(); //Paso 3 del esquema (lee las coordenadas enviadas por el cliente)
                    //Envia el mensaje al resto de servidores hijos
                    
                    for(int i = 0; i < colas.size(); i++) //Paso 4 del esquema (Envia al resto de servidores clientes el mensaje a enviar)
                    {
                        if(i != id)
                        {
                            (colas.get(i)).add(mensaje);
                        }
                    }
                    
                    barrera.await(); 
                    
                    /*
                        Revisar la funcion de abajo
                    */
                    
                    //Lee el buzon del cliente de este servidor hilo
                    for(int i = 0; i < colas.size(); i++) //Lee el buzon con todos los mensajes que el server ha enviado a un nodo en particular
                    {
                        String respuesta = (String)(colas.get(id)).poll(100,TimeUnit.MILLISECONDS); //Lee el mensaje enviado
                        if(respuesta != null) //Si existe ese mensaje
                        {
                            dos.writeUTF(respuesta); //Lo notifica al cliente 
                            int iduser = dis.readInt(); //El cliente envia respuesta de vuelta al servidor
                            System.out.println("El iduser es: " + iduser);
                            (colas.get(i)).add("Paquete enviado");
                        }
                    }
                    
                    barrera.await();
                    //Vuelve a leer el buffer de mensajes de este cliente
                    for(int i = 0; i < colas.size(); i++)
                    {
                        String respuesta = (String)(colas.get(id)).poll(100,TimeUnit.MILLISECONDS); //Lee el mensaje enviado
                        if(respuesta != null) //Si existe ese mensaje
                        {
                            dos.writeUTF(respuesta); //Lo notifica al cliente 
                        }
                    }
                    
                    barrera.await();
                    if(id == 0)
                    {
                        //Vacia todos los buzones
                        for(int i = 0 ; i < colas.size();i++)
                        {
                          (colas.get(i)).clear();
                          System.out.println("El tamaño de la cola despues de limpiar es " + (colas.get(i)).size());
                        }
                    }
                    }
                }
        }
        catch (IOException ex) {
            Logger.getLogger(Persona.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(ServidorHiloCliente.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BrokenBarrierException ex) {
            Logger.getLogger(ServidorHiloCliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }

}
