import java.net.*;
import java.io.*;
import java.awt.*;
import java.util.*;
import javax.swing.*;

public class Cliente extends Frame implements Runnable
{   //Variables para interfaz y IO.
    Socket soc;    
    TextField tf_Texto;
    TextArea ta_AreaTexto;
    Button btn_Enviar, btn_Cerrar;
    String sendTo;
    String sUsuario;
    Thread t_Thread = null;
    DataOutputStream dout;
    static DataInputStream din;
    Cliente(String sUsuario, String ipServidor) throws Exception
    {   //Constructor. Inicializa los sockets, conexión e interfaz
        super(sUsuario);
        this.sUsuario = sUsuario;
        tf_Texto = new TextField(50);
        ta_AreaTexto = new TextArea(50,50);
        btn_Enviar = new Button("Enviar");
        btn_Cerrar = new Button("Cerrar");
        soc = new Socket(ipServidor, 5000);
        //Inicializa IO streams para intercambio de informacion con servidor
        din = new DataInputStream(soc.getInputStream()); 
        dout = new DataOutputStream(soc.getOutputStream());        
        dout.writeUTF(sUsuario);
        //Inicializa thread para multiusuario
        t_Thread = new Thread(this);
        t_Thread.start();
    }
    void setup()
    {   //Dimensiones de interfaz. La muestra.
        setSize(650,450);
        //Renglon, columna, gap altura, gap anchura
        setLayout(new GridLayout(2 ,2 , 50, 50));
        add(ta_AreaTexto);
        Panel p_Panel = new Panel();
        p_Panel.add(tf_Texto);
        p_Panel.add(btn_Enviar);
        p_Panel.add(btn_Cerrar);
        add(p_Panel);
        show();        
    }
    @Override
    public boolean action(Event e,Object o)
    {
        if(e.arg.equals("Enviar"))
        {   //Revisa si el mensaje es privado o no.
            String sMensaje = tf_Texto.getText();
            StringTokenizer sToken = new StringTokenizer(sMensaje);
            if(sMensaje.charAt(0) == '@')
            {
                try
                {   //Obtiene el usuario al que sera mensaje privado.
                    sendTo = sToken.nextToken();
                    sendTo = sendTo.substring(1);
                    System.out.println("Privado a: " + sendTo);
                    dout.writeUTF(sendTo + " "  + "PRIVATE" + " " + sUsuario + " " + tf_Texto.getText());             
                    tf_Texto.setText("");
                }
                catch(Exception ex)
                {
                } 
            }else
            {
                try
                {
                    dout.writeUTF(sUsuario + " "  + "BROADCAST" + " " + tf_Texto.getText());            
                    tf_Texto.setText("");
                }
                catch(Exception ex)
                {
                }
            }
        }
        else if(e.arg.equals("Cerrar"))
        {
            try
            {
                dout.writeUTF(sUsuario + " LOGOUT");
                System.exit(1);
            }
            catch(Exception ex)
            {
            }   
        }
        return super.action(e,o);
    }
    
    //Pide al usuario el IP del servidor
    private static String getIPServidor() {
        return JOptionPane.showInputDialog(
            null,
            "Diga la IP del Servidor.\nFormato 125.125.125.125",
            "Bienvenido",
            JOptionPane.QUESTION_MESSAGE);
    }
    
    //Pide al usuario su nombre de usuario
    private static String getUsuario() {
        return JOptionPane.showInputDialog(
            null,
            "Diga su nombre de usuario. Si se repite este mensaje es porque ya existe ese usuario.",
            "Login",
            JOptionPane.QUESTION_MESSAGE);
    }
    
    
    
    public static void main(String args[]) throws Exception
    {
        System.out.println(InetAddress.getLocalHost());
        int iExiste;
        String Usuario, ipServidor;
        Cliente obj_Cliente;
        do{
        Usuario = getUsuario();
        ipServidor = getIPServidor();
        obj_Cliente = new Cliente(Usuario, ipServidor);
        iExiste = Integer.parseInt(din.readUTF());
        System.out.println("iExiste: " + iExiste);
        }while(iExiste != 1);
        obj_Cliente.setup();        
    }  
    
    @Override
    public void run()
    {        
        while(true)
        {
            try
            {
                ta_AreaTexto.append( "\n" +  din.readUTF());  
            }
            catch(Exception ex)
            {
            }
        }
    }
}