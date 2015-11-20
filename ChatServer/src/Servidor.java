import java.net.*;
import java.util.*;
import java.io.*;

public class Servidor
{   //Vectores para almacenar sockets y UserNames
    static Vector Vec_CSockets;
    static Vector Vec_UserNames;
    //Constructor
    Servidor() throws Exception
    {   //Puero default e inicializador de vectores.
        ServerSocket Socket_Servidor = new ServerSocket(5000);
        Vec_CSockets = new Vector();
        Vec_UserNames = new Vector();
        //Escucha constantemente
        while(true)
        {    
            Socket Socket_Cliente = Socket_Servidor.accept();        
            AceptaCliente obCliente = new AceptaCliente(Socket_Cliente);
        }
    }
    
    public static void main(String args[]) throws Exception
    {
        //Simple main que inicializa el Servidor
        Servidor Serv = new Servidor();
    }

    class AceptaCliente extends Thread
    {   //Variables para input y output
        Socket ClientSocket;
        DataInputStream dInput;
        DataOutputStream dOutput;
        AceptaCliente(Socket ClSoc) throws Exception
        {
            ClientSocket = ClSoc;
            //Inicializa el stream IO.
            dInput = new DataInputStream(ClientSocket.getInputStream());
            dOutput = new DataOutputStream(ClientSocket.getOutputStream());

            String UserName = dInput.readUTF();
            System.out.println("Entró al chat :" + UserName);
            //Guarda usuario y socket en los vectores de manera paralela.
            Vec_UserNames.add(UserName);
            Vec_CSockets.add(ClientSocket);    
            start();
        }

        public void run()
        {
            while(true)
            {
                try
                {   //Recibe mensaje y revisa que tipo es. (LOGOUT || PRIVATE || BROADCAST)
                    String mensajeCliente = dInput.readUTF();
                    StringTokenizer sToken = new StringTokenizer(mensajeCliente);
                    String sUsuario = sToken.nextToken();                
                    String sAccion = sToken.nextToken();
                    boolean bEncontro = false;
                    int iContador = 0;
                    //Revisa si se desconecto.
                    if(sAccion.equals("LOGOUT"))
                    {   //LOGOUT
                        for(iContador = 0; iContador < Vec_UserNames.size(); iContador++)
                        {
                            if(Vec_UserNames.elementAt(iContador).equals(sUsuario))
                            {   //Elimina en los vectores el usuario desconectado y su socket paralelamente.
                                Vec_UserNames.removeElementAt(iContador);
                                Vec_CSockets.removeElementAt(iContador);
                                System.out.println("El usuario " + sUsuario +" se desconectó.");
                                break;
                            }
                        }

                    }else if(sAccion.equals("PRIVATE"))
                    {   //PRIVATE
                        //Envio de mensaje privado
                        String sMensaje = "";
                        String sSendTo = sUsuario;
                        sUsuario = sToken.nextToken();
                        sToken.nextToken();
                        while(sToken.hasMoreTokens())
                        {   //Lee el mensaje hasta el final
                            sMensaje += " " + sToken.nextToken();
                        }
                        for(iContador = 0; iContador < Vec_UserNames.size(); iContador++)
                        {   //Busca el usuario privado
                            if(Vec_UserNames.elementAt(iContador).equals(sUsuario) || Vec_UserNames.elementAt(iContador).equals(sSendTo))
                            {   
                                bEncontro = true;
                                Socket Socket_Encontrado = (Socket)Vec_CSockets.elementAt(iContador);                            
                                DataOutputStream streamOut = new DataOutputStream(Socket_Encontrado.getOutputStream());
                                streamOut.writeUTF(sUsuario + " : " + sMensaje);                            
                                //break;
                            }
                        }
                        if(!bEncontro)
                        {   //Si no lo encontró, despliega que está desconectado.
                            dOutput.writeUTF("El usuario está Offline");
                        }
                    }
                    else
                    {   //BROADCAST
                        //Envio de mensaje broadcast
                        String sMensaje = "";
                        while(sToken.hasMoreTokens())
                        {   //Lee el mensaje hasta el final
                            sMensaje += " " + sToken.nextToken();
                        }
                        for(iContador = 0; iContador < Vec_UserNames.size(); iContador++)
                        {   //Busca el usuario privado
                            Socket Socket_Encontrado = (Socket)Vec_CSockets.elementAt(iContador);                            
                            DataOutputStream streamOut = new DataOutputStream(Socket_Encontrado.getOutputStream());
                            streamOut.writeUTF(sUsuario + " : " + sMensaje);                            
                        }
                        /*if(iContador == Vec_UserNames.size())
                        {   //Si no lo encontró, despliega que está desconectado.
                            dOutput.writeUTF("Offline");
                        }*/
                    }
                    if(sAccion.equals("LOGOUT"))
                    {
                        break;
                    }
                }
                catch(Exception ex)
                {   //No se que hace, mi IDE lo agregó. Es para ver la Excepción en la pila.
                    ex.printStackTrace();
                }       
            }        
        }
    }
}