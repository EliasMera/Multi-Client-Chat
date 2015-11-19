import java.net.*;
import java.io.*;
import java.awt.*;
import java.util.StringTokenizer;

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
    DataInputStream din;
    Cliente(String sUsuario,String chatwith) throws Exception
    {   //Constructor. Inicializa los sockets, conexión e interfaz
        super(sUsuario);
        this.sUsuario = sUsuario;
        sendTo=chatwith;
        tf_Texto = new TextField(50);
        ta_AreaTexto = new TextArea(50,50);
        btn_Enviar = new Button("Enviar");
        btn_Cerrar = new Button("Cerrar");
        soc = new Socket("127.0.0.1", 5000);
        //Inicializa IO streams para intercambio de informacion con servidor
        din=new DataInputStream(soc.getInputStream()); 
        dout=new DataOutputStream(soc.getOutputStream());        
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
                    dout.writeUTF(sendTo + " "  + "PRIVATE" + " " + tf_Texto.getText());            
                    ta_AreaTexto.append("\n" + sUsuario + " Says:" + tf_Texto.getText());    
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
                    ta_AreaTexto.append("\n" + sUsuario + " Says:" + tf_Texto.getText());    
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
    public static void main(String args[]) throws Exception
    {
        Cliente obj_Cliente = new Cliente("b", "elias");
        obj_Cliente.setup();                
    }  
    
    @Override
    public void run()
    {        
        while(true)
        {
            try
            {
                ta_AreaTexto.append( "\n" + sendTo + " : " + din.readUTF());  
            }
            catch(Exception ex)
            {
            }
        }
    }
}